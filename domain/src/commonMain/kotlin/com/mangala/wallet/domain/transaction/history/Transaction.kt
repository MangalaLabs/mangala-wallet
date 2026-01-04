package com.mangala.wallet.domain.transaction.history

import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.ionspin.kotlin.bignum.decimal.toBigDecimal
import com.mangala.wallet.utils.ext.orZero
import com.mangala.wallet.utils.ext.sumOf
import com.mangala.wallet.utils.ext.weiToEth
import com.mangala.wallet.utils.toBigDecimalOrNull
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable
import kotlin.math.log

data class Transaction(
    val blockHeight: Int,
    val blockSignedAt: Instant,
    val feesPaid: String,
    val fromAddress: String,
    val fromAddressLabel: String,
    val gasMetadata: GasMetadata,
    val gasOffered: Long,
    val gasPrice: Long,
    val gasQuote: Double,
    val gasQuoteRate: Double,
    val gasSpent: Long,
    val minerAddress: String,
    val prettyGasQuote: String,
    val prettyValueQuote: String,
    val status: TransactionStatus,
    val toAddress: String,
    val toAddressLabel: String,
    val txHash: String,
    val txOffset: Int,
    val value: String,
    val valueQuote: Double,
    val logEvents: List<LogEvent>?,
    val transactionType: TransactionType,
    val accountId: String,
    val isNftTransaction: Boolean = false,
    // Additional data from Moralis
    val nativeTransfers: List<NativeTransfer>? = null,
    val erc20Transfers: List<Erc20Transfer>? = null,
    val nftTransfers: List<NftTransfer>? = null,
    val methodLabel: String? = null
) {
    data class GasMetadata(
        val contractAddress: String? = "",
        val contractDecimals: Int? = 0,
        val contractName: String? = "",
        val contractTickerSymbol: String? = "",
        val logoUrl: String? = ""
    )

    data class LogEvent(
        val blockHeight: Int? = 0,
        val blockSignedAt: String? = "",
        val decoded: Decoded? = Decoded(),
        val logOffset: Int? = 0,
        val rawLogData: String? = "",
        val rawLogTopics: List<String?>? = listOf(),
        val senderAddress: String? = "",
        val senderAddressLabel: String? = "",
        val senderContractDecimals: Int? = null,
        val senderContractTickerSymbol: String? = "",
        val senderLogoUrl: String? = "",
        val senderName: String? = "",
        val txHash: String? = "",
        val txOffset: Int? = 0
    ) {
        data class Decoded(
            val name: String? = "",
            val params: List<Param?>? = listOf(),
            val signature: String? = ""
        ) {
            data class Param(
                val decoded: Boolean? = false,
                val indexed: Boolean? = false,
                val name: String? = "",
                val type: String? = "",
                val value: ParamValue? = null
            )

            sealed class ParamValue {
                class Primitive(val value: String) : ParamValue()
                class Array(val values: List<ArrayElement>) : ParamValue() {
                    data class ArrayElement(
                        val value: String,
                        val bitSize: Int,
                        val typeAsString: String
                    )
                }

                data class Unknown(val value: String) : ParamValue()
            }

            val isTransferEvent: Boolean get() = name == TRANSFER_TRANSACTION_LOG_EVENT_NAME
        }
    }

    // Data serialized by Moralis
    @Serializable
    data class NativeTransfer(
        override val amountFormatted: String,
        override val tokenSymbol: String,
        val direction: String,
    ): MoralisTransferData {
        override val assetAddress: String = ""
    }

    @Serializable
    data class Erc20Transfer(
        val tokenName: String,
        override val tokenSymbol: String,
        val direction: String,
        override val amountFormatted: String,
        override val assetAddress: String
    ): MoralisTransferData

    @Serializable
    data class NftTransfer(
        override val amountFormatted: String,
        override val tokenSymbol: String,
        val direction: String,
        override val assetAddress: String
    ): MoralisTransferData

    sealed interface MoralisTransferData {
        val assetAddress: String
        val amountFormatted: String
        val tokenSymbol: String
    }

    // TODO: For Moralis transactions, we have no logEvents
    val isNativeCoinTransaction =
        logEvents.isNullOrEmpty() // TODO: Handle case transactions initiated from our wallet. We need to insert some placeholder data into logEvents

    // TODO: Handle case where event log contains receive transaction for multiple coins
    // key = value transacted
    // value = symbol of the asset being transacted
    fun getValueTransacted(address: String): Pair<BigDecimal?, String?> {
        return when (transactionType) {
            TransactionType.SEND -> {
                // TODO: Show all the transfers that are in this transaction, not just conflating it into one
                val moralisSummaryTransfer = getMoralisSummaryTransfer()
                if (moralisSummaryTransfer != null) {
                    return moralisSummaryTransfer.amountFormatted.toBigDecimalOrNull() to moralisSummaryTransfer.tokenSymbol
                }

                if (isNativeCoinTransaction) {
                    (if (value == "0") BigDecimal.ZERO else value.toBigDecimal().weiToEth(gasMetadata.contractDecimals.orZero())) to gasMetadata.contractTickerSymbol
                } else {
                    val logEvents = logEvents?.filter {
                        it.decoded?.isTransferEvent == true && it.decoded.params?.find { param ->
                            param?.name == "from" && param.value is LogEvent.Decoded.ParamValue.Primitive && param.value.value == address
                        } != null
                    }
                    val result = logEvents?.sumOf {
                        val transactedValue =
                            it.decoded?.params?.find { param -> param?.name == "value" && param.value is LogEvent.Decoded.ParamValue.Primitive }
                        (transactedValue?.value as? LogEvent.Decoded.ParamValue.Primitive)?.value?.toBigDecimalOrNull()
                            ?.weiToEth(it.senderContractDecimals ?: 0) ?: BigDecimal.ZERO
                    } ?: kotlin.run { return@run null }

                    result to logEvents?.firstOrNull()?.senderContractTickerSymbol.orEmpty() // TODO: Handle logs containing multiple events
                }
            }
            TransactionType.RECEIVE -> {
                val moralisSummaryTransfer = getMoralisSummaryTransfer()

                if (moralisSummaryTransfer != null) {
                    return moralisSummaryTransfer.amountFormatted.toBigDecimalOrNull() to moralisSummaryTransfer.tokenSymbol
                }

                if (isNativeCoinTransaction) {
                    value.toBigDecimal().weiToEth(gasMetadata.contractDecimals.orZero()) to gasMetadata.contractTickerSymbol
                } else {
                    getTokenReceivedValue(address)
                }
            }
            TransactionType.SWAP -> {
                val extractReceivedTokenFromSwap = getMoralisSummaryTransfer()

                if (extractReceivedTokenFromSwap != null) {
                    return extractReceivedTokenFromSwap.amountFormatted.toBigDecimalOrNull() to extractReceivedTokenFromSwap.tokenSymbol
                }

                getTokenReceivedValue(address)
            }
            TransactionType.CONTRACT_CALL -> {
                BigDecimal.ZERO to gasMetadata.contractTickerSymbol.orEmpty()
            }
            TransactionType.CONTRACT_DEPLOYMENT -> {
                BigDecimal.ZERO to gasMetadata.contractTickerSymbol.orEmpty()
            }
        }
    }

    fun getTransactedTokenAddress(): String? {
        if (logEvents.isNullOrEmpty().not()) {
            // In case transaction from Covalent, we can get the token address from the log events
            return logEvents?.first()?.senderAddress
        }

        // For Moralis data, we can get the token address from the list of transfers
        return getMoralisSummaryTransfer()?.assetAddress
    }

    private fun getMoralisSummaryTransfer(): MoralisTransferData? {
        // Gets the most significant transaction from the list of transfers

        return when (transactionType) {
            TransactionType.SEND -> {
                nativeTransfers?.firstOrNull { it.direction == "send" }?.let {
                    return it
                }

                erc20Transfers?.firstOrNull { it.direction == "send" }?.let {
                    return it
                }

                nftTransfers?.firstOrNull { it.direction == "send" }?.let {
                    return it
                }
            }
            TransactionType.RECEIVE -> {
                nativeTransfers?.firstOrNull { it.direction == "receive" }?.let {
                    return it
                }

                erc20Transfers?.firstOrNull { it.direction == "receive" }?.let {
                    return it
                }

                nftTransfers?.firstOrNull { it.direction == "receive" }?.let {
                    return it
                }
            }
            TransactionType.SWAP -> {
                erc20Transfers?.find { it.direction == "receive" }?.let {
                    return it
                }

                nativeTransfers?.find { it.direction == "receive" }?.let {
                    return it
                }

                nftTransfers?.find { it.direction == "receive" }?.let {
                    return it
                }
            }
            TransactionType.CONTRACT_CALL -> return null
            TransactionType.CONTRACT_DEPLOYMENT -> return null
        }
    }

    private fun getTokenReceivedValue(
        address: String
    ): Pair<BigDecimal?, String> {
        val logEvents = logEvents?.filter {
            it.decoded?.isTransferEvent == true && it.decoded.params?.find { param ->
                param?.name == "to" && param.value is LogEvent.Decoded.ParamValue.Primitive && param.value.value == address
            } != null
        }
        val result = logEvents?.sumOf {
            val transactedValue =
                it.decoded?.params?.find { param -> param?.name == "value" && param.value is LogEvent.Decoded.ParamValue.Primitive }
            (transactedValue?.value as? LogEvent.Decoded.ParamValue.Primitive)?.value?.toBigDecimalOrNull()
                ?.weiToEth(it.senderContractDecimals ?: 0) ?: BigDecimal.ZERO
        } ?: kotlin.run { return@run null }

        return result to logEvents?.firstOrNull()?.senderContractTickerSymbol.orEmpty() // TODO: Handle logs containing multiple events
    }

    companion object {
        const val TRANSFER_TRANSACTION_LOG_EVENT_NAME = "Transfer"
        const val TRANSFER_FROM_TRANSACTION_LOG_EVENT_NAME = "TransferFrom"
    }
}