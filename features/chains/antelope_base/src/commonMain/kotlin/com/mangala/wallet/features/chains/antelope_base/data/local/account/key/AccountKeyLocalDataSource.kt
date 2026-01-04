package com.mangala.wallet.features.chains.antelope_base.data.local.account.key

import com.mangala.wallet.features.chains.antelopebase.AntelopeAccountKeyEntity

internal interface AccountKeyLocalDataSource {
    suspend fun getSyncedAccountKeysByAccountNameAndPermissionName(accountName: String, permissionName: String, blockchainUid: String): List<AntelopeAccountKeyEntity>
    suspend fun getAccountKeysByAccountNameAndPermissionName(accountName: String, permissionName: String, blockchainUid: String): List<AntelopeAccountKeyEntity>
    suspend fun insertKey(key: AntelopeAccountKeyEntity)
    suspend fun insertKeys(keys: List<AntelopeAccountKeyEntity>)
}