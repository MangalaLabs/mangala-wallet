package com.mangala.wallet.features.addressbook.data.repository.avatar


import com.mangala.wallet.features.addressbook.data.local.avatar.AvatarLocalDataSource
import com.mangala.wallet.features.addressbook.domain.repository.avatar.AvatarRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

class AvatarRepositoryImpl(
    private val avatarLocalDataSource: AvatarLocalDataSource
) : AvatarRepository {

    override suspend fun saveImage(entityId: String, imageData: ByteArray): String = withContext(Dispatchers.IO) {
        avatarLocalDataSource.saveImage(entityId, imageData)
    }

    override suspend fun deleteImage(filePath: String?): Boolean = withContext(Dispatchers.IO) {
        avatarLocalDataSource.deleteImage(filePath)
    }

    override suspend fun copyImageToInternalStorage(sourcePath: String, entityType: String): String? = withContext(Dispatchers.IO) {
        avatarLocalDataSource.copyImageToInternalStorage(sourcePath, entityType)
    }
}