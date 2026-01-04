package com.mangala.wallet.domain.datastore.usecases

import com.mangala.wallet.domain.datastore.repository.DataStoreRepository

class SaveInitialDatabaseUseCase(private val dataStoreRepository: DataStoreRepository) {

        suspend operator fun invoke(value: Boolean) = dataStoreRepository.saveInitialDatabaseIfNeeded(value)

}