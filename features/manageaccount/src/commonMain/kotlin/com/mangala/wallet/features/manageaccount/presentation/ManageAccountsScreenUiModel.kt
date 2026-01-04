package com.mangala.wallet.features.manageaccount.presentation

import com.mangala.wallet.model.account.domain.AccountBlockchainModel
import com.mangala.wallet.model.token.domain.TokenBalanceModel
import com.mangala.wallet.ui.utils.formattedCompactBalance

data class ManageAccountsScreenUiModel(
    val accounts: List<AccountItemUiModel> = emptyList()
)

data class AccountItemUiModel(
    val account: AccountBlockchainModel,
    val balance: List<TokenBalanceModel>
) {
    val nativeCoinBalance get(): String {
        val nativeCoin = balance.find { it.isCoin }
        return nativeCoin?.formattedCompactBalance().orEmpty()
    }
}
