package com.mangala.wallet.features.addressbook.data.local.avatar

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import platform.Foundation.*
import platform.UIKit.UIDevice
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class IosAvatarLocalDataSource : AvatarLocalDataSource {

    @OptIn(ExperimentalForeignApi::class)
    private val avatarDir: String by lazy {
        val documentDirectoryURL = NSFileManager.defaultManager.URLsForDirectory(
            NSDocumentDirectory,
            NSUserDomainMask
        ).firstOrNull() as? NSURL
        val documentDirectory = documentDirectoryURL?.path ?: ""
        val avatarsPath = "$documentDirectory/avatars"

        // Create directory if it doesn't exist
        val fileManager = NSFileManager.defaultManager
        if (!fileManager.fileExistsAtPath(avatarsPath)) {
            fileManager.createDirectoryAtPath(
                avatarsPath,
                withIntermediateDirectories = true,
                attributes = null,
                error = null
            )
        }
        avatarsPath
    }

    @OptIn(ExperimentalUuidApi::class)
    override suspend fun saveImage(entityId: String, imageData: ByteArray): String = withContext(Dispatchers.Default) {
        val fileName = "${entityId}-${Uuid.random().toString().substring(0, 8)}.jpg"
        val filePath = "$avatarDir/$fileName"

        val nsData = imageData.toNSData()
        nsData.writeToFile(filePath, atomically = true)

        return@withContext filePath
    }

    @OptIn(ExperimentalForeignApi::class)
    override suspend fun deleteImage(filePath: String?): Boolean = withContext(Dispatchers.Default) {
        if (filePath == null) return@withContext false

        val fileManager = NSFileManager.defaultManager
        return@withContext try {
            fileManager.removeItemAtPath(filePath, error = null)
            true
        } catch (e: Exception) {
            false
        }
    }

    @OptIn(ExperimentalUuidApi::class, ExperimentalForeignApi::class)
    override suspend fun copyImageToInternalStorage(sourcePath: String, entityType: String): String? = withContext(Dispatchers.Default) {
        try {
            val fileManager = NSFileManager.defaultManager
            if (fileManager.fileExistsAtPath(sourcePath)) {
                val timestamp = NSDate().timeIntervalSince1970.toLong() * 1000
                val fileName = "avatar-${timestamp}-${Uuid.random().toString().substring(0, 4)}.jpg"
                val destinationPath = "$avatarDir/$fileName"

                val success = fileManager.copyItemAtPath(sourcePath, toPath = destinationPath, error = null)
                println("IosAvatarLocalDataSource: Image copied to: $destinationPath")
                return@withContext if (success) destinationPath else null
            }
            return@withContext null
        } catch (e: Exception) {
            println("IosAvatarLocalDataSource: Error copying image: $e")
            return@withContext null
        }
    }

    @OptIn(ExperimentalForeignApi::class)
    private fun ByteArray.toNSData(): NSData {
        return this.usePinned { pinned ->
            NSData.create(bytes = pinned.addressOf(0), length = this.size.toULong())
        }
    }
}