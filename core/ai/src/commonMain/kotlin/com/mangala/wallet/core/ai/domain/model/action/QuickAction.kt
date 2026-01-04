package com.mangala.wallet.core.ai.domain.model.action

data class QuickAction(
    val id: String,
    val label: String,
    val icon: String? = null,
    val actionType: QuickActionType,
    val metadata: Map<String, Any> = emptyMap()
)

sealed class QuickActionType {
    data class Navigate(val destination: String) : QuickActionType()
    data class ExecuteFunction(val functionName: String) : QuickActionType()
    data class ShowDialog(val dialogType: String) : QuickActionType()
}