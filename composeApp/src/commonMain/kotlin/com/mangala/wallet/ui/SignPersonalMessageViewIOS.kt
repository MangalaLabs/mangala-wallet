package com.mangala.wallet.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.CurrentScreen
import cafe.adriel.voyager.navigator.Navigator
import com.mangala.browser_bridge_base.personal.SignPersonalMessageScreen
import com.mangala.wallet.MangalaWalletTheme
import com.mangala.wallet.ui.utils.navigation.BackHandler
import com.mangala.wallet.ui.utils.navigation.LocalBackPressedHandler

@Composable
internal fun SignPersonalMessageViewIOS(url: String,
                                        callbackId: Long,
                                        message: ByteArray?,
                                        onSign: (message: String) -> Unit,
                                        onConfirm: (isOpenPin: Boolean) -> Unit,
                                        onDecline: () -> Unit) {
    MangalaWalletTheme {
        val screen = SignPersonalMessageScreen(
            url = url,
            callbackId = callbackId,
            message = message,
            onSign = { onSign(it)},
            onConfirm = { onConfirm(it) },
            onDecline = { onDecline() }
        )
        Navigator(
            screen,
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

