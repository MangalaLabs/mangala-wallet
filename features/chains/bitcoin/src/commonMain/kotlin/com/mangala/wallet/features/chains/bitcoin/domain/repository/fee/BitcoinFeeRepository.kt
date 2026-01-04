package com.mangala.wallet.features.chains.bitcoin.domain.repository.fee

import com.mangala.wallet.features.chains.bitcoin.domain.model.fee.FeeratePerVbyte
import com.mangala.wallet.features.chains.bitcoin.domain.model.fee.FeeRatePriority
import com.mangala.wallet.model.blockchain.BlockchainType

interface BitcoinFeeRepository {
    suspend fun getRecommendedFeeRates(blockchainType: BlockchainType): Result<Map<FeeRatePriority, FeeratePerVbyte>>
}