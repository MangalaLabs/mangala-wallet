package com.mangala.wallet.features.conversationui.domain.usecase

import com.mangala.wallet.core.ai.domain.model.message.ImageMessage
import com.mangala.wallet.core.ai.domain.model.message.Message
import com.mangala.wallet.core.ai.domain.model.message.MultiModalMessage
import com.mangala.wallet.core.ai.domain.model.message.TextMessage
import com.mangala.wallet.features.conversationui.domain.repository.ChatHistoryRepository
import kotlinx.datetime.Clock
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

@Serializable
data class ExportedChatLog(
    val sessionId: String,
    val exportTime: String,
    val messageCount: Int,
    val messages: List<ExportedMessage>
)

@Serializable
data class ExportedMessage(
    val id: String,
    val isFromUser: Boolean,
    val senderId: String,
    val timestamp: String,
    val type: String,
    val content: ExportedMessageContent
)

@Serializable
data class ExportedMessageContent(
    val text: String? = null,
    val imageData: String? = null, // Base64 encoded
    val mimeType: String? = null,
    val multiModalMessages: List<ExportedMessage>? = null
)

data class ExportResult(
    val jsonContent: String,
    val filePath: String?,
    val filename: String
)

expect class FileExporter {
    suspend fun exportToFile(filename: String, content: String): String?
}

class ExportChatLogUseCase(
    private val chatHistoryRepository: ChatHistoryRepository,
    private val fileExporter: FileExporter
) {
    private val json = Json {
        prettyPrint = false
        ignoreUnknownKeys = true
    }

    @OptIn(ExperimentalEncodingApi::class)
    suspend operator fun invoke(sessionId: String): ExportResult {
        val session = chatHistoryRepository.getSession(sessionId)
            ?: throw IllegalArgumentException("Session not found: $sessionId")
        
        val messages = session.messages
        
        val exportedChatLog = ExportedChatLog(
            sessionId = sessionId,
            exportTime = Clock.System.now().toString(),
            messageCount = messages.size,
            messages = messages.map { message -> mapMessageToExported(message) }
        )
        
        val jsonString = json.encodeToString(exportedChatLog)
        
        // Print to logcat
        println("=== CHAT EXPORT JSON ===")
        println(jsonString)
        println("=== END EXPORT ===")
        
        // Generate filename with timestamp
        val timestamp = Clock.System.now().toString().replace(":", "-").replace(".", "-")
        val filename = "chat_export_${sessionId}_$timestamp.json"
        
        // Export to file
        val filePath = fileExporter.exportToFile(filename, jsonString)
        
        return ExportResult(
            jsonContent = jsonString,
            filePath = filePath,
            filename = filename
        )
    }
    
    @OptIn(ExperimentalEncodingApi::class)
    private fun mapMessageToExported(message: Message): ExportedMessage {
        return ExportedMessage(
            id = message.id,
            isFromUser = message.isFromUser,
            senderId = message.senderId,
            timestamp = message.timestamp.toString(),
            type = message::class.simpleName ?: "Unknown",
            content = when (message) {
                is TextMessage -> ExportedMessageContent(
                    text = message.text
                )
                is ImageMessage -> ExportedMessageContent(
                    imageData = Base64.encode(message.imageData),
                    mimeType = message.mimeType
                )
                is MultiModalMessage -> ExportedMessageContent(
                    multiModalMessages = message.messages.map { subMessage ->
                        mapMessageToExported(subMessage)
                    }
                )
                else -> ExportedMessageContent(
                    text = "Unsupported message type: ${message::class.simpleName}"
                )
            }
        )
    }
}