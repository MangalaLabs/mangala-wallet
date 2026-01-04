package com.mangala.wallet.features.chains.antelope_base.data.remote.createaccount

import com.mangala.wallet.features.chains.antelope_base.data.remote.createaccount.model.CreateAccountResponse
import com.mangala.wallet.model.blockchain.BlockchainType
import com.mangala.wallet.remote.di.ApiResponse
import com.mangala.wallet.remote.network.CustomError

class CreateAccountRemoteDataSource(
    private val createAccountFirebaseFunctionDataSource: CreateAccountFirebaseFunctionDataSource
) {

    suspend fun createAccount(
        accountName: String,
        blockchainType: BlockchainType,
        purchaseToken: String,
        activePublicKey: String,
        ownerPublicKey: String
    ): ApiResponse<CreateAccountResponse, CustomError> {
        return createAccountFirebaseFunctionDataSource.createAccount(
            accountName = accountName,
            blockchainType = blockchainType,
            purchaseToken = purchaseToken,
            activePublicKey = activePublicKey,
            ownerPublicKey = ownerPublicKey
        )
    }
}