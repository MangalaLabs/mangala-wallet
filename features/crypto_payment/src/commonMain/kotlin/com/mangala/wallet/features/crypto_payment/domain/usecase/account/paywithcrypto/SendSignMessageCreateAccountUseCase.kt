package com.mangala.wallet.features.crypto_payment.domain.usecase.account.paywithcrypto

import com.mangala.wallet.features.crypto_payment.domain.model.account.paywithcrypto.SignMessageRequest
import com.mangala.wallet.features.crypto_payment.domain.model.account.paywithcrypto.SignMessageResponse
import com.mangala.wallet.features.crypto_payment.domain.repository.account.paywithcrypto.EosAccountRepository
import com.mangala.wallet.model.blockchain.BlockchainType
import com.mangala.wallet.remote.di.ApiResponse

class SendSignMessageCreateAccountUseCase(private val repository: EosAccountRepository) {
    suspend operator fun invoke(blockchainType: BlockchainType, request: SignMessageRequest): SignMessageResponse? {
        return repository.signMessage(blockchainType, request).let {
            if (it is ApiResponse.Success) {
                it.body
            } else {
                null
            }
        }
    }
}