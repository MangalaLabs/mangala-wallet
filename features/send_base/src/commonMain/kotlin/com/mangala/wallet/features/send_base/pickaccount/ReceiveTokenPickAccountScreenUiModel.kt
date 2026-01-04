package com.mangala.wallet.features.send_base.pickaccount

import com.mangala.features.wallet.presentation.AntelopeAccountItemUiModel
import com.mangala.wallet.model.account.domain.AccountBlockchainModel
import com.mangala.wallet.model.token.domain.TokenBalanceModel
import com.mangala.wallet.model.token.domain.totalValueOrZero
import com.mangala.wallet.utils.ext.formatFiat

data class ReceiveTokenPickAccountScreenUiModel(
    val items: List<AccountUiModelPickAccount>
)
sealed interface AccountUiModelPickAccount {
    val key: String

    data class Evm(val account: AccountUiModel) : AccountUiModelPickAccount {
        override val key = account.account.account.id
    }

    data class Antelope(val account: AntelopeAccountItemUiModel, val fiatCurrencySymbol: String) : AccountUiModelPickAccount {
        override val key = account.account.accountName
    }
}

data class AccountUiModel(
    val account: AccountBlockchainModel,
    val balance: List<TokenBalanceModel>,
    val currencySymbol: String
) {
    val formattedValue: String = balance.totalValueOrZero().formatFiat(symbol = currencySymbol)
}
