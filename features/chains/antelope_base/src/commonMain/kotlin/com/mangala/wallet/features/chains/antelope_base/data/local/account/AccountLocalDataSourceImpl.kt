package com.mangala.wallet.features.chains.antelope_base.data.local.account

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.mangala.wallet.features.chains.antelope_base.data.local.AntelopeDatabaseWrapper
import com.mangala.wallet.features.chains.antelope_base.domain.model.AntelopeAccount
import com.mangala.wallet.features.chains.antelope_base.domain.model.AntelopeAccountBasicInfo
import com.mangala.wallet.features.chains.antelopebase.AntelopeAccountEntity
import com.mangala.wallet.utils.ext.toLong
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

internal class AccountLocalDataSourceImpl(
    databaseWrapper: AntelopeDatabaseWrapper,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
): AccountLocalDataSource {

    private val database = databaseWrapper.instance
    private val dbQuery = database.antelopeDatabaseQueries

    override suspend fun countImportedAccounts(
        blockchainUid: String,
        includeTempAccounts: Boolean,
        includeIapInitializedAccounts: Boolean
    ): Int = withContext(ioDispatcher) {
        return@withContext if (includeTempAccounts) {
            dbQuery.countAllAccounts(
                blockchainUid,
                getExcludeCreateAccountStateFilter(includeIapInitializedAccounts)
            ).executeAsOneOrNull()?.toInt() ?: 0
        } else {
            dbQuery.countAllPermanentAccounts(
                blockchainUid,
                getExcludeCreateAccountStateFilter(includeIapInitializedAccounts)
            ).executeAsOneOrNull()?.toInt() ?: 0
        }
    }

    override suspend fun getAccounts(
        blockchainUid: String,
        includeTempAccounts: Boolean,
        includeIapInitializedAccounts: Boolean
    ): List<AntelopeAccountEntity> = withContext(ioDispatcher) {
        return@withContext if (includeTempAccounts) {
            dbQuery.selectAllAccounts(
                blockchainUid,
                getExcludeCreateAccountStateFilter(includeIapInitializedAccounts)
            ).executeAsList()
        } else {
            dbQuery.selectAllPermanentAccounts(
                blockchainUid,
                getExcludeCreateAccountStateFilter(includeIapInitializedAccounts)
            ).executeAsList()
        }
    }

    override fun getAccountsFlow(
        blockchainUid: String,
        includeTempAccounts: Boolean,
        includeIapInitializedAccounts: Boolean
    ): Flow<List<AntelopeAccountEntity>> {
        return if (includeTempAccounts) {
            dbQuery.selectAllAccounts(
                blockchainUid,
                getExcludeCreateAccountStateFilter(includeIapInitializedAccounts)
            ).asFlow().mapToList(Dispatchers.IO)
        } else {
            dbQuery.selectAllPermanentAccounts(
                blockchainUid,
                getExcludeCreateAccountStateFilter(includeIapInitializedAccounts)
            ).asFlow().mapToList(Dispatchers.IO)
        }
    }

    override fun getAccountsFlowWithBasicInfo(
        blockchainUid: String,
        includeTempAccounts: Boolean,
        includeIapInitializedAccounts: Boolean
    ): Flow<List<AntelopeAccountBasicInfo>> {
        return if (includeTempAccounts) {
            dbQuery.selectAllAccountsBasicInfo(
                blockchainUid,
                getExcludeCreateAccountStateFilter(includeIapInitializedAccounts)
            ).asFlow().mapToList(Dispatchers.IO).map {
                it.map {
                    AntelopeAccountBasicInfo(it.account_name, it.blockchain_uid)
                }
            }
        } else {
            dbQuery.selectAllPermanentAccountsBasicInfo(
                blockchainUid,
                getExcludeCreateAccountStateFilter(includeIapInitializedAccounts)
            ).asFlow().mapToList(Dispatchers.IO).map {
                it.map {
                    AntelopeAccountBasicInfo(it.account_name, it.blockchain_uid)
                }
            }
        }
    }

    override suspend fun getAccountByName(accountName: String, blockchainUid: String): AntelopeAccountEntity? = withContext(ioDispatcher) {
        return@withContext dbQuery.selectAccountByName(accountName, blockchainUid).executeAsOneOrNull()
    }

    override fun getAccountByNameFlow(
        accountName: String,
        blockchainUid: String
    ): Flow<AntelopeAccountEntity?> {
        return dbQuery.selectAccountByName(accountName, blockchainUid).asFlow().map {
            it.executeAsOneOrNull()
        }.flowOn(ioDispatcher)
    }

    override suspend fun insertAccount(accountEntity: AntelopeAccountEntity, isReplace: Boolean) = withContext(ioDispatcher) {
        if (isReplace) {
            dbQuery.insertOrReplaceAccount(
                account_name = accountEntity.account_name,
                is_active = accountEntity.is_active,
                is_temp = accountEntity.is_temp,
                create_account_state = accountEntity.create_account_state,
                purchase_token = accountEntity.purchase_token,
                blockchain_uid = accountEntity.blockchain_uid,
                last_updated = accountEntity.last_updated,
                is_notification_registered = false.toLong()
            )
        } else {
            dbQuery.resetDeletedFlag(
                account_name = accountEntity.account_name,
                blockchain_uid = accountEntity.blockchain_uid
            )
            dbQuery.insertAccount(
                account_name = accountEntity.account_name,
                is_active = accountEntity.is_active,
                is_temp = accountEntity.is_temp,
                create_account_state = accountEntity.create_account_state,
                purchase_token = accountEntity.purchase_token,
                blockchain_uid = accountEntity.blockchain_uid,
                last_updated = accountEntity.last_updated,
                is_notification_registered = false.toLong()
            )
        }
    }

    override suspend fun updateAccountStatus(
        accountName: String,
        isTemp: Boolean,
        blockchainUid: String,
        createAccountState: AntelopeAccount.CreateAccountState
    ) = withContext(ioDispatcher) {
        dbQuery.updateAccountStatus(
            is_temp = isTemp.toLong(),
            account_name = accountName,
            blockchain_uid = blockchainUid,
            create_account_state = createAccountState.name
        )
    }

    override suspend fun updateAccountStatus(
        accountName: String,
        isTemp: Boolean,
        blockchainUid: String,
        createAccountState: AntelopeAccount.CreateAccountState,
        purchaseToken: String,
        purchaseId: String
    ) = withContext(ioDispatcher) {
        dbQuery.updateAccountStatusAndPurchaseToken(
            is_temp = isTemp.toLong(),
            account_name = accountName,
            blockchain_uid = blockchainUid,
            create_account_state = createAccountState.name,
            purchase_token = purchaseToken,
            purchase_id = purchaseId
        )
    }

    override suspend fun updateNotificationRegistered(
        accountName: String,
        blockchainUid: String,
        isNotificationRegistered: Boolean
    ) = withContext(ioDispatcher) {
        dbQuery.updateNotificationRegistered(
            is_notification_registered = isNotificationRegistered.toLong(),
            account_name = accountName,
            blockchain_uid = blockchainUid
        )
    }

    override suspend fun getAccountsFailedToRegisterNotification(blockchainUid: String): List<AntelopeAccountEntity> = withContext(ioDispatcher) {
        return@withContext dbQuery.selectAccountsFailedToRegisterNotification(blockchainUid).executeAsList()
    }

    override suspend fun listSoftDeletedAccounts(blockchainUid: String): List<AntelopeAccountEntity> = withContext(ioDispatcher) {
        return@withContext dbQuery.selectSoftDeletedAccounts(blockchainUid).executeAsList()
    }

    override suspend fun updateAccount(accountEntity: AntelopeAccountEntity) = withContext(ioDispatcher) {
        updateAccountWithoutSuspend(accountEntity)
    }

    override suspend fun updateAccounts(accountEntities: List<AntelopeAccountEntity>) = withContext(ioDispatcher) {
        dbQuery.transaction {
            accountEntities.forEach {
                updateAccountWithoutSuspend(it)
            }
        }
    }

    override suspend fun deleteAccount(accountName: String, blockchainUid: String) = withContext(ioDispatcher) {
        dbQuery.deleteAccount(
            account_name = accountName,
            blockchain_uid = blockchainUid
        )
    }

    override suspend fun deleteAccounts(
        blockchainUid: String,
        lastUpdatedTimeBefore: Long,
        createAccountState: AntelopeAccount.CreateAccountState
    ) = withContext(ioDispatcher) {
        dbQuery.deleteAccounts(
            blockchain_uid = blockchainUid,
            last_updated = lastUpdatedTimeBefore,
            create_account_state = createAccountState.name
        )
    }

    override suspend fun softDeleteAccount(accountName: String, blockchainUid: String) = withContext(ioDispatcher) {
        dbQuery.softDeleteAccount(
            account_name = accountName,
            blockchain_uid = blockchainUid
        )
    }

    private fun getExcludeCreateAccountStateFilter(includeIapInitializedAccounts: Boolean): String? {
        return if (includeIapInitializedAccounts) {
            null
        } else {
            AntelopeAccount.CreateAccountState.IAP_PAYMENT_INITIALIZED.name
        }
    }

    private fun updateAccountWithoutSuspend(accountEntity: AntelopeAccountEntity) {
        dbQuery.updateAccountBalance(
            account_name = accountEntity.account_name,
            core_liquid_balance = accountEntity.core_liquid_balance,
            cpu_limit_max = accountEntity.cpu_limit_max,
            cpu_limit_available = accountEntity.cpu_limit_available,
            cpu_limit_current_used = accountEntity.cpu_limit_current_used,
            net_limit_max = accountEntity.net_limit_max,
            net_limit_available = accountEntity.net_limit_available,
            net_limit_current_used = accountEntity.net_limit_current_used,
            ram_quota = accountEntity.ram_quota,
            ram_usage = accountEntity.ram_usage,
            rex_balance = accountEntity.rex_balance,
            self_delegated_bandwidth_cpu_weight = accountEntity.self_delegated_bandwidth_cpu_weight,
            self_delegated_bandwidth_net_weight = accountEntity.self_delegated_bandwidth_net_weight,
            last_updated = accountEntity.last_updated,
            blockchain_uid = accountEntity.blockchain_uid,
            total_resources_cpu_weight = accountEntity.total_resources_cpu_weight,
            total_resources_net_weight = accountEntity.total_resources_net_weight
        )
    }
}