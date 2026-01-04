package com.mangala.wallet.features.swap_base.presentation

import com.mangala.wallet.model.account.domain.AccountBlockchainModel
import com.mangala.wallet.uniswap.TradeError
import com.mangala.wallet.uniswap.domain.models.Dex

data class SwapTokenUiModel(
    val selectedAccount: AccountBlockchainModel,
    val listTokens: List<TokenUiModel>,
    val selectedFromToken: TokenUiModel,
    val selectedToToken: TokenUiModel,
    val selectedDex: Dex,
    val tradeError: TradeError? = null,
    val isInsufficientAmount: Boolean = false,
    val price: String
) {
    data class TokenUiModel(
        val tokenCode: String,
        val logoUrl: String = "",
        val balance: String,
        val address: String,
        val decimal: Long,
        val isNative: Boolean
    )

    val isSwapEnabled: Boolean
        get() = tradeError == null && !isInsufficientAmount
}