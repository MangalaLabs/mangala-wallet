package com.mangala.wallet.common.mokoresources

import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.ui.graphics.Color
import com.mangala.wallet.common.mokoresources.Colors

val DarkColorPalette = darkColors(
    primary = Colors.darkGray,
    primaryVariant = Colors.darkGray,
    secondary = Colors.gray,
    secondaryVariant = Colors.gray,
    surface = Colors.coral,
    background = Color.White,
    error = Colors.orangeRed,
    onPrimary = Colors.darkGray,
    onSecondary = Color.White,
    onSurface = Color.White,
    onBackground = Color.White,
)

val LightColorPalette = lightColors(
    primary = Color.White,
    primaryVariant = Color.White,
    onPrimary = Colors.darkGray,
    secondary = Colors.gray,
    // color of the switch (checked)
    secondaryVariant = Colors.coral,
    onSecondary = Colors.teal,
    background = Color.White,
    onBackground = Colors.darkGray,
    // color of the switch (unchecked)
    surface = Color.White,
    onSurface = Color.White,
    error = Colors.orangeRed,
    onError = Colors.gray,
)
