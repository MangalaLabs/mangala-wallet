package com.mangala.wallet.ui.utils.navigation

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.staticCompositionLocalOf
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.Navigator

val LocalBackPressedHandler = compositionLocalOf<MutableState<((currentScreen: Screen) -> Boolean)?>> { mutableStateOf( { true } ) }