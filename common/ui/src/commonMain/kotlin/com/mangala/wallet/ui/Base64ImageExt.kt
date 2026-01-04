package com.mangala.wallet.ui

import androidx.compose.ui.graphics.ImageBitmap

expect fun ByteArray.decodeBase64ToImageBitmap(): ImageBitmap?

expect fun ByteArray.toImageBitmap(): ImageBitmap?