package com.mangala.wallet.menu_base.presentation.connectwithus

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Scaffold
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.mangala.wallet.common.mokoresources.CornerRadius
import com.mangala.wallet.common.mokoresources.Dimensions
import com.mangala.wallet.common.mokoresources.Spacing
import com.mangala.wallet.common.mokoresources.icons.MangalaWalletPack
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.Facebook
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.Reddit
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.Telegram
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.Twitter
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.Youtube
import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.ui.TextNormal
import com.mangala.wallet.ui.component.MangalaWalletTopBarCenteredTitle
import com.mangala.wallet.ui.component.OnboardingGradientBackground
import com.mangala.wallet.ui.theme.mangalaColors
import com.mangala.wallet.ui.utils.screenmodel.BaseScreen
import com.mangala.wallet.utils.analytics.MangalaAnalytics
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.desc.desc

class ConnectWithUsScreen : BaseScreen<ConnectWithUsScreenModel>() {

    override val screenName: String = MangalaAnalytics.Screens.CONNECT_WITH_US
    override val screenClassName: String = ConnectWithUsScreen::class.simpleName.orEmpty()

    override val isBottomBarVisible: Boolean = false

    @Composable
    override fun createScreenModel(): ConnectWithUsScreenModel {
        return getScreenModel()
    }

    @Composable
    override fun ScreenContent(screenModel: ConnectWithUsScreenModel) {
        val navigator = LocalNavigator.currentOrThrow

        ConnectWithUs(onBackPressed = { navigator.pop() })
    }

    @Composable
    fun ConnectWithUs(onBackPressed: () -> Unit) {
        OnboardingGradientBackground {
            Scaffold(
                topBar = {
                    MangalaWalletTopBarCenteredTitle(
                        title = MR.strings.all_connect_with_us.desc().localized(),
                        onBackClicked = onBackPressed
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
                    Spacer(modifier = Modifier.height(Spacing.SMALL))
                    Column(
                        modifier = Modifier.clip(RoundedCornerShape(CornerRadius.Small)),
                        verticalArrangement = Arrangement.spacedBy(1.dp)
                    ) {
                        ConnectWithUsRow(
                            title = "Telegram",
                            onClickNavigate = {},
                            iconRepresent = MangalaWalletPack.Telegram
                        )
                        ConnectWithUsRow(
                            title = "Twitter",
                            onClickNavigate = {},
                            iconRepresent = MangalaWalletPack.Twitter
                        )
                        ConnectWithUsRow(
                            title = "Youtube",
                            onClickNavigate = {},
                            iconRepresent = MangalaWalletPack.Youtube
                        )
                        ConnectWithUsRow(
                            title = "Facebook",
                            onClickNavigate = {},
                            iconRepresent = MangalaWalletPack.Facebook
                        )
                        ConnectWithUsRow(
                            title = "Reddit",
                            onClickNavigate = {},
                            iconRepresent = MangalaWalletPack.Reddit
                        )
                    }
                }

            }
        }

    }

    @Composable
    fun ConnectWithUsRow(
        title: String,
        onClickNavigate: () -> Unit,
        iconRepresent: ImageVector,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.mangalaColors.bgInnerCard)
                .clickable(onClick = onClickNavigate)
                .padding(
                    start = Dimensions.Padding.default,
                    top = Dimensions.Padding.small,
                    bottom = Dimensions.Padding.small,
                    end = Dimensions.Padding.default
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Image(
                imageVector = iconRepresent,
                contentDescription = null,
                modifier = Modifier.width(Dimensions.IconButtonSize)
                    .height(Dimensions.IconButtonSize)
            )
            Spacer(modifier = Modifier.width(Dimensions.Padding.half))
            TextNormal(text = title, color = MaterialTheme.mangalaColors.textPrimary)
        }
    }
}