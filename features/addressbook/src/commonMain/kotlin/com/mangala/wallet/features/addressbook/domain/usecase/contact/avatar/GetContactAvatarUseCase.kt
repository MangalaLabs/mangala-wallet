package com.mangala.wallet.features.addressbook.domain.usecase.contact.avatar

import com.mangala.wallet.features.addressbook.data.repository.ContactRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * UseCase để lấy đường dẫn avatar của một contact
 */
class GetContactAvatarUseCase(
    private val contactRepository: ContactRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Default
) {
    suspend operator fun invoke(contactId: String): Result<String?> = withContext(dispatcher) {
        try {
            val avatarPath = contactRepository.getContactAvatar(contactId)
            Result.success(avatarPath)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
