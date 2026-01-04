package com.mangala.wallet.local.language

import com.mangala.wallet.model.language.Language


interface LanguageLocalDataSource{
    fun getCurrentLanguage(): Language
    fun changeLanguage(language: Language)
}