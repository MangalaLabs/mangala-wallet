package com.mangala.wallet.ui

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

@OptIn(ExperimentalEncodingApi::class)
actual fun ByteArray.decodeBase64ToImageBitmap(): ImageBitmap? {
    val imageBytes = Base64.decode(this)

    return imageBytes.toImageBitmap()
}

actual fun ByteArray.toImageBitmap(): ImageBitmap? {
    return org.jetbrains.skia.Image.makeFromEncoded(this).toComposeImageBitmap()
}