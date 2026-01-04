package com.mangala.wallet.local.currency

import com.mangala.wallet.model.currency.Currency
import kotlinx.coroutines.flow.Flow

interface CurrencyLocalDataSource {
    fun getCurrentCurrencyCodeFlow(): Flow<String>
    fun changeCurrency(currency: Currency)
}
