package com.mangala.wallet.core.ai.data.remote

import com.mangala.wallet.core.ai.domain.model.function.FunctionCallRequest
import com.mangala.wallet.core.ai.domain.model.function.FunctionResult
import com.mangala.wallet.core.ai.domain.model.message.UiTag

sealed class AIResponse {
    data class TextResponse(val text: String, val uiTags: List<UiTag> = emptyList()) : AIResponse()

    data class ImageResponse(
        val imageData: ByteArray,
        val mimeType: String
    ) : AIResponse() {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other == null || this::class != other::class) return false

            other as ImageResponse

            if (!imageData.contentEquals(other.imageData)) return false
            if (mimeType != other.mimeType) return false

            return true
        }

        override fun hashCode(): Int {
            var result = imageData.contentHashCode()
            result = 31 * result + mimeType.hashCode()
            return result
        }
    }

    data class FunctionCallResponse(
        val functionCall: FunctionCallRequest,
        val reasoning: String? = null
    ) : AIResponse()
    
    data class ConfirmationRequiredResponse(
        val functionCall: FunctionCallRequest,
        val message: String
    ) : AIResponse()

    data class FunctionResultResponse(
        val result: FunctionResult,
        val functionName: String
    ) : AIResponse()

    data class ErrorResponse(
        val error: String
    ) : AIResponse()
    
    data class MultiModalResponse(
        val responses: List<AIResponse>
    ) : AIResponse()
}