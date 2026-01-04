package com.mangala.wallet.features.addressbook.presentation.security

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.mangala.wallet.ui.LocalBottomNavigationVisibility
import com.mangala.wallet.ui.component.OnboardingGradientBackground
import org.koin.compose.koinInject

/**
 * Màn hình xác thực tổng hợp
 */
class SecureAuthScreen : Screen {
    @Composable
    override fun Content() {
        val coordinator: SecureAuthFlowCoordinator = koinInject()
        LocalBottomNavigationVisibility.current.value = false

        val localNavigator = LocalNavigator.currentOrThrow
        val navigateBack = remember(localNavigator) {
            {
                localNavigator.popUntil { it is SecureAuthScreen }
                if (localNavigator.canPop) {
                    localNavigator.pop()
                }
            }
        }

        OnboardingGradientBackground {
            SecureAuthFlow(
                coordinator = coordinator,
                onNavigateBack = navigateBack,
                localNavigator = localNavigator
            )
        }
    }
}

/**
 * Interface định nghĩa navigator cho ứng dụng
 */
interface Navigator {
    fun navigateToSecureAuth()
    fun navigateBack()
}

/**
 * Triển khai Navigator sử dụng Voyager
 */
class VoyagerNavigator(
    private val navigator: cafe.adriel.voyager.navigator.Navigator
) : Navigator {

    override fun navigateToSecureAuth() {
        navigator.push(SecureAuthScreen())
    }

    override fun navigateBack() {
        if (navigator.canPop) {
            navigator.pop()
        }
    }
}

/**
 * Composable Factory cho Navigator
 */
@Composable
fun rememberNavigator(
    navigator: cafe.adriel.voyager.navigator.Navigator
): Navigator {
    return remember(navigator) {
        VoyagerNavigator(
            navigator = navigator
        )
    }
}