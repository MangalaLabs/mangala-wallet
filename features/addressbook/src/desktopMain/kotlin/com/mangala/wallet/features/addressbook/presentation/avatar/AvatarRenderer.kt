package com.mangala.wallet.features.addressbook.presentation.avatar

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mangala.wallet.features.addressbook.domain.model.AvatarSource
import com.mangala.wallet.mokoresources.MR
import dev.icerock.moko.resources.compose.painterResource

/**
 * Desktop-specific implementation của AvatarRenderer
 */
actual object AvatarRenderer {
    @Composable
    actual fun RenderAvatar(
        name: String,
        avatarSource: AvatarSource,
        modifier: Modifier,
        size: Dp,
        borderWidth: Dp,
        borderColor: Color?,
        backgroundColor: Color?,
        contentColor: Color?
    ) {
        println("Leonard DEBUG Desktop AvatarRenderer: $avatarSource")
        
        val borderColorPrimary = borderColor ?: MaterialTheme.colorScheme.primary
        val backgroundColorSecondary = backgroundColor ?: MaterialTheme.colorScheme.secondaryContainer
        val contentColorSecondary = contentColor ?: Color.White

        val baseModifier = modifier
            .size(size)
            .clip(CircleShape)
            .then(
                if (borderWidth > 0.dp) {
                    Modifier.border(borderWidth, borderColorPrimary, CircleShape)
                } else {
                    Modifier
                }
            )

        when (avatarSource) {
            is AvatarSource.ImageUrl -> {
                // Desktop sẽ cần triển khai riêng để đọc hình ảnh
                // TODO: Triển khai đọc ảnh cho Desktop
                RenderFirstLetter(name, backgroundColorSecondary, contentColorSecondary, baseModifier, size)
            }
            is AvatarSource.Emoji -> {
                // Sử dụng background color từ AvatarSource nếu có
                val emojiBackgroundColor = avatarSource.backgroundColor?.let { hexColor ->
                    try {
                        Color(hexColor.removePrefix("#").toLong(16) or 0xFF000000)
                    } catch (e: Exception) {
                        backgroundColorSecondary
                    }
                } ?: backgroundColorSecondary
                
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = baseModifier
                        .background(emojiBackgroundColor, CircleShape)
                ) {
                    Text(
                        text = avatarSource.emoji,
                        fontSize = size.value.div(1.5f).sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(4.dp)
                    )
                }
            }
            is AvatarSource.None -> {
                RenderFirstLetter(name, backgroundColorSecondary, contentColorSecondary, baseModifier, size)
            }

            is AvatarSource.DefaultAvatar -> {
                // Hiển thị hình ảnh mặc định từ resource (Desktop implementation)
                val avatarMap = mapOf(
                    "AvatarA" to MR.images.AvatarA,
                    "AvatarB" to MR.images.AvatarB,
                    "AvatarC" to MR.images.AvatarC,
                    "AvatarD" to MR.images.AvatarD,
                    "AvatarE" to MR.images.AvatarE
                )
                val imageResource = avatarMap[avatarSource.resourceName] ?: MR.images.AvatarA

                Image(
                    painter = painterResource(imageResource),
                    contentDescription = "Default avatar for $name",
                    contentScale = ContentScale.Crop,
                    modifier = baseModifier
                )
            }
        }
    }
    
    @Composable
    private fun RenderFirstLetter(
        name: String,
        backgroundColor: Color,
        contentColor: Color,
        modifier: Modifier,
        size: Dp
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = modifier
                .background(backgroundColor, CircleShape)
        ) {
            val firstLetter = name.firstOrNull()?.uppercase() ?: "#"
            Text(
                text = firstLetter,
                color = contentColor,
                fontSize = size.value.div(2f).sp,
                textAlign = TextAlign.Center
            )
        }
    }
}