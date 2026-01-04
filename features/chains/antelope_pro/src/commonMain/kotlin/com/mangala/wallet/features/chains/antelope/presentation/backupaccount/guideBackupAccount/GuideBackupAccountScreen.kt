package com.mangala.wallet.features.chains.antelope.presentation.backupaccount.guideBackupAccount

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.text.font.FontWeight
import cafe.adriel.voyager.core.registry.ScreenRegistry
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.mangala.wallet.common.mokoresources.Dimensions
import com.mangala.wallet.common.mokoresources.FontType
import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.ui.SharedScreen
import com.mangala.wallet.ui.component.MangalaGradientButton
import com.mangala.wallet.ui.TextDescription2
import com.mangala.wallet.ui.TextNormal
import com.mangala.wallet.ui.TextTitle3
import com.mangala.wallet.ui.component.MangalaWalletTopBarCenteredTitle
import com.mangala.wallet.ui.component.MaxSizeColumn
import com.mangala.wallet.ui.component.OnboardingGradientBackground
import com.mangala.wallet.ui.theme.mangalaColors
import com.mangala.wallet.ui.component.MaxWidthColumn
import com.mangala.wallet.ui.utils.collectAsStateMultiplatform
import com.mangala.wallet.ui.utils.screenmodel.BaseScreen
import com.mangala.wallet.utils.analytics.MangalaAnalytics
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.desc.desc

class GuideBackupAccountScreen(private val accountName: String): BaseScreen<GuideBackupAccountScreenModel>() {

    override val screenName: String = MangalaAnalytics.Screens.ANTELOPE_BACKUP_ACCOUNT_GUIDE
    override val screenClassName: String = GuideBackupAccountScreen::class.simpleName.orEmpty()

    @Composable
    override fun createScreenModel(): GuideBackupAccountScreenModel =
        getScreenModel<GuideBackupAccountScreenModel>()


    override val isBottomBarVisible: Boolean
        get() = false

    @Composable
    override fun ScreenContent(screenModel: GuideBackupAccountScreenModel) {
        val navigator = LocalNavigator.currentOrThrow
        val uiModel = screenModel.uiModel.collectAsStateMultiplatform()

        OnboardingGradientBackground {
            MaxSizeColumn(
                modifier = Modifier
                    .windowInsetsPadding(WindowInsets.safeDrawing),
            ) {
                MangalaWalletTopBarCenteredTitle(
                    title = "",
                    onBackClicked = {
                        if (uiModel.value.isFirstStep) navigator.pop()
                        else screenModel.previousStep()
                    }
                )

                MaxSizeColumn(
                    modifier = Modifier
                        .padding(Dimensions.Padding.default),
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.Start
                ) {
                    TextTitle3(
                        text = MR.strings.title_guide_export_private_key.desc().localized(),
                        color = MaterialTheme.mangalaColors.textPrimary
                    )

                    Spacer(Modifier.height(Dimensions.Height.xxxLarge))

        //                TextTitle2(
        //                    text = "1 cai anh minh hoa o day",
        //                    color = Colors.main1Text
        //                )

        //                Spacer(Modifier.height(Dimensions.Height.xLarge))

                    GuideSection(step = uiModel.value.currentStep)

                    Spacer(Modifier.weight(1f))

                    MangalaGradientButton(
                        label = if (uiModel.value.isFinalStep) MR.strings.button_guide_export_private_key_startBackup.desc()
                            .localized() else MR.strings.next.desc().localized(),
                        onClick = {
                            if (uiModel.value.isFinalStep) {
                                val selectPermissionToBackupScreen =
                                    ScreenRegistry.get(
                                        SharedScreen.SelectPermissionToBackupScreen(
                                            accountName = accountName
                                        )
                                    )

                                navigator.push(selectPermissionToBackupScreen)
                            } else {
                                screenModel.nextStep()
                            }
                        },
                        enabled = uiModel.value.isEnableNextButton,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }

    @Composable
    private fun GuideSection(
        step: Int
    ) {
        val circleBehindStepNumberColor = MaterialTheme.mangalaColors.bgInnerCard
        MaxWidthColumn {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextNormal(
                    text = step.toString(),
                    color = MaterialTheme.mangalaColors.textPrimary,
                    modifier = Modifier
                        .padding(Dimensions.Padding.small)
                        .drawBehind {
                            drawCircle(
                                color = circleBehindStepNumberColor,
                                radius = this.size.maxDimension / 1.5f
                            )
                        },
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(Modifier.width(Dimensions.Width.mediumSmall))

                TextNormal(
                    text = when (step) {
                        1 -> MR.strings.title_guide_export_private_key_guide1.desc().localized()
                        2 -> MR.strings.title_guide_export_private_key_guide2.desc().localized()
                        3 -> MR.strings.title_guide_export_private_key_guide3.desc().localized()
                        else -> ""
                    },
                    color = MaterialTheme.mangalaColors.textPrimary,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = FontType.REGULAR_22,
                )
            }

            Spacer(Modifier.height(Dimensions.Width.mediumSmall))

            TextDescription2(
                text = when (step) {
                    1 -> MR.strings.message_guide_export_private_key_guide1.desc().localized()
                    2 -> MR.strings.message_guide_export_private_key_guide2.desc().localized()
                    3 -> MR.strings.message_guide_export_private_key_guide3.desc().localized()
                    else -> ""
                },
                color = MaterialTheme.mangalaColors.textSecondary,
                fontSize = FontType.REGULAR,
            )
        }
    }
}