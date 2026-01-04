package com.mangala.wallet.features.crypto_payment.domain.usecase.account.paywithcrypto


import cafe.adriel.voyager.core.concurrent.AtomicInt32
import com.mangala.wallet.core.hdwallet.domain.usecases.GenerateHDKeyUseCase
import com.mangala.wallet.domain.wallet.usecases.GetSelectedWalletUseCase
import com.mangala.wallet.features.chains.evmcompatible.core.TransactionBuilder
import com.mangala.wallet.features.chains.evmcompatible.core.signer.Signer
import com.mangala.wallet.model.provider.JsonRpcNodeResponse
import com.mangala.wallet.features.chains.evmcompatible.domain.usecases.GetNonceUseCase
import com.mangala.wallet.features.chains.evmcompatible.domain.usecases.SendRawTransactionUseCase
import com.mangala.wallet.features.chains.evmcompatible.model.Address
import com.mangala.wallet.features.chains.evmcompatible.model.DefaultBlockParameter
import com.mangala.wallet.features.chains.evmcompatible.model.GasPrice
import com.mangala.wallet.features.chains.evmcompatible.model.RawTransaction
import com.mangala.wallet.features.chains.evmcompatible.model.Signature
import com.mangala.wallet.features.chains.evmcompatible.model.TransactionData
import com.mangala.wallet.model.blockchain.AddressType
import com.mangala.wallet.model.blockchain.Blockchain
import com.mangala.wallet.model.blockchain.BlockchainType
import com.mangala.wallet.model.blockchain.Chain
import kotlinx.serialization.json.Json
import kotlin.random.Random

class SignAndSendTransactionDataUseCase(
    private val getSelectedWalletUseCase: GetSelectedWalletUseCase,
    private val generateHDKeyUseCase: GenerateHDKeyUseCase,
    private val getNonceUseCase: GetNonceUseCase,
    private val sendRawTransactionUseCase: SendRawTransactionUseCase,
    private val parsingJson: Json
) {
    private val currentId = AtomicInt32(Random.nextInt(100))

    suspend operator fun invoke(
        blockchainType: BlockchainType,
        from: Address,
        transactionData: TransactionData,
        gasPrice: GasPrice,
        gas: Long?
    ): String? {
        val chain = Chain.fromBlockchainType(blockchainType)
        val rpcUrl = blockchainType.getRpcUrl().first()

        val selectedWallet = getSelectedWalletUseCase() ?: throw IllegalStateException("No wallet selected")
        val seed = selectedWallet.words.split(" ")

        val hdKey = generateHDKeyUseCase.invoke(
            seed,
            "",
            Blockchain(blockchainType, blockchainType.uid, ""),
            AddressType.Bip44
        )

        val nonce = getNonce(rpcUrl, from)
        val signer = Signer.getInstance(hdKey.privateKey, from, chain)
        val transactionBuilder = TransactionBuilder(from, chain.id)

        return signAndSendData(
            rpcUrl,
            signer,
            transactionBuilder,
            transactionData,
            gasPrice,
            gas,
            nonce
        )
    }

    suspend fun signAndSendData(
        rpcUrl: String,
        signer: Signer,
        transactionBuilder: TransactionBuilder,
        transactionData: TransactionData,
        gasPrice: GasPrice,
        gasLimit: Long?,
        nonce: Long?
    ): String? {
        if (gasLimit != null && nonce != null) {
            val rawTransaction = rawTransaction(transactionData, gasPrice, gasLimit, nonce)
            rawTransaction?.let {
                val signature: Signature = signer.signature(rawTransaction)
                val encoded = transactionBuilder.encode(rawTransaction, signature)
                return sendRawTransaction(rpcUrl, encoded)
            }
        }
        return null
    }

    private fun rawTransaction(
        transactionData: TransactionData,
        gasPrice: GasPrice,
        gasLimit: Long,
        nonce: Long
    ): RawTransaction? {
        return transactionData.to?.let {
            RawTransaction(
                gasPrice = gasPrice,
                gasLimit = gasLimit,
                to = it,
                value = transactionData.value,
                nonce = nonce,
                data = transactionData.input
            )
        } ?: run { null }
    }

    private suspend fun getNonce(rpcUrl: String, address: Address): Long? {
        return getNonceUseCase.getNonceLong(
            rpcUrl,
            currentId.getAndIncrement(),
            address,
            DefaultBlockParameter.Pending
        )
    }

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