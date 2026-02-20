package com.mangala.wallet.ui.utils.screenmodel

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.lifecycle.LifecycleEffect
import cafe.adriel.voyager.core.screen.Screen
import com.mangala.wallet.ui.LocalBottomNavigationVisibility
import com.mangala.wallet.ui.SecureScreen
import com.mangala.wallet.utils.analytics.MangalaAnalytics
import kotlin.jvm.Transient

abstract class BaseScreen<T : BaseScreenModel>: Screen {

    @Transient
    protected var onBackPressedCallback: (() -> Boolean)? = null // return true to pop the current screen, false otherwise

    @Composable
    abstract fun createScreenModel(): T

    open val isBottomBarVisible: Boolean = true

    /**
     * Per-screen opt-in flag for screenshot / screen-recording protection.
     *
     * Override to `true` to protect this screen regardless of [SecureScreenConfig].
     * [SecureScreenConfig.secureScreenClassNames] is always checked as well — either
     * this flag OR the registry entry being present is sufficient to enable protection.
     *
     * Example:
     * ```kotlin
     * class MyScreen : BaseScreen<MyScreenModel>() {
     *     override val isSecure = true
     * }
     * ```
     */
    open val isSecure: Boolean = false

    abstract val screenName: String
    abstract val screenClassName: String

    @Composable
    override fun Content() {
        val screenModel = createScreenModel()

        LocalBottomNavigationVisibility.current.value = isBottomBarVisible

        val shouldSecure = isSecure || SecureScreenConfig.isSecure(screenClassName)
        if (shouldSecure) {
            SecureScreen {
                ScreenContent(screenModel)
            }
        } else {
            ScreenContent(screenModel)
        }

        LifecycleEffect(
            onStarted = {
                MangalaAnalytics.trackScreenView(screenName, screenClassName)
                screenModel.onComposableStarted()
            },
            onDisposed = {
                screenModel.onComposableDisposed()
            }
        )
    }

    @Composable
    abstract fun ScreenContent(screenModel: T)

    fun onBackPressed(): Boolean {
        return onBackPressedCallback?.invoke() ?: true
    }
}
