package com.mangala.wallet.features.chains.bitcoin.data.repository.utxo.mapper

import com.mangala.wallet.features.chains.bitcoin.data.remote.balance.response.MempoolUtxoResponseItem
import com.mangala.wallet.features.chains.bitcoin.domain.model.utxo.BitcoinUtxo

fun List<MempoolUtxoResponseItem>.toBitcoinUtxos(): List<BitcoinUtxo> = map {
    BitcoinUtxo(
        vout = it.vout ?: 0,
        txId = it.txid.orEmpty(),
        amountInSatoshis = it.value?.toLong() ?: 0,
        isConfirmed = it.status?.confirmed ?: false,
        blockHeight = it.status?.blockHeight
    )
}