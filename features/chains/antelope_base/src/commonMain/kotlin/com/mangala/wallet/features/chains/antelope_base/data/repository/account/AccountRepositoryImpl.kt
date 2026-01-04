package com.mangala.wallet.features.chains.antelope_base.data.repository.account

import com.mangala.antelope.base.api.model.GetAccountRequest
import com.mangala.antelope.base.api.model.GetAccountsByAuthorizersRequest
import com.mangala.antelope.base.api.remote.AntelopeRemoteDataSource
import com.mangala.wallet.features.chains.antelope_base.data.local.account.AccountLocalDataSource
import com.mangala.wallet.features.chains.antelope_base.data.repository.account.mapper.toAccount
import com.mangala.wallet.features.chains.antelope_base.data.repository.account.mapper.toAntelopeAccountByAuthorizer
import com.mangala.wallet.features.chains.antelope_base.data.repository.account.mapper.toEntity
import com.mangala.wallet.features.chains.antelope_base.domain.model.AntelopeAccount
import com.mangala.wallet.features.chains.antelope_base.domain.model.AntelopeAccountBasicInfo
import com.mangala.wallet.features.chains.antelope_base.domain.model.account.AntelopeAccountByAuthorizer
import com.mangala.wallet.features.chains.antelope_base.domain.repository.AccountRepository
import com.mangala.wallet.features.chains.antelopebase.AntelopeAccountEntity
import com.mangala.wallet.model.blockchain.BlockchainType
import com.mangala.wallet.model.util.Resource
import com.mangala.wallet.remote.di.ApiResponse
import com.mangala.wallet.remote.utils.cachedResource
import com.mangala.wallet.remote.utils.networkBoundResource
import com.mangala.wallet.utils.ext.toLong
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Clock

