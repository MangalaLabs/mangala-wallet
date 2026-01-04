package com.mangala.wallet.features.chains.antelope_base.domain.usecase.multisigs.proposal

import com.mangala.wallet.features.chains.antelope_base.domain.model.multisig.AccountWeight
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.account.GetAccountInfoUseCase
import com.mangala.wallet.model.blockchain.BlockchainType


@Suppress("NAME_SHADOWING", "DEPRECATION")
class GetAccountWeightUseCase(
    private val getAccountInfoUseCase: GetAccountInfoUseCase
) {
    suspend operator fun invoke(
        blockchainType: BlockchainType,
        authorizationAccount: String,
        authorizationPermission: String,
    ): AccountWeight {
        val account =
            getAccountInfoUseCase.withResult(blockchainType, authorizationAccount).getOrNull()
                ?: return AccountWeight(emptyMap(), 0L)


        val permissions =
            account.permissions.filter { it.permissionType.permissionName == authorizationPermission }
        val threshold = permissions.first().requiredAuth.threshold.toLong()

        if (permissions.isEmpty()) {
            return AccountWeight(emptyMap(), 0L)
        }

        val keys = permissions.firstOrNull()?.requiredAuth?.keys
        val accounts = permissions.firstOrNull()?.requiredAuth?.accounts
        val waits = permissions.firstOrNull()?.requiredAuth?.waits

        val accountsMap: Map<String?, Long> = accounts?.associate { account ->
            val actor = account.permission.actor
            val permission = account.permission.permission
            val key = "$actor@$permission"
            val weight = account.weight
            key to weight
        } ?: emptyMap()

        val keysMap: Map<String?, Long> = keys?.associate { key ->
            key.key to key.weight.toLong()
        } ?: emptyMap()

        return AccountWeight(accountsMap + keysMap, threshold)
    }

}