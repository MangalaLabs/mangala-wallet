package com.mangala.wallet.features.crypto_payment.data.remote.account.paywithcrypto

import com.mangala.wallet.model.blockchain.BlockchainType
import com.mangala.wallet.remote.di.provideKtorfit
import io.ktor.client.engine.HttpClientEngine

const val EOS_TESTNET_ACCOUNT_BASE_URL = "http://89.147.102.6:8000/"

class EosAccountApiFactory(private val httpClientEngine: HttpClientEngine) {
    fun createEosAccountApi(blockchainType: BlockchainType): EosAccountApi {

        println("=== createEosAccountApi.blockchainType ${blockchainType.name} ===")
        println("=== createEosAccountApi.getEosAccountApiUrl ${getEosAccountApiUrl(blockchainType)} ===")

        return provideKtorfit(
            getEosAccountApiUrl(blockchainType),
            true,
            "",
            "",
            false,
            httpClientEngine
        ).create()
    }

    private fun getEosAccountApiUrl(blockchainType: BlockchainType): String {
        return when (blockchainType) {
            BlockchainType.EosJungleTestnet -> {
                val dynamicUrl = EOS_TESTNET_ACCOUNT_BASE_URL
                println("=== dynamicUrl: $dynamicUrl ===")
                dynamicUrl
            }
            else -> throw IllegalArgumentException("Unsupported blockchain type")
        }
    }
}