package com.mangala.wallet.features.menu.presentation.menu

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.mangala.wallet.menu_base.presentation.menu.BaseMenuScreen
import com.mangala.wallet.menu_base.presentation.menu.BaseMenuScreenContent
import com.mangala.wallet.menu_base.presentation.menu.BaseMenuScreenModel
import com.mangala.wallet.utils.analytics.MangalaAnalytics

class MenuScreen : BaseMenuScreen() {

    override val screenName: String = MangalaAnalytics.Screens.MENU
    override val screenClassName: String = MenuScreen::class.simpleName.orEmpty()

    @Composable
    override fun ScreenContent(screenModel: BaseMenuScreenModel) {
        val navigator = LocalNavigator.currentOrThrow

        BaseMenuScreenContent(
            screenModel = screenModel,
            onBackPressed = { navigator.pop() },
            secondGroupAdditionalContent = { }
        )
    }
}