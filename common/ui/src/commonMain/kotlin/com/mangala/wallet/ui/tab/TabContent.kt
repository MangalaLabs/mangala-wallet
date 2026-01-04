package com.mangala.wallet.ui.tab

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.lifecycle.LifecycleEffect
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import cafe.adriel.voyager.navigator.tab.Tab
//import cafe.adriel.voyager.transitions.SlideTransition

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun Tab.TabContent() {
    val tabTitle = options.title

    LifecycleEffect(
        onStarted = {  },
        onDisposed = {  },
    )

    Navigator(BasicNavigationScreen(index = 0)) { navigator ->
//        Na(navigator) { screen ->
//            Column {
//                screen.Content()
//                println("Navigator Last Event: ${navigator.lastEvent}")
//            }
//        }
    }
}


@Composable
fun RowScope.TabNavigationButton(
    tab: Tab
) {
    val tabNavigator = LocalTabNavigator.current

    Button(
        enabled = tabNavigator.current.key != tab.key,
        onClick = { tabNavigator.current = tab },
        modifier = Modifier.weight(1f)
    ) {
        Text(text = tab.options.title)
    }
}
