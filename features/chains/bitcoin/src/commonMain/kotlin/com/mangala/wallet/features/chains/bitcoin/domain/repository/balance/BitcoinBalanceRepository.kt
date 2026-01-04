package com.mangala.wallet.features.chains.bitcoin.domain.repository.balance

import com.mangala.wallet.features.chains.bitcoin.data.remote.balance.response.MempoolGetBalanceResponse
import com.mangala.wallet.features.chains.bitcoin.domain.model.balance.BitcoinBalance
import com.mangala.wallet.model.blockchain.BlockchainType
import com.mangala.wallet.model.util.Resource
import kotlinx.coroutines.flow.Flow

interface BitcoinBalanceRepository {
    suspend fun getBalance(
        forceRefresh: Boolean,
        accountId: String,
        address: String,
        blockchainType: BlockchainType
    ): Flow<Resource<BitcoinBalance?>>
}