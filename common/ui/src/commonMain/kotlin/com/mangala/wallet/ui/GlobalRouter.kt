package com.mangala.wallet.ui

import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.staticCompositionLocalOf
import cafe.adriel.voyager.navigator.Navigator

// Router to navigate on top of the Bottom Tab Bar
val LocalGlobalNavigator: ProvidableCompositionLocal<Navigator> =
    staticCompositionLocalOf { error("LocalGlobalNavigator not initialized") }
