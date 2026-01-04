package com.mangala.wallet.features.chains.evmcompatible.data.model.jsonrpc

import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.ionspin.kotlin.bignum.integer.BigInteger
import com.mangala.wallet.features.chains.evmcompatible.core.toHexString
import com.mangala.wallet.features.chains.evmcompatible.model.Address
import com.mangala.wallet.features.chains.evmcompatible.model.GasPrice
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

class EstimateGasJsonRpc(
    val from: Address,
    val to: Address?,
    val amount: BigInteger?,
    val gasLimit: Long?,
    val gasPrice: GasPrice,
    val data: ByteArray?
) : JsonRpc(
    method = "eth_estimateGas",
    params = listOf( estimateGasParams(from, to, amount, gasLimit, gasPrice, data))
) {

    companion object {
        private fun estimateGasParams(from: Address, to: Address?, amount: BigInteger?, gasLimit: Long?, gasPrice: GasPrice, data: ByteArray?): EstimateGasParams {
            return when (gasPrice) {
                is GasPrice.Eip1559 -> {
                    EstimateGasParams.Eip1559(
                        from,
                        to,
                        amount,
                        gasLimit,
                        gasPrice.maxFeePerGas,
                        gasPrice.maxPriorityFeePerGas,
                        data
                    )

                }
                is GasPrice.Legacy -> {
//                    listOf<String?>(from.hex, to?.hex,amount?.toString(16), gasLimit.toString(), gasPrice.legacyGasPrice.toString(), data?.toHexString())
                    EstimateGasParams.Legacy(
                        from,
                        to,
                        amount,
                        gasLimit,
                        gasPrice.legacyGasPrice,
                        data
                    )
                }
            }
        }
    }

    private sealed class EstimateGasParams {
        data class Legacy(
            val from: Address,
            val to: Address?,
            @SerialName("value")
            val amount: BigInteger?,
            @SerialName("gas")
            val gasLimit: Long?,
            val gasPrice: Long?,
            val data: ByteArray?
        ) : EstimateGasParams(){

        }

        data class Eip1559(
            val from: Address,
            val to: Address?,
            @SerialName("value")
            val amount: BigInteger?,
            @SerialName("gas")
            val gasLimit: Long?,
            val maxFeePerGas: Long,
            val maxPriorityFeePerGas: Long,
            val data: ByteArray?
        ) : EstimateGasParams()
    }
}