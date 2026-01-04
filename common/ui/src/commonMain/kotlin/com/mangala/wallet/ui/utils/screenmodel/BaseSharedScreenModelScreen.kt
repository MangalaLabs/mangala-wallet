package com.mangala.wallet.ui.utils.screenmodel

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.CurrentScreen
import cafe.adriel.voyager.navigator.Navigator
import com.mangala.wallet.ui.utils.navigation.BackHandler

abstract class BaseSharedScreenModelScreen<T : BaseScreenModel> : BaseScreen<T>() {

    abstract fun getScreen(): Screen

    @Composable
    override fun Content() {
        Navigator(
            getScreen(),
            onBackPressed = { BackHandler.handleBackPressed(it) }
        ) {
            super.Content()
        }
    }

    @Composable
    override fun ScreenContent(screenModel: T) {
        CurrentScreen()
    }
}