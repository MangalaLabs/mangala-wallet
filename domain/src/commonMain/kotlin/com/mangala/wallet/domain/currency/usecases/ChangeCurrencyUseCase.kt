package com.mangala.wallet.domain.currency.usecases

import com.mangala.wallet.domain.currency.repository.CurrencyRepository
import com.mangala.wallet.domain.datastore.repository.DataStoreRepository
import com.mangala.wallet.model.currency.Currency

class ChangeCurrencyUseCase(private val dataStoreRepository: DataStoreRepository) {
    suspend operator fun invoke(currency: Currency) = dataStoreRepository.saveSelectedCurrencyCode(currency.code)
}