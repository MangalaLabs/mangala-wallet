package com.mangala.wallet.menu_base.presentation.iconsinapp

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.mangala.wallet.common.mokoresources.Colors
import com.mangala.wallet.common.mokoresources.icons.MangalaWalletPackPreview
import com.mangala.wallet.ui.component.MangalaWalletTopBar

class IconsInAppScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow

        IconsInApp(onBackClicked = { navigator.pop() })
    }

    @Composable
    fun IconsInApp(onBackClicked: () -> Unit) {
        Scaffold(
            topBar = {
                MangalaWalletTopBar(
                    modifier = Modifier.background(Colors.cloudGray),
                    text = "Icons in App", // Debug screen, no need for localization
                    onBackClicked = onBackClicked
                )
            },
            modifier = Modifier.background(Colors.cloudGray).windowInsetsPadding(WindowInsets.safeDrawing)
        ) {
            MangalaWalletPackPreview()
        }
    }
}