package com.linh.antelope_qr.domain.usecase

import com.mangala.antelope.base.domain.usecase.GetInfoUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.utils.base64uToByteArray
import com.mangala.wallet.model.blockchain.BlockchainType
import com.mangala.wallet.utils.decompressRawZlib
import com.mangala.wallet.utils.ext.toRawHexString
import com.memtrip.eos.chain.actions.transaction.abi.TransactionAbi
import com.memtrip.eos.chain.actions.transaction.decoder.AbiBinaryTransactionReader
import com.memtrip.eos.chain.actions.transaction.esr.EsrSigningRequestArgs
import com.memtrip.eos.core.block.BlockIdDetails
import kotlinx.datetime.Clock
import kotlin.time.DurationUnit
import kotlin.time.toDuration
import kotlin.experimental.and

class DecodeEsrUseCase(
    private val getInfoUseCase: GetInfoUseCase
) {

    fun canDecode(esrUri: String): EsrSigningRequestArgs {
        return constructEsrSigningRequestArgs(esrUri)
    }

    suspend operator fun invoke(esrUri: String): EsrSigningRequestArgs {
        val rawRequestArgs  = constructEsrSigningRequestArgs(esrUri)

        val actions = rawRequestArgs.actions
        return if (actions != null && rawRequestArgs.transaction == null) {
            val blockchainType = BlockchainType.fromChainId(rawRequestArgs.resolvedChainId.orEmpty())
            val chainInfo = getInfoUseCase(blockchainType)
                ?: throw UnsupportedOperationException("Chain info not found for ${rawRequestArgs.resolvedChainId}")
            val blockIdDetails = BlockIdDetails(chainInfo.headBlockId.orEmpty())

            return rawRequestArgs.copy(
                transaction = TransactionAbi(
                    transactionDefaultExpiry(),
                    blockIdDetails.blockNum,
                    blockIdDetails.blockPrefix,
                    0,
                    0,
                    0,
                    emptyList(),
                    actions,
                    emptyList(),
                    emptyList(),
                    emptyList()
                )
            )
        } else {
            rawRequestArgs
        }
    }

    private fun constructEsrSigningRequestArgs(esrUri: String): EsrSigningRequestArgs {
        val parts = esrUri.split(":")
        val prefix = parts.getOrNull(0)
        var rawPath = parts.getOrNull(1)

        if (prefix == null || rawPath == null) throw UnsupportedOperationException("Invalid ESR URI")

        if (prefix != "esr" && prefix != "web+esr") throw UnsupportedOperationException("Only esr and web+esr schemes are supported")

        val path = rawPath.removePrefix("//")

        val decoded = path.base64uToByteArray()

        val header = (decoded[0] and 0xff.toByte()).toInt()
        val version = (header and ((1 shl 7).inv()))
        if (version.toUByte() !in PROTOCOL_VERSION_SUPPORTED) throw UnsupportedOperationException("Unsupported ESR protocol version ${version.toUByte()}")

        val payload: ByteArray = decoded.copyOfRange(1, decoded.size) + byteArrayOf(0)
        val compressionEnabled = (header and (1 shl 7)) != 0

        val data = if (compressionEnabled) {
            payload.decompressRawZlib()
        } else {
            payload
        }

        val transactionReader = AbiBinaryTransactionReader(data.toRawHexString())
        val esrSigningRequestArgs = transactionReader.readEsrSigningRequestArgs()

        return esrSigningRequestArgs
    }

    private fun transactionDefaultExpiry() = with(Clock.System.now()) {
        plus(TRANSACTION_EXPIRY_MINUTES.toDuration(DurationUnit.MINUTES))
    }

    companion object {
        private val PROTOCOL_VERSION_SUPPORTED: List<UByte> = listOf(2u, 3u)
        private const val TRANSACTION_EXPIRY_MINUTES = 5 // Default expiry time for constructed transactions
    }
}