package com.mangala.wallet.menu_base.presentation.notifications

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Scaffold
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.mangala.wallet.common.mokoresources.Colors
import com.mangala.wallet.common.mokoresources.CornerRadius
import com.mangala.wallet.common.mokoresources.Dimensions
import com.mangala.wallet.common.mokoresources.FontType
import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.common.mokoresources.Spacing
import com.mangala.wallet.ui.TextDescription2
import com.mangala.wallet.ui.TextNormal
import com.mangala.wallet.ui.component.MangalaWalletSwitch
import com.mangala.wallet.ui.component.MangalaWalletTopBar
import com.mangala.wallet.ui.component.MangalaWalletTopBarCenteredTitle
import com.mangala.wallet.ui.component.OnboardingGradientBackground
import com.mangala.wallet.ui.theme.mangalaColors
import com.mangala.wallet.ui.utils.screenmodel.BaseScreen
import com.mangala.wallet.utils.analytics.MangalaAnalytics
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.desc.desc


class NotificationsScreen : BaseScreen<NotificationsScreenModel>() {

    override val screenName: String = MangalaAnalytics.Screens.NOTIFICATIONS
    override val screenClassName: String = NotificationsScreen::class.simpleName.orEmpty()

    override val isBottomBarVisible: Boolean = false

    @Composable
    override fun createScreenModel(): NotificationsScreenModel {
        return getScreenModel()
    }

    @Composable
    override fun ScreenContent(screenModel: NotificationsScreenModel) {
        val navigator = LocalNavigator.currentOrThrow

        Notifications(onBackPressed = navigator::pop)
    }

    @Composable
    fun Notifications(onBackPressed: () -> Unit) {
        OnboardingGradientBackground{
            Scaffold(
                topBar = {
                    MangalaWalletTopBarCenteredTitle(
                        title = MR.strings.all_notifications.desc().localized(),
                        onBackClicked = onBackPressed
                    )
                },
                modifier = Modifier.statusBarsPadding(),
                backgroundColor = Color.Transparent,
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = Spacing.SMALL)
                        .verticalScroll(rememberScrollState())
                ) {
                    Spacer(modifier = Modifier.height(Spacing.SMALL))
                    NotificationRow(MR.strings.title_push_notifications.desc().localized(), enable = false)
                    Spacer(modifier = Modifier.height(Spacing.XTINY))
                    TextDescription2(
                        text = MR.strings.note_title_push_notifications.desc().localized(),
                        modifier = Modifier.padding(start = Dimensions.Padding.default),
                        color = MaterialTheme.mangalaColors.textSecondary,
                        fontSize = FontType.TINY
                    )
                    Spacer(modifier = Modifier.height(Spacing.SMALL))
                    NotificationRow(MR.strings.title_product_announcement.desc().localized(), enable = true)
                    Spacer(modifier = Modifier.height(Spacing.XTINY))
                    TextDescription2(
                        text = MR.strings.note_title_product_announcement.desc().localized(),
                        modifier = Modifier.padding(start = Dimensions.Padding.default),
                        color = MaterialTheme.mangalaColors.textSecondary,
                        fontSize = FontType.TINY
                    )

                }
            }
        }
    }


    @Composable
    fun NotificationRow(
        nameRow: String,
        enable: Boolean
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .background(MaterialTheme.mangalaColors.bgInnerCard, RoundedCornerShape(CornerRadius.Small))
                .padding(
                    horizontal = Dimensions.Padding.default,
                    vertical = Dimensions.Padding.half
                )
                .fillMaxSize()
        ) {
            TextNormal(
                text = nameRow,
                modifier = Modifier.weight(1f),
                color = MaterialTheme.mangalaColors.textPrimary,
            )
            MangalaWalletSwitch(
                checked = enable,
                onCheckedChange = {
                }
            )
        }
    }

}