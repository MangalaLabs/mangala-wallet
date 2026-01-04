package com.mangala.wallet.features.addressbook.utils.avatar

import java.awt.Image
import java.awt.image.BufferedImage
import com.mangala.wallet.features.addressbook.domain.model.AvatarConstants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import javax.imageio.ImageIO

/**
 * Triển khai AvatarImageProcessor cho Desktop (JVM).
 * Sử dụng Java AWT/ImageIO API để xử lý ảnh.
 */
class DesktopAvatarImageProcessor constructor() : AvatarImageProcessor {

    private val maxSize = AvatarConstants.MAX_AVATAR_SIZE
    private val maxFileSizeKb = AvatarConstants.MAX_AVATAR_FILE_SIZE_KB
    private val defaultQuality = AvatarConstants.DEFAULT_JPEG_QUALITY

    /**
     * Xử lý ảnh từ Uri (resize và nén)
     * Chú ý: Trên Desktop, Uri nên là file:// URI
     */

    override suspend fun process(path: String): ByteArray = withContext(Dispatchers.IO) {
        try {
            // Chuyển đổi Uri thành InputStream
            val url = java.net.URL(path)
            url.openStream().use { inputStream ->
                return@withContext process(inputStream)
            }
        } catch (e: Exception) {
            throw IOException("Lỗi khi xử lý ảnh: ${e.message}", e)
        }
    }

    override suspend fun process(bytes: ByteArray, mimeType: String?): ByteArray {
        TODO("Not yet implemented")
    }

    /**
     * Xử lý ảnh từ InputStream (resize và nén)
     */
    private suspend fun process(inputStream: InputStream): ByteArray = withContext(
        Dispatchers.IO) {
        try {
            // Đọc ảnh từ InputStream
            val originalImage = ImageIO.read(inputStream)
                ?: throw IOException("Không thể đọc ảnh từ InputStream")

            // Resize ảnh nếu cần
            val resizedImage = resizeImageIfNeeded(originalImage)

            // Nén ảnh thành JPEG
            val outputStream = ByteArrayOutputStream()
            var quality = defaultQuality / 100f  // ImageIO sử dụng 0.0-1.0 cho quality

            // Tạo JPEG writer
            val jpegWriter = ImageIO.getImageWritersByFormatName("jpg").next()
            val jpegWriteParam = jpegWriter.defaultWriteParam

            // Cấu hình chất lượng nén
            jpegWriteParam.compressionMode = javax.imageio.ImageWriteParam.MODE_EXPLICIT
            jpegWriteParam.compressionQuality = quality

            // Thực hiện nén
            jpegWriter.output = javax.imageio.stream.MemoryCacheImageOutputStream(outputStream)
            jpegWriter.write(null, javax.imageio.IIOImage(resizedImage, null, null), jpegWriteParam)
            jpegWriter.dispose()

            // Kiểm tra kích thước và điều chỉnh chất lượng nếu cần
            var attempts = 0
            while (outputStream.size() > maxFileSizeKb * 1024 && quality > 0.1f && attempts < 9) {
                outputStream.reset()
                quality -= 0.1f
                jpegWriteParam.compressionQuality = quality

                jpegWriter.output = javax.imageio.stream.MemoryCacheImageOutputStream(outputStream)
                jpegWriter.write(null, javax.imageio.IIOImage(resizedImage, null, null), jpegWriteParam)

                attempts++
            }

            return@withContext outputStream.toByteArray()
        } catch (e: Exception) {
            throw IOException("Lỗi khi xử lý ảnh: ${e.message}", e)
        }
    }

    /**
     * Lấy MIME type của ảnh sau khi xử lý
     */
    override fun getProcessedMimeType(): String {
        return "image/jpeg"
    }

    /**
     * Resize ảnh nếu kích thước lớn hơn maxSize
     */
    private fun resizeImageIfNeeded(image: BufferedImage): BufferedImage {
        val width = image.width
        val height = image.height

        // Chỉ resize nếu cần
        if (width <= maxSize && height <= maxSize) {
            return image
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
        val resizedImage = image.getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH)

        // Chuyển đổi về BufferedImage
        val bufferedImage = BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB)
        val graphics = bufferedImage.createGraphics()
        graphics.drawImage(resizedImage, 0, 0, null)
        graphics.dispose()

        return bufferedImage
    }
}