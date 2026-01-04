package com.mangala.contract.wizard.presentation

import com.mangala.wallet.model.account.domain.AccountBlockchainModel


sealed interface ContractWizardScreenUiState {
    object Loading: ContractWizardScreenUiState
    data class Data(
        val accounts: List<AccountBlockchainModel> = emptyList(),
        val chainId: Long = 1L,
        val rpcUrl: String = "",
        val deploy: String,
    ): ContractWizardScreenUiState
}