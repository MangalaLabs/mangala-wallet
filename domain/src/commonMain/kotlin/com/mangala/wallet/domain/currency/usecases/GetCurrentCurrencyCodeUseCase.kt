package com.mangala.wallet.domain.currency.usecases

import com.mangala.wallet.domain.datastore.repository.DataStoreRepository

class GetCurrentCurrencyCodeUseCase(private val dataStoreRepository: DataStoreRepository) {
    suspend operator fun invoke() = dataStoreRepository.getSelectedCurrencyCode()
    suspend fun invokeFlow() = dataStoreRepository.getSelectedCurrencyCodeFlow()
}