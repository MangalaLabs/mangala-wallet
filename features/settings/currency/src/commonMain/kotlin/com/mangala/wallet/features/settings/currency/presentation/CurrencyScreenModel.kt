package com.mangala.wallet.features.settings.currency.presentation

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.mangala.wallet.domain.currency.usecases.ChangeCurrencyUseCase
import com.mangala.wallet.domain.currency.usecases.GetCurrentCurrencyCodeUseCase
import com.mangala.wallet.model.currency.Currency
import com.mangala.wallet.ui.utils.screenmodel.BaseScreenModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CurrencyScreenModel(
    private val currenciesSupported: List<CurrencyScreenUiModel>,
    private val changeCurrencyUseCase: ChangeCurrencyUseCase,
    private val getCurrentCurrencyCodeUseCase: GetCurrentCurrencyCodeUseCase
) : BaseScreenModel() {

    private val _currenciesList = MutableStateFlow(listOf<CurrencyScreenUiModel>())
    val currenciesList = _currenciesList.asStateFlow()

    private val _searchText = MutableStateFlow("")
    val searchText = _searchText.asStateFlow()

    init {
        screenModelScope.launch {
            getAllSupportedCurrencies(currenciesSupported)
        }
    }

    fun changeCurrency(currency: Currency) {
        screenModelScope.launch {
            changeCurrencyUseCase.invoke(currency)
        }
    }

    private suspend fun getAllSupportedCurrencies(currencies: List<CurrencyScreenUiModel>) {
        val currentCurrency = getCurrentCurrencyCodeUseCase.invokeFlow()

        currentCurrency.collect {
            _currenciesList.update {
                currencies.map { currencyModel ->
                    CurrencyScreenUiModel(
                        currency = currencyModel.currency,
                        currencyName = currencyModel.currencyName,
                        isSelected = currencyModel.currency.code == currentCurrency.first(),
                        currencyIcon = currencyModel.currencyIcon
                    )
                }
            }
        }
    }


    fun onSearchTextChanged(text: String) {
        _searchText.value = text
        _currenciesList.update {
            if (text.isNotBlank()) {
                currenciesSupported.filter { it.currencyName.contains(text, true) }
            } else {
                currenciesSupported
            }
        }
    }
}