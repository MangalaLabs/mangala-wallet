package com.mangala.wallet.features.nft_base.presentation

import com.mangala.wallet.model.account.domain.AccountBlockchainModel
import com.mangala.wallet.ui.utils.WrappedStringResource

sealed class NftScreenUiState(val accounts: List<NftScreenUiModel.AccountUiModel>) {

    data class Loading(
        private val accountss: List<NftScreenUiModel.AccountUiModel>
    ) : NftScreenUiState(accountss)

    data class Success(
        private val accountss: List<NftScreenUiModel.AccountUiModel>,
        val collections: List<NftScreenUiModel.NftCollectionUiModel>
    ) : NftScreenUiState(accountss)

    data class Error(
        private val accountss: List<NftScreenUiModel.AccountUiModel>,
        val message: WrappedStringResource
    ) : NftScreenUiState(accountss)

    val selectedAccount = accounts.find { it.isSelected }
}

data class NftScreenUiModel(
    val collections: List<NftCollectionUiModel>
) {
    data class AccountUiModel(
        val account: AccountBlockchainModel,
        val isSelected: Boolean
    )

    data class NftCollectionUiModel(
        val contractAddress: String,
        val name: String,
        val items: List<NftItemUiModel>,
        val isExpanded: Boolean = true,
        val isFavorite: Boolean = false
    )

    data class NftItemUiModel(
        val collectionContractAddress: String, // To handle clicking on favorite items
        val tokenId: String,
        val name: String,
        val description: String,
        val imageUrl: String
    )
}

fun List<AccountBlockchainModel>?.mapToUiModel(selectedAccountId: String?): List<NftScreenUiModel.AccountUiModel> {
    return this?.mapIndexed { index, it ->
        NftScreenUiModel.AccountUiModel(
            it,
            if (selectedAccountId != null) it.account.id == selectedAccountId else index == 0
        )
    } ?: emptyList()
}