package com.mangala.wallet.domain.transaction.history.usecases

import app.cash.paging.PagingData
import com.mangala.wallet.domain.account.usecases.GetAccountByIdUseCase
import com.mangala.wallet.domain.datastore.usecases.GetSelectedNetworkUseCase
import com.mangala.wallet.domain.transaction.history.Transaction
import com.mangala.wallet.domain.transaction.history.TransactionStatus
import com.mangala.wallet.domain.transaction.history.TransactionType
import com.mangala.wallet.domain.transaction.history.repository.TransactionRepository
import com.mangala.wallet.model.blockchain.BlockchainType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.Instant

class GetTransactionHistoryUseCase(
    private val getSelectedNetworkUseCase: GetSelectedNetworkUseCase,
    private val getAccountByIdUseCase: GetAccountByIdUseCase,
    private val transactionRepository: TransactionRepository
) {

    // null for no filter
    operator fun invoke(
        accountId: String,
        transactionTypeFilter: TransactionType?,
        transactionStatusFilter: TransactionStatus?,
        startDateFilter: Instant?,
        endDateFilter: Instant?
    ): Flow<PagingData<Transaction>> {
        // TODO: try to implement this without runBlocking

        return runBlocking {
            val network = getSelectedNetworkUseCase()

            val account = getAccountByIdUseCase(accountId)

            return@runBlocking transactionRepository.getPaginatedTransactionsForAddress(
                accountId,
                network.blockchainType,
                account.bip44Address, // TODO: Handle network with different address format
                transactionTypeFilter,
                transactionStatusFilter,
                startDateFilter,
                endDateFilter
            )
        }
    }
}