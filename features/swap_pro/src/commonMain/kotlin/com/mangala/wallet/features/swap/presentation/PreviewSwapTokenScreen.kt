package com.mangala.wallet.features.swap.presentation

import cafe.adriel.voyager.core.registry.ScreenRegistry
import cafe.adriel.voyager.navigator.Navigator
import com.mangala.wallet.features.chains.evmcompatible.model.SignTransactionRequest
import com.mangala.wallet.features.swap_base.presentation.preview.BasePreviewSwapTokenScreen
import com.mangala.wallet.ui.SharedScreen
import com.mangala.wallet.uniswap.domain.models.Dex
import com.mangala.wallet.uniswap.domain.models.TradeData
import com.mangala.wallet.utils.analytics.MangalaAnalytics
import kotlin.experimental.ExperimentalObjCRefinement
import kotlin.native.HiddenFromObjC

@OptIn(ExperimentalObjCRefinement::class)
@HiddenFromObjC
class PreviewSwapTokenScreen(
    accountAddress: String,
    accountName: String,
    accountId: String,
    tokenFromSymbol: String,
    tokenFromLogoUrl: String,
    tokenToSymbol: String,
    tokenToLogoUrl: String,
    tradeData: TradeData,
    dex: Dex
): BasePreviewSwapTokenScreen(accountAddress, accountName, accountId, tokenFromSymbol, tokenFromLogoUrl, tokenToSymbol, tokenToLogoUrl, tradeData, dex) {

    override val screenName: String = MangalaAnalytics.Screens.EVM_SWAP_PREVIEW
    override val screenClassName: String = PreviewSwapTokenScreen::class.simpleName.orEmpty()

    override fun onClickExecuteTransaction(
        navigator: Navigator,
        signTransactionRequest: SignTransactionRequest,
        onUnlockSuccess: () -> Unit
    ) {
        val pinScreen = getPinConfirmScreen(navigator, onUnlockSuccess)
        navigator.push(pinScreen)
    }

    private fun getPinConfirmScreen(navigator: Navigator, onUnlockSuccess: () -> Unit) =
        ScreenRegistry.get(
            SharedScreen.UnlockPinScreen(
                SharedScreen.UnlockPinScreen.VERIFY_SEND_TRANSACTION,
                onUnlockSuccess = {
                    onUnlockSuccess()
                    navigator.pop()
                },
                antelopeAccountName = null
            )
        )
}