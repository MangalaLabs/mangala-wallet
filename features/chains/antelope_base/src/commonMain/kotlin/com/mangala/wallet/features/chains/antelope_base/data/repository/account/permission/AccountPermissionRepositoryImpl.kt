package com.mangala.wallet.features.chains.antelope_base.data.repository.account.permission

import com.mangala.wallet.features.chains.antelope_base.data.local.account.key.AccountKeyLocalDataSource
import com.mangala.wallet.features.chains.antelope_base.data.local.account.permission.AccountPermissionLocalDataSource
import com.mangala.wallet.features.chains.antelope_base.data.repository.account.mapper.toAntelopeAccountKeyEntity
import com.mangala.wallet.features.chains.antelope_base.data.repository.account.mapper.toAntelopeKey
import com.mangala.wallet.features.chains.antelope_base.domain.model.AntelopeAccountPermission
import com.mangala.wallet.features.chains.antelope_base.domain.repository.AccountPermissionRepository

internal class AccountPermissionRepositoryImpl(
    private val accountPermissionLocalDataSource: AccountPermissionLocalDataSource,
    private val accountKeyLocalDataSource: AccountKeyLocalDataSource
): AccountPermissionRepository {

    override suspend fun getAccountPermissionsByAccountName(
        accountName: String,
        blockchainUid: String
    ): List<AntelopeAccountPermission> {
        val accountPermissions = accountPermissionLocalDataSource.getAccountPermissionsByAccountName(accountName, blockchainUid)
        return accountPermissions.map { accountPermission ->
            val keys = accountKeyLocalDataSource.getAccountKeysByAccountNameAndPermissionName(
                accountName = accountName,
                permissionName = accountPermission.permission_name,
                blockchainUid = blockchainUid
            )
            accountPermission.toAntelopeAccountPermission(keys.map { it.toAntelopeKey() })
        }
    }

    override suspend fun getAccountPermissionsByAccountNameAndPermissionName(
        accountName: String,
        permissionName: String,
        blockchainUid: String
    ): AntelopeAccountPermission? {
        val accountPermission =
            accountPermissionLocalDataSource.getAccountPermissionByAccountAndPermissionName(
                accountName = accountName,
                permissionName = permissionName,
                blockchainUid = blockchainUid
            ) ?: return null
        val keys = accountKeyLocalDataSource.getAccountKeysByAccountNameAndPermissionName(
            accountName = accountName,
            permissionName = permissionName,
            blockchainUid = blockchainUid
        )
        return accountPermission.toAntelopeAccountPermission(keys.map { it.toAntelopeKey() })
    }

    override suspend fun insertAccountPermission(
        accountPermission: AntelopeAccountPermission,
        accountName: String,
        blockchainUid: String
    ) {
        val keys = accountPermission.requiredAuth.keys
        accountPermissionLocalDataSource.insertPermission(
            accountPermission.toAntelopeAccountPermissionEntity(
                accountName,
                blockchainUid
            )
        )
        accountKeyLocalDataSource.insertKeys(keys.map {
            it.toAntelopeAccountKeyEntity(
                accountName,
                accountPermission.permissionType.permissionName,
                blockchainUid
            )
        })
    }

    override suspend fun insertAccountPermissions(
        accountPermissions: List<AntelopeAccountPermission>,
        accountName: String,
        blockchainUid: String
    ) {
        accountPermissions.forEach { insertAccountPermission(it, accountName, blockchainUid) }
    }
}