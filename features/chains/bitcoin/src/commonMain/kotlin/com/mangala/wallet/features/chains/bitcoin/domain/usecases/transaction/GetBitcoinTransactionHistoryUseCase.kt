package com.mangala.wallet.features.chains.bitcoin.domain.usecases.transaction

import app.cash.paging.PagingData
import com.mangala.wallet.features.chains.bitcoin.domain.model.transaction.BitcoinTransaction
import com.mangala.wallet.features.chains.bitcoin.domain.repository.transaction.BitcoinTransactionRepository
import com.mangala.wallet.model.blockchain.BlockchainType
import kotlinx.coroutines.flow.Flow

class GetBitcoinTransactionHistoryUseCase(
    private val bitcoinTransactionRepository: BitcoinTransactionRepository
) {
    operator fun invoke(address: String, blockchainType: BlockchainType): Flow<PagingData<BitcoinTransaction>> {
        return bitcoinTransactionRepository.getTransactionHistoryByAddress(
            address = address,
            blockchainType = blockchainType
        )
    }
}