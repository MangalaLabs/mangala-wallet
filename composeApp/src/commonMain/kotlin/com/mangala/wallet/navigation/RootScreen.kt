package com.mangala.wallet.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.mangala.wallet.ui.utils.collectAsStateMultiplatform

class RootScreen : Screen {
    @Composable
    override fun Content() {
        val screenModel = rememberScreenModel { RootScreenModel() }
        val navigationState by screenModel.navigationState.collectAsStateMultiplatform()
        
        when (navigationState) {
            is NavigationState.Loading -> {
                Box(Modifier.fillMaxSize()) {
                    // Could show app logo or minimal loading state
                    // Empty for now to avoid flash
                }
            }
            is NavigationState.Ready -> {
                val navigator = LocalNavigator.currentOrThrow
                LaunchedEffect(navigationState) {
                    navigator.replace((navigationState as NavigationState.Ready).screen)
                }
            }
        }
    }
}