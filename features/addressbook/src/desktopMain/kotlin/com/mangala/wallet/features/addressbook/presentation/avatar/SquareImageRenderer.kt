package com.mangala.wallet.features.addressbook.presentation.avatar

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.jetbrains.skia.Image
import java.io.File

/**
 * Desktop implementation cho SquareImageRenderer - hiển thị ảnh hình vuông
 */
@Composable
actual fun SquareImageRenderer(
    imageUrl: String,
    fillContainer: Boolean,
    size: Dp,
    modifier: Modifier
) {
    // Load bitmap từ file path
    val bitmap = remember(imageUrl) {
        try {
            val file = File(imageUrl)
            if (file.exists() && file.length() > 0) {
                val bytes = file.readBytes()
                Image.makeFromEncoded(bytes).toComposeImageBitmap()
            } else null
        } catch (e: Exception) {
            println("SquareImageRenderer Desktop: Error loading image: $e")
            null
        }
    }
    
    if (bitmap != null) {
        // Hiển thị ảnh hình vuông (không clip)
        Image(
            bitmap = bitmap,
            contentDescription = "Square avatar",
            contentScale = ContentScale.Crop,
            modifier = if (fillContainer) modifier else modifier.size(size)
        )
    } else {
        // Fallback placeholder
        Box(
            modifier = if (fillContainer) modifier.background(Color(0xFFF0F0F0)) 
                      else modifier.size(size).background(Color(0xFFF0F0F0)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Photo,
                contentDescription = "Image placeholder",
                tint = Color.Gray,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}