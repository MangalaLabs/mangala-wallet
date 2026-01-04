package com.mangala.wallet.domain.transaction.history

import com.ionspin.kotlin.bignum.decimal.toBigDecimal
import kotlinx.datetime.Clock
import kotlin.test.Test
import kotlin.test.assertEquals

class TransactionTest {
    
    @Test
    fun `Given native coin send transaction_When getValueTransacted_Then return value in ETH`() {
        val transaction = createNativeCoinSendTransaction()

        val value = transaction.getValueTransacted("testAddress")

        assertEquals("1.0".toBigDecimal(), value.first)
    }

    @Test
    fun `Given token send transaction with valid logEvents_When getValueTransacted_Then return sum of transferred value`() {
        val logEvents = listOf(
            createLogEvent("testAddress", "2000000000000000000"),
            createLogEvent("testAddress", "3000000000000000000")
        )
        val transaction = createTokenSendTransactionWithLogEvents(logEvents)

        val value = transaction.getValueTransacted("testAddress")

        assertEquals("5.0".toBigDecimal(), value.first)
    }

    @Test
    fun `Given token send transaction with valid logEvents with decimal zero_When getValueTransacted_Then return sum of transferred value`() {
        val logEvents = listOf(
            createLogEvent("testAddress", "2", 0)
        )
        val transaction = createTokenSendTransactionWithLogEvents(logEvents)

        val value = transaction.getValueTransacted("testAddress")

        assertEquals("2.0".toBigDecimal(), value.first)
    }

    @Test
    fun `Given token send transaction without matching address in logEvents_When getValueTransacted_Then return zero`() {
        val logEvents = listOf(
            createLogEvent("testAddress", "2000000000000000000"),
        )
        val transaction = createTokenSendTransactionWithLogEvents(logEvents)

        val value = transaction.getValueTransacted("differentAddress")

        assertEquals("0.0".toBigDecimal(), value.first)
    }

    @Test
    fun `Given token send transaction without logEvents_When getValueTransacted_Then return zero`() {
        val transaction = createTokenSendTransactionWithoutLogEvents()

        val value = transaction.getValueTransacted("testAddress")

        assertEquals("0.0".toBigDecimal(), value.first)
    }
    
    private fun createNativeCoinSendTransaction(): Transaction {
        return Transaction(
            accountId = "",
            blockHeight = 0,
            blockSignedAt = Clock.System.now(),
            feesPaid = "0",
            fromAddress = "fromAddress",
            fromAddressLabel = "label",
            gasMetadata = Transaction.GasMetadata(contractDecimals = 18),
            gasOffered = 0,
            gasPrice = 0,
            gasQuote = 0.0,
            gasQuoteRate = 0.0,
            gasSpent = 0,
            minerAddress = "minerAddress",
            prettyGasQuote = "0",
            prettyValueQuote = "0",
            status = TransactionStatus.SUCCESS,
            toAddress = "testAddress",
            toAddressLabel = "label",
            txHash = "txHash",
            txOffset = 0,
            value = "1000000000000000000", // 1.0 ETH in wei
            valueQuote = 0.0,
            logEvents = null,
            transactionType = TransactionType.SEND
        )
    }

    private fun createTokenSendTransactionWithLogEvents(logEvents: List<Transaction.LogEvent>): Transaction {
        return Transaction(
            accountId = "",
            blockHeight = 0,
            blockSignedAt = Clock.System.now(),
            feesPaid = "0",
            fromAddress = "fromAddress",
            fromAddressLabel = "label",
            gasMetadata = Transaction.GasMetadata(contractDecimals = 18),
            gasOffered = 0,
            gasPrice = 0,
            gasQuote = 0.0,
            gasQuoteRate = 0.0,
            gasSpent = 0,
            minerAddress = "minerAddress",
            prettyGasQuote = "0",
            prettyValueQuote = "0",
            status = TransactionStatus.SUCCESS,
            toAddress = "testAddress",
            toAddressLabel = "label",
            txHash = "txHash",
            txOffset = 0,
            value = "0",
            valueQuote = 0.0,
            logEvents = logEvents,
            transactionType = TransactionType.SEND
        )
    }

    private fun createTokenSendTransactionWithoutLogEvents(): Transaction {
        return Transaction(
            accountId = "",
            blockHeight = 0,
            blockSignedAt = Clock.System.now(),
            feesPaid = "0",
            fromAddress = "fromAddress",
            fromAddressLabel = "label",
            gasMetadata = Transaction.GasMetadata(contractDecimals = 18),
            gasOffered = 0,
            gasPrice = 0,
            gasQuote = 0.0,
            gasQuoteRate = 0.0,
            gasSpent = 0,
            minerAddress = "minerAddress",
            prettyGasQuote = "0",
            prettyValueQuote = "0",
            status = TransactionStatus.SUCCESS,
            toAddress = "testAddress",
            toAddressLabel = "label",
            txHash = "txHash",
            txOffset = 0,
            value = "0",
            valueQuote = 0.0,
            logEvents = null,
            transactionType = TransactionType.SEND
        )
    }

    private fun createLogEvent(toAddress: String, value: String, decimals: Int? = 18): Transaction.LogEvent {
        return Transaction.LogEvent(
            decoded = Transaction.LogEvent.Decoded(
                name = "Transfer",
                params = listOf(
                    Transaction.LogEvent.Decoded.Param(name = "to", value = Transaction.LogEvent.Decoded.ParamValue.Primitive(toAddress)),
                    Transaction.LogEvent.Decoded.Param(name = "value", value = Transaction.LogEvent.Decoded.ParamValue.Primitive(value))
                ),
                signature = "Transfer"
            ),
            senderContractDecimals = decimals
        )
    }
}