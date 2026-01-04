package com.mangala.wallet.features.chains.bitcoin.data.repository.transaction.mapper

import com.mangala.wallet.features.chains.bitcoin.data.remote.transaction.response.BitcoinTransactionResponse
import com.mangala.wallet.features.chains.bitcoin.domain.model.transaction.BitcoinTransaction

fun BitcoinTransactionResponse.toBitcoinTransaction(): BitcoinTransaction {
    return BitcoinTransaction(
        txid = this.txid ?: "",
        version = this.version ?: 0,
        locktime = this.locktime?.toLong() ?: 0L,
        size = this.size ?: 0,
        weight = this.weight ?: 0,
        fee = this.fee?.toLong() ?: 0L,
        status = this.status?.toDomain() ?: BitcoinTransaction.TransactionStatus(confirmed = false),
        vin = this.vin?.mapNotNull { it?.toDomain() } ?: emptyList(),
        vout = this.vout?.mapNotNull { it?.toDomain() } ?: emptyList()
    )
}

private fun BitcoinTransactionResponse.Status.toDomain(): BitcoinTransaction.TransactionStatus {
    return BitcoinTransaction.TransactionStatus(
        confirmed = this.confirmed ?: false,
        block_height = this.blockHeight,
        block_hash = this.blockHash,
        block_time = this.blockTime?.toLong()
    )
}

private fun BitcoinTransactionResponse.Vin.toDomain(): BitcoinTransaction.TransactionInput {
    return BitcoinTransaction.TransactionInput(
        txid = this.txid ?: "",
        vout = this.vout ?: 0,
        prevout = this.prevout?.toDomain(),
        scriptsig = this.scriptsig ?: "",
        scriptsig_asm = this.scriptsigAsm ?: "",
        witness = this.witness?.filterNotNull(),
        sequence = this.sequence ?: 0L,
        is_coinbase = this.isCoinbase ?: false
    )
}

private fun BitcoinTransactionResponse.Vin.Prevout.toDomain(): BitcoinTransaction.TransactionOutput {
    return BitcoinTransaction.TransactionOutput(
        scriptpubkey = this.scriptpubkey ?: "",
        scriptpubkeyAsm = this.scriptpubkeyAsm ?: "",
        scriptpubkeyType = this.scriptpubkeyType ?: "",
        scriptpubkeyAddress = this.scriptpubkeyAddress,
        value = this.value ?: 0L
    )
}

private fun BitcoinTransactionResponse.Vout.toDomain(): BitcoinTransaction.TransactionOutput {
    return BitcoinTransaction.TransactionOutput(
        scriptpubkey = this.scriptpubkey ?: "",
        scriptpubkeyAsm = this.scriptpubkeyAsm ?: "",
        scriptpubkeyType = this.scriptpubkeyType ?: "",
        scriptpubkeyAddress = this.scriptpubkeyAddress,
        value = this.value ?: 0L
    )
}