internal class AccountRepositoryImpl(
    private val accountLocalDataSource: AccountLocalDataSource,
    private val remoteDataSource: AntelopeRemoteDataSource
) : AccountRepository {
    override suspend fun countImportedAccounts(
        blockchainType: BlockchainType,
        includeTempAccounts: Boolean,
        includeIapInitializedAccounts: Boolean
    ): Int =
        accountLocalDataSource.countImportedAccounts(
            blockchainUid = blockchainType.uid,
            includeTempAccounts = includeTempAccounts,
            includeIapInitializedAccounts = includeIapInitializedAccounts
        )

    override suspend fun getAccountsByAuthorizers(
        key: String,
        blockchainType: BlockchainType
    ): Result<List<AntelopeAccountByAuthorizer>> {
        val result = remoteDataSource.getAccountsByAuthorizers(
            blockchainType,
            GetAccountsByAuthorizersRequest(keys = listOf(key), accounts = emptyList())
        )

        return if (result is ApiResponse.Success) {
            val mappedResponse = result.body.toAntelopeAccountByAuthorizer(blockchainType.uid)

            if (mappedResponse == null) {
                Result.failure(Exception("Failed to get accounts by authorizers"))
            } else {
                Result.success(mappedResponse)
            }
        } else {
            Result.failure(Exception("Failed to get accounts by authorizers"))
        }
    }

    override suspend fun updateAccountStatus(
        accountName: String,
        isTemp: Boolean,
        blockchainType: BlockchainType,
        createAccountState: AntelopeAccount.CreateAccountState
    ) {
        return accountLocalDataSource.updateAccountStatus(
            accountName,
            isTemp,
            blockchainType.uid,
            createAccountState
        )
    }

    override suspend fun updateAccountStatus(
        accountName: String,
        isTemp: Boolean,
        blockchainType: BlockchainType,
        createAccountState: AntelopeAccount.CreateAccountState,
        purchaseToken: String,
        purchaseId: String
    ) {
        return accountLocalDataSource.updateAccountStatus(
            accountName,
            isTemp,
            blockchainType.uid,
            createAccountState,
            purchaseToken,
            purchaseId
        )
    }

    override suspend fun updateNotificationRegistered(
        accountName: String,
        blockchainType: BlockchainType,
        isNotificationRegistered: Boolean
    ) {
        return accountLocalDataSource.updateNotificationRegistered(
            accountName,
            blockchainType.uid,
            isNotificationRegistered
        )
    }

    override suspend fun getAccountsFailedToRegisterNotification(blockchainType: BlockchainType): List<AntelopeAccount> =
        accountLocalDataSource
            .getAccountsFailedToRegisterNotification(blockchainType.uid)
            .map { it.toAccount() }

    override suspend fun listSoftDeletedAccounts(blockchainType: BlockchainType): List<AntelopeAccount> =
        accountLocalDataSource
            .listSoftDeletedAccounts(blockchainType.uid)
            .map { it.toAccount() }

    override suspend fun getAccountWithBalanceInfo(
        accountName: String,
        blockchainType: BlockchainType,
        forceRefresh: Boolean
    ): Result<AntelopeAccount?> {
        return cachedResource(
            query = { accountLocalDataSource.getAccountByName(accountName, blockchainType.uid) },
            fetch = {
                remoteDataSource.getAccount(blockchainType, GetAccountRequest(accountName))
            },
            saveFetchResult = {
                accountLocalDataSource.updateAccount(it.toEntity(blockchainType.uid))
            },
            shouldFetch = { cachedResponse -> shouldFetch(cachedResponse, forceRefresh) },
            entityToDomain = { it?.toAccount() }
        )
    }

    override suspend fun getAccountWithBalanceInfoFlow(
        accountName: String,
        blockchainType: BlockchainType,
        includeIapInitializedAccounts: Boolean,
        forceRefresh: Boolean
    ): Flow<Resource<AntelopeAccount?>> {
        return networkBoundResource(
            query = {
                accountLocalDataSource.getAccountByNameFlow(accountName, blockchainType.uid)
            },
            fetch = {
                remoteDataSource.getAccount(blockchainType, GetAccountRequest(accountName))
            },
            saveFetchResult = { dto ->
                dto?.let {
                    accountLocalDataSource.updateAccount(dto.toEntity(blockchainType.uid))
                }
            },
            shouldFetch = { cachedResponse -> shouldFetch(cachedResponse, forceRefresh) },
            entityToDomain = { it?.toAccount() },
        )
    }

    override suspend fun getAccountsWithBalanceInfoFlow(
        accountNames: List<String>,
        blockchainType: BlockchainType,
        includeTempAccounts: Boolean,
        includeIapInitializedAccounts: Boolean,
        forceRefresh: Boolean
    ): Flow<Resource<List<AntelopeAccount>?>> {
        return networkBoundResource(
            query = {
                accountLocalDataSource.getAccountsFlow(
                    blockchainUid = blockchainType.uid,
                    includeTempAccounts = includeTempAccounts,
                    includeIapInitializedAccounts = includeIapInitializedAccounts
                )
            },
            fetch = { cachedData ->
                coroutineScope {
                    val accountsToLoad = if (forceRefresh) {
                        accountNames
                    } else {
                        val cachedAccountNames = cachedData.map { it.account_name }
                        val accountsWithoutBalanceData =
                            cachedData.filter { it.cpu_limit_available == null }
                                .map { it.account_name }
                        accountNames.filter { accountName ->
                            accountName !in cachedAccountNames || accountName in accountsWithoutBalanceData
                        }
                    }

                    val accountAsync = accountsToLoad.map { accountName ->
                        async {
                            remoteDataSource.getAccount(
                                blockchainType,
                                GetAccountRequest(accountName)
                            )
                        }
                    }
                    val result = accountAsync.awaitAll()
                    if (result.any { it is ApiResponse.Success }) {
                        ApiResponse.Success(result.mapNotNull { it as? ApiResponse.Success })
                    } else {
                        ApiResponse.Error.NetworkError(Exception("Failed to get account with balance info"))
                    }
                }
            },
            saveFetchResult = { dto ->
                dto?.let {
                    accountLocalDataSource.updateAccounts(dto.map { it.body.toEntity(blockchainType.uid) })
                }
            },
            shouldFetch = { cachedResponse ->
                shouldFetch(
                    accountNames,
                    cachedResponse,
                    forceRefresh
                )
            },
            entityToDomain = { it.map { it.toAccount() } },
        )
    }

    override suspend fun getAccount(
        blockchainType: BlockchainType,
        accountName: String
    ): Result<AntelopeAccount> {
        val response = remoteDataSource.getAccount(blockchainType, GetAccountRequest(accountName))

        if (response is ApiResponse.Success) {
            return Result.success(response.body.toAccount())
        }

        return Result.failure(Exception("Failed to get account"))
    }

    override suspend fun getAccounts(
        blockchainType: BlockchainType,
        includeTempAccounts: Boolean,
        includeIapInitializedAccounts: Boolean
    ): List<AntelopeAccount> {
        return accountLocalDataSource.getAccounts(
            blockchainUid = blockchainType.uid,
            includeTempAccounts = includeTempAccounts,
            includeIapInitializedAccounts = includeIapInitializedAccounts
        ).map { it.toAccount() }
    }

    override fun invokeWithBasicInfo(
        includeTempAccounts: Boolean,
        includeIapInitializedAccounts: Boolean,
        blockchainType: BlockchainType
    ): Flow<List<AntelopeAccountBasicInfo>> {
        return accountLocalDataSource.getAccountsFlowWithBasicInfo(
            blockchainType.uid,
            includeTempAccounts,
            includeIapInitializedAccounts
        )
    }

    override fun getAccountsFlow(
        blockchainType: BlockchainType,
        includeTempAccounts: Boolean,
        includeIapInitializedAccounts: Boolean
    ): Flow<List<AntelopeAccount>> {
        return accountLocalDataSource.getAccountsFlow(
            blockchainType.uid,
            includeTempAccounts,
            includeIapInitializedAccounts
        )
            .map { it.map { accountEntity -> accountEntity.toAccount() } }
    }

    override suspend fun getAccountByName(
        accountName: String,
        blockchainType: BlockchainType
    ): AntelopeAccount? {
        return accountLocalDataSource.getAccountByName(accountName, blockchainType.uid)?.toAccount()
    }

    override suspend fun insertAccount(
        account: AntelopeAccount,
        blockchainType: BlockchainType,
        isReplace: Boolean
    ) {
        val accountEntity = AntelopeAccountEntity(
            account_name = account.accountName,
            is_active = account.isActive.toLong(),
            is_temp = account.isTemp.toLong(),
            create_account_state = account.createAccountState.name,
            purchase_token = account.purchaseToken,
            purchase_id = account.purchaseId,
            blockchain_uid = blockchainType.uid,
            core_liquid_balance = null,
            cpu_limit_max = null,
            cpu_limit_available = null,
            cpu_limit_current_used = null,
            net_limit_max = null,
            net_limit_available = null,
            net_limit_current_used = null,
            ram_quota = null,
            ram_usage = null,
            rex_balance = null,
            self_delegated_bandwidth_cpu_weight = null,
            self_delegated_bandwidth_net_weight = null,
            total_resources_cpu_weight = null,
            total_resources_net_weight = null,
            last_updated = Clock.System.now().toEpochMilliseconds(),
            is_notification_registered = account.isNotificationRegistered.toLong(),
            is_deleted = account.isDeleted.toLong()
        )

        accountLocalDataSource.insertAccount(
            accountEntity = accountEntity,
            isReplace = isReplace
        )
    }

    override suspend fun deleteAccount(accountName: String, blockchainType: BlockchainType) {
        accountLocalDataSource.deleteAccount(
            accountName = accountName,
            blockchainUid = blockchainType.uid
        )
    }

    override suspend fun deleteAccounts(
        blockchainType: BlockchainType,
        lastUpdatedTimeBefore: Long,
        createAccountState: AntelopeAccount.CreateAccountState
    ) {
        accountLocalDataSource.deleteAccounts(
            blockchainUid = blockchainType.uid,
            lastUpdatedTimeBefore = lastUpdatedTimeBefore,
            createAccountState = createAccountState
        )
    }

    override suspend fun softDeleteAccount(accountName: String, blockchainType: BlockchainType) =
        accountLocalDataSource.softDeleteAccount(accountName, blockchainType.uid)

    private fun shouldFetch(
        accountNamesToLoad: List<String>,
        cachedResponse: List<AntelopeAccountEntity>,
        forceRefresh: Boolean
    ): Boolean {
        val cachedAccountNames = cachedResponse.map { it.account_name }
        return accountNamesToLoad.any { it !in cachedAccountNames } || cachedResponse.any { it.cpu_limit_available == null } || forceRefresh || cachedResponse.any {
            isCacheExpired(
                it
            )
        }
    }

    private fun shouldFetch(
        cachedResponse: AntelopeAccountEntity?,
        forceRefresh: Boolean
    ): Boolean {
        return cachedResponse?.cpu_limit_available == null || forceRefresh || isCacheExpired(
            cachedResponse
        )
    }

    private fun isCacheExpired(cachedResponse: AntelopeAccountEntity): Boolean {
        return cachedResponse.last_updated + CACHE_EXPIRATION_TIME < Clock.System.now()
            .toEpochMilliseconds()
    }

    companion object {
        private const val CACHE_EXPIRATION_TIME = 1000 * 60 * 5 // 5 minutes
    }
}