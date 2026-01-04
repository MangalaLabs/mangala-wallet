package com.mangala.wallet.core.ai.domain.model.process.preprocess.preprocessors

import com.mangala.wallet.core.ai.domain.model.process.preprocess.BasePreprocessor

/**
 * Preprocessor that standardizes cryptocurrency-related terminology.
 * Useful for handling various ways users might refer to cryptocurrencies
 * and blockchain concepts.
 */
class CryptoTermPreprocessor : BasePreprocessor() {
    private val cryptoTerms = mapOf(
        "btc" to "bitcoin",
        "eth" to "ethereum",
        "xrp" to "ripple",
        "ada" to "cardano",
        "sol" to "solana",
        "doge" to "dogecoin",
        "wallet address" to "address",
        "crypto wallet" to "wallet",
        "private key" to "privateKey",
        "public key" to "publicKey"
    )

    override fun processImpl(input: String): String {
        var result = input
        cryptoTerms.forEach { (informal, formal) ->
            val regex = "\\b$informal\\b".toRegex(RegexOption.IGNORE_CASE)
            result = result.replace(regex, formal)
        }
        return result
    }
}