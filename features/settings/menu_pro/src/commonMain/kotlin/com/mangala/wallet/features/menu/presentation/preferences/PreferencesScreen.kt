package com.mangala.wallet.features.menu.presentation.preferences

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.registry.rememberScreen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.mangala.wallet.menu_base.presentation.preferences.BasePreferencesScreen
import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.ui.SharedScreen
import com.mangala.wallet.ui.component.OnboardingGradientBackground
import com.mangala.wallet.ui.utils.collectAsStateMultiplatform
import com.mangala.wallet.utils.analytics.MangalaAnalytics
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.desc.desc
import kotlin.experimental.ExperimentalObjCRefinement
import kotlin.native.HiddenFromObjC

@OptIn(ExperimentalObjCRefinement::class)
@HiddenFromObjC
class PreferencesScreen : BasePreferencesScreen() {

    override val screenName: String = MangalaAnalytics.Screens.PREFERENCES
    override val screenClassName: String = PreferencesScreen::class.simpleName.orEmpty()

    @Composable
    override fun Content() {
        val currencyScreen = rememberScreen(SharedScreen.CurrencyScreen)

        val navigator = LocalNavigator.currentOrThrow
        val screenModel: PreferencesScreenModel = getScreenModel()

        val uiModel = screenModel.uiModel.collectAsStateMultiplatform().value

        OnboardingGradientBackground {
            BasePreferencesScreenContent(
                uiModel = uiModel
            ) {
                if (uiModel.isDevEnvironment) {
                    PreferencesRow(
                        title = MR.strings.all_currency.desc().localized(),
                        showSelectedName = uiModel.currency?.code ?: "",
                        onClickNavigate = { navigator.push(currencyScreen) },
                        url = uiModel.currency?.flagUrl ?: ""
                    )
                }
            }
        }
    }
}