package com.mangala.wallet.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.CurrentScreen
import cafe.adriel.voyager.navigator.Navigator
import com.mangala.wallet.MangalaWalletTheme
import com.mangala.wallet.features.settings.network.NetworkScreen
import com.mangala.wallet.ui.utils.navigation.BackHandler
import com.mangala.wallet.ui.utils.navigation.LocalBackPressedHandler

@Composable
internal fun NetworkComposeView(chainIdCallback: (Long) -> Unit) {
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
