package com.mangala.wallet.features.contacts.presentation.addcontact

import com.mangala.wallet.model.blockchain.BlockchainNetworkData
import com.mangala.wallet.ui.RecipientValidationStatus

data class AddContactScreenUiModel(
    val name: String = "",
    val network: BlockchainNetworkData? = null,
    val address: String = "",
    val recipientValidationStatus: RecipientValidationStatus = RecipientValidationStatus.NotValidated,
    val addressDone: Boolean = false
) {
    var isAddressErrorVisible =
        address.isNotBlank() && recipientValidationStatus is RecipientValidationStatus.Invalid
    val saveButtonEnabled =
        name.isNotBlank() && network != null && address.isNotBlank() && recipientValidationStatus is RecipientValidationStatus.Valid && addressDone
}