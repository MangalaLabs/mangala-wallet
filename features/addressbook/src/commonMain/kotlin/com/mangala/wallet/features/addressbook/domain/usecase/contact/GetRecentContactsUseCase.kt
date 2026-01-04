package com.mangala.wallet.features.addressbook.domain.usecase.contact

import app.cash.paging.PagingData
import com.mangala.wallet.features.addressbook.data.model.ContactRecentTransactionModel
import com.mangala.wallet.features.addressbook.data.model.enum.TransactionStatus
import com.mangala.wallet.features.addressbook.domain.repository.contact.ContactRepository
import kotlinx.coroutines.flow.Flow

class GetRecentContactsUseCase(private val contactRepository: ContactRepository) {

    companion object {
        private val DEFAULT_STATUSES = listOf(
            TransactionStatus.CONFIRMED,
            TransactionStatus.PENDING
        )
    }

    // Cash App Paging flow for automatic pagination
    fun getPaginatedContactRecentTransactions(
        searchQuery: String? = null,
        statuses: List<TransactionStatus> = DEFAULT_STATUSES,
    ): Flow<PagingData<ContactRecentTransactionModel>> {
        return contactRepository.getPaginatedContactRecentTransactions(
            searchQuery = searchQuery,
            statuses = statuses.map { it.name }
        )
    }
}