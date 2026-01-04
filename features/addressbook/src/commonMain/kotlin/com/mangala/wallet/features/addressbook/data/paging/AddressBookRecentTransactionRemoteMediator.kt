package com.mangala.wallet.features.addressbook.data.paging

import app.cash.paging.ExperimentalPagingApi
import app.cash.paging.LoadType
import app.cash.paging.PagingState
import app.cash.paging.RemoteMediator
import app.cash.paging.RemoteMediatorInitializeAction
import app.cash.paging.RemoteMediatorMediatorResult
import app.cash.paging.RemoteMediatorMediatorResultError
import app.cash.paging.RemoteMediatorMediatorResultSuccess
import com.mangala.antelope.base.api.remote.EosRemoteDataSource
import com.mangala.wallet.domain.datastore.repository.DataStoreRepository
import com.mangala.wallet.features.addressbook.data.local.transaction.AddressBookRecentTxRemoteKeyLocalDataSource
import com.mangala.wallet.features.addressbook.data.local.transaction.TransactionLocalDataSource
import com.mangala.wallet.features.addressbook.data.mapping.toAddressBookTransactionHistory
import com.mangala.wallet.features.addressbook.database.SelectRecentContactsWithSearch
import com.mangala.wallet.features.addressbook.domain.repository.blockchain.BlockchainRepository
import com.mangala.wallet.features.addressbook.domain.repository.contact.WalletAddressRepository
import com.mangala.wallet.features.addressbook.domain.repository.transaction.TransactionRepository
import com.mangala.wallet.features.chains.antelope_base.data.local.account.AccountLocalDataSource
import com.mangala.wallet.remote.di.ApiResponse
import com.mangala.wallet.utils.currentTimeInMillis
import com.mangala.wallet.utils.ext.orZero
import com.mangala.wallet.utils.ext.toBoolean
import com.mangala.wallet.utils.isNotNullOrBlank
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

