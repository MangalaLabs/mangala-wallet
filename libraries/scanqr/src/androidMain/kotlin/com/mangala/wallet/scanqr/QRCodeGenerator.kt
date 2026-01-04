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

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.compose.ui.graphics.asImageBitmap
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.sql.Timestamp
import java.util.Date
import java.util.UUID
import androidx.core.graphics.set
import androidx.core.graphics.createBitmap
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel

actual class QRCodeGenerator(private val context: Context) {

    actual fun saveTempBitmap(data: String): Any? {
        val bitmap = generateBitmap(data)

        val tempDir = context.cacheDir
        val fileName = generateUniqueFileName("png")
        val tempFile = File(tempDir, fileName)

        return try {
            FileOutputStream(tempFile).use { out ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            }
            tempFile
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    actual fun saveBitmapToFile(data: String): Boolean {
        val bitmap = generateBitmap(data)

//        val externalFilesDir = ContextCompat.getExternalFilesDirs(context, null).first()
//        val qrCodeFile = File(externalFilesDir, "qr_code.png")
//        try {
//            val outputStream = FileOutputStream(qrCodeFile)
//            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
//            outputStream.flush()
//            outputStream.close()
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val values = ContentValues()
            values.put(MediaStore.Images.Media.RELATIVE_PATH, "DCIM")
            values.put(MediaStore.Images.Media.IS_PENDING, true)

            val uri: Uri? = context.contentResolver
                .insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)

            if (uri != null) {
                saveImageToStream(bitmap, context.contentResolver.openOutputStream(uri))
                values.put(MediaStore.Images.Media.IS_PENDING, false)
                context.contentResolver.update(uri, values, null, null)
            }

            return true
        } else {
            var dir = File(
                context.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                ""
            )

            // getExternalStorageDirectory is deprecated in API 29

            if (!dir.exists()) {
                dir.mkdirs()
            }

            val date = Date()

            val fullFileName = "myFileName.jpeg"

            val fileName = fullFileName.substring(0, fullFileName.lastIndexOf("."))
            val extension = fullFileName.substring(fullFileName.lastIndexOf("."))

            var imageFile = File(
                dir.absolutePath
                    .toString() + File.separator
                        + fileName + "_" + Timestamp(date.time).toString()
                        + ".jpg"
            )

            println("imageFile: $imageFile")

            saveImageToStream(bitmap, FileOutputStream(imageFile))

            val values = ContentValues()

            values.put(MediaStore.Images.Media.DATA, imageFile.absolutePath)

            // .DATA is deprecated in API 29

            context.contentResolver
                .insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)

            return true
        }
    }

    actual fun generateQRCode(data: String): Any? {
        val bitmap = generateBitmap(data)
        return bitmap.asImageBitmap()
    }

    private fun generateBitmap(data: String): Bitmap {
        val hints = mapOf(
            EncodeHintType.MARGIN to 0,
            EncodeHintType.ERROR_CORRECTION to ErrorCorrectionLevel.H
        )
        val bitMatrix = MultiFormatWriter().encode(data, BarcodeFormat.QR_CODE, 500, 500, hints)
        val bitmap = createBitmap(bitMatrix.width, bitMatrix.height, Bitmap.Config.RGB_565)
        for (x in 0 until bitMatrix.width) {
            for (y in 0 until bitMatrix.height) {
                bitmap[x, y] = if (bitMatrix[x, y]) Color.BLACK else Color.WHITE
            }
        }
        return bitmap
    }

    private fun contentValues(): ContentValues? {

        val values = ContentValues()

        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        values.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis() / 1000)
        values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis())

        return values

    }

    private fun saveImageToStream(bitmap: Bitmap, outputStream: OutputStream?) {

        if (outputStream != null) {
            try {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                outputStream.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun generateUniqueFileName(suffix: String): String {
        val uuid = UUID.randomUUID().toString()
        return "$uuid.$suffix"
    }
}
