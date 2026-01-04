package com.mangala.wallet.ui

import android.R.attr.bitmap
import android.graphics.BitmapFactory
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

@OptIn(ExperimentalEncodingApi::class)
actual fun ByteArray.decodeBase64ToImageBitmap(): ImageBitmap? {
    val imageBytes = Base64.decode(this)

    return imageBytes.toImageBitmap()
}

actual fun ByteArray.toImageBitmap(): ImageBitmap? {
    return try {
        val bitmap = BitmapFactory.decodeByteArray(this, 0, this.size)

        bitmap.asImageBitmap()
    } catch (_: Exception) {
        null
    }
}