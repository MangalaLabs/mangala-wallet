package com.mangala.wallet.menu_base.presentation.language

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.mangala.wallet.common.mokoresources.icons.MangalaWalletPack
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.Chinese
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.English
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.French
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.German
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.Italian
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.Japanese
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.Korean
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.Vietnamese
import com.mangala.wallet.domain.language.usecase.ChangeLanguageUseCase
import com.mangala.wallet.domain.language.usecase.GetAllSupportedLanguageUseCase
import com.mangala.wallet.domain.language.usecase.GetCurrentLanguageUseCase
import com.mangala.wallet.model.language.Language
import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.ui.imageloader.ImageHolder
import com.mangala.wallet.ui.utils.screenmodel.BaseScreenModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LanguageScreenModel(
    getAllSupportedLanguageUseCase: GetAllSupportedLanguageUseCase,
    private val changeLanguageUseCase: ChangeLanguageUseCase,
    private val getCurrentLanguageUseCase: GetCurrentLanguageUseCase
) : BaseScreenModel() {

    private val _uiState: MutableStateFlow<LanguageScreenUiState> = MutableStateFlow(LanguageScreenUiState.Loading)
    val uiState: StateFlow<LanguageScreenUiState> = _uiState.asStateFlow()

    private val languages = getAllSupportedLanguageUseCase()

    init {
        screenModelScope.launch {
            getAllSupportedLanguages()
        }
    }

    fun changeLanguage(language: Language) {
        screenModelScope.launch {
            changeLanguageUseCase.invokeFlow(language.code)
        }
    }


    private suspend fun getAllSupportedLanguages() {
        val currentLanguage = getCurrentLanguageUseCase.invokeFlow()
        currentLanguage.collect { selectedLanguage ->
            _uiState.update {
                LanguageScreenUiState.Success(languages.toLanguageUiModelList(selectedLanguage), "")
            }
        }
    }

    fun onSearchTextChanged(text: String) {
        _uiState.update {
            (it as? LanguageScreenUiState.Success)?.copy(query = text) ?: it
        }
    }

    private fun List<Language>.toLanguageUiModelList(currentLanguageCode: String) =
        this.map { language ->
            LanguageUiModel(
                language = language,
                iconLanguage = language.getImageHolder(),
                isSelected = language.code == currentLanguageCode
            )
        }

    private fun Language.getImageHolder(): ImageHolder {
        return when (this) {
            Language.Chinese -> ImageHolder.Vector(MangalaWalletPack.Chinese)
            Language.English -> ImageHolder.Vector(MangalaWalletPack.English)
            Language.French -> ImageHolder.Vector(MangalaWalletPack.French)
            Language.German -> ImageHolder.Vector(MangalaWalletPack.German)
            Language.Italian -> ImageHolder.Vector(MangalaWalletPack.Italian)
            Language.Japanese -> ImageHolder.Vector(MangalaWalletPack.Japanese)
            Language.Korean -> ImageHolder.Vector(MangalaWalletPack.Korean)
            Language.Spanish -> ImageHolder.Paint(MR.images.spanish)
            Language.Vietnamese -> ImageHolder.Vector(MangalaWalletPack.Vietnamese)
        }
    }
}