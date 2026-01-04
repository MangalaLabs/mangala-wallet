package com.mangala.wallet.features.transactionhistory.presentation.utils

import com.mangala.wallet.domain.transaction.history.TransactionType
import com.mangala.wallet.features.chains.bitcoin.domain.model.transaction.BitcoinTransaction

/**
 * Helper function to determine transaction type (send/receive)
 * based on inputs and outputs
 */
fun BitcoinTransaction.mapBitcoinTransactionType(
    userAddress: String
): TransactionType {
    // Check if the address is in the inputs (sending)
    val isInInputs = vin.any {
        it.prevout?.scriptpubkeyAddress == userAddress
    }

    // Check if the address is in the outputs (receiving)
    val isInOutputs = vout.any {
        it.scriptpubkeyAddress == userAddress
    }

    return when {
        isInInputs && isInOutputs -> {
            // If address is both in inputs and outputs, it's likely a send with change
            TransactionType.SEND
        }

        isInInputs -> {
            // If address is in inputs only, it's a send
            TransactionType.SEND
        }

        isInOutputs -> {
            // If address is in outputs only, it's a receive
            TransactionType.RECEIVE
        }

        else -> {
            // Default case (shouldn't happen if transaction is related to this address)
            TransactionType.CONTRACT_CALL
        }
    }
}

fun BitcoinTransaction.getTransactionAmount(
    type: TransactionType,
    bitcoinAddress: String
): Long {
    return when (type) {
        TransactionType.RECEIVE -> {
            // Sum of outputs to our address
            vout.filter { it.scriptpubkeyAddress == bitcoinAddress }
                .sumOf { it.value }
        }

        else -> {
            // Sum of outputs to other addresses + fee
            val outputValue = vout.filter { it.scriptpubkeyAddress != bitcoinAddress }
                .sumOf { it.value }
            outputValue + fee
        }
    }
}