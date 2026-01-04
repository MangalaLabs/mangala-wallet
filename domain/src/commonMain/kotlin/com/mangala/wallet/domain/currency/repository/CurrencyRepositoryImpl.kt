package com.mangala.wallet.domain.currency.repository

import com.mangala.wallet.local.currency.CurrencyLocalDataSource
import com.mangala.wallet.model.currency.Currency
import kotlinx.coroutines.flow.Flow

class CurrencyRepositoryImpl(private val currencyLocalDataSource: CurrencyLocalDataSource) : CurrencyRepository {
    override fun getCurrentCurrencyCodeFlow(): Flow<String> {
        return currencyLocalDataSource.getCurrentCurrencyCodeFlow()
    }

    override fun changeCurrency(currency: Currency) {
        currencyLocalDataSource.changeCurrency(currency)
    }

}