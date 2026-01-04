package com.mangala.wallet.local.transaction.history

import app.cash.paging.PagingSource
import app.cash.sqldelight.paging3.QueryPagingSource
import com.mangala.wallet.local.MangalaWalletDatabaseWrapper
import commangalawalletdatabase.TransactionsEntity
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

class TransactionLocalDataSourceImpl(
    databaseWrapper: MangalaWalletDatabaseWrapper,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
): TransactionLocalDataSource {

    private val database = databaseWrapper.instance
    private val dbQuery = database.mangalaWalletDatabaseQueries

    override suspend fun insertTransactions(transactions: List<TransactionsEntity>) = withContext(ioDispatcher) {
        dbQuery.transaction {
            transactions.forEach { transaction ->
                insertTransactionWithoutSuspend(transaction)
            }
        }
    }

    override suspend fun insertTransaction(transaction: TransactionsEntity) = withContext(ioDispatcher) {
        insertTransactionWithoutSuspend(transaction)
    }

    override suspend fun getPendingTransactions(blockchainUid: String): List<TransactionsEntity> = withContext(ioDispatcher) {
        return@withContext dbQuery.getPendingTransactionsByBlockchainUid(blockchainUid).executeAsList()
    }

    override fun getTransactionPagingSource(
        accountId: String,
        blockchainUid: String,
        transactionType: String?,
        transactionStatus: String?,
        startDateFilter: Long?,
        endDateFilter: Long?
    ): PagingSource<Int, TransactionsEntity> = QueryPagingSource(
        countQuery = dbQuery.countTransactions(
            accountId = accountId,
            blockchainUid = blockchainUid,
            transactionType = transactionType,
            transactionStatus = transactionStatus,
            blockSignedAtStart = startDateFilter,
            blockSignedAtEnd = endDateFilter
        ),
        transacter = dbQuery,
        context = Dispatchers.IO,
        queryProvider = { limit, offset ->
            dbQuery.getTransactionsByAccountIdPaged(
                accountId = accountId,
                blockchainUid = blockchainUid,
                transactionType = transactionType,
                transactionStatus = transactionStatus,
                blockSignedAtStart = startDateFilter,
                blockSignedAtEnd = endDateFilter,
                limit = limit,
                offset = offset
            )
        }
    )

    override suspend fun getTransactionByTxHash(
        accountId: String,
        blockchainUid: String,
        txHash: String
    ): TransactionsEntity = withContext(ioDispatcher) {
        return@withContext dbQuery.getTransactionsByTxHash(
            txHash = txHash,
            blockchainUid = blockchainUid,
            accountId = accountId
        ).executeAsOne()
    }

    override suspend fun updateTransactionStatus(
        accountId: String,
        blockchainUid: String,
        txHashes: List<String>,
        status: String
    ) = withContext(ioDispatcher) {
        return@withContext dbQuery.updateTransactionStatus(
            accountId = accountId,
            blockchain_uid = blockchainUid,
            transactionHash = txHashes,
            status = status
        )
    }

    override suspend fun clearAllTransactions() = withContext(ioDispatcher) {
        dbQuery.clearAllTransactions()
    }

    private fun insertTransactionWithoutSuspend(
        transaction: TransactionsEntity
    ) {
        with(transaction) {
            dbQuery.insertTransaction(
                blockHeight = blockHeight,
                blockSignedAt = blockSignedAt,
                feesPaid = feesPaid,
                fromAddress = fromAddress,
                fromAddressLabel = fromAddressLabel,
                gasContractAddress = gasContractAddress,
                gasContractDecimals = gasContractDecimals,
                gasContractName = gasContractName,
                gasContractTickerSymbol = gasContractTickerSymbol,
                gasLogoUrl = gasLogoUrl,
                gasOffered = gasOffered,
                gasPrice = gasPrice,
                gasQuote = gasQuote,
                gasQuoteRate = gasQuoteRate,
                gasSpent = gasSpent,
                minerAddress = minerAddress,
                prettyGasQuote = prettyGasQuote,
                prettyValueQuote = prettyValueQuote,
                status = status,
                toAddress = toAddress,
                toAddressLabel = toAddressLabel,
                transactionHash = transactionHash,
                txOffset = txOffset,
                value_ = value_,
                valueQuote = valueQuote,
                accountId = accountId,
                transactionType = transactionType,
                logEvents = logEvents,
                blockchain_uid = blockchain_uid,
                is_nft_transaction = is_nft_transaction,
                nft_transfers = nft_transfers,
                erc20_transfers = erc20_transfers,
                native_transfers = native_transfers,
            )
        }
    }
}