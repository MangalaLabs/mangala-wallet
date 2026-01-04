package com.mangala.wallet.features.conversationui.domain.usecase

import com.mangala.wallet.core.ai.data.remote.AIResponse
import com.mangala.wallet.core.ai.domain.model.function.FunctionResult
import com.mangala.wallet.core.ai.domain.model.message.FunctionCallMessage
import com.mangala.wallet.core.ai.domain.model.message.FunctionResultMessage
import com.mangala.wallet.core.ai.domain.repository.AiRepository
import com.mangala.wallet.core.ai.domain.model.message.Message
import com.mangala.wallet.core.ai.domain.model.message.TextMessage
import com.mangala.wallet.core.ai.domain.model.message.ImageMessage
import com.mangala.wallet.core.ai.domain.model.message.MultiModalMessage
import com.mangala.wallet.core.ai.domain.model.message.FunctionCallConfirmationRequiredMessage
import com.mangala.wallet.core.ai.domain.model.function.definition.FunctionRegistry
import com.mangala.wallet.core.ai.domain.model.function.handler.FunctionHandlerRegistry
import com.mangala.wallet.core.ai.domain.model.message.SystemMessage
import com.mangala.wallet.core.ai.domain.model.process.preprocess.PreprocessorChain
import com.mangala.wallet.core.ai.domain.model.process.preprocess.preprocessors.TextNormalizationPreprocessor
import com.mangala.wallet.core.security.models.SecurityLevel
import com.mangala.wallet.core.ai.domain.model.factory.MessageFactoryRegistry
import com.mangala.wallet.features.conversationui.domain.repository.ChatHistoryRepository
import com.mangala.wallet.features.conversationui.domain.util.mapAIResponseToMessage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.transform
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

