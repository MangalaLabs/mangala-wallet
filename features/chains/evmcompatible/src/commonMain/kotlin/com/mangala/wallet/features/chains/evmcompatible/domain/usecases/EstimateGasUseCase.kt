package com.mangala.wallet.features.chains.evmcompatible.domain.usecases

import com.ionspin.kotlin.bignum.integer.BigInteger
import com.mangala.wallet.features.chains.evmcompatible.core.hexStringToLongOrNull
import com.mangala.wallet.features.chains.evmcompatible.data.model.jsonrpc.EstimateGasJsonRpc
import com.mangala.wallet.features.chains.evmcompatible.data.model.provider.infura.EstimateGasDto
import com.mangala.wallet.features.chains.evmcompatible.domain.repository.NodeRepository
import com.mangala.wallet.features.chains.evmcompatible.model.Address
import com.mangala.wallet.features.chains.evmcompatible.model.GasPrice
import com.mangala.wallet.features.chains.evmcompatible.model.TransactionData
import kotlinx.serialization.json.Json

class EstimateGasUseCase(
    private val nodeRepository: NodeRepository,
    private val parsingJson: Json
) {

    private val maxGasLimit: Long = 2_000_000

    // Passing in transactionData from caller for reusability
    suspend operator fun invoke(
        url: String,
        id: Int,
        from: Address,
        to: Address?,
        amount: BigInteger,
        gasPrice: GasPrice,
        transactionData: TransactionData?,
        isContract: Boolean = false
    ): Long? {
        val estimateGasJsonRpc = if (transactionData != null) {
            EstimateGasJsonRpc(
                from,
                transactionData.to,
                transactionData.value,
                maxGasLimit,
                gasPrice,
                transactionData.input
            )
        } else {
            EstimateGasJsonRpc(
                from,
                to,
                amount,
                maxGasLimit,
                gasPrice,
                null
            )
        }
        val nodeResponse =  nodeRepository.estimateGas(url, id, estimateGasJsonRpc, isContract)
        println("EstimateGasUseCase: data: $nodeResponse")
        return try {
            val gas = nodeResponse.result?.hexStringToLongOrNull() // TODO: Check if using long is enough for data size
            gas
        } catch (e: Exception) {
            null
        }
    }

    suspend fun estimateGas(url: String, id: Int, from: Address, transactionData: TransactionData, gasPrice: GasPrice): Long?{
        return invoke(url, id, from, transactionData.to, transactionData.value, gasPrice, transactionData)
    }
}