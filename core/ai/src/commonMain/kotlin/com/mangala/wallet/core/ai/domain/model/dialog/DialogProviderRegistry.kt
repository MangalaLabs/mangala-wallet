package com.mangala.wallet.core.ai.domain.model.dialog

interface DialogProviderRegistry {
    fun registerProvider(provider: DialogProvider)
    fun getProvider(type: String, context: Map<String, Any>): DialogProvider?
}

class DefaultDialogProviderRegistry(providers: List<DialogProvider>) : DialogProviderRegistry {
    private val providers = providers.toMutableList()
    
    override fun registerProvider(provider: DialogProvider) {
        providers.add(provider)
    }
    
    override fun getProvider(type: String, context: Map<String, Any>): DialogProvider? {
        return providers.asSequence()
            .filter { provider -> provider.canProvideDialog(type, context) }
            .firstOrNull()
    }
}