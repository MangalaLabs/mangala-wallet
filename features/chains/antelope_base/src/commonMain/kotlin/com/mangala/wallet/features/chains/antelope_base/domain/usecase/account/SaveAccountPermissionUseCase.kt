package com.mangala.wallet.features.chains.antelope_base.domain.usecase.account

import com.mangala.wallet.antelope_key_manager.EosKeyManager
import com.mangala.wallet.features.chains.antelope_base.domain.model.AntelopeAccountPermission
import com.mangala.wallet.features.chains.antelope_base.domain.repository.AccountPermissionRepository
import com.memtrip.eos.core.crypto.EosPrivateKey

class SaveAccountPermissionUseCase(
    private val accountPermissionRepository: AccountPermissionRepository,
    private val eosKeyManager: EosKeyManager
) {

    suspend fun saveActivePermission(
        privateKey: EosPrivateKey,
        accountName: String,
        blockchainUid: String
    ) {
        val permission = AntelopeAccountPermission.createInitialActivePermission(
            privateKey.publicKey.toString(),
            true
        )
        savePermission(privateKey, permission, accountName, blockchainUid)
    }

    suspend fun saveOwnerPermission(privateKey: EosPrivateKey, accountName: String, blockchainUid: String) {
        val permission = AntelopeAccountPermission.createInitialOwnerPermission(
            privateKey.publicKey.toString(),
            true
        )
        savePermission(privateKey, permission, accountName, blockchainUid)
    }

    private suspend fun savePermission(
        privateKey: EosPrivateKey,
        accountPermission: AntelopeAccountPermission,
        accountName: String,
        blockchainUid: String
    ) {
        eosKeyManager.importPrivateKey(privateKey)
        accountPermissionRepository.insertAccountPermission(
            accountPermission,
            accountName,
            blockchainUid
        )
    }
}