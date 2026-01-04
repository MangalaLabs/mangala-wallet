package com.mangala.wallet.domain.language.repository

import com.mangala.wallet.local.language.LanguageLocalDataSource
import com.mangala.wallet.model.language.Language

class LanguageRepositoryImpl(private val languageLocalDataSource: LanguageLocalDataSource) : LanguageRepository {

    override fun getCurrentLanguage() = languageLocalDataSource.getCurrentLanguage()

    override fun changeLanguage(language: Language) = languageLocalDataSource.changeLanguage(language)

    override fun getAllSupportedLanguages() = Language.values().toList()

}