package com.mangala.wallet

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.CurrentScreen
import cafe.adriel.voyager.navigator.Navigator
import com.mangala.wallet.common.mokoresources.LightColorPalette
import com.mangala.wallet.pin.presentation.unlock.UnlockPinScreen
import com.mangala.wallet.features.onboarding.presentation.onboarding.OnboardingScreen
import com.mangala.wallet.navigation.RootScreen
import com.mangala.wallet.ui.LocalGlobalNavigator
import com.mangala.wallet.ui.SharedScreen
import com.mangala.wallet.ui.component.MaxSizeBox
import com.mangala.wallet.ui.utils.navigation.BackHandler
import com.mangala.wallet.viewmodel.ApplicationViewModel
import dev.icerock.moko.resources.desc.StringDesc
import dev.theolm.rinku.DeepLink
import dev.theolm.rinku.compose.ext.DeepLinkListener
import org.jetbrains.compose.ui.tooling.preview.Preview
import com.mangala.wallet.ui.MangalaMaterial3DynamicAppTheme

@Composable
@Preview
fun App(viewModel: ApplicationViewModel, modifier: Modifier = Modifier) {
    var deepLink by remember { mutableStateOf<DeepLink?>(null) }
    DeepLinkListener { deepLink = it }
    StringDesc.localeType = StringDesc.LocaleType.Custom(viewModel.getCurrentLanguage().code)

    MainScreen(
        deepLink,
        onProcessDeepLink = {
            viewModel.processDeepLink(deepLink)
        }
    )
}

@Composable
fun MainScreen(deepLink: DeepLink?, onProcessDeepLink: (DeepLink) -> Screen) {
    MangalaMaterial3DynamicAppTheme {
        MangalaWalletTheme {
            Navigator(
                screen = RootScreen(),
                onBackPressed = {
                    BackHandler.handleBackPressed(it)
                }
            ) { navigator ->
                CompositionLocalProvider {
                    MaxSizeBox {
                        CompositionLocalProvider(LocalGlobalNavigator provides navigator) {
                            CurrentScreen()

                            LaunchedEffect(deepLink) {
                                if (deepLink != null) {
                                    val resolvedDestinationScreen = onProcessDeepLink(deepLink)
                                    navigator.push(resolvedDestinationScreen)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
internal fun MangalaWalletTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
//    val colors = if (darkTheme) {
//        DarkColorPalette
//    } else {
    val colors = LightColorPalette
//    }

    MaterialTheme(
        colors = colors,
        content = content,
    )
}
