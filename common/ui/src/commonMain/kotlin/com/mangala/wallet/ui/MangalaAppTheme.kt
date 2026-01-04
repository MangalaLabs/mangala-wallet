package com.mangala.wallet.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import com.mangala.wallet.common.mokoresources.LightColorPalette

@Composable
fun MangalaAppTheme(
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