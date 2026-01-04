package com.mangala.wallet.local.currency

import app.cash.sqldelight.coroutines.asFlow
import com.mangala.wallet.local.securestorage.SecureStorageWrapper
import com.mangala.wallet.model.currency.Currency
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

class CurrencyLocalDataSourceImpl(private val storageWrapper: SecureStorageWrapper) :
    CurrencyLocalDataSource {
    override fun getCurrentCurrencyCodeFlow(): Flow<String> {
        return flowOf(storageWrapper.getValue(CURRENCY_KEY) ?: Currency.DEFAULT_CURRENCY.code)
    }

    override fun changeCurrency(currency: Currency) {
        storageWrapper.saveValue(CURRENCY_KEY, currency.code)
    }

    companion object {
        private const val CURRENCY_KEY = "currency"
    }
}