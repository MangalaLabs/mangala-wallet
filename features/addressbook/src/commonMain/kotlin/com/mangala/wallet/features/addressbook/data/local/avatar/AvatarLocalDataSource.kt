package com.mangala.wallet.features.addressbook.data.local.avatar

interface AvatarLocalDataSource {
    suspend fun saveImage(entityId: String, imageData: ByteArray): String
    suspend fun deleteImage(filePath: String?): Boolean
    
    // Platform-specific implementation để copy ảnh từ content URI/path
    suspend fun copyImageToInternalStorage(sourcePath: String, entityType: String = "avatar"): String?
}