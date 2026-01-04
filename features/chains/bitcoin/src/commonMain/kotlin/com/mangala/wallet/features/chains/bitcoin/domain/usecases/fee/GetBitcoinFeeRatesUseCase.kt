package com.mangala.wallet.features.chains.bitcoin.domain.usecases.fee

import com.mangala.wallet.features.chains.bitcoin.domain.model.fee.FeeRatePriority
import com.mangala.wallet.features.chains.bitcoin.domain.model.fee.FeeratePerVbyte
import com.mangala.wallet.features.chains.bitcoin.domain.repository.fee.BitcoinFeeRepository
import com.mangala.wallet.model.blockchain.BlockchainType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GetBitcoinFeeRatesUseCase(
    private val feeRepository: BitcoinFeeRepository
) {

    operator fun invoke(blockchainType: BlockchainType): Flow<Result<Map<FeeRatePriority, FeeratePerVbyte>>> = flow {
        val result = feeRepository.getRecommendedFeeRates(blockchainType)
        emit(result)
    }
}