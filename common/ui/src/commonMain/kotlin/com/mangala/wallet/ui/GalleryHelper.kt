package com.mangala.wallet.ui

import androidx.compose.ui.graphics.ImageBitmap
import com.mangala.wallet.utils.currentTimeInMillis


interface IGalleryHelper {
    fun saveImageToGallery(image: ImageBitmap, imageName: String = "mangala-${currentTimeInMillis()}"): Boolean
}

expect class GalleryHelper: IGalleryHelper {
    override fun saveImageToGallery(image: ImageBitmap, imageName: String): Boolean
}