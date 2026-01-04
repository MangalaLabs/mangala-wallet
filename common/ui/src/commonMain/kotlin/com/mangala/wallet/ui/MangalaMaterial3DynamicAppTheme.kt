package com.mangala.wallet.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.mangala.wallet.ui.theme.ProvideMangalaColors
import com.mangala.wallet.ui.theme.darkMangalaColors
import com.mangala.wallet.ui.theme.lightMangalaColors

@Composable
fun MangalaMaterial3DynamicAppTheme(
//    darkTheme: Boolean = isSystemInDarkTheme(),
    darkTheme: Boolean = true,
    content: @Composable () -> Unit,
) {
    val mangalaColors = if (darkTheme) darkMangalaColors() else lightMangalaColors()

    // Map semantic colors to Material 3 ColorScheme
    val colorScheme = if (darkTheme) {
        darkColorScheme(
            primary = Color(0xFF3B90FF), // textLink color
            primaryContainer = Color(0xFF8647F3), // bgBadge color
            secondary = mangalaColors.textSecondary,
            secondaryContainer = Color(0xFF8647F3), // bgBadge for containers
            background = mangalaColors.bg,
            surface = mangalaColors.bgInnerCard,
            surfaceVariant = mangalaColors.bgInnerCard,
            error = Color(0xFFFF5A5F), // Keep existing error color
            onPrimary = Color.White,
            onPrimaryContainer = Color.White,
            onSecondary = mangalaColors.textPrimary,
            onSecondaryContainer = Color.White,
            onBackground = mangalaColors.textPrimary,
            onSurface = mangalaColors.textPrimary,
            onSurfaceVariant = mangalaColors.textSecondary,
            onError = Color.White,
        )
    } else {
        lightColorScheme(
            primary = Color(0xFF3B90FF), // textLink color
            primaryContainer = Color(0xFF8647F3), // bgBadge color
            secondary = mangalaColors.textSecondary,
            secondaryContainer = Color(0xFF8647F3), // bgBadge for containers
            background = mangalaColors.bg,
            surface = mangalaColors.bgInnerCard,
            surfaceVariant = mangalaColors.bgInnerCard,
            error = Color(0xFFFF5A5F), // Keep existing error color
            onPrimary = Color.White,
            onPrimaryContainer = Color.White,
            onSecondary = mangalaColors.textPrimary,
            onSecondaryContainer = Color.White,
            onBackground = mangalaColors.textPrimary,
            onSurface = mangalaColors.textPrimary,
            onSurfaceVariant = mangalaColors.textSecondary,
            onError = Color.White,
        )
    }

    ProvideMangalaColors(darkTheme = darkTheme) {
        MaterialTheme(
            colorScheme = colorScheme,
            content = content,
        )
    }
}