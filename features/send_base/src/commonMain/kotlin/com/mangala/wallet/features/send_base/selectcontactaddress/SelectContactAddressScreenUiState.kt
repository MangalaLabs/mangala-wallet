package com.mangala.wallet.features.send_base.selectcontactaddress

import com.mangala.wallet.features.addressbook.data.model.ContactModel
import com.mangala.wallet.features.addressbook.data.model.WalletAddressWithNetworkModel

sealed class SelectContactAddressScreenUiState {
    data object Loading : SelectContactAddressScreenUiState()
    
    data class Error(
        val message: String
    ) : SelectContactAddressScreenUiState()
    
    data class Data(
        val contact: ContactModel,
        val addressGroups: List<BlockchainAddressGroup>,
        val selectedAddress: WalletAddressWithNetworkModel? = null,
        val isLoading: Boolean = false
    ) : SelectContactAddressScreenUiState()
}

data class BlockchainAddressGroup(
    val blockchainId: String,
    val blockchainName: String,
    val blockchainSymbol: String,
    val blockchainIcon: String?,
    val blockchainColor: String?,
    val addresses: List<WalletAddressWithNetworkModel>
)