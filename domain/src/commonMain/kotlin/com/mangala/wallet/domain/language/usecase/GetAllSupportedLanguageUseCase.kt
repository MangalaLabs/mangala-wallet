package com.mangala.wallet.domain.language.usecase

import com.mangala.wallet.domain.language.repository.LanguageRepository

class GetAllSupportedLanguageUseCase(private val languageRepository: LanguageRepository) {

    operator fun invoke() = languageRepository.getAllSupportedLanguages()
}