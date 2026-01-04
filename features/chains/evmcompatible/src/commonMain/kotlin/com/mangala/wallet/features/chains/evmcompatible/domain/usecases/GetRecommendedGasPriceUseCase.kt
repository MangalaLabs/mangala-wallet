package com.mangala.wallet.features.chains.evmcompatible.domain.usecases

import com.mangala.wallet.features.chains.evmcompatible.core.hexStringToLongOrNull
import com.mangala.wallet.features.chains.evmcompatible.data.model.provider.infura.FeeHistoryDto
import com.mangala.wallet.features.chains.evmcompatible.data.model.provider.infura.FeeHistoryModel
import com.mangala.wallet.features.chains.evmcompatible.data.model.provider.infura.GasPriceDto
import com.mangala.wallet.features.chains.evmcompatible.model.GasPrice
import com.mangala.wallet.model.blockchain.Chain
import kotlinx.serialization.json.Json

class GetRecommendedGasPriceUseCase(
    private val getFeeHistoryUseCase: GetFeeHistoryUseCase,
    private val getGasPriceUseCase: GetGasPriceUseCase,
    private val parsingJson: Json
) {

    suspend operator fun invoke(chain: Chain, url: String, id: Int): Result<GasPrice> {

        return try {
            if (chain.isEIP1559Supported) {
                getEip1559GasPrice(url, id, parsingJson, chain)
            } else {
                getLegacyGasPrice(url, id, parsingJson)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun getLegacyGasPrice(
        url: String,
        id: Int,
        json: Json
    ): Result<GasPrice.Legacy> {
        val gasPrice = getGasPriceUseCase(url, id)
        val gasPriceDto = json.decodeFromString(GasPriceDto.serializer(), gasPrice)
        return gasPriceDto.result?.hexStringToLongOrNull()?.let { gasPrice ->
            Result.success(GasPrice.Legacy(gasPrice))
        } ?: Result.failure(Exception("Gas price is null"))
    }

    private suspend fun getEip1559GasPrice(
        url: String,
        id: Int,
        json: Json,
        chain: Chain
    ): Result<GasPrice> {
        val feeHistory = getFeeHistoryUseCase(url, id)

        val feeHistoryDto = json.decodeFromString(FeeHistoryDto.serializer(), feeHistory)
        val feeHistoryModel = feeHistoryDto.result?.mapToDomainModel()
        feeHistoryModel?.let {
            return handleFeeHistory(it)
        }
        return Result.failure(Exception("Fee history is null"))
    }

    private fun handleFeeHistory(feeHistory: FeeHistoryModel): Result<GasPrice> {
        var recommendedBaseFee: Long? = null
        var recommendedPriorityFee: Long? = null

        feeHistory.baseFeePerGas.lastOrNull()?.let { currentBaseFee ->
            recommendedBaseFee = currentBaseFee
        }

        var priorityFeeSum: Long = 0
        var priorityFeesCount = 0
        feeHistory.reward.forEach { priorityFeeArray ->
            priorityFeeArray.firstOrNull()?.let { priorityFee ->
                priorityFeeSum += priorityFee
                priorityFeesCount += 1
            }
        }

        if (priorityFeesCount > 0) {
            recommendedPriorityFee = priorityFeeSum / priorityFeesCount
        }

        recommendedBaseFee?.let { baseFee ->
            recommendedPriorityFee?.let { priorityFee ->
                val gasPrice = GasPrice.Eip1559(
                    maxFeePerGas = baseFee + priorityFee,
                    maxPriorityFeePerGas = priorityFee,
                    baseFee = baseFee
                )
                return Result.success(gasPrice)
            }
        }
        return Result.failure(Exception("Can't get recommended gas price"))
    }
}