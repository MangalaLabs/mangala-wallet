package com.mangala.eticket.presentation.home

import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import cafe.adriel.voyager.core.registry.ScreenRegistry
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.mangala.eticket.ETicketSharedScreen
import com.mangala.eticket.presentation.favourite.UserEventFavouriteScreen
import com.mangala.wallet.ui.MangalaTextButton
import com.mangala.wallet.ui.utils.collectAsStateMultiplatform
import com.mangala.wallet.ui.utils.screenmodel.BaseScreen
import com.mangala.wallet.utils.analytics.MangalaAnalytics

class ETicketHomeScreen: BaseScreen<ETicketHomeScreenModel>() {

    override val screenName: String = MangalaAnalytics.Screens.ETICKET_HOME
    override val screenClassName: String = ETicketHomeScreen::class.simpleName.orEmpty()

    override val isBottomBarVisible: Boolean = false

    @Composable
    override fun createScreenModel(): ETicketHomeScreenModel {
        return getScreenModel()
    }

    @Composable
    override fun ScreenContent(screenModel: ETicketHomeScreenModel) {
        val localNavigator = LocalNavigator.currentOrThrow
        val isNavToCategory = screenModel.isNavToCategoryPage.collectAsStateMultiplatform().value

        Button(
            onClick = { screenModel.onClickToCategory() },
            colors = ButtonDefaults.buttonColors(
                backgroundColor = Color.Blue,
                contentColor = Color.White
            )
        ) {
            Text(text = "Category")
        }

        MangalaTextButton(
            text = "Your favourite events",
            color = Color.Black,
            onClick = {
                localNavigator.push(UserEventFavouriteScreen())
            }
        )

        if (isNavToCategory) {
            val categoryScreen = ScreenRegistry.get(ETicketSharedScreen.CategoryScreen)
            localNavigator.push(categoryScreen)
            screenModel.onNavToCategorySuccess()
        }
    }
}