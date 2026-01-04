package com.mangala.wallet.local.language

import com.mangala.wallet.local.securestorage.SecureStorageWrapper
import com.mangala.wallet.model.language.Language

class LanguageLocalDataSourceImpl(private val storageWrapper: SecureStorageWrapper) : LanguageLocalDataSource {
    override fun getCurrentLanguage(): Language {
        val currentLanguageCode = storageWrapper.getValue(LANGUAGE_KEY)
        val currentLanguage = Language.values().find { it.code == currentLanguageCode }
        return currentLanguage ?: Language.English
    }

    override fun changeLanguage(language: Language) {
        storageWrapper.saveValue(LANGUAGE_KEY, language.code)
    }

    companion object {
        private const val LANGUAGE_KEY = "language"
    }
}