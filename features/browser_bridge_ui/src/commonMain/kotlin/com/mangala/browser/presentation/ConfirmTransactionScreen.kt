package com.mangala.browser.presentation

import cafe.adriel.voyager.navigator.Navigator
import com.mangala.browser.presentation.qr.TransactionQrScreen
import com.mangala.browser_bridge_base.BaseConfirmTransactionScreen
import com.mangala.browser_bridge_base.ConfirmTransactionViewModel
import com.mangala.wallet.features.chains.evmcompatible.model.Address

class ConfirmTransactionScreen(
    url: String,
    accountId: String,
    coinDecimals: Long,
    chainId: Long,
    callbackId: Long,
    private val value: String,
    private val recipient: String,
    private val payload: String,
    private val nonce: Long,
    isLegacyTransaction: Boolean,
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
        val request = viewModel.getSignTransactionRequest(Address(recipient), payload, value)
        request?.let {
            val screen = TransactionQrScreen(request)
            navigator.push(screen)
        }
    }
}