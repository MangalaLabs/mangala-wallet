package com.mangala.wallet.features.chains.antelope_base.domain.repository.createaccount

import com.mangala.wallet.model.blockchain.BlockchainType

interface CreateAccountRepository {
    suspend fun createAccount(
        accountName: String,
        blockchainType: BlockchainType,
        purchaseToken: String,
        activePublicKey: String,
        ownerPublicKey: String
    ): Result<String>

    fun isInBlackListAccountName(accountName: String): Result<Boolean>
}