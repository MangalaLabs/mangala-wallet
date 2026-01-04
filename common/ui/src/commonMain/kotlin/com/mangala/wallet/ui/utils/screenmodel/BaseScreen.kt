package com.mangala.wallet.ui.utils.screenmodel

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.lifecycle.LifecycleEffect
import cafe.adriel.voyager.core.screen.Screen
import com.mangala.wallet.ui.LocalBottomNavigationVisibility
import com.mangala.wallet.utils.analytics.MangalaAnalytics
import kotlin.jvm.Transient

abstract class BaseScreen<T : BaseScreenModel>: Screen {

    @Transient
    protected var onBackPressedCallback: (() -> Boolean)? = null // return true to pop the current screen, false otherwise

    @Composable
    abstract fun createScreenModel(): T

    open val isBottomBarVisible: Boolean = true

    abstract val screenName: String
    abstract val screenClassName: String

    @Composable
    override fun Content() {
        val screenModel = createScreenModel()

        LocalBottomNavigationVisibility.current.value = isBottomBarVisible

        ScreenContent(screenModel)

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
