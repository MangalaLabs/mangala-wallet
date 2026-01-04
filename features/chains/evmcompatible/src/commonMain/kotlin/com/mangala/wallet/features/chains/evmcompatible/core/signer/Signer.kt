package com.mangala.wallet.features.chains.evmcompatible.core.signer

import com.ionspin.kotlin.bignum.integer.BigInteger
import com.mangala.wallet.features.chains.evmcompatible.core.TransactionBuilder
import com.mangala.wallet.features.chains.evmcompatible.core.TransactionSigner
import com.mangala.wallet.features.chains.evmcompatible.crypto.CryptoUtils
import com.mangala.wallet.features.chains.evmcompatible.crypto.EIP712Encoder
import com.mangala.wallet.features.chains.evmcompatible.crypto.TypedData
import com.mangala.wallet.features.chains.evmcompatible.model.*
import com.mangala.wallet.model.blockchain.Chain

class Signer(
    private val transactionBuilder: TransactionBuilder,
    private val transactionSigner: TransactionSigner,
    private val ethSigner: EthSigner
) {

    fun signature(rawTransaction: RawTransaction): Signature {
        return transactionSigner.signatureLegacy(rawTransaction)
    }

    fun signedTransaction(
        address: Address,
        value: BigInteger,
        transactionInput: ByteArray,
        gasPrice: GasPrice,
        gasLimit: Long,
        nonce: Long
    ): ByteArray {
        val rawTransaction = RawTransaction(
            gasPrice,
            gasLimit,
            address,
            value,
            nonce,
            transactionInput
        )
        val signature = transactionSigner.signatureLegacy(rawTransaction)
        return transactionBuilder.encode(rawTransaction, signature)
    }

    fun signByteArray(message: ByteArray): ByteArray {
        return ethSigner.signByteArray(message)
    }

    fun signByteArrayLegacy(message: ByteArray): ByteArray {
        return ethSigner.signByteArrayLegacy(message)
    }

    fun signTypedData(rawJsonMessage: String): ByteArray {
        return ethSigner.signTypedData(rawJsonMessage)
    }

    fun parseTypedData(rawJsonMessage: String): TypedData? {
        return ethSigner.parseTypedData(rawJsonMessage)
    }

    companion object {
        fun getInstance(privateKey: ByteArray, address: Address, chain: Chain): Signer {
            val transactionSigner = TransactionSigner(privateKey, chain.id)
            val transactionBuilder = TransactionBuilder(address, chain.id)
            val ethSigner = EthSigner(privateKey, CryptoUtils, EIP712Encoder())

            return Signer(transactionBuilder, transactionSigner, ethSigner)
        }

    }

    open class PrivateKeyValidationError : Exception() {
        class InvalidDataString : PrivateKeyValidationError()
        class InvalidDataLength : PrivateKeyValidationError()
    }
}