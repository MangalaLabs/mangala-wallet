package com.mangala.wallet.features.addressbook.domain.sharing

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import platform.UIKit.*
import platform.Foundation.*
import platform.CoreGraphics.*
import kotlinx.cinterop.*

actual class ImageSharingHelper {

    actual suspend fun shareQrImage(
        image: ImageBitmap,
        title: String,
        text: String?
    ): ShareResult = withContext(Dispatchers.Main) {
        try {
            // Convert ImageBitmap to UIImage
            val uiImage = convertImageBitmapToUIImage(image)

            if (uiImage != null) {
                val items = mutableListOf<Any>().apply {
                    add(uiImage)
                    text?.let { add(it) }
                }

                val activityController = UIActivityViewController(
                    activityItems = items,
                    applicationActivities = null
                )

                // Present the share dialog
                val rootViewController = UIApplication.sharedApplication.keyWindow?.rootViewController
                rootViewController?.presentViewController(
                    activityController,
                    animated = true,
                    completion = null
                )

                ShareResult.Success
            } else {
                ShareResult.Error("Failed to convert image")
            }
        } catch (e: Exception) {
            ShareResult.Error(e.message ?: "Unknown error")
        }
    }

    @OptIn(ExperimentalForeignApi::class)
    actual suspend fun saveQrImageToGallery(
        image: ImageBitmap,
        filename: String
    ): SaveResult = withContext(Dispatchers.Main) {
        try {
            val uiImage = convertImageBitmapToUIImage(image)

            if (uiImage != null) {
                UIImageWriteToSavedPhotosAlbum(
                    image = uiImage,
                    completionTarget = null,
                    completionSelector = null,
                    contextInfo = null
                )
                SaveResult.Success
            } else {
                SaveResult.Error("Failed to convert image")
            }
        } catch (e: Exception) {
            SaveResult.Error(e.message ?: "Unknown error")
        }
    }

    private fun convertImageBitmapToUIImage(imageBitmap: ImageBitmap): UIImage? {
        // This is a simplified implementation
        // In a real implementation, you would need to convert ImageBitmap to CGImage/UIImage
        // For now, returning null to indicate this needs platform-specific implementation
        return null
    }
}