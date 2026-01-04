package com.mangala.wallet.core.ai.domain.model.message

sealed class UiTag(val tag: String) {
    data object SelectNetwork : UiTag("[SELECT_NETWORK]")
    @Deprecated("Use EnterAddress instead")
    data object RequestAddressInput : UiTag("[REQUEST_ADDRESS_INPUT]")
    data class EnterAddress(val networkName: String) : UiTag("[ENTER_ADDRESS:$networkName]")
    
    companion object {
        fun fromString(tagString: String): UiTag? {
            return when {
                tagString == "[SELECT_NETWORK]" -> SelectNetwork
                tagString == "[REQUEST_ADDRESS_INPUT]" -> RequestAddressInput
                tagString.startsWith("[ENTER_ADDRESS:") && tagString.endsWith("]") -> {
                    val networkName = tagString.removePrefix("[ENTER_ADDRESS:").removeSuffix("]")
                    EnterAddress(networkName)
                }
                else -> null
            }
        }
    }
}