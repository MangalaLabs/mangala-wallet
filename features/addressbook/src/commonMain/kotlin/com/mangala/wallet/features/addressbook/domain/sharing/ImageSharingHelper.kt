package com.mangala.wallet.features.addressbook.domain.sharing

import androidx.compose.ui.graphics.ImageBitmap

/**
 * Platform-specific image sharing interface
 */
expect class ImageSharingHelper {
    /**
     * Share QR code image with system share dialog
     */
    suspend fun shareQrImage(
        image: ImageBitmap,
        title: String,
        text: String? = null
    ): ShareResult
    
    /**
     * Save QR code image to device gallery/photos
     */
    suspend fun saveQrImageToGallery(
        image: ImageBitmap,
        filename: String
    ): SaveResult
}

sealed class ShareResult {
    object Success : ShareResult()
    data class Error(val message: String) : ShareResult()
    object Cancelled : ShareResult()
}

sealed class SaveResult {
    object Success : SaveResult()
    data class Error(val message: String) : SaveResult()
    object PermissionDenied : SaveResult()
}