package com.mangala.wallet.domain.datastore.usecases

import com.mangala.wallet.domain.datastore.repository.DataStoreRepository

class SavePrePermissionDoneUseCase(
    private val dataStoreRepository: DataStoreRepository
) {
    suspend operator fun invoke(done: Boolean = true) {
        dataStoreRepository.savePrePermissionDone(done)
    }
}
