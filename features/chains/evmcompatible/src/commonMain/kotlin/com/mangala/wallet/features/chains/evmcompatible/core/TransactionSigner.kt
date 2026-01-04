package com.mangala.wallet.features.chains.evmcompatible.core

import com.mangala.wallet.features.chains.evmcompatible.crypto.CryptoUtils
import com.mangala.wallet.features.chains.evmcompatible.model.GasPrice
import com.mangala.wallet.features.chains.evmcompatible.model.RawTransaction
import com.mangala.wallet.features.chains.evmcompatible.model.Signature
import com.mangala.wallet.features.chains.evmcompatible.spv.rlp.RLP

class TransactionSigner(
    private val privateKey: ByteArray,
    private val chainId: Int
) {

    fun signatureLegacy(rawTransaction: RawTransaction): Signature {
        return when (val gasPrice = rawTransaction.gasPrice) {
            is GasPrice.Eip1559 -> {
                val signatureData = signEip1559(rawTransaction, gasPrice.maxFeePerGas, gasPrice.maxPriorityFeePerGas)
                signatureEip1559(signatureData)
            }
            is GasPrice.Legacy -> {
                val signatureData = signEip155(rawTransaction, gasPrice.legacyGasPrice)
                signatureLegacy(signatureData)
            }
        }
    }

    fun signaturePersonalMessage(message: ByteArray): ByteArray {
        val transactionHash = CryptoUtils.sha3(message)
        println("transaction hash: ${transactionHash.toHexString()}")
        val signatureData = CryptoUtils.ellipticSign(transactionHash, privateKey)
        return patchSignatureVComponent(signatureData)
    }

    private fun patchSignatureVComponent(signature: ByteArray): ByteArray {
        signature?.let {
            if (it.size == 65 && it[64] < 27) {
                it[64] = (it[64] + 0x1b).toByte()
            }
        }
        return signature
    }

    private fun signatureLegacy(signatureData: ByteArray): Signature {
        return Signature(
            v = signatureData[64] + if (chainId == 0) 27 else (35 + 2 * chainId),
            r = signatureData.copyOfRange(0, 32),
            s = signatureData.copyOfRange(32, 64)
        )
    }

    private fun signatureEip1559(signatureData: ByteArray): Signature {
        return Signature(
            v = signatureData[64].toInt(),
            r = signatureData.copyOfRange(0, 32),
            s = signatureData.copyOfRange(32, 64)
        )
    }

    private fun signEip155(rawTransaction: RawTransaction, legacyGasPrice: Long): ByteArray {
        val encodedTransaction =
            if(rawTransaction.isDeploySmartContract){
                RLP.encodeList(
                    RLP.encodeLong(rawTransaction.nonce),
                    RLP.encodeLong(legacyGasPrice),
                    RLP.encodeLong(rawTransaction.gasLimit),
                    RLP.encodeElement("0x".hexStringToByteArray()),
                    RLP.encodeBigInteger(rawTransaction.value),
                    RLP.encodeElement(rawTransaction.data),
                    RLP.encodeInt(chainId),
                    RLP.encodeElement(ByteArray(0)),
                    RLP.encodeElement(ByteArray(0)))
            }else{
                RLP.encodeList(
                    RLP.encodeLong(rawTransaction.nonce),
                    RLP.encodeLong(legacyGasPrice),
                    RLP.encodeLong(rawTransaction.gasLimit),
                    RLP.encodeElement(rawTransaction.to.raw),
                    RLP.encodeBigInteger(rawTransaction.value),
                    RLP.encodeElement(rawTransaction.data),
                    RLP.encodeInt(chainId),
                    RLP.encodeElement(ByteArray(0)),
                    RLP.encodeElement(ByteArray(0)))
            }

        val rawTransactionHash = CryptoUtils.sha3(encodedTransaction)

        return CryptoUtils.ellipticSign(rawTransactionHash, privateKey)
    }

    private fun signEip1559(rawTransaction: RawTransaction, maxFeePerGas: Long, maxPriorityFeePerGas: Long): ByteArray {
        val encodedTransaction =
            if(rawTransaction.isDeploySmartContract){
                RLP.encodeList(
                    RLP.encodeInt(chainId),
                    RLP.encodeLong(rawTransaction.nonce),
                    RLP.encodeLong(maxPriorityFeePerGas),
                    RLP.encodeLong(maxFeePerGas),
                    RLP.encodeLong(rawTransaction.gasLimit),
                    RLP.encodeElement("0x".hexStringToByteArray()),
                    RLP.encodeBigInteger(rawTransaction.value),
                    RLP.encodeElement(rawTransaction.data),
                    RLP.encode(arrayOf<Any>(), true)
                )
            }else{
                RLP.encodeList(
                    RLP.encodeInt(chainId),
                    RLP.encodeLong(rawTransaction.nonce),
                    RLP.encodeLong(maxPriorityFeePerGas),
                    RLP.encodeLong(maxFeePerGas),
                    RLP.encodeLong(rawTransaction.gasLimit),
                    RLP.encodeElement(rawTransaction.to.raw),
                    RLP.encodeBigInteger(rawTransaction.value),
                    RLP.encodeElement(rawTransaction.data),
                    RLP.encode(arrayOf<Any>(), true)
                )
            }

        val rawTransactionHash = CryptoUtils.sha3("0x02".hexStringToByteArray() + encodedTransaction)

        return CryptoUtils.ellipticSign(rawTransactionHash, privateKey)
    }

}