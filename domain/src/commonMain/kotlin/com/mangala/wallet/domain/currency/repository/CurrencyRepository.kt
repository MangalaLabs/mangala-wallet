package com.mangala.wallet.domain.currency.repository

import com.mangala.wallet.model.currency.Currency
import kotlinx.coroutines.flow.Flow

interface CurrencyRepository {
    fun getCurrentCurrencyCodeFlow(): Flow<String>
    fun changeCurrency(currency: Currency)
}