package com.mangala.wallet.ui

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toAwtImage
import org.koin.core.component.KoinComponent
import java.io.File
import javax.imageio.ImageIO

actual class GalleryHelper : IGalleryHelper, KoinComponent {
    actual override fun saveImageToGallery(image: ImageBitmap, imageName: String): Boolean {
        val file = File(imageName)
        ImageIO.write(image.toAwtImage(), "png", file)

        return true
    }
}