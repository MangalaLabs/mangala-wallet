package com.mangala.wallet.features.chains.antelope_base.domain.usecase.account

import com.mangala.wallet.antelope_key_manager.EosKeyManager
import com.mangala.wallet.domain.datastore.usecases.GetSelectedNetworkUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.repository.AccountPermissionRepository
import com.mangala.wallet.model.blockchain.BlockchainType
import com.memtrip.eos.core.crypto.EosPrivateKey

class GetAccountPrivateKeyUseCase(
    private val accountPermissionRepository: AccountPermissionRepository,
    private val eosKeyManager: EosKeyManager,
    private val getSelectedNetworkUseCase: GetSelectedNetworkUseCase
) {
    suspend operator fun invoke(
        account: String,
        permissionName: String,
        blockchainType: BlockchainType? = null
    ): EosPrivateKey? {
        val blockchainUid = blockchainType?.uid ?: getSelectedNetworkUseCase().blockChainUid

        val accountPermissionDetail =
            accountPermissionRepository.getAccountPermissionsByAccountNameAndPermissionName(
                account, permissionName, blockchainUid
            ) ?: run {
                return null
            }
        val accountKey = accountPermissionDetail.requiredAuth.keys
        val accountPublicKeyExisted =
            accountKey.firstOrNull { eosKeyManager.publicKeyExists(it.key) } ?: return null
        return eosKeyManager.getPrivateKey(accountPublicKeyExisted.key)
    }

    suspend fun getAccountPrivateKeyByPublicKey(publicKey: String): EosPrivateKey? {
        val accountPublicKeyExisted =
            eosKeyManager.publicKeyExists(publicKey)
        return if (accountPublicKeyExisted) eosKeyManager.getPrivateKey(publicKey) else null
    }
}