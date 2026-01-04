package com.mangala.wallet.domain.token.historicalprice.mapper

import com.mangala.wallet.model.provider.coingecko.CoingeckoPriceHistoryResponse
import com.mangala.wallet.model.token.TokenHistoricalPrice
import commangalawalletdatabase.TokenHistoricalPriceEnitity

fun TokenHistoricalPriceEnitity.toTokenHistoricalPrice(): TokenHistoricalPrice {
    return TokenHistoricalPrice(
        date = this.date ?: "",
        coingeckoId = this.coinGeckoId ?: "",
        marketData = TokenHistoricalPrice.MarketData(
            currentPrice = TokenHistoricalPrice.MarketData.CurrentPrice(
                usd = this.usd ?: 0.0,
                aed = this.aed ?: 0.0,
                ars = this.ars ?: 0.0,
                aud = this.aud ?: 0.0,
                bdt = this.bdt ?: 0.0,
                bhd = this.bhd ?: 0.0,
                bmd = this.bmd ?: 0.0,
                brl = this.brl ?: 0.0,
                cad = this.cad ?: 0.0,
                chf = this.chf ?: 0.0,
                clp = this.clp ?: 0.0,
                cny = this.cny ?: 0.0,
                czk = this.czk ?: 0.0,
                dkk = this.dkk ?: 0.0,
                eur = this.eur ?: 0.0,
                gbp = this.gbp ?: 0.0,
                hkd = this.hkd ?: 0.0,
                huf = this.huf ?: 0.0,
                idr = this.idr ?: 0.0,
                ils = this.ils ?: 0.0,
                inr = this.inr ?: 0.0,
                jpy = this.jpy ?: 0.0,
                krw = this.krw ?: 0.0,
                kwd = this.kwd ?: 0.0,
                lkr = this.lkr ?: 0.0,
                mmk = this.mmk ?: 0.0,
                mxn = this.mxn ?: 0.0,
                myr = this.myr ?: 0.0,
                ngn = this.ngn ?: 0.0,
                nok = this.nok ?: 0.0,
                nzd = this.nzd ?: 0.0,
                php = this.php ?: 0.0,
                pkr = this.pkr ?: 0.0,
                pln = this.pln ?: 0.0,
                rub = this.rub ?: 0.0,
                sar = this.sar ?: 0.0,
                sek = this.sek ?: 0.0,
                sgd = this.sgd ?: 0.0,
                thb = this.thb ?: 0.0,
                `try` = this.try_ ?: 0.0,
                twd = this.twd ?: 0.0,
                uah = this.uah ?: 0.0,
                vef = this.vef ?: 0.0,
                vnd = this.vnd ?: 0.0,
                zar = this.zar ?: 0.0,
                btc = this.btc ?: 0.0
            )
        )
    )
}

fun CoingeckoPriceHistoryResponse.toTokenHistoricalPrice(date: String): TokenHistoricalPrice {
    return TokenHistoricalPrice(
        date,
        this.id.orEmpty(),
        marketData = TokenHistoricalPrice.MarketData(
            currentPrice = TokenHistoricalPrice.MarketData.CurrentPrice(
                usd = marketData?.currentPrice?.usd ?: 0.0,
                aed = marketData?.currentPrice?.aed ?: 0.0,
                ars = marketData?.currentPrice?.ars ?: 0.0,
                aud = marketData?.currentPrice?.aud ?: 0.0,
                bdt = marketData?.currentPrice?.bdt ?: 0.0,
                bhd = marketData?.currentPrice?.bhd ?: 0.0,
                bmd = marketData?.currentPrice?.bmd ?: 0.0,
                brl = marketData?.currentPrice?.brl ?: 0.0,
                cad = marketData?.currentPrice?.cad ?: 0.0,
                chf = marketData?.currentPrice?.chf ?: 0.0,
                clp = marketData?.currentPrice?.clp ?: 0.0,
                cny = marketData?.currentPrice?.cny ?: 0.0,
                czk = marketData?.currentPrice?.czk ?: 0.0,
                dkk = marketData?.currentPrice?.dkk ?: 0.0,
                eur = marketData?.currentPrice?.eur ?: 0.0,
                gbp = marketData?.currentPrice?.gbp ?: 0.0,
                hkd = marketData?.currentPrice?.hkd ?: 0.0,
                huf = marketData?.currentPrice?.huf ?: 0.0,
                idr = marketData?.currentPrice?.idr ?: 0.0,
                ils = marketData?.currentPrice?.ils ?: 0.0,
                inr = marketData?.currentPrice?.inr ?: 0.0,
                jpy = marketData?.currentPrice?.jpy ?: 0.0,
                krw = marketData?.currentPrice?.krw ?: 0.0,
                kwd = marketData?.currentPrice?.kwd ?: 0.0,
                lkr = marketData?.currentPrice?.lkr ?: 0.0,
                mmk = marketData?.currentPrice?.mmk ?: 0.0,
                mxn = marketData?.currentPrice?.mxn ?: 0.0,
                myr = marketData?.currentPrice?.myr ?: 0.0,
                ngn = marketData?.currentPrice?.ngn ?: 0.0,
                nok = marketData?.currentPrice?.nok ?: 0.0,
                nzd = marketData?.currentPrice?.nzd ?: 0.0,
                php = marketData?.currentPrice?.php ?: 0.0,
                pkr = marketData?.currentPrice?.pkr ?: 0.0,
                pln = marketData?.currentPrice?.pln ?: 0.0,
                rub = marketData?.currentPrice?.rub ?: 0.0,
                sar = marketData?.currentPrice?.sar ?: 0.0,
                sek = marketData?.currentPrice?.sek ?: 0.0,
                sgd = marketData?.currentPrice?.sgd ?: 0.0,
                thb = marketData?.currentPrice?.thb ?: 0.0,
                `try` = marketData?.currentPrice?.tryX ?: 0.0,
                twd = marketData?.currentPrice?.twd ?: 0.0,
                uah = marketData?.currentPrice?.uah ?: 0.0,
                vef = marketData?.currentPrice?.vef ?: 0.0,
                vnd = marketData?.currentPrice?.vnd ?: 0.0,
                zar = marketData?.currentPrice?.zar ?: 0.0,
                btc = marketData?.currentPrice?.btc ?: 0.0,
            )
        )
    )
}