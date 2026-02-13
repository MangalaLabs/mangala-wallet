package com.mangala.features.wallet.presentationv2.evm.model

import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.ionspin.kotlin.bignum.decimal.DecimalMode
import com.ionspin.kotlin.bignum.decimal.RoundingMode
import com.mangala.features.wallet.presentationv2.evm.EVMAccountInfo
import com.mangala.wallet.model.token.domain.TokenBalanceModel
import com.mangala.wallet.utils.calculatingDecimalMode

data class EVMAggregatedToken(
    val contractAddress: String,
    val contractSymbol: String,
    val contractName: String,
    val logoUrl: String,
    val contractDecimals: Long,
    val totalBalance: BigDecimal,
    val totalValueUsd: BigDecimal,
    val priceChangePercentage24h: String?,
    val currentPrice: String?,
    val isCoin: Boolean,
    val accountBreakdown: List<EVMTokenAccountBreakdown>
)

data class EVMTokenAccountBreakdown(
    val accountName: String,
    val accountAddress: String,
    val balance: BigDecimal,
    val valueUsd: BigDecimal,
    val percentage: Float
)

fun List<EVMAccountInfo>.toAggregatedTokens(): List<EVMAggregatedToken> {
    data class TokenEntry(
        val accountName: String,
        val accountAddress: String,
        val token: TokenBalanceModel
    )

    val allEntries = this.flatMap { account ->
        (account.balances ?: emptyList()).map { token ->
            TokenEntry(
                accountName = account.accountName,
                accountAddress = account.address,
                token = token
            )
        }
    }

    val grouped = allEntries.groupBy { it.token.contractAddress.lowercase() }

    return grouped.map { (_, entries) ->
        val firstToken = entries.first().token

        val totalBalance = entries.fold(BigDecimal.ZERO) { acc, entry ->
            val rawBalance = BigDecimal.parseString(entry.token.balance)
            acc + rawBalance
        }

        val totalValueUsd = entries.fold(BigDecimal.ZERO) { acc, entry ->
            acc + (entry.token.todaysValue ?: BigDecimal.ZERO)
        }

        val breakdown = entries.map { entry ->
            val rawBalance = BigDecimal.parseString(entry.token.balance)
            val balanceHuman = rawBalance.divide(
                BigDecimal.TEN.pow(firstToken.contractDecimals.toInt()),
                calculatingDecimalMode
            )
            val valueUsd = entry.token.todaysValue ?: BigDecimal.ZERO
            val pct = if (totalBalance > BigDecimal.ZERO) {
                rawBalance.divide(totalBalance, DecimalMode(10, RoundingMode.ROUND_HALF_CEILING))
                    .floatValue(exactRequired = false)
            } else {
                0f
            }
            EVMTokenAccountBreakdown(
                accountName = entry.accountName,
                accountAddress = entry.accountAddress,
                balance = balanceHuman,
                valueUsd = valueUsd,
                percentage = pct
            )
        }

        val totalBalanceHuman = totalBalance.divide(
            BigDecimal.TEN.pow(firstToken.contractDecimals.toInt()),
            calculatingDecimalMode
        )

        EVMAggregatedToken(
            contractAddress = firstToken.contractAddress,
            contractSymbol = firstToken.contractSymbol,
            contractName = firstToken.contractName,
            logoUrl = firstToken.logoUrl,
            contractDecimals = firstToken.contractDecimals,
            totalBalance = totalBalanceHuman,
            totalValueUsd = totalValueUsd,
            priceChangePercentage24h = firstToken.priceChangePercentage24h,
            currentPrice = firstToken.currentPrice,
            isCoin = firstToken.isCoin,
            accountBreakdown = breakdown
        )
    }
}
