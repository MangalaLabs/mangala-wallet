package com.mangala.wallet.features.chains.antelope_base.domain.usecase.account

import com.mangala.wallet.antelope_key_manager.EosKeyManager
import com.mangala.wallet.domain.datastore.usecases.GetSelectedNetworkUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.model.AntelopeKey
import com.mangala.wallet.features.chains.antelope_base.domain.repository.AccountPermissionRepository

class ListAccountPublicKeysUseCase(
    private val accountPermissionRepository: AccountPermissionRepository,
    private val eosKeyManager: EosKeyManager,
    private val getSelectedNetworkUseCase: GetSelectedNetworkUseCase
) {
    suspend operator fun invoke(account: String, permissionName: String): List<AntelopeKey> {
        val blockchainUid = getSelectedNetworkUseCase().blockChainUid

        val accountPermissionDetail =
            accountPermissionRepository.getAccountPermissionsByAccountNameAndPermissionName(
                account, permissionName, blockchainUid
            ) ?: run {
                return emptyList()
            }
        val accountKey = accountPermissionDetail.requiredAuth.keys
        return accountKey.filter { eosKeyManager.publicKeyExists(it.key) }
    }
}