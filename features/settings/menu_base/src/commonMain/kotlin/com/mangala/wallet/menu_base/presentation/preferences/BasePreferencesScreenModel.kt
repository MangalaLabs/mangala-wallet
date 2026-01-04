package com.mangala.wallet.menu_base.presentation.preferences

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.mangala.wallet.domain.language.usecase.GetCurrentLanguageUseCase
import com.mangala.wallet.model.language.Language
import com.mangala.wallet.utils.BuildEnvironmentProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

open class BasePreferencesScreenModel(
    private val getCurrentLanguageUseCase: GetCurrentLanguageUseCase,
    private val buildEnvironmentProvider: BuildEnvironmentProvider
) : ScreenModel {

    protected val _uiModel = MutableStateFlow(
        PreferencesScreenUiModel(
            null,
            null,
            isDevEnvironment = buildEnvironmentProvider.isDevelopmentEnvironment()
        )
    )
    val uiModel: StateFlow<PreferencesScreenUiModel> get() = _uiModel

    init {
        screenModelScope.launch {
            getCurrentLanguage()
        }
    }

    private suspend fun getCurrentLanguage() {
        getCurrentLanguageUseCase.invokeFlow().collect { languageCode ->
            _uiModel.update {
                it.copy(language = Language.entries.find { language ->
                    language.code == languageCode
                })
            }
        }
    }
}