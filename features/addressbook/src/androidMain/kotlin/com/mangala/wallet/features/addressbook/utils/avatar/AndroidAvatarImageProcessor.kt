package com.mangala.wallet.features.addressbook.utils.avatar

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import com.mangala.wallet.features.addressbook.domain.model.AvatarConstants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream

/**
 * Triển khai AvatarImageProcessor cho Android.
 * Sử dụng Android API để xử lý ảnh.
 */
class AndroidAvatarImageProcessor(
    private val context: Context
) : AvatarImageProcessor {

    private val maxSize = AvatarConstants.MAX_AVATAR_SIZE
    private val maxFileSizeKb = AvatarConstants.MAX_AVATAR_FILE_SIZE_KB
    private val defaultQuality = AvatarConstants.DEFAULT_JPEG_QUALITY

    /**
     * Xử lý ảnh từ Uri (resize và nén)
     */
    suspend fun processUri(uri: Uri): ByteArray = withContext(Dispatchers.IO) {
        try {
            // Sử dụng ContentResolver để mở stream
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                return@withContext processInputStream(inputStream, getMimeType(uri))
            } ?: throw IOException("Không thể mở ảnh từ Uri: $uri")
        } catch (e: Exception) {
            throw IOException("Lỗi khi xử lý ảnh: ${e.message}", e)
        }
    }

    /**
     * Xử lý ảnh từ path String (triển khai interface)
     */
    override suspend fun process(path: String): ByteArray = withContext(Dispatchers.IO) {
        return@withContext processUri(Uri.parse(path))
    }

    /**
     * Xử lý ảnh từ ByteArray (triển khai interface)
     */
    override suspend fun process(bytes: ByteArray, mimeType: String?): ByteArray = withContext(Dispatchers.IO) {
        try {
            // Đọc bitmap từ ByteArray
            val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                ?: throw IOException("Không thể decode bitmap từ ByteArray")

            // Resize và compress
            return@withContext processBitmap(bitmap)
        } catch (e: Exception) {
            throw IOException("Lỗi khi xử lý ảnh: ${e.message}", e)
        }
    }

    /**
     * Xử lý ảnh từ InputStream
     */
    private suspend fun processInputStream(inputStream: InputStream, mimeType: String?): ByteArray = withContext(Dispatchers.IO) {
        try {
            // Đọc bitmap từ inputStream
            val bitmap = decodeBitmapFromStream(inputStream)

            return@withContext processBitmap(bitmap)
        } catch (e: Exception) {
            throw IOException("Lỗi khi xử lý ảnh: ${e.message}", e)
        }
    }

    /**
     * Xử lý bitmap (resize và compress)
     */
    private fun processBitmap(bitmap: Bitmap): ByteArray {
        // Resize bitmap nếu cần
        val resizedBitmap = resizeBitmapIfNeeded(bitmap)

        // Compress bitmap thành JPEG
        val outputStream = ByteArrayOutputStream()
        var quality = defaultQuality
        resizedBitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)

        // Nén tiếp cho đến khi kích thước nhỏ hơn maxFileSizeKb
        while (outputStream.size() > maxFileSizeKb * 1024 && quality > 10) {
            outputStream.reset()
            quality -= 10
            resizedBitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
        }

        // Giải phóng bitmap nếu khác với bitmap gốc
        if (resizedBitmap != bitmap) {
            resizedBitmap.recycle()
        }
        bitmap.recycle()

        return outputStream.toByteArray()
    }

    /**
     * Lấy MIME type của ảnh sau khi xử lý
     */
    override fun getProcessedMimeType(): String {
        return "image/jpeg"
    }

    /**
     * Decode bitmap từ InputStream với tối ưu bộ nhớ
     */
    private fun decodeBitmapFromStream(inputStream: InputStream): Bitmap {
        // Đọc thông tin ảnh mà chưa load toàn bộ vào bộ nhớ
        val options = BitmapFactory.Options().apply {
            inJustDecodeBounds = true
        }

        // Đọc kích thước ảnh
        val tempBytes = inputStream.readBytes()
        BitmapFactory.decodeByteArray(tempBytes, 0, tempBytes.size, options)

        // Tính toán inSampleSize để giảm kích thước khi đọc
        options.inSampleSize = calculateInSampleSize(options, maxSize, maxSize)
        options.inJustDecodeBounds = false

        // Đọc ảnh với inSampleSize đã tính
        return BitmapFactory.decodeByteArray(tempBytes, 0, tempBytes.size, options)
            ?: throw IOException("Không thể decode bitmap")
    }

    /**
     * Tính toán inSampleSize để giảm kích thước bitmap khi load
     */
    private fun calculateInSampleSize(
        options: BitmapFactory.Options,
        reqWidth: Int,
        reqHeight: Int
    ): Int {
        val (height, width) = options.outHeight to options.outWidth
        var inSampleSize = 1

        if (height > reqHeight || width > reqWidth) {
            val halfHeight = height / 2
            val halfWidth = width / 2

            // Tính toán giá trị inSampleSize lớn nhất là lũy thừa của 2
            // vẫn cho phép ảnh lớn hơn hoặc bằng kích thước yêu cầu
            while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2
            }
        }

        return inSampleSize
    }

    /**
     * Resize bitmap nếu kích thước lớn hơn maxSize
     */
    private fun resizeBitmapIfNeeded(bitmap: Bitmap): Bitmap {
        val width = bitmap.width
        val height = bitmap.height

        // Chỉ resize nếu ảnh lớn hơn maxSize
        if (width <= maxSize && height <= maxSize) {
            return bitmap
        }

        // Tính toán tỷ lệ để giữ aspect ratio
        val ratio = width.toFloat() / height.toFloat()

        val targetWidth: Int
        val targetHeight: Int

        if (width > height) {
            targetWidth = maxSize
            targetHeight = (maxSize / ratio).toInt()
        } else {
            targetHeight = maxSize
            targetWidth = (maxSize * ratio).toInt()
        }

        // Thực hiện resize
        return Bitmap.createScaledBitmap(bitmap, targetWidth, targetHeight, true)
    }

    /**
     * Lấy MIME type từ Uri
     */
    private fun getMimeType(uri: Uri): String? {
        return context.contentResolver.getType(uri)
    }
}