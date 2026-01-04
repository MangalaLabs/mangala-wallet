package com.mangala.wallet.features.addressbook.presentation.avatar

import android.graphics.BitmapFactory
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import java.io.File

/**
 * Android implementation cho SquareImageRenderer - hiển thị ảnh hình vuông
 */
@Composable
actual fun SquareImageRenderer(
    imageUrl: String,
    fillContainer: Boolean,
    size: Dp,
    modifier: Modifier
) {
    val context = LocalContext.current
    
    // Load bitmap từ URL/path
    val bitmap = remember(imageUrl) {
        try {
            when {
                imageUrl.startsWith("content://") -> {
                    // Content URI từ image picker
                    val contentUri = android.net.Uri.parse(imageUrl)
                    val inputStream = context.contentResolver.openInputStream(contentUri)
                    inputStream?.use { stream ->
                        BitmapFactory.decodeStream(stream)
                    }
                }
                else -> {
                    // File path
                    val file = File(imageUrl)
                    if (file.exists() && file.length() > 0) {
                        BitmapFactory.decodeFile(imageUrl)
                    } else null
                }
            }
        } catch (e: Exception) {
            println("SquareImageRenderer: Error loading image: $e")
            null
        }
    }
    
    if (bitmap != null) {
        // Hiển thị ảnh hình vuông (không clip)
        Image(
            bitmap = bitmap.asImageBitmap(),
            contentDescription = "Square avatar",
            contentScale = ContentScale.Crop,
            modifier = if (fillContainer) modifier else modifier.size(size)
        )
    } else {
        // Fallback placeholder
        Box(
            modifier = if (fillContainer) {
                modifier.background(Color(0xFFF0F0F0))
            } else {
                modifier.size(size).background(Color(0xFFF0F0F0))
            },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Photo,
                contentDescription = "Image placeholder",
                tint = Color.Gray,
                modifier = Modifier.size(16.dp)
            )
            println("SquareImageRenderer: Failed to load image: $imageUrl")
        }
    }
}