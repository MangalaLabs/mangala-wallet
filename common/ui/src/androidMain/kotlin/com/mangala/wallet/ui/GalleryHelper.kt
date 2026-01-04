package com.mangala.wallet.ui

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.provider.MediaStore
import android.util.Log
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import org.koin.core.component.KoinComponent

actual class GalleryHelper(private val applicationContext: Context) : IGalleryHelper, KoinComponent {
    actual override fun saveImageToGallery(image: ImageBitmap, imageName: String): Boolean {
        //TODO: Add permission check
        val bitmap = image.asAndroidBitmap()
        val contentValues = ContentValues().apply {
            put(
                MediaStore.MediaColumns.DISPLAY_NAME,
                "$imageName.jpg"
            )
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
        }

        val uri =
            applicationContext.contentResolver.insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues
            )

        uri?.let {
            applicationContext.contentResolver.openOutputStream(it)?.use { outputStream ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            }
            return true
        } ?: run {
            Log.e("Mangala", "GalleryHelper: Failed to create new MediaStore record.")
            return false
        }
    }
}