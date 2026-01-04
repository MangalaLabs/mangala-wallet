package com.mangala.wallet.ui

import androidx.compose.ui.graphics.ImageBitmap
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.refTo
import org.koin.core.component.KoinComponent
import platform.CoreImage.*
import platform.Foundation.*
import platform.UIKit.*
import platform.CoreGraphics.*

actual class GalleryHelper : IGalleryHelper, KoinComponent {
    @OptIn(ExperimentalForeignApi::class)
    actual override fun saveImageToGallery(image: ImageBitmap, imageName: String): Boolean {
        val uiImage = image.toUIImage() ?: return false
        UIImageWriteToSavedPhotosAlbum(uiImage, null, null, null)

        return true
    }

    @OptIn(ExperimentalForeignApi::class)
    private fun ImageBitmap.toUIImage(): UIImage? {
        val width = this.width
        val height = this.height
        val buffer = IntArray(width * height)

        this.readPixels(buffer)

        val colorSpace = CGColorSpaceCreateDeviceRGB()
        val context = CGBitmapContextCreate(
            data = buffer.refTo(0),
            width = width.toULong(),
            height = height.toULong(),
            bitsPerComponent = 8u,
            bytesPerRow = (4 * width).toULong(),
            space = colorSpace,
            bitmapInfo = CGImageAlphaInfo.kCGImageAlphaPremultipliedLast.value
        )

        val cgImage = CGBitmapContextCreateImage(context)
        return cgImage?.let { UIImage.imageWithCGImage(it) }
    }
}