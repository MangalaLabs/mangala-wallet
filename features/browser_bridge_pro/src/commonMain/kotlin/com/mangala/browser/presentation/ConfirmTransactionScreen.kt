package com.mangala.browser.presentation

import cafe.adriel.voyager.core.registry.ScreenRegistry
import cafe.adriel.voyager.navigator.Navigator
import com.mangala.browser_bridge_base.BaseConfirmTransactionScreen
import com.mangala.browser_bridge_base.ConfirmTransactionViewModel
import com.mangala.wallet.ui.SharedScreen

class ConfirmTransactionScreen(
    url: String,
    private val accountId: String,
    private val coinDecimals: Long,
    chainId: Long,
    callbackId: Long,
    private val value: String,
    private val recipient: String,
    private val payload: String,
    private val nonce: Long,
    private val isLegacyTransaction: Boolean,
    onSignMessageFail: () -> Unit,
    onSignMessageSuccessful: (callbackId: Long, signHex: String) -> Unit,
    onConfirm: (isOpenPin: Boolean) -> Unit,
    onDecline: () -> Unit
): BaseConfirmTransactionScreen(
    url, accountId, coinDecimals, chainId, callbackId, value, recipient, payload, nonce, isLegacyTransaction, onSignMessageFail, onSignMessageSuccessful, onConfirm, onDecline
) {

    override val analyticsClassName: String = ConfirmTransactionScreen::class.simpleName.orEmpty()

    override fun onClickConfirm(
        navigator: Navigator,
        onConfirmComplete: () -> Unit,
        viewModel: ConfirmTransactionViewModel
    ) {
        val pinScreen = ScreenRegistry.get(
            SharedScreen.UnlockPinScreen(
                SharedScreen.UnlockPinScreen.CONFIRM_DAPP,
                unlockPinCallback = {
                    onConfirmComplete()

                    if (it) {
                        viewModel.signTransaction(
                            accountId,
                            recipient,
                            isLegacyTransaction,
                            nonce,
                            payload,
                            value,
                            coinDecimals
                        )
                    }
                },
                antelopeAccountName = null
            )
        )
        navigator.push(pinScreen)
    }
}