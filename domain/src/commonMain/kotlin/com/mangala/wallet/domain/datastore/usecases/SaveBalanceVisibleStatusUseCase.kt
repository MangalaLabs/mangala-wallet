package com.mangala.wallet.domain.datastore.usecases

import com.mangala.wallet.domain.datastore.repository.DataStoreRepository

class SaveBalanceVisibleStatusUseCase(private val dataStoreRepository: DataStoreRepository) {
    suspend operator fun invoke(value: Boolean) = dataStoreRepository.saveBalanceVisibleStatus(value)
}