package com.mangala.wallet.features.chains.antelope.presentation.esr

import com.mangala.wallet.model.blockchain.BlockchainType
import com.memtrip.eos.chain.actions.transaction.esr.EsrSigningRequestArgs

sealed interface EsrScreenUiState {
    data object Loading : EsrScreenUiState
    data class Data(
        val uiModel: EsrDataUiModel,
        val error: String? = null
    ) : EsrScreenUiState

    data class Error(
        val error: String
    ) : EsrScreenUiState

    data object Signing : EsrScreenUiState

    data object Success : EsrScreenUiState
}

sealed interface EsrDataUiModel {
    val esrSigningRequest: EsrSigningRequestArgs
    val blockchainType: BlockchainType

    data class SignTransaction(
        override val esrSigningRequest: EsrSigningRequestArgs,
        override val blockchainType: BlockchainType,
        val selectedAuthorization: String? = null,
        val validAuthorizations: List<String> = emptyList()
    ) : EsrDataUiModel

    data class Identity(
        override val esrSigningRequest: EsrSigningRequestArgs,
        override val blockchainType: BlockchainType,
        val accounts: List<String>? = null,
        val permissions: List<String>? = null,
        val selectedAccount: String? = null,
        val selectedPermission: String? = null
    ) : EsrDataUiModel
}