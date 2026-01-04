package com.mangala.wallet.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf

private val LocalMangalaColors = staticCompositionLocalOf<MangalaColors> {
    error("No MangalaColors provided")
}

val MaterialTheme.mangalaColors: MangalaColors
    @Composable
    @ReadOnlyComposable
    get() = LocalMangalaColors.current

@Composable
fun ProvideMangalaColors(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) darkMangalaColors() else lightMangalaColors()
    
    CompositionLocalProvider(
        LocalMangalaColors provides colors,
        content = content
    )
}