package com.mangala.wallet.features.chains.evmcompatible.domain.usecases

import com.ionspin.kotlin.bignum.integer.BigInteger
import com.mangala.wallet.features.chains.evmcompatible.model.Address
import com.mangala.wallet.features.chains.evmcompatible.model.GasPrice
import com.mangala.wallet.features.chains.evmcompatible.model.SignTransactionRequest
import com.mangala.wallet.features.chains.evmcompatible.model.SignTransactionType
import com.mangala.wallet.features.chains.evmcompatible.model.TransactionData
import com.mangala.wallet.model.blockchain.BlockchainType
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.test.Test

class EncodeSignTransactionRequestUseCaseTest {

//    @Test
//    fun `Given a sign transaction request_When invoked_then return sign transaction request encoded in JSON`() {
//        val signTransactionRequest = SignTransactionRequest(
//            walletId = "walletId",
//            accountId = "accountId",
//            nonce = 1,
//            blockchainType = BlockchainType.BinanceSmartChainTestNet,
//            transactionData = TransactionData(
//                to = Address("0x000000000000000000000000000000000000dEaD"),
//                value = BigInteger.ONE,
//                input = byteArrayOf(0, 1, 54, 54, 23, 45)
//            ),
//            gasPrice = GasPrice.Legacy(1000L),
//            gasLimit = 1,
//            transactionType = SignTransactionType.SEND_COIN,
//            fromAddress = ""
//        )
//        val encoded = Json.encodeToString(signTransactionRequest)
//
//        println(encoded)
//    }
}