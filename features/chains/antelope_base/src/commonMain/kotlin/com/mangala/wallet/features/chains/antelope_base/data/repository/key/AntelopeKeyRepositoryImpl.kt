package com.mangala.wallet.features.chains.antelope_base.data.repository.key

import com.mangala.wallet.features.chains.antelope_base.data.local.account.key.AccountKeyLocalDataSource
import com.mangala.wallet.features.chains.antelope_base.data.repository.account.mapper.toAntelopeKey
import com.mangala.wallet.features.chains.antelope_base.domain.model.AntelopeKey
import com.mangala.wallet.features.chains.antelope_base.domain.repository.AntelopeKeyRepository

internal class AntelopeKeyRepositoryImpl(private val keyLocalDataSource: AccountKeyLocalDataSource): AntelopeKeyRepository {
    override suspend fun getSyncedKeysByAccountNameAndPermissionName(
        accountName: String,
        permissionName: String,
        blockchainUid: String
    ): List<AntelopeKey> {
        return keyLocalDataSource.getSyncedAccountKeysByAccountNameAndPermissionName(
            accountName = accountName,
            permissionName = permissionName,
            blockchainUid = blockchainUid
        ).map {
            it.toAntelopeKey()
        }
    }
}