//@OptIn(ExperimentalUuidApi::class)
//@Deprecated("Use SendSocketMessageUseCase instead for WebSocket support")
//class SendMessageUseCase(
//    private val chatHistoryRepository: ChatHistoryRepository,
//    private val aiRepository: AiRepository,
//    private val functionHandlerRegistry: FunctionHandlerRegistry,
//    private val functionRegistry: FunctionRegistry,
//    private val messageFactoryRegistry: MessageFactoryRegistry
//) {
//    @OptIn(ExperimentalStdlibApi::class)
//    suspend operator fun invoke(userId: String, message: Message): Flow<AIResponse> {
//        var currentSession = try {
//            chatHistoryRepository.getCurrentSession(userId).first()
//        } catch (e: Exception) {
//            chatHistoryRepository.createSession(userId)
//        }
//
//        if (currentSession.messages.isEmpty()) {
//            // Adds system prompt
//            chatHistoryRepository.saveMessage(
//                currentSession.id,
//                SystemMessage(
//                    text = """
//                    You are a chatbot for a cryptocurrency wallet app. Your role is to assist users in managing their contacts by creating, editing, and deleting contacts.
//
//                    CREATE CONTACT FLOW:
//                    User initiates the request to create a new contact.
//                    You ask for the contact's name.
//                    After receiving the name, you ask for the contact's blockchain network first.
//                    After receiving the network, you ask for the contact's blockchain address.
//                    Once all information is provided, you trigger a function call to create the contact.
//
//                    EDIT CONTACT FLOW:
//                    User initiates the request to edit an existing contact.
//                    You ask which contact they want to edit (if not specified).
//                    You ask what information they want to change (name, address, or network).
//                    You collect the new information and trigger a function call to update the contact.
//
//                    DELETE CONTACT FLOW:
//                    User initiates the request to delete a contact.
//                    You ask which contact they want to delete (if not specified).
//                    You confirm the deletion and trigger a function call to delete the contact.
//
//                    When you are asking the user to select the blockchain network, and that is the only thing you are asking for in that message, include the tag [SELECT_NETWORK] at the end of your response. For example, your response should be something like:
//                    "Which blockchain network would you like to use? [SELECT_NETWORK]"
//                    This tag is for the frontend to know that it should display a picker for the user to choose from the list of supported networks. Do not include this tag in any other messages.
//
//                    When you are asking the user to enter a blockchain address for a specific network, and that is the only thing you are asking for in that message, include the tag [ENTER_ADDRESS:networkname] at the end of your response, where "networkname" is the exact network name the user specified. For example, your response should be something like:
//                    "What is Mike's blockchain address on the EOS Jungle Testnet network? [ENTER_ADDRESS:EOS Jungle Testnet]"
//                    This tag is for the frontend to know that it should display an address input field for the specified network. Do not include this tag in any other messages.
//
//                    Important Notes:
//                    [SELECT_NETWORK] should only be included when your entire message is dedicated to asking for the blockchain network.
//                    [ENTER_ADDRESS:networkname] should only be included when your entire message is dedicated to asking for a blockchain address for a specific network.
//                    Do not include these tags if you are asking for other information (e.g., name) or providing additional context.
//                    These tags are crucial for the frontend to know when to display the appropriate input components, so it's important to use them correctly.
//                """.trimIndent(),
//                    senderId = "system"
//                )
//            )
//        }
//
//        val processedMessage = preprocessMessage(message)
//
//        chatHistoryRepository.saveMessage(currentSession.id, processedMessage)
//
//        currentSession = chatHistoryRepository.getSession(currentSession.id) ?: return flowOf()
//
//        val newMessageId = Uuid.random().toString()
//
//        return aiRepository.processConversation(
//            userId = userId,
//            messages = currentSession.messages,
//            isMultiModalEnabled = false
//        ).transform { aiResponse ->
//            println("ProcessMessageUseCase aiResponse $aiResponse")
//
//            if (aiResponse is AIResponse.FunctionCallResponse) {
//                val functionDef = functionRegistry.getFunctionByName(aiResponse.functionCall.name)
//
//                if (functionDef?.securityLevel?.index == null || functionDef.securityLevel.index > SecurityLevel.None.index) {
//                    // Create a confirmation required message
//                    val confirmationMessage = FunctionCallConfirmationRequiredMessage(
//                        id = Uuid.random().toString(),
//                        senderId = "",
//                        functionCall = aiResponse.functionCall,
//                        confirmationPrompt = "This action requires your confirmation:",
//                        functionDescription = functionDef?.description
//                    )
//
//                    // Save the confirmation message to chat history
//                    chatHistoryRepository.saveMessage(currentSession.id, confirmationMessage)
//
//                    // Emit a response indicating confirmation is required
//                    val confirmationResponse = AIResponse.ConfirmationRequiredResponse(
//                        functionCall = aiResponse.functionCall,
//                        message = "Confirmation required for: ${functionDef?.description}"
//                    )
//
//                    emit(confirmationResponse)
//                    return@transform
//                } else {
//                    // First, save the function call message11 to history
//                    val functionCallMsg = FunctionCallMessage(
//                        id = Uuid.random().toString(),
//                        senderId = "",
//                        functionName = aiResponse.functionCall.name,
//                        parameters = aiResponse.functionCall.parameters,
//                        explanation = aiResponse.reasoning,
//                        callId = aiResponse.functionCall.callId
//                    )
//                    chatHistoryRepository.saveMessage(currentSession.id, functionCallMsg)
//
//                    // Execute the function
//                    val result = functionHandlerRegistry.getHandlerByName(aiResponse.functionCall.name)?.execute(aiResponse.functionCall.parameters)
//                    println("ProcessMessageUseCase executed function ${aiResponse.functionCall.name} $result")
//
//                    // Try to create feature-specific message if result has UI hints
//                    if (result is FunctionResult.Success && result.uiHint != null) {
//                        val featureMessage = messageFactoryRegistry.createMessageFromFunctionResult(
//                            functionName = aiResponse.functionCall.name,
//                            result = result,
//                            messageId = Uuid.random().toString(),
//                            senderId = ""
//                        )
//
//                        featureMessage?.let {
//                            chatHistoryRepository.saveMessage(currentSession.id, it)
//                        }
//                    }
//
//                    // Send the result back to the LLM
//                    if (result != null) {
//                        // Save the function result message to history
//                        val functionResultMsg = FunctionResultMessage(
//                            id = Uuid.random().toString(),
//                            senderId = userId,
//                            functionName = aiResponse.functionCall.name,
//                            result = result,
//                            callId = aiResponse.functionCall.callId
//                        )
//                        chatHistoryRepository.saveMessage(currentSession.id, functionResultMsg)
//
//                        // Get updated session with all messages
//                        currentSession = chatHistoryRepository.getSession(currentSession.id) ?: return@transform
//
//                        val result = aiRepository.processConversation(
//                            userId = userId,
//                            messages = currentSession.messages,
//                            isMultiModalEnabled = false
//                        ).first()
//                        result.mapAIResponseToMessage(Uuid.random().toString())?.let {
//                            chatHistoryRepository.saveMessage(currentSession.id, it)
//                        }
//                        emit(result)
//                        return@transform
//                    }
//                }
//            }
//
//            val responseMessage = aiResponse.mapAIResponseToMessage(newMessageId)
//            if (responseMessage != null) {
//                println("SendMessageUseCase: Saving AI response message to session ${currentSession.id}")
//                println("SendMessageUseCase: Message type: ${responseMessage::class.simpleName}, ID: ${responseMessage.id}")
//                chatHistoryRepository.saveMessage(currentSession.id, responseMessage)
//            } else {
//                println("SendMessageUseCase: No message created from AIResponse type: ${aiResponse::class.simpleName}")
//            }
//
//            emit(aiResponse)
//        }
//    }
//
//    /**
//     * Save a user message to the chat history without processing it through AI
//     * This is used when messages are sent via WebSocket to the backend
//     */
//    suspend fun saveUserMessage(userId: String, sessionId: String, message: Message) {
//        val processedMessage = preprocessMessage(message)
//        chatHistoryRepository.saveMessage(sessionId, processedMessage)
//    }
//
//    private fun preprocessMessage(message: Message): Message {
//        return when (message) {
//            is TextMessage -> {
//                val preprocessorChain = PreprocessorChain.createChain(
//                    listOf(
//                        TextNormalizationPreprocessor(toLowercase = false)
//                    )
//                )
//
//                val result = preprocessorChain?.process(message.text)
//
//                message.copy(text = result ?: message.text)
//            }
//            is ImageMessage -> message
//            is MultiModalMessage -> message
//
//            else -> message
//        }
//    }
//}