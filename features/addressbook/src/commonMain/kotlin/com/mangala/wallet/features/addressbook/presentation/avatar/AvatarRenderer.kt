package com.mangala.wallet.features.addressbook.presentation.avatar

import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import com.mangala.wallet.features.addressbook.domain.model.AvatarSource

/**
 * Interface chung cho việc hiển thị avatar trên các nền tảng
 * Sẽ được triển khai cụ thể cho Android, iOS và Desktop
 */
expect object AvatarRenderer {
    /**
     * Hiển thị avatar từ AvatarSource
     */
    @Composable
    fun RenderAvatar(
        name: String,
        avatarSource: AvatarSource,
        modifier: Modifier = Modifier,
        size: Dp,
        borderWidth: Dp = Dp.Hairline,
        borderColor: Color? = null,
        backgroundColor: Color? = null,
        contentColor: Color? = null
    )
}
