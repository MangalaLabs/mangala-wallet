package com.mangala.wallet.features.chains.antelope_base.data.remote.createaccount

import com.mangala.wallet.features.chains.antelope_base.data.remote.createaccount.model.CreateAccountResponse
import com.mangala.wallet.model.blockchain.BlockchainType
import com.mangala.wallet.remote.di.ApiResponse
import com.mangala.wallet.remote.network.CustomError

expect class CreateAccountFirebaseFunctionDataSource() {

    suspend fun createAccount(
        accountName: String,
        blockchainType: BlockchainType,
        purchaseToken: String,
        activePublicKey: String,
        ownerPublicKey: String
    ): ApiResponse<CreateAccountResponse, CustomError>
}

const val CREATE_ACCOUNT_FUNCTION_JUNGLE_TESTNET =
    "https://us-central1-mangala-wallet-cb7c1.cloudfunctions.net/eosAccountTestnet"
const val CREATE_ACCOUNT_FUNCTION_EOS_MAINNET_URL =
    "https://asia-east2-mangala-wallet-cb7c1.cloudfunctions.net/eosAccountMainet"