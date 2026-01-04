package com.mangala.wallet.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.CurrentScreen
import cafe.adriel.voyager.navigator.Navigator
import com.mangala.wallet.MangalaWalletTheme
import com.mangala.wallet.features.settings.network.NetworkScreen
import com.mangala.wallet.ui.utils.navigation.BackHandler

@Composable
fun ActivityComposeView(screen: Screen) {
    MangalaWalletTheme {
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

@Composable
fun NetworkActivityComposeView(chainIdCallback: (Long) -> Unit) {
    MangalaWalletTheme {
        val screen = NetworkScreen()
        screen.setChainIdCallback(chainIdCallback)
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



