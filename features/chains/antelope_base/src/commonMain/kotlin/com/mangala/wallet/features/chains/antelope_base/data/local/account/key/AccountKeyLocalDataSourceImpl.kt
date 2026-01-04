package com.mangala.wallet.features.chains.antelope_base.data.local.account.key

import com.benasher44.uuid.uuid4
import com.mangala.wallet.features.chains.antelope_base.data.local.AntelopeDatabaseWrapper
import com.mangala.wallet.features.chains.antelopebase.AntelopeAccountKeyEntity
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

internal class AccountKeyLocalDataSourceImpl(
    databaseWrapper: AntelopeDatabaseWrapper,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
): AccountKeyLocalDataSource {

    private val database = databaseWrapper.instance
    private val dbQuery = database.antelopeDatabaseQueries

    override suspend fun getSyncedAccountKeysByAccountNameAndPermissionName(
        accountName: String,
        permissionName: String,
        blockchainUid: String
    ): List<AntelopeAccountKeyEntity> = withContext(ioDispatcher) {
        return@withContext dbQuery.selectSyncedAccountKeysByAccountNameAndPermissionName(
            account_name = accountName,
            permission_name = permissionName,
            blockchain_uid = blockchainUid
        ).executeAsList()
    }

    override suspend fun getAccountKeysByAccountNameAndPermissionName(
        accountName: String,
        permissionName: String,
        blockchainUid: String
    ): List<AntelopeAccountKeyEntity> = withContext(ioDispatcher) {
        return@withContext dbQuery.selectAccountKeyByAccountNameAndPermissionName(
            account_name = accountName,
            permission_name = permissionName,
            blockchain_uid = blockchainUid
        ).executeAsList()
    }

    override suspend fun insertKeys(keys: List<AntelopeAccountKeyEntity>) = withContext(ioDispatcher) {
        dbQuery.transaction {
            keys.forEach {
                insertKeyWithoutSuspend(it)
            }
        }
    }

    override suspend fun insertKey(key: AntelopeAccountKeyEntity) = withContext(ioDispatcher) {
        insertKeyWithoutSuspend(key)
    }

    private fun insertKeyWithoutSuspend(key: AntelopeAccountKeyEntity) {
        dbQuery.insertAccountKey(
            id = uuid4().toString(),
            public_key = key.public_key,
            weight = key.weight,
            account_name = key.account_name,
            permission_name = key.permission_name,
            is_synced = key.is_synced,
            blockchain_uid = key.blockchain_uid
        )
    }
}