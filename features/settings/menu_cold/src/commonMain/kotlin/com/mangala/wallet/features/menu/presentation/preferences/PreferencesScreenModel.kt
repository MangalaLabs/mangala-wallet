package com.mangala.wallet.features.menu.presentation.preferences

import com.mangala.wallet.domain.language.usecase.GetCurrentLanguageUseCase
import com.mangala.wallet.menu_base.presentation.preferences.BasePreferencesScreenModel
import com.mangala.wallet.utils.BuildEnvironmentProvider

class PreferencesScreenModel(
    getCurrentLanguageUseCase: GetCurrentLanguageUseCase,
    buildEnvironmentProvider: BuildEnvironmentProvider
): BasePreferencesScreenModel(getCurrentLanguageUseCase, buildEnvironmentProvider)