package com.mangala.wallet.domain.language.repository

import com.mangala.wallet.model.language.Language

interface LanguageRepository {
    fun getCurrentLanguage(): Language
    fun changeLanguage(language: Language)
    fun getAllSupportedLanguages(): List<Language>
}