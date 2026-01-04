package com.mangala.wallet.features.chains.antelope.presentation.importaccount.step2

import com.mangala.wallet.model.blockchain.BlockchainType

sealed interface Step2ImportAccountSelectAccountUiState {
    data class NotImported(
        val accounts: List<String>,
        val error: String? = null,
        val blockchainType: BlockchainType? = null
    ) : Step2ImportAccountSelectAccountUiState

    data class Imported(val accountName: String, val blockchainType: BlockchainType, val isPinSetup: Boolean) : Step2ImportAccountSelectAccountUiState
    data class AccountCreated(val accountName: String, val blockchainType: BlockchainType,val isPinSetup: Boolean) : Step2ImportAccountSelectAccountUiState
}
