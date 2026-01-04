package com.mangala.wallet.menu_base.presentation.preferences

import com.mangala.wallet.model.currency.Currency
import com.mangala.wallet.model.language.Language

data class PreferencesScreenUiModel(
    val language: Language?,
    val currency: Currency?,
    val isDevEnvironment: Boolean
)