package com.mangala.wallet.core.ai.domain.model.action

sealed class ActionResult {
    data class ShowQuickActions(
        val messageId: String,
        val actions: List<QuickAction>,
        val context: Map<String, Any> = emptyMap()
    ) : ActionResult()
    
    data class ShowDialog(
        val dialogType: String,
        val context: Map<String, Any>
    ) : ActionResult()
    
    data class Navigate(
        val destination: String,
        val context: Map<String, Any>
    ) : ActionResult()
    
    data class UpdateState(
        val updates: Map<String, Any>
    ) : ActionResult()
    
    data class ShowToast(
        val message: String,
        val isError: Boolean = false
    ) : ActionResult()
    
    object Handled : ActionResult()
    object NotHandled : ActionResult()
}