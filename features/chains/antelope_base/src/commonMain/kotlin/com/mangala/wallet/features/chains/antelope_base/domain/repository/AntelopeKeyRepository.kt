package com.mangala.wallet.features.chains.antelope_base.domain.repository

import com.mangala.wallet.features.chains.antelope_base.domain.model.AntelopeKey

interface AntelopeKeyRepository {
    suspend fun getSyncedKeysByAccountNameAndPermissionName(accountName: String, permissionName: String, blockchainUid: String): List<AntelopeKey>
}