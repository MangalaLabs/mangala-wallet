package com.mangala.wallet.features.chains.evmcompatible.model

import com.ionspin.kotlin.bignum.integer.BigInteger
import com.mangala.wallet.model.blockchain.BlockchainType
import com.mangala.wallet.utils.ByteArrayAsBase64StringSerializer
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable
data class SignTransactionRequest(
    val requestId: String,
    val walletId: String,
    val accountId: String,
    val fromAddress: String,
    val nonce: Long,
    @Serializable(with = BlockchainTypeSerializer::class)
    val blockchainType: BlockchainType,
    val transactionTo: String,
    @Serializable(with = BigIntegerSerializer::class)
    val transactionValue: BigInteger,
    @Serializable(with = ByteArrayAsBase64StringSerializer::class)
    val transactionInput: ByteArray,
    val legacyGasPrice: Long?,
    val maxFeePerGas: Long?,
    val maxPriorityFeePerGas: Long?,
    val baseFee: Long?,
    val gasLimit: Long,
    val gasFiatValue: String,
    val transactionType: SignTransactionType,
    val contactName: String?,
    val contactAddress: String?
) {
    val transactionData by lazy {
        TransactionData(Address(transactionTo), transactionValue, transactionInput)
    }
    val gasPrice by lazy {
        if (legacyGasPrice != null) {
            GasPrice.Legacy(legacyGasPrice)
        } else {
            GasPrice.Eip1559(maxFeePerGas!!, maxPriorityFeePerGas!!, baseFee!!)
        }
    }
    companion object {
        operator fun invoke(
            requestId: String,
            walletId: String,
            accountId: String,
            fromAddress: String,
            nonce: Long,
            blockchainType: BlockchainType,
            transactionData: TransactionData,
            gasPrice: GasPrice,
            gasLimit: Long,
            gasFiatValue: String,
            transactionType: SignTransactionType,
            contactName: String? = null,
            contactAddress: String?
        ): SignTransactionRequest {
            val eip1559GasPrice = gasPrice as? GasPrice.Eip1559
            return SignTransactionRequest(
                requestId,
                walletId,
                accountId,
                fromAddress,
                nonce,
                blockchainType,
                transactionData.to?.hex.orEmpty(),
                transactionData.value,
                transactionData.input,
                (gasPrice as? GasPrice.Legacy)?.legacyGasPrice,
                eip1559GasPrice?.maxFeePerGas,
                eip1559GasPrice?.maxPriorityFeePerGas,
                eip1559GasPrice?.baseFee,
                gasLimit,
                gasFiatValue,
                transactionType,
                contactName,
                contactAddress
            )
        }
    }
}

object BlockchainTypeSerializer: KSerializer<BlockchainType> {
    override fun deserialize(decoder: Decoder): BlockchainType {
        val string = decoder.decodeString()
        return BlockchainType.fromUid(string)
    }

    override val descriptor: SerialDescriptor
        get() = PrimitiveSerialDescriptor("BlockchainType", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: BlockchainType) {
        return encoder.encodeString(value.uid)
    }
}

object BigIntegerSerializer: KSerializer<BigInteger> {
    override fun deserialize(decoder: Decoder): BigInteger {
        val string = decoder.decodeString()
        return BigInteger.parseString(string)
    }

    override val descriptor: SerialDescriptor
        get() = PrimitiveSerialDescriptor("BigInteger", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: BigInteger) {
        return encoder.encodeString(value.toString())
    }
}

@Serializable
sealed class SignTransactionType {
    @Serializable
    data class SendCoinOrErc20Token(val amount: String, val symbol: String, val fiatValue: String): SignTransactionType()
    @Serializable
    data class SendErc721Or1155Token(val collectionName: String, val tokenId: String): SignTransactionType()
    @Serializable
    data class Swap(val fromToken: String, val toToken: String, val fromAmount: String, val toAmount: String): SignTransactionType()
    @Serializable
    data class Erc20Approve(val token: String, val amount: String): SignTransactionType()
    @Serializable
    data class SignWeb3(val url: String, val payload: String): SignTransactionType()
}