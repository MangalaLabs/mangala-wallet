package com.mangala.wallet.features.menu.presentation.preferences

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.mangala.wallet.menu_base.presentation.preferences.BasePreferencesScreen
import com.mangala.wallet.ui.utils.collectAsStateMultiplatform
import com.mangala.wallet.utils.analytics.MangalaAnalytics

class PreferencesScreen: BasePreferencesScreen() {

    override val screenName: String = MangalaAnalytics.Screens.PREFERENCES
    override val screenClassName: String = PreferencesScreen::class.simpleName.orEmpty()

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val screenModel: PreferencesScreenModel = getScreenModel()

        val uiModel = screenModel.uiModel.collectAsStateMultiplatform()

        BasePreferencesScreenContent(
            uiModel = uiModel.value
        ) {

        }
    }
}