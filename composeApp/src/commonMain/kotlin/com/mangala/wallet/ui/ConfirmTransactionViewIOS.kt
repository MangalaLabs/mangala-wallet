package com.mangala.wallet.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import cafe.adriel.voyager.core.registry.ScreenRegistry
import cafe.adriel.voyager.navigator.CurrentScreen
import cafe.adriel.voyager.navigator.Navigator
import com.mangala.wallet.MangalaWalletTheme
import com.mangala.wallet.ui.utils.navigation.BackHandler

@Composable
internal fun ConfirmTransactionViewIOS(
    url: String,
    accountId: String,
    coinDecimals: Long,
    chainId: Long,
    callbackId: Long,
    value: String,
    recipient: String,
    payload: String,
    nonce: Long,
    isLegacyTransaction: Boolean,
    onSignMessageFail: () -> Unit,
    onSignMessageSuccessful: (callbackId: Long, signHex: String) -> Unit,
    onConfirm: (isOpenPin: Boolean) -> Unit,
    onDecline: () -> Unit
) {
    MangalaWalletTheme {
        val unlockPinScreen = ScreenRegistry.get(
            SharedScreen.BrowserConfirmTransactionScreen(
                url = url,
                accountId = accountId,
                coinDecimals = coinDecimals,
                chainId = chainId,
                callbackId = callbackId,
                value = value,
                recipient = recipient,
                payload = payload,
                nonce = nonce,
                isLegacyTransaction = isLegacyTransaction,
                onSignMessageFail = onSignMessageFail,
                onSignMessageSuccessful = onSignMessageSuccessful,
                onConfirm = { onConfirm(it) },
                onDecline = { onDecline() }
            )
        )
        Navigator(
            unlockPinScreen,
            onBackPressed = {
                BackHandler.handleBackPressed(it)
            }
        ){navigator ->
            CompositionLocalProvider(LocalGlobalNavigator provides navigator) {
                CurrentScreen()
            }
        }
    }
}

