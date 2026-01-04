package com.mangala.wallet.features.chains.bitcoin.domain.usecases.utxo

import com.mangala.wallet.features.chains.bitcoin.data.remote.balance.response.MempoolUtxoResponseItem
import com.mangala.wallet.features.chains.bitcoin.domain.model.utxo.BitcoinUtxo
import com.mangala.wallet.features.chains.bitcoin.domain.repository.utxo.BitcoinUtxoRepository
import com.mangala.wallet.model.blockchain.BlockchainType

class GetBitcoinAddressUtxoUseCase(
    private val utxoRepository: BitcoinUtxoRepository
) {
    suspend operator fun invoke(address: String, blockchainType: BlockchainType): List<BitcoinUtxo> {
        return utxoRepository.getUtxo(address, blockchainType)
    }
}