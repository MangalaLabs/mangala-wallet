package com.mangala.wallet.domain.language.usecase

import com.mangala.wallet.domain.datastore.repository.DataStoreRepository
import com.mangala.wallet.domain.language.repository.LanguageRepository
import com.mangala.wallet.model.language.Language

class ChangeLanguageUseCase(private val languageRepository: LanguageRepository, private val dataStoreRepository: DataStoreRepository) {

    operator fun invoke(language: Language) = languageRepository.changeLanguage(language)
    suspend fun invokeFlow(language: String) = dataStoreRepository.saveSelectedLanguageCode(language)
}