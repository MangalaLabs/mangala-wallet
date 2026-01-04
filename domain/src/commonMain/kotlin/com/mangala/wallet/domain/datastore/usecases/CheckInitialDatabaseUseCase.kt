package com.mangala.wallet.domain.datastore.usecases

import com.mangala.wallet.domain.datastore.repository.DataStoreRepository

class CheckInitialDatabaseUseCase(private val dataStoreRepository: DataStoreRepository) {

        suspend operator fun invoke() = dataStoreRepository.getInitialDatabaseIfNeeded()

}