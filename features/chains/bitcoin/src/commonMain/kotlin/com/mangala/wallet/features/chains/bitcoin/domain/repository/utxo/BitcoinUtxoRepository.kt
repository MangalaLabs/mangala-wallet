package com.mangala.wallet.features.chains.bitcoin.domain.repository.utxo

import com.mangala.wallet.features.chains.bitcoin.domain.model.utxo.BitcoinUtxo
import com.mangala.wallet.model.blockchain.BlockchainType

interface BitcoinUtxoRepository {
    suspend fun getUtxo(address: String, blockchainType: BlockchainType): List<BitcoinUtxo>
}