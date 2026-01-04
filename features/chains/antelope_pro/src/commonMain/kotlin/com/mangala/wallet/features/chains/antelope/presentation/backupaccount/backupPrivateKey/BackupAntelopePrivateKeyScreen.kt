package com.mangala.wallet.features.chains.antelope.presentation.backupaccount.backupPrivateKey

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.mangala.wallet.common.mokoresources.Colors
import com.mangala.wallet.common.mokoresources.CornerRadius
import com.mangala.wallet.common.mokoresources.Dimensions
import com.mangala.wallet.common.mokoresources.Spacing
import com.mangala.wallet.common.mokoresources.icons.MangalaWalletPack
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.InfoCircle
import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.qrcode.ComposeUIWrapper
import com.mangala.wallet.ui.TextDescription2
import com.mangala.wallet.ui.TextTiny
import com.mangala.wallet.ui.component.MangalaGradientButton
import com.mangala.wallet.ui.component.MangalaOutlinedButtonNew
import com.mangala.wallet.ui.component.MangalaWalletDropDown
import com.mangala.wallet.ui.component.MangalaWalletTopBarCenteredTitle
import com.mangala.wallet.ui.component.MaxSizeColumn
import com.mangala.wallet.ui.component.MaxWidthRow
import com.mangala.wallet.ui.component.OnboardingGradientBackground
import com.mangala.wallet.ui.theme.mangalaColors
import com.mangala.wallet.ui.utils.collectAsStateMultiplatform
import com.mangala.wallet.ui.utils.screenmodel.BaseScreen
import com.mangala.wallet.utils.analytics.MangalaAnalytics
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.desc.desc
import org.koin.core.parameter.parametersOf

class BackupAntelopePrivateKeyScreen(
    private val accountName: String,
    private val permissionName: String
) : BaseScreen<BackupAntelopePrivateKeyScreenModel>() {

    override val screenName: String = MangalaAnalytics.Screens.ANTELOPE_BACKUP_PRIVATE_KEY
    override val screenClassName: String =
        BackupAntelopePrivateKeyScreen::class.simpleName.orEmpty()

    @Composable
    override fun createScreenModel(): BackupAntelopePrivateKeyScreenModel =
        getScreenModel<BackupAntelopePrivateKeyScreenModel>(
            parameters = {
                parametersOf(accountName, permissionName)
            }
        )


    override val isBottomBarVisible: Boolean
        get() = false

    @Composable
    override fun ScreenContent(screenModel: BackupAntelopePrivateKeyScreenModel) {
        val navigator = LocalNavigator.currentOrThrow

        val publicKeys = screenModel.accountPublicKeys.collectAsStateMultiplatform()
        val selectedPublicKey = screenModel.selectedPublicKey.collectAsStateMultiplatform()
        val selectedPrivateKey = screenModel.selectedPrivateKey.collectAsStateMultiplatform()
        val isShowPrivateKey = screenModel.isShowPrivateKey.collectAsStateMultiplatform()
        val composeUIWrapper = remember { ComposeUIWrapper() }

        OnboardingGradientBackground {
            MaxSizeColumn(
                modifier = Modifier.windowInsetsPadding(WindowInsets.safeDrawing),
            ) {
                MangalaWalletTopBarCenteredTitle(
                    title = MR.strings.title_export_private_key_show_private_key_top_bar.desc()
                        .localized(),
                    onBackClicked = navigator::pop
                )

                Spacer(modifier = Modifier.height(Spacing.SMALL))

                MaxSizeColumn(
                    modifier = Modifier
                        .padding(Dimensions.Padding.default)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    MaxWidthRow(
                        modifier = Modifier
                            .background(
                                color = Colors.lightYellow,
                                shape = RoundedCornerShape(CornerRadius.Small),
                            )
                            .padding(
                                vertical = Dimensions.Padding.small,
                                horizontal = Dimensions.Padding.default
                            ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            MangalaWalletPack.InfoCircle,
                            contentDescription = null,
                            tint = Colors.gray,
                            modifier = Modifier.size(Dimensions.ButtonIconSize)
                        )

                        Spacer(modifier = Modifier.width(Spacing.TINY))

                        TextTiny(
                            text = MR.strings.message_export_private_key_show_private_key.desc()
                                .localized(),
                            color = Colors.gray,
                        )
                    }

                    Spacer(modifier = Modifier.height(Spacing.TINY))

                    MangalaWalletDropDown(
                        chosenOptionName = selectedPublicKey.value,
                        chosenOptionImageUrl = "",
                        listOptionImagesUrl = emptyList(),
                        listOptionName = publicKeys.value,
                        textColor = MaterialTheme.mangalaColors.textPrimary,
                        dropdownMenuBoxModifier = Modifier
                            .background(
                                color = MaterialTheme.mangalaColors.bgInnerCard,
                                shape = RoundedCornerShape(CornerRadius.Small),
                            )
                            .padding(Dimensions.Padding.half)
                    ) {
                        screenModel.onSelectPublicKey(publicKeys.value[it])
                    }

                    if (selectedPrivateKey.value.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(Spacing.BASE))

                        Box {
                            composeUIWrapper.QRCodeImage(
                                selectedPrivateKey.value,
                            )

                            if (isShowPrivateKey.value.not()) Box(
                                modifier = Modifier.background(MaterialTheme.mangalaColors.bgInnerCard)
                                    .matchParentSize()
                            )
                        }

                        Spacer(modifier = Modifier.height(Spacing.XXBASE))

                        Box {
                            TextDescription2(
                                text = selectedPrivateKey.value,
                                modifier = Modifier
                                    .background(
                                        color = MaterialTheme.mangalaColors.bgInnerCard,
                                        shape = RoundedCornerShape(CornerRadius.Small),
                                    ).border(
                                        width = 1.dp,
                                        color = MaterialTheme.mangalaColors.border,
                                        shape = RoundedCornerShape(CornerRadius.Small),
                                    )
                                    .padding(
                                        vertical = Dimensions.Padding.small,
                                        horizontal = Dimensions.Padding.default
                                    ),
                                color = MaterialTheme.mangalaColors.textPrimary,
                            )
                            if (isShowPrivateKey.value.not()) Box(
                                modifier = Modifier
                                    .background(
                                        color = MaterialTheme.mangalaColors.bgInnerCard,
                                        shape = RoundedCornerShape(CornerRadius.Small),
                                    )
                                    .matchParentSize()
                            )
                        }

                        Spacer(modifier = Modifier.weight(1f))

                        MaxWidthRow(
                            horizontalArrangement = Arrangement.End
                        ) {
                            MangalaOutlinedButtonNew(
                                label = if (isShowPrivateKey.value) MR.strings.button_export_private_key_hide.desc()
                                    .localized()
                                else MR.strings.button_export_private_key_show.desc().localized(),
                                onClick = screenModel::onToggleShowHidePrivateKey,
                                modifier = Modifier.defaultMinSize(0.dp).weight(0.37f)
                            )

                            Spacer(modifier = Modifier.width(Spacing.SMALL))

                            MangalaGradientButton(
                                label = MR.strings.button_export_private_key_copy.desc()
                                    .localized(),
                                onClick = screenModel::copyPrivateKeyToClipboard,
                                modifier = Modifier.defaultMinSize(0.dp).weight(0.63f)
                            )
                        }
                    }
                }
            }
        }
    }
}