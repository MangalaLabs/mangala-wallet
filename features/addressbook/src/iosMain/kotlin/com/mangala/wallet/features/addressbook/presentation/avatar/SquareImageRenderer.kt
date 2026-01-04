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
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.jetbrains.skia.Image
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.reinterpret
import kotlinx.cinterop.usePinned
import platform.posix.memcpy
import platform.Foundation.NSData
import platform.Foundation.NSFileManager
import platform.Foundation.NSURL
import platform.Foundation.dataWithContentsOfURL

/**
 * iOS implementation cho SquareImageRenderer - hiển thị ảnh hình vuông
 */
@OptIn(ExperimentalForeignApi::class)
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
            val fileManager = NSFileManager.defaultManager
            val url = NSURL.fileURLWithPath(imageUrl)

            if (fileManager.fileExistsAtPath(imageUrl)) {
                val data = NSData.dataWithContentsOfURL(url)
                if (data != null) {
                    val bytes = ByteArray(data.length.toInt())
                    bytes.usePinned { pinned ->
                        memcpy(pinned.addressOf(0), data.bytes, data.length)
                    }
                    Image.makeFromEncoded(bytes).toComposeImageBitmap()
                } else null
            } else null
        } catch (e: Exception) {
            println("SquareImageRenderer iOS: Error loading image: $e")
            null
        }
    }

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