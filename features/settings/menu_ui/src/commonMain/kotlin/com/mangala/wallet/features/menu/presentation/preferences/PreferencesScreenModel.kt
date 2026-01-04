package com.mangala.wallet.features.menu.presentation.preferences

import cafe.adriel.voyager.core.model.screenModelScope
import com.mangala.wallet.domain.currency.usecases.GetCurrentCurrencyCodeUseCase
import com.mangala.wallet.domain.language.usecase.GetCurrentLanguageUseCase
import com.mangala.wallet.menu_base.presentation.preferences.BasePreferencesScreenModel
import com.mangala.wallet.model.currency.Currency
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PreferencesScreenModel(
    private val getCurrentCurrencyCodeUseCase: GetCurrentCurrencyCodeUseCase,
    getCurrentLanguageUseCase: GetCurrentLanguageUseCase,
): BasePreferencesScreenModel(getCurrentLanguageUseCase) {

    init {
        screenModelScope.launch {
            getCurrentCurrency()
        }
    }

    private suspend fun getCurrentCurrency() {
        getCurrentCurrencyCodeUseCase.invokeFlow().collect { currencyCode ->
            _uiModel.update {
                it.copy(currency = Currency.entries
                    .find { currency -> currency.code == currencyCode })
            }

        }
    }
}