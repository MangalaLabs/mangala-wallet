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
package com.mangala.wallet.scanqr

import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import java.awt.Color
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

actual class QRCodeGenerator {

    actual fun saveBitmapToFile(data: String): Boolean {
        val bufferedImage = generateBufferedImage(data)

        val file = File("qrcode.png")
        ImageIO.write(bufferedImage, "png", file)

        return true
    }

    actual fun generateQRCode(data: String): Any? {
        val bufferedImage = generateBufferedImage(data)

        val tempFile = File.createTempFile("qrcode", ".png").apply {
            deleteOnExit() // Delete file when JVM exits
            ImageIO.write(bufferedImage, "png", this)
        }
        return tempFile.absolutePath
    }

    actual fun saveTempBitmap(data: String): Any?{
        return null
    }

    private fun generateBufferedImage(data: String): BufferedImage {
        val barcodeWriter = QRCodeWriter()
        val bitMatrix = barcodeWriter.encode(data, BarcodeFormat.QR_CODE, 200, 200)

        val bufferedImage =
            BufferedImage(bitMatrix.width, bitMatrix.height, BufferedImage.TYPE_INT_RGB)
        for (x in 0 until bitMatrix.width) {
            for (y in 0 until bitMatrix.height) {
                bufferedImage.setRGB(
                    x,
                    y,
                    if (bitMatrix.get(x, y)) Color.BLACK.rgb else Color.WHITE.rgb
                )
            }
        }
        return bufferedImage
    }

}