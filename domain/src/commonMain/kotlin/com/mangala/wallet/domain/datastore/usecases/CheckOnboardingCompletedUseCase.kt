package com.mangala.wallet.domain.datastore.usecases

import com.mangala.wallet.domain.datastore.repository.DataStoreRepository
import kotlinx.coroutines.flow.Flow

class CheckOnboardingCompletedUseCase(
    private val dataStoreRepository: DataStoreRepository
) {
    suspend operator fun invoke(): Boolean {
        return dataStoreRepository.getOnboardingCompleted()
    }
    
    fun flow(): Flow<Boolean> {
        return dataStoreRepository.getOnboardingCompletedFlow()
    }
}