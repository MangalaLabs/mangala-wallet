package com.mangala.wallet.features.chains.antelope_base.data.remote.ram

import com.mangala.antelope.base.api.model.eoseyes.RamOhlcResponse
import com.mangala.wallet.model.blockchain.BlockchainType
import com.mangala.wallet.remote.di.ApiResponse
import com.mangala.wallet.remote.di.safeApiCall
import com.mangala.wallet.remote.network.CustomError

class EosEyesRemoteDataSource(
    private val eosEyesApi: EosEyesApi
): RamChartRemoteDataSource {
    override suspend fun getRamOhlcData(blockchainType: BlockchainType, unit: String, start: Long, end: Long): ApiResponse<RamOhlcResponse, CustomError> {
        return when (blockchainType) {
            BlockchainType.Eos -> {
                safeApiCall { eosEyesApi.getRamOhlcData(unit, start, end) }
            }
            BlockchainType.EosJungleTestnet -> {
                ApiResponse.Error.UnknownError("Unsupported blockchain type for RAM OHLC data fetching")
            }
            else -> throw IllegalArgumentException("Unsupported blockchain type")
        }
    }
}