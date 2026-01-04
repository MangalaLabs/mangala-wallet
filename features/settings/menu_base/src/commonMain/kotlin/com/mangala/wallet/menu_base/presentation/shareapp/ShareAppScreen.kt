package com.mangala.wallet.menu_base.presentation.shareapp

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.mangala.wallet.common.mokoresources.Dimensions
import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.ui.component.MangalaWalletTopBarCenteredTitle
import com.mangala.wallet.ui.component.OnboardingGradientBackground
import com.mangala.wallet.ui.utils.screenmodel.BaseScreen
import com.mangala.wallet.utils.analytics.MangalaAnalytics
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.desc.desc

class ShareAppScreen : BaseScreen<ShareAppScreenModel>() {

    override val screenName: String = MangalaAnalytics.Screens.SHARE_APP
    override val screenClassName: String = ShareAppScreen::class.simpleName.orEmpty()

    @Composable
    override fun createScreenModel(): ShareAppScreenModel {
        return getScreenModel()
    }

    override val isBottomBarVisible: Boolean = false

    @Composable
    override fun ScreenContent(screenModel: ShareAppScreenModel) {
        val navigator = LocalNavigator.currentOrThrow

        ShareApp(onBackClicked = navigator::pop)
    }

    @Composable
    fun ShareApp(onBackClicked: () -> Unit) {
        OnboardingGradientBackground {
            Scaffold(
                topBar = {
                    MangalaWalletTopBarCenteredTitle(
                        title = MR.strings.all_share_app.desc().localized(),
                        onBackClicked = onBackClicked
                    )
                },
                modifier = Modifier.statusBarsPadding(),
                backgroundColor = Color.Transparent
            ) {
                Column(
                    modifier = Modifier
                        .padding(
                            start = Dimensions.Padding.default,
                            end = Dimensions.Padding.default
                        )
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {

                }

            }
        }
    }
}