package com.mangala.wallet.features.chains.evmcompatible.domain.usecases

import cafe.adriel.voyager.core.concurrent.AtomicInt32
import com.mangala.wallet.features.chains.evmcompatible.core.TransactionBuilder
import com.mangala.wallet.model.provider.JsonRpcNodeResponse
import com.mangala.wallet.features.chains.evmcompatible.model.Address
import com.mangala.wallet.features.chains.evmcompatible.model.RawTransaction
import com.mangala.wallet.features.chains.evmcompatible.model.Signature
import com.mangala.wallet.model.blockchain.Chain
import kotlinx.serialization.json.Json
import kotlin.random.Random

class SendSignedTransactionUseCase(
    private val sendRawTransactionUseCase: SendRawTransactionUseCase,
    private val parsingJson: Json
) {
    private val currentId = AtomicInt32(Random.nextInt(100))

    suspend operator fun invoke(
        rpcUrl: String,
        chain: Chain,
        from: Address,
        rawTransaction: RawTransaction,
        signature: Signature
    ): String {
        val transactionBuilder = TransactionBuilder(from, chain.id)
        val encoded = transactionBuilder.encode(rawTransaction, signature)
        return sendRawTransaction(rpcUrl, encoded)
    }

    // TODO: Refactor, this function is copied
    private suspend fun sendRawTransaction(rpcUrl: String, signedTransaction: ByteArray): String {
        val response = sendRawTransactionUseCase.invoke(
            rpcUrl,
            currentId.getAndIncrement(),
            signedTransaction
        )
        val jsonRpcNodeResponse = try {
            // TODO: Upgrade to Result wrapper for more robust error handling
            parsingJson.decodeFromString(JsonRpcNodeResponse.serializer(), response)
        } catch (e: Exception) {
            return ""
        }

        return jsonRpcNodeResponse.result.orEmpty() // TODO: Check fail case
    }
}