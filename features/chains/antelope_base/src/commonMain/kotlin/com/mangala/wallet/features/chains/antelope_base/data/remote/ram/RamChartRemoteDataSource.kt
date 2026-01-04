package com.mangala.wallet.features.chains.antelope_base.data.remote.ram

import com.mangala.antelope.base.api.model.eoseyes.RamOhlcResponse
import com.mangala.wallet.model.blockchain.BlockchainType
import com.mangala.wallet.remote.di.ApiResponse
import com.mangala.wallet.remote.network.CustomError

interface RamChartRemoteDataSource {
    suspend fun getRamOhlcData(blockchainType: BlockchainType, unit: String, start: Long, end: Long): ApiResponse<RamOhlcResponse, CustomError>
}