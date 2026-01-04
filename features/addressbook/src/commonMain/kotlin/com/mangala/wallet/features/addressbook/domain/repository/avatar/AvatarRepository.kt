package com.mangala.wallet.features.addressbook.domain.repository.avatar


interface AvatarRepository {
    suspend fun saveImage(entityId: String, imageData: ByteArray): String
    suspend fun deleteImage(filePath: String?): Boolean
    suspend fun copyImageToInternalStorage(sourcePath: String, entityType: String = "avatar"): String?
}