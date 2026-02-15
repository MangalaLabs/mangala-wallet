package com.mangala.wallet.domain.datastore.usecases

import com.mangala.wallet.domain.datastore.repository.DataStoreRepository
import kotlinx.coroutines.flow.Flow

class CheckPrePermissionDoneUseCase(
    private val dataStoreRepository: DataStoreRepository
) {
    suspend operator fun invoke(): Boolean {
        return dataStoreRepository.getPrePermissionDone()
    }

    fun flow(): Flow<Boolean> {
        return dataStoreRepository.getPrePermissionDoneFlow()
    }
}
