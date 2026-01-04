package com.mangala.wallet.features.crypto_payment.domain.repository.account.paywithcrypto

import com.mangala.wallet.features.crypto_payment.data.remote.account.paywithcrypto.EosAccountDataSource
import com.mangala.wallet.features.crypto_payment.data.remote.account.paywithcrypto.PayWithCryptoError
import com.mangala.wallet.features.crypto_payment.domain.model.account.paywithcrypto.CreateEosAccountRequest
import com.mangala.wallet.features.crypto_payment.domain.model.account.paywithcrypto.SignMessageRequest
import com.mangala.wallet.features.crypto_payment.domain.model.account.paywithcrypto.SignMessageResponse
import com.mangala.wallet.model.blockchain.BlockchainType
import com.mangala.wallet.remote.di.ApiResponse

class EosAccountRepositoryImpl(private val dataSource: EosAccountDataSource): EosAccountRepository {
    override suspend fun signMessage(
        blockchainType: BlockchainType,
        request: SignMessageRequest
    ): ApiResponse<SignMessageResponse, PayWithCryptoError> {
        return dataSource.signMessage(blockchainType, request)
    }

    override suspend fun createAccount(
        blockchainType: BlockchainType,
        deviceId: String,
        request: CreateEosAccountRequest
    ): ApiResponse<Unit, PayWithCryptoError> {
        return dataSource.createAccount(blockchainType, deviceId, request)
    }
}