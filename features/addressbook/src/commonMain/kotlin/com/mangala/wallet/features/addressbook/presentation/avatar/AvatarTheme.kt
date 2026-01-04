package com.mangala.wallet.features.addressbook.presentation.avatar

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Cấu trúc chứa các thuộc tính tùy chỉnh cho avatar
 */
@Immutable
data class AvatarTheme(
    val defaultSize: Dp = 40.dp,
    val smallSize: Dp = 32.dp,
    val largeSize: Dp = 64.dp,
    val borderWidth: Dp = 1.dp,
    val defaultBackgroundColor: Color = Color(0xFFE0E0E0),
    val defaultContentColor: Color = Color(0xFF424242),
    val defaultBorderColor: Color = Color.Transparent
)

/**
 * LocalComposition để cung cấp AvatarTheme cho toàn bộ ứng dụng
 */
val LocalAvatarTheme = staticCompositionLocalOf {
    AvatarTheme()
}

/**
 * Extension function cho MaterialTheme để truy cập AvatarTheme
 */
val MaterialTheme.avatarTheme: AvatarTheme
    @Composable
    @ReadOnlyComposable
    get() = LocalAvatarTheme.current