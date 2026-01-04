package com.mangala.wallet.features.nft_base.presentation.send

import com.mangala.wallet.features.nft_base.domain.model.NftCollection
import com.mangala.wallet.model.blockchain.Blockchain
import com.mangala.wallet.model.blockchain.BlockchainType
import com.mangala.wallet.model.contact.ContactEntity

sealed interface SendNftScreenUiState {
    data object Loading : SendNftScreenUiState
    data class Success(val uiModel: SendNftScreenUiModel) : SendNftScreenUiState
    data class Error(val message: String) : SendNftScreenUiState
}

data class SendNftScreenUiModel(
    val nftCollection: NftCollection,
    val contacts: List<ContactEntity>,
    val contactsFilter: String,
    val address: String,
    val isValidAddress: Boolean,
    val contactId: Long?,
    val isSaveRecipientEnabled: Boolean,
    val newRecipientName: String? = null,
    val isDoneSelectAddress: Boolean,
    val isDoneEnterRecipientName: Boolean,
    val isDoneEnterInfo: Boolean = false,
    val blockchainType: BlockchainType
) {
    val filteredContacts = if (contactsFilter.isBlank()) contacts else contacts.filter { it.name.contains(contactsFilter) }
    val continueButtonEnabled = isValidAddress && (isSaveRecipientEnabled.not() || isSaveRecipientEnabled && newRecipientName?.isNotEmpty() == true)
    val saveRecipientSwitchEnabled = isDoneSelectAddress && address.isNotEmpty()
}