package com.mangala.wallet.features.chains.antelope_base.data.local.account.permission

import com.mangala.wallet.features.chains.antelope_base.data.local.AntelopeDatabaseWrapper
import com.mangala.wallet.features.chains.antelopebase.AntelopeAccountPermissionEntity
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

internal class AccountPermissionLocalDataSourceImpl(
    databaseWrapper: AntelopeDatabaseWrapper,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
): AccountPermissionLocalDataSource {

    private val database = databaseWrapper.instance
    private val dbQuery = database.antelopeDatabaseQueries

    override suspend fun getAccountPermissionByAccountAndPermissionName(
        accountName: String,
        permissionName: String,
        blockchainUid: String
    ): AntelopeAccountPermissionEntity? = withContext(ioDispatcher) {
        return@withContext dbQuery.selectAccountPermissionByAccountAndPermissionName(accountName, permissionName, blockchainUid).executeAsOneOrNull()
    }

    override suspend fun getAccountPermissionsByAccountName(accountName: String, blockchainUid: String): List<AntelopeAccountPermissionEntity> = withContext(ioDispatcher) {
        return@withContext dbQuery.selectAccountPermissionsByAccountName(accountName, blockchainUid).executeAsList()
    }

    override suspend fun insertPermission(permission: AntelopeAccountPermissionEntity) = withContext(ioDispatcher) {
        dbQuery.insertAccountPermission(
            account_name = permission.account_name,
            permission_name = permission.permission_name,
            parent = permission.parent,
            blockchain_uid = permission.blockchain_uid
        )
    }
}