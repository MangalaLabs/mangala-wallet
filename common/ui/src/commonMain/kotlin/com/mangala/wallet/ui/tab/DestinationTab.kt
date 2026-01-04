package com.mangala.wallet.ui.tab

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.navigator.CurrentScreen
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.tab.Tab
import kotlin.jvm.Transient

interface DestinationTab : Tab {
    val route: String
}

/**
 * Base implementation for tabs with pop-to-root behavior.
 * Automatically pops the navigator to root when the tab is selected from bottom navigation.
 */
abstract class PopToRootTab : DestinationTab {
    @delegate:Transient
    private var selectionCount by mutableStateOf(0)

    /**
     * Call this when the tab is selected to trigger pop-to-root behavior.
     */
    fun onTabSelected() {
        selectionCount++
    }

    /**
     * Wraps a Navigator with pop-to-root behavior.
     * When selectionCount changes and the navigator has more than 1 screen, it pops to root.
     */
    @Composable
    protected fun NavigatorWithPopToRoot(
        navigator: Navigator,
        content: @Composable () -> Unit = { CurrentScreen() }
    ) {
        LaunchedEffect(selectionCount) {
            if (selectionCount > 0 && navigator.size > 1) {
                navigator.popUntilRoot()
            }
        }

        content()
    }
}