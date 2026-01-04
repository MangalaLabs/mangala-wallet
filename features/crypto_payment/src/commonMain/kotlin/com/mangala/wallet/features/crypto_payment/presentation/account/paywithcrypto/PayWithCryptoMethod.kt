package com.mangala.wallet.features.crypto_payment.presentation.account.paywithcrypto

import com.mangala.wallet.model.blockchain.BlockchainType

class PayWithCryptoMethod {
    companion object {
        fun listSupportedNetwork() : List<BlockchainType> {
            return listOf(
                BlockchainType.BinanceSmartChainTestNet
            )
        }
    }
}