@ExperimentalPagingApi
class AddressBookRecentTransactionRemoteMediator(
    private val queryString: String? = null,
    private val remoteDataSource: EosRemoteDataSource,
    private val localDataSource: TransactionLocalDataSource,
    private val antelopeAccountLocalDataSource: AccountLocalDataSource,
    private val remoteKeyDataSource: AddressBookRecentTxRemoteKeyLocalDataSource,
    private val blockchainRepository: BlockchainRepository,
    private val walletAddressRepository: WalletAddressRepository,
    private val transactionRepository: TransactionRepository,
    private val dataStoreRepository: DataStoreRepository,
    private val filter: String? = null,
    private val sort: String,
    private val transferTo: String? = null,
    private val transferFrom: String? = null,
    private val after: String? = null,
    private val before: String? = null,
) : RemoteMediator<Int, SelectRecentContactsWithSearch>() {
    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, SelectRecentContactsWithSearch>
    ): RemoteMediatorMediatorResult {
        val currentSelectedBlockchainType = dataStoreRepository.getSelectedNetwork().blockchainType
        val limit = state.config.pageSize
        return try {
            coroutineScope {
                val key = when (loadType) {
                    LoadType.REFRESH -> {
                        remoteKeyDataSource.deleteRemoteKeyByQuery(queryString ?: "")
                        null
                    }

                    LoadType.PREPEND ->
                        return@coroutineScope RemoteMediatorMediatorResultSuccess(
                            endOfPaginationReached = true
                        )

                    LoadType.APPEND -> {
                        val nextKey =
                            remoteKeyDataSource.getRemoteKeyByQuery(queryString ?: "")

                        if (nextKey == null || nextKey.end_of_pagination_reached.toBoolean()) {
                            return@coroutineScope RemoteMediatorMediatorResultSuccess(
                                endOfPaginationReached = true
                            )
                        }

                        nextKey
                    }
                }

                val importedAccountName = antelopeAccountLocalDataSource.getAccounts(
                    blockchainUid = currentSelectedBlockchainType.uid,
                    includeTempAccounts = false,
                    includeIapInitializedAccounts = false
                ).map { it.account_name }

                if (importedAccountName.isEmpty()) {
                    return@coroutineScope RemoteMediatorMediatorResultSuccess(
                        endOfPaginationReached = true
                    )
                }

                val actionsResponse = remoteDataSource.getActions(
                    blockchainType = currentSelectedBlockchainType,
                    accountName = queryString.takeIf { it.isNotNullOrBlank() },
                    filter = filter,
                    skip = key?.last_requested_page?.toInt() ?: 0,
                    limit = limit,
                    sort = sort,
                    transferTo = transferTo,
                    transferFrom = transferFrom,
                    after = after,
                    before = before,
                    receiptsReceiver = importedAccountName.joinToString(",") { it.trim() }
                )

                when {
                    actionsResponse is ApiResponse.Success -> {
                        val data = actionsResponse.body.actions ?: emptyList()
                        val isEndOfPagination = data.size < limit

                        val nextKey = key?.last_requested_page?.toInt()?.plus(limit) ?: limit

                        data
//                            .asSequence()
                            .map { action ->
                                async {
                                    // Insert transaction with imported account names for is_from_imported_wallet field
                                    val transaction = action.toAddressBookTransactionHistory(
                                        currentSelectedBlockchainType,
                                        importedAccountName
                                    )
                                    localDataSource.insertTransaction(transaction)

                                    // Link transaction to contacts like TestDataGenerator does
                                    val fromAddress = action.act?.from ?: ""
                                    val toAddress = action.act?.to ?: ""
                                    val isToImportedAccount = importedAccountName.contains(toAddress)

                                    // Find and link sender contact
                                    val linkFromContactDeferred = async {
                                        if (fromAddress.isNotEmpty() && transaction.isFromImportedWallet.not()) {
                                            val senderContact =
                                                blockchainRepository.findContactByWalletAddress(
                                                    fromAddress
                                                )
                                            senderContact?.let { contact ->
                                                // Find the wallet address entity for the contact
                                                val walletAddress =
                                                    walletAddressRepository.getWalletAddressByAddress(
                                                        fromAddress
                                                    )
                                                walletAddress?.let { wallet ->
                                                    transactionRepository.linkTransactionToContact(
                                                        contactId = contact.id,
                                                        transactionId = transaction.id,
                                                        walletAddressId = wallet.id,
                                                        isSender = false
                                                    )
                                                }
                                            }
                                        }
                                    }

                                    // Find and link receiver contact
                                    val linkToContactDeferred = async {
                                        if (toAddress.isNotEmpty() && isToImportedAccount.not()) {
                                            val receiverContact =
                                                blockchainRepository.findContactByWalletAddress(
                                                    toAddress
                                                )
                                            receiverContact?.let { contact ->
                                                // Find the wallet address entity for the contact
                                                val walletAddress =
                                                    walletAddressRepository.getWalletAddressByAddress(
                                                        toAddress
                                                    )
                                                walletAddress?.let { wallet ->
                                                    transactionRepository.linkTransactionToContact(
                                                        contactId = contact.id,
                                                        transactionId = transaction.id,
                                                        walletAddressId = wallet.id,
                                                        isSender = true
                                                    )
                                                }
                                            }
                                        }
                                    }
                                    linkToContactDeferred.await()
                                    linkFromContactDeferred.await()
                                }
                            }
//                            .toList()
                            .awaitAll()

                        remoteKeyDataSource.insertOrReplaceKey(
                            query = queryString ?: "",
                            lastRequestedPage = nextKey.toLong(),
                            lastUpdatedAt = if (loadType == LoadType.REFRESH) currentTimeInMillis()
                            else key?.last_updated_at ?: currentTimeInMillis(),
                            endOfPaginationReached = isEndOfPagination
                        )

                        RemoteMediatorMediatorResultSuccess(
                            endOfPaginationReached = isEndOfPagination
                        )
                    }

                    else -> {
                        println("Error in loading addressbook recent tx: $actionsResponse")
                        RemoteMediatorMediatorResultError(Exception("Network error in loading addressbook recent tx $actionsResponse"))
                    }
                }

            }
        } catch (e: Exception) {
            println("Error in loading addressbook recent tx: $e")
            RemoteMediatorMediatorResultError(e)
        }
    }

    override suspend fun initialize(): RemoteMediatorInitializeAction {
        val timeNow = currentTimeInMillis()
        val lastSynchedTimestamp =
            remoteKeyDataSource.getLastUpdatedTimestamp(queryString ?: "").orZero()
        val shouldRefresh =
            timeNow - lastSynchedTimestamp > RECENT_TX_CACHE_TIMEOUT_MILLIS

        return if (shouldRefresh) {
            RemoteMediatorInitializeAction.LAUNCH_INITIAL_REFRESH
        } else {
            RemoteMediatorInitializeAction.SKIP_INITIAL_REFRESH
        }
    }

    companion object {
        private const val RECENT_TX_CACHE_TIMEOUT_MILLIS = 3 * 60 * 60 * 1000L
    }
}