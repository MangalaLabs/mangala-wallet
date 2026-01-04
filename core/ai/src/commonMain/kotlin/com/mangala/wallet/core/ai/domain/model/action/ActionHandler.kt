package com.mangala.wallet.core.ai.domain.model.action

interface ActionHandler {
    fun getSupportedActions(): Set<String>
    fun canHandle(action: String, context: Map<String, Any>): Boolean
    fun handleAction(action: String, context: Map<String, Any>): ActionResult
}