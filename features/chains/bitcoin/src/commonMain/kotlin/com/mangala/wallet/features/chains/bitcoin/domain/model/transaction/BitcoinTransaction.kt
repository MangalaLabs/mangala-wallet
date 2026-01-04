package com.mangala.wallet.features.chains.bitcoin.domain.model.transaction

data class BitcoinTransaction(
    val txid: String,
    val version: Int,
    val locktime: Long,
    val size: Int,
    val weight: Int,
    val fee: Long,
    val status: TransactionStatus,
    val vin: List<TransactionInput>,
    val vout: List<TransactionOutput>
) {
    data class TransactionStatus(
        val confirmed: Boolean,
        val block_height: Int? = null,
        val block_hash: String? = null,
        val block_time: Long? = null
    )

    data class TransactionInput(
        val txid: String,
        val vout: Int,
        val prevout: TransactionOutput? = null,
        val scriptsig: String,
        val scriptsig_asm: String,
        val witness: List<String>? = null,
        val sequence: Long,
        val is_coinbase: Boolean = false
    )

    data class TransactionOutput(
        val scriptpubkey: String,
        val scriptpubkeyAsm: String,
        val scriptpubkeyType: String,
        val scriptpubkeyAddress: String? = null,
        val value: Long
    )
}