package com.mangala.wallet.features.crypto_payment.domain.usecase.account.paywithcrypto

import com.mangala.wallet.features.crypto_payment.domain.model.account.paywithcrypto.CreateEosAccountRequest
import com.mangala.wallet.features.crypto_payment.domain.repository.account.paywithcrypto.EosAccountRepository
import com.mangala.wallet.model.blockchain.BlockchainType
import com.mangala.wallet.remote.di.ApiResponse

class CreateEosAccountUseCase(private val repository: EosAccountRepository) {
    suspend operator fun invoke(
        blockchainType: BlockchainType,
        deviceId: String,
        request: CreateEosAccountRequest
    ): Result<String> {
        return repository.createAccount(blockchainType, deviceId, request).let {
            if (it is ApiResponse.Success) {
                Result.success("Account created successfully")
            } else {
                Result.failure(Exception("Account creation failed"))
            }
        }
    }
}