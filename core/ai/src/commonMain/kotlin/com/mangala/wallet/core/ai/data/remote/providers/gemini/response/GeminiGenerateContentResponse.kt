package com.mangala.wallet.core.ai.data.remote.providers.gemini.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

@Serializable
data class GeminiGenerateContentResponse(
    @SerialName("candidates")
    val candidates: List<Candidate?>? = listOf(),
    @SerialName("modelVersion")
    val modelVersion: String? = "",
    @SerialName("usageMetadata")
    val usageMetadata: UsageMetadata? = UsageMetadata()
) {
    @Serializable
    data class Candidate(
        @SerialName("avgLogprobs")
        val avgLogprobs: Double? = 0.0,
        @SerialName("content")
        val content: Content? = Content(),
        @SerialName("finishReason")
        val finishReason: String? = "",
        @SerialName("safetyRatings")
        val safetyRatings: List<SafetyRating?>? = listOf()
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
                @SerialName("functionCall")
                val functionCall: FunctionCall? = null,
                @SerialName("text")
                val text: String? = null,
                @SerialName("inlineData")
                val inlineData: InlineData? = null
            ) {
                @Serializable
                data class FunctionCall(
                    @SerialName("args")
                    val args: JsonObject? = null,
                    @SerialName("name")
                    val name: String? = ""
                )

                @Serializable
                data class InlineData(
                    @SerialName("mimeType")
                    val mimeType: String? = null,
                    @SerialName("data")
                    val data: String? = null
                )
            }
        }

        @Serializable
        data class SafetyRating(
            @SerialName("category")
            val category: String? = "",
            @SerialName("probability")
            val probability: String? = ""
        )
    }

    @Serializable
    data class UsageMetadata(
        @SerialName("candidatesTokenCount")
        val candidatesTokenCount: Int? = 0,
        @SerialName("candidatesTokensDetails")
        val candidatesTokensDetails: List<CandidatesTokensDetail?>? = listOf(),
        @SerialName("promptTokenCount")
        val promptTokenCount: Int? = 0,
        @SerialName("promptTokensDetails")
        val promptTokensDetails: List<PromptTokensDetail?>? = listOf(),
        @SerialName("totalTokenCount")
        val totalTokenCount: Int? = 0
    ) {
        @Serializable
        data class CandidatesTokensDetail(
            @SerialName("modality")
            val modality: String? = "",
            @SerialName("tokenCount")
            val tokenCount: Int? = 0
        )

        @Serializable
        data class PromptTokensDetail(
            @SerialName("modality")
            val modality: String? = "",
            @SerialName("tokenCount")
            val tokenCount: Int? = 0
        )
    }
}