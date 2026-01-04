package com.mangala.wallet.model.token

import com.mangala.wallet.model.currency.Currency
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

data class TokenHistoricalPrice(
    val date: String,
    val coingeckoId: String,
    val marketData: MarketData
) {
    data class MarketData(
        val currentPrice: CurrentPrice
    ) {
        data class CurrentPrice(
            val usd: Double,
            val aed: Double,
            val ars: Double,
            val aud: Double,
            val bdt: Double,
            val bhd: Double,
            val bmd: Double,
            val brl: Double,
            val cad: Double,
            val chf: Double,
            val clp: Double,
            val cny: Double,
            val czk: Double,
            val dkk: Double,
            val eur: Double,
            val gbp: Double,
            val hkd: Double,
            val huf: Double,
            val idr: Double,
            val ils: Double,
            val inr: Double,
            val jpy: Double,
            val krw: Double,
            val kwd: Double,
            val lkr: Double,
            val mmk: Double,
            val mxn: Double,
            val myr: Double,
            val ngn: Double,
            val nok: Double,
            val nzd: Double,
            val php: Double,
            val pkr: Double,
            val pln: Double,
            val rub: Double,
            val sar: Double,
            val sek: Double,
            val sgd: Double,
            val thb: Double,
            val `try`: Double,
            val twd: Double,
            val uah: Double,
            val vef: Double,
            val vnd: Double,
            val zar: Double,
            val btc: Double
        )
    }

    fun getPriceInCurrency(currency: Currency): Double {
        with(marketData.currentPrice) {
            return when(currency) {
                Currency.USD -> usd
                Currency.AED -> aed
                Currency.ARS -> ars
                Currency.AUD -> aud
                Currency.BDT -> bdt
                Currency.BHD -> bhd
                Currency.BMD -> bmd
                Currency.BRL -> brl
                Currency.CAD -> cad
                Currency.CHF -> chf
                Currency.CLP -> clp
                Currency.CNY -> cny
                Currency.CZK -> czk
                Currency.DKK -> dkk
                Currency.EUR -> eur
                Currency.GBP -> gbp
                Currency.HKD -> hkd
                Currency.HUF -> huf
                Currency.IDR -> idr
                Currency.ILS -> ils
                Currency.INR -> inr
                Currency.JPY -> jpy
                Currency.KRW -> krw
                Currency.KWD -> kwd
                Currency.LKR -> lkr
                Currency.MMK -> mmk
                Currency.MXN -> mxn
                Currency.MYR -> myr
                Currency.NGN -> ngn
                Currency.NOK -> nok
                Currency.NZD -> nzd
                Currency.PHP -> php
                Currency.PKR -> pkr
                Currency.PLN -> pln
                Currency.RUB -> rub
                Currency.SAR -> sar
                Currency.SEK -> sek
                Currency.SGD -> sgd
                Currency.THB -> thb
                Currency.TRY -> `try`
                Currency.TWD -> twd
                Currency.UAH -> uah
                Currency.VEF -> vef
                Currency.VND -> vnd
                Currency.ZAR -> zar
                Currency.BTC -> btc
            }
        }
    }
}