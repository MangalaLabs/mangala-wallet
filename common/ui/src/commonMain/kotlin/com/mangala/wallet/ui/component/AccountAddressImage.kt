package com.mangala.wallet.ui.component

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.mangala.wallet.ui.imageloader.RemoteImage

@Composable
fun AccountAddressImage(address: String, size: Dp = 40.dp) {
    RemoteImage(Modifier.size(size).clip(CircleShape), "https://api.dicebear.com/6.x/identicon/svg?seed=${address}")
}