package com.mangala.wallet.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.mangala.wallet.common.mokoresources.Colors
import com.mangala.wallet.ui.imageloader.LocalImage
import com.mangala.wallet.ui.imageloader.RemoteImage
import com.mangala.wallet.ui.theme.mangalaColors
import dev.icerock.moko.resources.ImageResource

@Composable
fun MangalaWalletIconButton(
    icon: ImageVector,
    modifier: Modifier = Modifier.size(24.dp),
    tint: Color = MaterialTheme.mangalaColors.iconPrimary,
    onClick: () -> Unit
) {
    IconButton(onClick = onClick, modifier = modifier) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = tint,
            modifier = Modifier.size(36.dp),
        )
    }
}

@Composable
fun MangalaWalletRemoteIconButton(
    iconUrl: ImageResource?,
    modifier: Modifier = Modifier.size(24.dp),
    tint: Color = MaterialTheme.mangalaColors.iconPrimary,
    onClick: () -> Unit
) {
    IconButton(onClick = onClick, modifier = modifier) {
        LocalImage(
            imageResource = iconUrl,
            modifier = Modifier.size(36.dp),
        )
    }
}

@Composable
fun MangalaWalletNoInherentPaddingIconButton(
    icon: ImageVector,
    modifier: Modifier = Modifier.size(24.dp),
    tint: Color = MaterialTheme.mangalaColors.iconPrimary,
    onClick: () -> Unit
) {
    Box(modifier.clickable { onClick() }) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = tint,
            modifier = Modifier.size(36.dp),
        )
    }
}

@Composable
fun MangalaWalletNoInherentPaddingRemoteIconButton(
    iconUrl: String,
    modifier: Modifier = Modifier.size(24.dp),
    tint: Color = MaterialTheme.mangalaColors.iconPrimary,
    onClick: () -> Unit
) {
    Box(modifier.clickable { onClick() }) {
        RemoteImage(
            url = iconUrl,
            modifier = Modifier.size(36.dp),
        )
    }
}

@Composable
fun MangalaWalletIconButtonCold(
    icon: ImageVector,
    modifier: Modifier = Modifier.size(36.dp),
    tint: Color = MaterialTheme.mangalaColors.iconPrimary,
    onClick: () -> Unit
) {
    IconButton(
        onClick = onClick,
        modifier = modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(MaterialTheme.mangalaColors.bg)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = tint,
            modifier = Modifier.size(24.dp),
        )
    }
}
