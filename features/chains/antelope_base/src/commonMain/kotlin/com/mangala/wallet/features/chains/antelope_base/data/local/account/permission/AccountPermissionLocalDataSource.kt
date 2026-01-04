package com.mangala.wallet.features.chains.antelope_base.data.local.account.permission

import com.mangala.wallet.features.chains.antelopebase.AntelopeAccountPermissionEntity

internal interface AccountPermissionLocalDataSource {
    suspend fun getAccountPermissionByAccountAndPermissionName(accountName: String, permissionName: String, blockchainUid: String): AntelopeAccountPermissionEntity?
    suspend fun getAccountPermissionsByAccountName(accountName: String, blockchainUid: String): List<AntelopeAccountPermissionEntity>
    suspend fun insertPermission(permission: AntelopeAccountPermissionEntity)
}