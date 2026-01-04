package com.mangala.wallet.features.chains.bitcoin.domain.model.utxo

data class BitcoinUtxo(
    val txId: String,
    val vout: Int,
    val amountInSatoshis: Long,
    val isConfirmed: Boolean = false,
    val blockHeight: Int? = null
)