package com.mangala.wallet.features.addressbook.presentation.security

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.koin.compose.koinInject

/**
 * A provider for secure authentication components throughout the app.
 * This eliminates the need to initialize these components in every screen.
 */
object SecureAuthProvider {
    // CompositionLocal for the enhanced SecureActionHandler
    private val LocalSecureActionHandler = compositionLocalOf<SecureActionHandler> {
        error("SecureActionHandler not provided. Make sure to wrap your content with SecureAuthProvider.Provider")
    }

    // Access the current SecureActionHandler instance
    val current: SecureActionHandler
        @Composable
        get() = LocalSecureActionHandler.current

    /**
     * Provider composable that initializes all required secure auth components
     * and provides them to the composition tree.
     *
     * IMPORTANT: This must be called INSIDE a Navigator content block
     * to ensure the LocalNavigator is available.
     * 
     * @param rootNavigator Optional root navigator to use instead of current navigator.
     * This is useful when Provider is used inside bottom sheets or modal screens.
     */
    @Composable
    fun Provider(
        rootNavigator: cafe.adriel.voyager.navigator.Navigator? = null,
        content: @Composable () -> Unit
    ) {
        // Initialize all required components
        val navigator = rootNavigator ?: LocalNavigator.currentOrThrow

        // Use Koin injection to avoid type inference issues
        val coordinator = koinInject<SecureAuthFlowCoordinator>()
        val secureAuthPolicyProvider = koinInject<SecureAuthPolicyProvider>()

        val voyagerNavigator = rememberNavigator(
            navigator = navigator
        )

        // Create the enhanced secure action handler
        val secureActionHandler = rememberSecureActionHandler(
            coordinator = coordinator,
            navigator = voyagerNavigator,
            policyProvider = secureAuthPolicyProvider
        )

        // Provide it to the composition
        CompositionLocalProvider(
            LocalSecureActionHandler provides secureActionHandler
        ) {
            content()
        }
    }
}