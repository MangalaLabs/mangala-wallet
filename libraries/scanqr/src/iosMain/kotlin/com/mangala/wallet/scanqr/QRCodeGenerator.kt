/*
 * Copyright 2023-2024 Mangala Wallet
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
@file:OptIn(ExperimentalForeignApi::class)

package com.mangala.wallet.scanqr

import androidx.compose.ui.graphics.toComposeImageBitmap
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.readBytes
import kotlinx.cinterop.usePinned
import org.jetbrains.skia.Image
import platform.CoreImage.*
import platform.Foundation.*
import platform.UIKit.*
import platform.CoreGraphics.*


actual class QRCodeGenerator {

    actual fun saveTempBitmap(data: String): Any? {
        val uiImage = generateQrCodeUIImage(data) ?: return null
        val imageData: NSData = UIImagePNGRepresentation(uiImage) ?: return null

        val tempDir = NSTemporaryDirectory()
        val tempDirUrl = NSURL.fileURLWithPath(tempDir)

        val fileUrl = tempDirUrl.URLByAppendingPathComponent("${NSUUID.UUID().UUIDString()}.png") ?: return null

        val isSuccess = imageData.writeToURL(fileUrl, atomically = true)

        return if (isSuccess) fileUrl.absoluteString else null
    }

    @OptIn(ExperimentalForeignApi::class)
    actual fun saveBitmapToFile(data: String): Boolean {
        UIImageWriteToSavedPhotosAlbum(generateQrCodeUIImage(data) as UIImage, null, null, null)

        return true
    }

    actual fun generateQRCode(data: String): Any? {
        val image = generateQrCodeUIImage(data) ?: return null
        val imageData: NSData = UIImagePNGRepresentation(image)!!
        val buffer = imageData.bytes?.readBytes(imageData.length.toInt())

        return if(buffer != null) {
            Image.makeFromEncoded(buffer).toComposeImageBitmap()
        }else{
            null
        }
    }

    private fun generateQrCodeUIImage(data: String): UIImage? {
        val nsData = data.encodeToByteArray().toNSData()
        val filter = CIFilter.filterWithName("CIQRCodeGenerator")
        filter?.setValue(nsData, forKey = "inputMessage")
        val transform = CGAffineTransformMakeScale(5.0, 5.0)
        val output = filter?.outputImage?.imageByApplyingTransform(transform)

        if (output != null) {
            return UIImage.imageWithCIImage(output)
        }
        return null
    }
}

private fun ByteArray.toNSData(): NSData = NSMutableData().apply {
    if (isEmpty()) return@apply
    this@toNSData.usePinned {
        appendBytes(it.addressOf(0), size.toULong())
    }
}