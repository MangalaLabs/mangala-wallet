package com.mangala.wallet.features.chains.evmcompatible.domain.usecases

import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.mangala.wallet.domain.transaction.fee.TransactionFeeOption
import com.mangala.wallet.domain.transaction.fee.TransactionFeeType
import com.mangala.wallet.features.chains.evmcompatible.model.GasPrice
import com.mangala.wallet.utils.ext.toBigDecimal

// https://github.com/MyEtherWallet/MyEtherWallet/blob/main/src/core/helpers/gasPriceHelper.js
class GetTransactionFeeOptionsUseCase {

    operator fun invoke(estimatedGasPrice: GasPrice): List<TransactionFeeOption> {
        return when (estimatedGasPrice) {
            is GasPrice.Legacy -> getLegacyGasBasedOnType(estimatedGasPrice.legacyGasPrice.toBigDecimal())
            is GasPrice.Eip1559 -> getEip1559GasBasedOnType(estimatedGasPrice)
        }
    }

    private fun getLegacyGasBasedOnType(estimatedGasPrice: BigDecimal): List<TransactionFeeOption> {
        return TransactionFeeType.values().map {
            val gasPrice = when(it) {
                TransactionFeeType.ECONOMY -> getEconomy(estimatedGasPrice)
                TransactionFeeType.REGULAR -> getRegular(estimatedGasPrice)
                TransactionFeeType.FAST -> getFast(estimatedGasPrice)
            }

            TransactionFeeOption(
                gasPrice = gasPrice,
                baseFee = null,
                priorityFee = null,
                transactionFeeType = it,
            )
        }
    }

    private fun getEip1559GasBasedOnType(gasPrice: GasPrice.Eip1559): List<TransactionFeeOption> {
        return TransactionFeeType.values().map {
            val baseFee = getBaseFeeBasedOnType(gasPrice.baseFee.toBigDecimal(), it)
            val priorityFee = getPriorityFeeBasedOnType(gasPrice.maxPriorityFeePerGas.toBigDecimal(), it)
            val maxFeePerGas = baseFee.add(priorityFee)

            TransactionFeeOption(
                gasPrice = maxFeePerGas,
                baseFee = baseFee,
                priorityFee = priorityFee,
                transactionFeeType = it,
            )
        }
    }

    private fun getPriorityFeeBasedOnType(priorityFeeBN: BigDecimal, type: TransactionFeeType): BigDecimal {
        val minFee = getMinPriorityFee()
        var returnVal = when (type) {
            TransactionFeeType.ECONOMY -> priorityFeeBN.multiply(BigDecimal.parseString("0.8"))
            TransactionFeeType.REGULAR -> priorityFeeBN // No change
            TransactionFeeType.FAST -> priorityFeeBN.multiply(BigDecimal.parseString(("1.25")))
            else -> minFee
        }

        return if (returnVal < minFee) minFee else returnVal
    }

    private fun getBaseFeeBasedOnType(baseFeeBN: BigDecimal, type: TransactionFeeType): BigDecimal {
        return when (type) {
            TransactionFeeType.ECONOMY -> baseFeeBN.multiply(BigDecimal.parseString(("1.25")))
            TransactionFeeType.REGULAR -> baseFeeBN.multiply(BigDecimal.parseString(("1.5")))
            TransactionFeeType.FAST -> baseFeeBN.multiply(BigDecimal.parseString(("1.75")))
            else -> baseFeeBN
        }
    }

    private fun getEconomy(gasPrice: BigDecimal): BigDecimal {
        return gasPrice.multiply(ECONOMY_CONST)
    }

    private fun getRegular(gasPrice: BigDecimal): BigDecimal {
        return if (gasPrice > LIMITER) {
            var initialValue = gasPrice.multiply(MED_MULTIPLIER)
            initialValue = initialValue.add(MED_CONST)
            initialValue
        } else {
            gasPrice.multiply(OLD_MED_CONST)
        }
    }

    private fun getFast(gasPrice: BigDecimal): BigDecimal {
        return if (gasPrice > LIMITER) {
            var initialValue = gasPrice.multiply(FAST_MULTIPLIER)
            initialValue = initialValue.add(FAST_CONST)
            initialValue
        } else {
            gasPrice.multiply(OLD_FAST_CONST)
        }
    }

    private fun getMinPriorityFee(): BigDecimal {
        return BigDecimal.parseString("1.25")
    }

    companion object {
        val MED_CONST = BigDecimal.parseString("21428571428.571")
        val MED_MULTIPLIER = BigDecimal.parseString("1.0714285714286")
        val FAST_CONST = BigDecimal.parseString("42857142857.145")
        val FAST_MULTIPLIER = BigDecimal.parseString("1.1428571428571")
        val ECONOMY_CONST = BigDecimal.parseString("0.8")
        val OLD_MED_CONST = BigDecimal.parseString("1.0") // Different value compared to MEW
        val OLD_FAST_CONST = BigDecimal.parseString("1.25") // Different value compared to MEW
        val LIMITER = BigDecimal.parseString("25000000000")
    }
}