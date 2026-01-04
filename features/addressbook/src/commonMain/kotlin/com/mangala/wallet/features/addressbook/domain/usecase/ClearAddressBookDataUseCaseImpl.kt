package com.mangala.wallet.features.addressbook.domain.usecase

import com.mangala.wallet.domain.reset.usecases.ClearAddressBookDataUseCase
import com.mangala.wallet.features.addressbook.domain.repository.contact.ContactRepository
import com.mangala.wallet.features.addressbook.domain.repository.group.GroupRepository
import com.mangala.wallet.features.addressbook.domain.repository.tag.TagRepository
import com.mangala.wallet.features.addressbook.domain.repository.transaction.TransactionRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

/**
 * Implementation for clearing AddressBook data during wallet reset
 */
class ClearAddressBookDataUseCaseImpl(
    private val contactRepository: ContactRepository,
    private val groupRepository: GroupRepository,
    private val tagRepository: TagRepository,
    private val transactionRepository: TransactionRepository,
) : ClearAddressBookDataUseCase {

    override suspend operator fun invoke(): Boolean = withContext(Dispatchers.IO) {
        val handler = CoroutineExceptionHandler { _, _ -> }

        return@withContext withContext(handler) {
            try {
                val results = listOf(
                    contactRepository.clearAllContacts(),
                    groupRepository.clearAllGroups(),
                    tagRepository.clearUserTags(),
                    transactionRepository.clearAllTransactionHistory(),
                    transactionRepository.clearAllOfflineQueue(),
                    transactionRepository.clearAllRecentTxRemoteKeys()
                )
                results.all { it }
            } catch (e: Exception) {
                false
            }
        }
    }
}