package com.mangala.wallet.features.crypto_payment.domain.usecase.account.paywithcrypto

import com.mangala.wallet.features.chains.evmcompatible.model.Address
import com.mangala.wallet.model.blockchain.Chain


class GetCryptoPaymentContractAddressUseCase {

    fun invoke(chain: Chain): Address {
        val routerAddress = getRouterAddress(chain)
        return Address(routerAddress)
    }

    sealed class UnsupportedChainError : Throwable() {
        object NoRouterAddress : UnsupportedChainError()
    }

    companion object {
        private fun getRouterAddress(chain: Chain) =
            when (chain) {
                Chain.BinanceSmartChainTestNet -> "0x3351e5896BEb8EaeDe16A4885Ad41596c01D96D1"
                Chain.EthereumHolesky -> "0x647C747af6A43784381bcFE469727232C795AAB3"
                else -> throw UnsupportedChainError.NoRouterAddress
            }
    }

}
