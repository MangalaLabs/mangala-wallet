package com.mangala.wallet.local.token.historicalprice

import app.cash.sqldelight.coroutines.asFlow
import com.mangala.wallet.local.MangalaWalletDatabaseWrapper
import com.mangala.wallet.model.token.TokenHistoricalPrice
import commangalawalletdatabase.TokenHistoricalPriceEnitity
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class TokenHistoricalPriceLocalDataSourceImpl(
    databaseWrapper: MangalaWalletDatabaseWrapper,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
): TokenHistoricalPriceLocalDataSource {

    private val database = databaseWrapper.instance
    private val dbQuery = database.mangalaWalletDatabaseQueries

    override suspend fun saveHistoricalPrice(tokenHistoricalPrice: TokenHistoricalPrice) = withContext(ioDispatcher) {
        with(tokenHistoricalPrice) {
            dbQuery.insertOrUpdateHistoricalPrice(
                coinGeckoId = coingeckoId,
                date = date,
                usd = marketData.currentPrice.usd,
                aed = marketData.currentPrice.aed,
                ars = marketData.currentPrice.ars,
                aud = marketData.currentPrice.aud,
                bdt = marketData.currentPrice.bdt,
                bhd = marketData.currentPrice.bhd,
                bmd = marketData.currentPrice.bmd,
                brl = marketData.currentPrice.brl,
                cad = marketData.currentPrice.cad,
                chf = marketData.currentPrice.chf,
                clp = marketData.currentPrice.clp,
                cny = marketData.currentPrice.cny,
                czk = marketData.currentPrice.czk,
                dkk = marketData.currentPrice.dkk,
                eur = marketData.currentPrice.eur,
                gbp = marketData.currentPrice.gbp,
                hkd = marketData.currentPrice.hkd,
                huf = marketData.currentPrice.huf,
                idr = marketData.currentPrice.idr,
                ils = marketData.currentPrice.ils,
                inr = marketData.currentPrice.inr,
                jpy = marketData.currentPrice.jpy,
                krw = marketData.currentPrice.krw,
                kwd = marketData.currentPrice.kwd,
                lkr = marketData.currentPrice.lkr,
                mmk = marketData.currentPrice.mmk,
                mxn = marketData.currentPrice.mxn,
                myr = marketData.currentPrice.myr,
                ngn = marketData.currentPrice.ngn,
                nok = marketData.currentPrice.nok,
                nzd = marketData.currentPrice.nzd,
                php = marketData.currentPrice.php,
                pkr = marketData.currentPrice.pkr,
                pln = marketData.currentPrice.pln,
                rub = marketData.currentPrice.rub,
                sar = marketData.currentPrice.sar,
                sek = marketData.currentPrice.sek,
                sgd = marketData.currentPrice.sgd,
                thb = marketData.currentPrice.thb,
                try_ = marketData.currentPrice.`try`,
                twd = marketData.currentPrice.twd,
                uah = marketData.currentPrice.uah,
                vef = marketData.currentPrice.vef,
                vnd = marketData.currentPrice.vnd,
                zar = marketData.currentPrice.zar,
                btc = marketData.currentPrice.btc
            )
        }
    }

    override suspend fun getPriceByDateAndId(
        coingeckoId: String,
        date: String
    ): TokenHistoricalPriceEnitity? = withContext(ioDispatcher) {
        return@withContext dbQuery.getHistoricalPriceByDateAndSymbol(
            date = date,
            coinGeckoId = coingeckoId
        ).executeAsOneOrNull()
    }

    override fun getPriceByDateAndIdFlow(
        coingeckoId: String,
        date: String
    ): Flow<TokenHistoricalPriceEnitity?> {
        return dbQuery.getHistoricalPriceByDateAndSymbol(
            date = date,
            coinGeckoId = coingeckoId
        ).asFlow().map { it.executeAsOneOrNull() }.flowOn(ioDispatcher)
    }
}