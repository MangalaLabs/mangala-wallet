package com.mangala.wallet.features.addressbook.domain.usecase.contact.avatar

import com.mangala.wallet.features.addressbook.data.repository.ContactRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * UseCase để lưu đường dẫn avatar vào bản ghi contact
 */
class SaveAvatarUseCase(
    private val contactRepository: ContactRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Default
) {
    suspend operator fun invoke(contactId: String, avatarPath: String): Result<Unit> = withContext(dispatcher) {
        try {
            contactRepository.updateContactAvatar(contactId, avatarPath)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
