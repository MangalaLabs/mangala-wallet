package com.mangala.wallet.features.chains.bitcoin.data.repository.utxo

import com.mangala.wallet.features.chains.bitcoin.data.remote.balance.MempoolRemoteDataSource
import com.mangala.wallet.features.chains.bitcoin.data.remote.balance.response.MempoolUtxoResponseItem
import com.mangala.wallet.features.chains.bitcoin.data.repository.utxo.mapper.toBitcoinUtxos
import com.mangala.wallet.features.chains.bitcoin.domain.model.utxo.BitcoinUtxo
import com.mangala.wallet.features.chains.bitcoin.domain.repository.utxo.BitcoinUtxoRepository
import com.mangala.wallet.model.blockchain.BlockchainType
import com.mangala.wallet.remote.di.ApiResponse

class BitcoinUtxoRepositoryImpl(
    private val mempoolRemoteDataSource: MempoolRemoteDataSource
): BitcoinUtxoRepository {
    override suspend fun getUtxo(address: String, blockchainType: BlockchainType): List<BitcoinUtxo> {
        val result = mempoolRemoteDataSource.getUtxo(blockchainType, address)

        return (result as? ApiResponse.Success)?.body?.toBitcoinUtxos().orEmpty()
    }
}