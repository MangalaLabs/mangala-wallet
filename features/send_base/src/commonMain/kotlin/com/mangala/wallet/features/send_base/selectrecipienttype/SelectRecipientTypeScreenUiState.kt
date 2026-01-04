package com.mangala.wallet.features.send_base.selectrecipienttype

import com.mangala.wallet.model.account.domain.AccountModel

sealed class SelectRecipientTypeScreenUiState {
    data object Loading: SelectRecipientTypeScreenUiState()
    data class Data(
        val blockchainUid: String
    ): SelectRecipientTypeScreenUiState()
}