package com.mangala.wallet.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.CurrentScreen
import cafe.adriel.voyager.navigator.Navigator
import com.mangala.browser_bridge_base.switchchain.SwitchChainScreen
import com.mangala.wallet.MangalaWalletTheme
import com.mangala.wallet.ui.utils.navigation.BackHandler
import com.mangala.wallet.ui.utils.navigation.LocalBackPressedHandler

@Composable
internal fun SwitchChainViewIOS(
    currentChainId: Long,
    newChainId: Long,
    onConfirm: () -> Unit,
    onDecline: () -> Unit,
) {
    MangalaWalletTheme {
        val screen = SwitchChainScreen(
            currentChainId,
            newChainId,
            onConfirm,
            onDecline,
        )
        Navigator(
            screen,
            onBackPressed = {
                BackHandler.handleBackPressed(it)
            }
        ) { navigator ->
            CompositionLocalProvider(LocalGlobalNavigator provides navigator) {
                CurrentScreen()
            }
        }
    }
}

