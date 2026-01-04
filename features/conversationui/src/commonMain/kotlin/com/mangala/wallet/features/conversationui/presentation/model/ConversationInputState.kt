package com.mangala.wallet.features.conversationui.presentation.model

import com.mangala.wallet.model.blockchain.BlockchainNetworkData

data class ConversationInputState(
    val mode: InputMode = InputMode.Normal,
    val addressValidation: AddressValidationState? = null,
    val amountValidation: AmountValidationState? = null,
    val messageText: String = "",
    // Network selection state
    val networkSelectionMessage: String? = null,
    val selectedNetworkId: String? = null
)

data class AmountValidationState(
    val isValid: Boolean? = null,
    val errorMessage: String? = null,
    val tokenSymbol: String,
    val balance: String
)

sealed class InputMode {
    data object Normal : InputMode()
    data class EnterAddress(val networkName: String) : InputMode()
    data object SelectNetwork : InputMode() // New mode for network selection
    data object EnterMemo : InputMode() // New mode for memo input
    data class EnterAmount(val tokenSymbol: String, val balance: String) : InputMode() // New mode for amount input
    data class ContactName(
        val placeholder: String = "Enter contact name",
        val label: String = "Contact Name",
        val defaultValue: String = ""
    ) : InputMode()
}

data class AddressValidationState(
    val isValid: Boolean?,
    val errorMessage: String?,
    val network: String,
    val formattedAddress: String?,
    val isLoading: Boolean = false
)