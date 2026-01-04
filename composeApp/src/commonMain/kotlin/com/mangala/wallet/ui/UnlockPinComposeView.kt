package com.mangala.wallet.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.Navigator
import com.mangala.wallet.MangalaWalletTheme
import com.mangala.wallet.pin.presentation.unlock.UnlockPinScreen
import com.mangala.wallet.ui.utils.navigation.BackHandler

@Composable
internal fun UnlockPinComposeView(isSuccess: (Boolean) -> Unit,) {
    MangalaWalletTheme {
        val unlockPinScreen = UnlockPinScreen(SharedScreen.UnlockPinScreen.CONFIRM_DAPP, unlockPinCallback = {
            isSuccess(it)
        }, antelopeAccountName = null)
        Navigator(unlockPinScreen, onBackPressed = {
            BackHandler.handleBackPressed(it)
        })
//        { navigator ->
//            CompositionLocalProvider(LocalGlobalNavigator provides navigator, LocalBackPressedHandler provides onBackPressed) {
//                CurrentScreen()
//            }
//        }
    }
}

