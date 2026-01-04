package com.mangala.wallet.ui.utils.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import cafe.adriel.voyager.core.screen.Screen
import com.mangala.wallet.ui.utils.screenmodel.BaseScreen

@Composable
public fun BackHandler(onBack: (Screen) -> Boolean) {
    // Safely update the current `onBack` lambda when a new one is provided
    val currentOnBack by rememberUpdatedState(onBack)

    val backPressedHandler = LocalBackPressedHandler.current

    LaunchedEffect(Unit) {
        backPressedHandler.value = {
            currentOnBack(it)
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            backPressedHandler.value = { true }
        }
    }
}

object BackHandler {
    fun handleBackPressed(it: Screen): Boolean {
        return (it as? BaseScreen<*>)?.onBackPressed() ?: true
    }
}