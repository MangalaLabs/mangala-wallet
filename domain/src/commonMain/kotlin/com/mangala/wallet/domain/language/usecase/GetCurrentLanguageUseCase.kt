package com.mangala.wallet.domain.language.usecase

import com.mangala.wallet.domain.datastore.repository.DataStoreRepository
import com.mangala.wallet.domain.language.repository.LanguageRepository

class GetCurrentLanguageUseCase(private val languageRepository: LanguageRepository, private val dataStoreRepository: DataStoreRepository) {
    operator fun invoke() = languageRepository.getCurrentLanguage()
    suspend fun invokeFlow() = dataStoreRepository.getSelectedLanguageCodeFlow()
}