package com.mangala.wallet.features.menu.presentation.wallet.details

import com.mangala.wallet.model.account.domain.AccountBlockchainModel

data class WalletDetailsScreenUiModel(
    val accounts: List<AccountItemUiModel> = emptyList()
)
data class AccountItemUiModel(
    val account: AccountBlockchainModel
)