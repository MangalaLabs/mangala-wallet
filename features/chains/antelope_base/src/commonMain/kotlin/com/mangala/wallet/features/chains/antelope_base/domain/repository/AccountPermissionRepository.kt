package com.mangala.wallet.features.chains.antelope_base.domain.repository

import com.mangala.wallet.features.chains.antelope_base.domain.model.AntelopeAccountPermission

interface AccountPermissionRepository {
    suspend fun getAccountPermissionsByAccountName(accountName: String, blockchainUid: String): List<AntelopeAccountPermission>
    suspend fun getAccountPermissionsByAccountNameAndPermissionName(accountName: String, permissionName: String, blockchainUid: String): AntelopeAccountPermission?
    suspend fun insertAccountPermission(accountPermission: AntelopeAccountPermission, accountName: String, blockchainUid: String)
    suspend fun insertAccountPermissions(accountPermissions: List<AntelopeAccountPermission>, accountName: String, blockchainUid: String)
}