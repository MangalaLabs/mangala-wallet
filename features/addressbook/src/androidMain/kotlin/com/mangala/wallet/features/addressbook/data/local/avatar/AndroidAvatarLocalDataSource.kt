package com.mangala.wallet.features.addressbook.data.local.avatar


import android.content.Context
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

class AndroidAvatarLocalDataSource(
    private val context: Context
) : AvatarLocalDataSource {
    private val avatarDir = File(context.filesDir, "avatars").apply {
        if (!exists()) mkdirs()
    }

    override suspend fun saveImage(entityId: String, imageData: ByteArray): String = withContext(Dispatchers.IO) {
        val fileName = "${entityId}-${UUID.randomUUID().toString().substring(0, 8)}.jpg"
        val avatarFile = File(avatarDir, fileName)

        FileOutputStream(avatarFile).use {
            it.write(imageData)
        }

        return@withContext avatarFile.absolutePath
    }

    override suspend fun deleteImage(filePath: String?): Boolean = withContext(Dispatchers.IO) {
        if (filePath == null) return@withContext false
        val file = File(filePath)
        return@withContext file.exists() && file.delete()
    }

    override suspend fun copyImageToInternalStorage(sourcePath: String, entityType: String): String? = withContext(Dispatchers.IO) {
        try {
            // Check if it's content URI or file path
            if (sourcePath.startsWith("content://")) {
                val uri = Uri.parse(sourcePath)
                val inputStream = context.contentResolver.openInputStream(uri)
                
                if (inputStream != null) {
                    // Use timestamp-based filename to avoid duplicates
                    val timestamp = System.currentTimeMillis()
                    val fileName = "avatar-${timestamp}-${UUID.randomUUID().toString().substring(0, 4)}.jpg"
                    val avatarFile = File(avatarDir, fileName)
                    
                    FileOutputStream(avatarFile).use { outputStream ->
                        inputStream.copyTo(outputStream)
                    }
                    
                    inputStream.close()
                    println("AndroidAvatarLocalDataSource: Image copied to: ${avatarFile.absolutePath}")
                    return@withContext avatarFile.absolutePath
                }
            } else {
                // File path - copy to internal storage
                val sourceFile = File(sourcePath)
                if (sourceFile.exists()) {
                    val timestamp = System.currentTimeMillis()
                    val fileName = "avatar-${timestamp}-${UUID.randomUUID().toString().substring(0, 4)}.jpg"
                    val avatarFile = File(avatarDir, fileName)
                    sourceFile.copyTo(avatarFile)
                    println("AndroidAvatarLocalDataSource: Image copied to: ${avatarFile.absolutePath}")
                    return@withContext avatarFile.absolutePath
                }
            }
            return@withContext null
        } catch (e: Exception) {
            println("AndroidAvatarLocalDataSource: Error copying image: $e")
            return@withContext null
        }
    }
}