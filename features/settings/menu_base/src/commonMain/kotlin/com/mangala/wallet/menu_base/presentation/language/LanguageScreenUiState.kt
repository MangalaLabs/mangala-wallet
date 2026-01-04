package com.mangala.wallet.menu_base.presentation.language

import com.mangala.wallet.model.language.Language
import com.mangala.wallet.ui.imageloader.ImageHolder

sealed interface LanguageScreenUiState {
    data object Loading: LanguageScreenUiState
    data class Success(private val languageUiModels: List<LanguageUiModel>, val query: String): LanguageScreenUiState {
        val filteredLanguageUiModels: List<LanguageUiModel>
            get() = languageUiModels.filter { it.language.languageName.contains(query, ignoreCase = true) }
    }
}

data class LanguageUiModel(
    val language: Language,
    val iconLanguage: ImageHolder,
    val isSelected: Boolean = false
)
