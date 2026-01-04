package com.mangala.wallet.core.ai.data.remote.providers.gemini.request

import com.mangala.wallet.core.ai.data.remote.providers.gemini.response.GeminiGenerateContentResponse
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

@Serializable
data class GeminiGenerateContentRequest(
    @SerialName("contents")
    val contents: List<Content?>? = listOf(),
    @SerialName("generationConfig")
    val generationConfig: GenerationConfig? = GenerationConfig(),
    @SerialName("safetySettings")
    val safetySettings: List<SafetySetting?>? = listOf(),
    @SerialName("tools")
    val tools: List<Tool?>? = listOf()
) {
    @Serializable
    data class Content(
        @SerialName("parts")
        val parts: List<Part?>? = listOf(),
        @SerialName("role")
        val role: String? = ""
    ) {
        @Serializable
        data class Part(
            @SerialName("text")
            val text: String? = null,
            @SerialName("file_data")
            val fileData: FileData? = null,
            @SerialName("inline_data")
            val inlineData: InlineData? = null,
            @SerialName("function_call")
            val functionCall: FunctionCall? = null,
            @SerialName("function_response")
            val functionResponse: FunctionResponse? = null
        ) {
            @Serializable
            data class FileData(
                @SerialName("mime_type")
                val mimeType: String,

                @SerialName("file_uri")
                val fileUri: String
            )

            @Serializable
            data class InlineData(
                @SerialName("mime_type")
                val mimeType: String,
                @SerialName("data")
                val data: String // Base64 encoded data
            )

            @Serializable
            data class FunctionCall(
                @SerialName("name")
                val name: String,
                @Serializable
                val args: JsonObject
            )

            @Serializable
            data class FunctionResponse(
                @SerialName("name")
                val name: String,
                @SerialName("response")
                val response: JsonObject?
            )
        }
    }

    @Serializable
    data class GenerationConfig(
        @SerialName("maxOutputTokens")
        val maxOutputTokens: Int? = 0,
        @SerialName("temperature")
        val temperature: Double? = 0.0,
        @SerialName("topK")
        val topK: Int? = 0,
        @SerialName("topP")
        val topP: Double? = 0.0,
        @SerialName("responseModalities")
        val responseModalities: List<String?>? = null
    )

    @Serializable
    data class SafetySetting(
        @SerialName("category")
        val category: String? = "",
        @SerialName("threshold")
        val threshold: String? = ""
    )

    @Serializable
    data class Tool(
        @SerialName("functionDeclarations")
        val functionDeclarations: List<FunctionDeclaration?>? = listOf()
    ) {
        @Serializable
        data class FunctionDeclaration(
            @SerialName("description")
            val description: String? = "",
            @SerialName("name")
            val name: String? = "",
            @SerialName("parameters")
            val parameters: Parameters? = Parameters()
        ) {
            @Serializable
            data class Parameters(
                @SerialName("properties")
                val properties: JsonObject? = null,
                @SerialName("required")
                val required: List<String?>? = listOf(),
                @SerialName("type")
                val type: String? = ""
            ) {
                @Serializable
                data class PropertyValue(
                    @SerialName("type")
                    val type: String,

                    @SerialName("description")
                    val description: String,

                    @SerialName("enum")
                    val enum: List<String>? = null,

                    @SerialName("items")
                    val items: PropertyValue? = null,

                    @SerialName("properties")
                    val properties: Map<String, PropertyValue>? = null
                )
            }
        }
    }
}