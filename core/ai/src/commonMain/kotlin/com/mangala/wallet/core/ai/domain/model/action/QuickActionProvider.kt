package com.mangala.wallet.core.ai.domain.model.action

interface QuickActionProvider {
    fun getQuickActionsForFunction(
        functionName: String, 
        context: Map<String, Any>
    ): List<QuickAction>
}