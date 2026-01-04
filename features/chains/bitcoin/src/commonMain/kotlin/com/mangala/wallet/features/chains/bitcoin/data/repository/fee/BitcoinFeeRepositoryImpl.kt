package com.mangala.wallet.features.chains.bitcoin.data.repository.fee

import com.mangala.wallet.features.chains.bitcoin.data.remote.balance.MempoolRemoteDataSource
import com.mangala.wallet.features.chains.bitcoin.domain.model.fee.FeeRatePriority
import com.mangala.wallet.features.chains.bitcoin.domain.model.fee.FeeratePerVbyte
import com.mangala.wallet.features.chains.bitcoin.domain.repository.fee.BitcoinFeeRepository
import com.mangala.wallet.model.blockchain.BlockchainType
import com.mangala.wallet.remote.di.ApiResponse

class BitcoinFeeRepositoryImpl(
    private val mempoolRemoteDataSource: MempoolRemoteDataSource
) : BitcoinFeeRepository {

    override suspend fun getRecommendedFeeRates(blockchainType: BlockchainType): Result<Map<FeeRatePriority, FeeratePerVbyte>> {
        val response = (mempoolRemoteDataSource.getRecommendedFees() as? ApiResponse.Success)?.body
            ?: return Result.failure(
                Exception("Failed to fetch recommended fee rates")
            )

        val feeRateMap = mapOf(
            FeeRatePriority.FASTEST to FeeratePerVbyte(response.fastestFee.toLong()),
            FeeRatePriority.MEDIUM to FeeratePerVbyte(response.halfHourFee.toLong()),
            FeeRatePriority.SLOW to FeeratePerVbyte(response.hourFee.toLong()),
            FeeRatePriority.ECONOMY to FeeratePerVbyte(response.economyFee.toLong()),
            FeeRatePriority.MINIMUM to FeeratePerVbyte(response.minimumFee.toLong())
        )

        return Result.success(feeRateMap)
    }
}