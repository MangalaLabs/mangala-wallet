package com.mangala.wallet.features.chains.evmcompatible.domain.usecases

import com.mangala.wallet.core.hdwallet.domain.model.HDKey
import com.mangala.wallet.features.chains.evmcompatible.core.TransactionSigner
import com.mangala.wallet.model.blockchain.Chain

class SignPersonalMessageUseCase {

    suspend operator fun invoke(
        hdKey: HDKey,
        chain: Chain,
        message: ByteArray
    ): ByteArray{
        val transactionSigner = TransactionSigner(hdKey.privateKey, chain.id)
        return transactionSigner.signaturePersonalMessage(message)
    }
}