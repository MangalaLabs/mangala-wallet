package com.mangala.wallet.features.crypto_payment.domain.repository.account.paywithcrypto
import com.mangala.wallet.features.crypto_payment.data.remote.account.paywithcrypto.PayWithCryptoError
import com.mangala.wallet.features.crypto_payment.domain.model.account.paywithcrypto.CreateEosAccountRequest
import com.mangala.wallet.features.crypto_payment.domain.model.account.paywithcrypto.SignMessageRequest
import com.mangala.wallet.features.crypto_payment.domain.model.account.paywithcrypto.SignMessageResponse
import com.mangala.wallet.model.blockchain.BlockchainType
import com.mangala.wallet.remote.di.ApiResponse


interface EosAccountRepository {
    suspend fun signMessage(
        blockchainType: BlockchainType,
        request: SignMessageRequest
    ): ApiResponse<SignMessageResponse, PayWithCryptoError>
    suspend fun createAccount(
        blockchainType: BlockchainType,
        deviceId: String,
        request: CreateEosAccountRequest
    ): ApiResponse<Unit, PayWithCryptoError>
}