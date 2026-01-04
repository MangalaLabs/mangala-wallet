package com.mangala.features.wallet.presentationv2.antelope.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.mangala.wallet.ui.WalletThemeV2
import com.mangala.wallet.ui.imageloader.ImageSource
import com.mangala.wallet.ui.imageloader.MultiImage

@Composable
fun TokenLogo(
    iconResource: ImageSource?,
    symbol: String,
    size: Dp = 24.dp,
    modifier: Modifier = Modifier
) {
    if (iconResource != null || (iconResource is ImageSource.Url && iconResource.url.isNotEmpty())) {
        Box(
            modifier = modifier
                .size(size)
                .clip(CircleShape)
                .background(Color.Transparent)
        ) {
            MultiImage(
                imageSource = iconResource,
                modifier = Modifier
                    .size(size)
                    .clip(CircleShape),
                contentScale = ContentScale.Fit
            )
        }
    } else {
        Box(
            modifier = modifier
                .size(size)
                .clip(CircleShape)
                .background(
                    when (symbol) {
                        "BSW" -> Color(0xFF6366F1)
                        "BNB" -> Color(0xFFF3BA2F)
                        "USDT" -> Color(0xFF26A17B)
                        "EOS" -> Color(0xFF000000)
                        "VAULTA" -> Color(0xFF8B5CF6)
                        "BTC" -> Color(0xFFF7931A)
                        "ETH" -> Color(0xFF627EEA)
                        "DOGE" -> Color(0xFFC2A633)
                        else -> Color(0xFF6B7280)
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = symbol.take(2),
                fontSize = WalletThemeV2.Typography.fontSizeBody,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}