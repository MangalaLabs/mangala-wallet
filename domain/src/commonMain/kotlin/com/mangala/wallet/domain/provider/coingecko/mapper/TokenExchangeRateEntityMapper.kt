package com.mangala.wallet.domain.provider.coingecko.mapper

import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.mangala.wallet.model.provider.coingecko.CoinGeckoTokenPriceModel
import com.mangala.wallet.utils.toBigDecimalOrNull
import commangalawalletdatabase.TokenExchangeRateEntity

fun List<TokenExchangeRateEntity>.toCoinGeckoTokenPriceModel(): CoinGeckoTokenPriceModel {
    val mappedData = associateBy(
        keySelector = {
            it.quote_currency
        },
        valueTransform = {
            it.rate.toBigDecimalOrNull() ?: BigDecimal.ZERO
        }
    )

    return CoinGeckoTokenPriceModel(
        data = mappedData
    )
}