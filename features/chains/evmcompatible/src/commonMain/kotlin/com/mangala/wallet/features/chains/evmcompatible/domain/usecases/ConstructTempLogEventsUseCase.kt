package com.mangala.wallet.features.chains.evmcompatible.domain.usecases

import com.ionspin.kotlin.bignum.integer.toBigInteger
import com.mangala.wallet.domain.transaction.history.Transaction

class ConstructTempLogEventsUseCase {

    /**
     * Construct temp log events to display info in transaction history
     */
    operator fun invoke(fromAddress: String, toAddress: String, txData: String, tokenDecimals: Long = 0): List<Transaction.LogEvent>? {
        val transactionName = when {
            txData.startsWith("0xa9059cbb") -> Transaction.TRANSFER_TRANSACTION_LOG_EVENT_NAME
            else -> return null // TODO: Handle mapping to other types of transactions
        }
        val amountHex = txData.substring(74, 138)
        val amountDecimal = amountHex.toBigInteger(16)
        val logEvent =
            Transaction.LogEvent(
                decoded = Transaction.LogEvent.Decoded(transactionName, listOf(
                    Transaction.LogEvent.Decoded.Param(
                        name = "from",
                        value = Transaction.LogEvent.Decoded.ParamValue.Primitive(fromAddress)
                    ),
                    Transaction.LogEvent.Decoded.Param(
                        name = "to",
                        value = Transaction.LogEvent.Decoded.ParamValue.Primitive(toAddress)
                    ),
                    Transaction.LogEvent.Decoded.Param(
                        name = "value",
                        value = Transaction.LogEvent.Decoded.ParamValue.Primitive(amountDecimal.toString())
                    ),
                )),
                senderContractDecimals = tokenDecimals.toInt()
            ) // TODO: Get info of token sent

        return listOf(logEvent)
    }
}