package com.mangala.wallet.core.ai.domain.model.function

import com.mangala.wallet.core.security.models.SecurityLevel

data class FunctionCallRequest(
    val name: String,
    val parameters: Map<String, Any?>,
    val callId: String? = null, // Useful for OpenAI responses
    val securityLevel: SecurityLevel
)