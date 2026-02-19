package com.mangala.wallet.features.menu.presentation.wallet.details

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.mangala.wallet.common.mokoresources.CornerRadius
import com.mangala.wallet.common.mokoresources.Dimensions
import com.mangala.wallet.common.mokoresources.Spacing
import com.mangala.wallet.common.mokoresources.icons.MangalaWalletPack
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.ArrowLeft
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.ExportPrivateKey
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.Hide
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.IcCopy
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.Show
import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.ui.component.OnboardingGradientBackground
import com.mangala.wallet.ui.theme.MangalaTypography
import com.mangala.wallet.ui.theme.mangalaColors
import com.mangala.wallet.ui.utils.collectAsStateMultiplatform
import com.mangala.wallet.ui.utils.screenmodel.BaseScreen
import com.mangala.wallet.utils.analytics.MangalaAnalytics
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.desc.desc
import kotlinx.coroutines.delay
import org.koin.core.parameter.parametersOf

internal class ExportPrivateKeyScreen(
    private val walletId: String,
    private val accountId: String
) : BaseScreen<ExportPrivateKeyScreenModel>() {
    private companion object {
        const val COPY_MESSAGE_DURATION_MS = 1200L
    }

    override val screenName: String = MangalaAnalytics.Screens.EVM_EXPORT_PRIVATE_KEY
    override val screenClassName: String = ExportPrivateKeyScreen::class.simpleName.orEmpty()
    override val isBottomBarVisible: Boolean = false

    @Composable
    override fun createScreenModel(): ExportPrivateKeyScreenModel = getScreenModel(
        parameters = { parametersOf(walletId, accountId) }
    )

    @Composable
    override fun ScreenContent(screenModel: ExportPrivateKeyScreenModel) {
        val navigator = LocalNavigator.currentOrThrow
        val uiModel by screenModel.uiModel.collectAsStateMultiplatform()
        val clipboardManager = LocalClipboardManager.current
        var showCopiedMessage by remember { mutableStateOf(false) }

        LaunchedEffect(showCopiedMessage) {
            if (showCopiedMessage.not()) return@LaunchedEffect
            delay(COPY_MESSAGE_DURATION_MS)
            showCopiedMessage = false
        }

        OnboardingGradientBackground(circleBackgroundEnabled = true) {
            Column(modifier = Modifier.fillMaxSize()) {
                Header(onBackClicked = { navigator.pop() })

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = Dimensions.Padding.default)
                        .padding(top = Spacing.MICRO, bottom = Dimensions.Padding.default),
                    verticalArrangement = Arrangement.spacedBy(Spacing.XSMALL)
                ) {
                    WarningCard()

                    PrivateKeyCard(
                        privateKey = when {
                            uiModel.isLoading -> MR.strings.all_loading.desc().localized()
                            uiModel.isError -> MR.strings.all_error_no_params.desc().localized()
                            else -> uiModel.privateKey
                        },
                        isVisible = uiModel.isPrivateKeyVisible,
                        isLoading = uiModel.isLoading,
                        isError = uiModel.isError,
                        onToggleVisibility = screenModel::onTogglePrivateKeyVisibility,
                        onCopy = {
                            if (uiModel.privateKey.isBlank()) return@PrivateKeyCard
                            clipboardManager.setText(AnnotatedString(uiModel.privateKey))
                            showCopiedMessage = true
                        }
                    )

                    if (showCopiedMessage) {
                        Text(
                            text = MR.strings.message_address_copied.desc().localized(),
                            style = MangalaTypography.Size12Medium(),
                            color = MaterialTheme.mangalaColors.textLink
                        )
                    }

                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }

    @Composable
    private fun Header(onBackClicked: () -> Unit) {
        val colors = MaterialTheme.mangalaColors

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(
                    start = Dimensions.Padding.default,
                    end = Dimensions.Padding.default,
                    top = Dimensions.Padding.xsmall,
                    bottom = Dimensions.Padding.half
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(Spacing.XXXBASE)
                    .clip(CircleShape)
                    .clickable(onClick = onBackClicked),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = MangalaWalletPack.ArrowLeft,
                    contentDescription = MR.strings.all_back.desc().localized(),
                    tint = colors.iconPrimary,
                    modifier = Modifier.size(Spacing.XXMEDIUM)
                )
            }

            Text(
                text = MR.strings.all_private_key.desc().localized(),
                style = MangalaTypography.Size17SemiBold(),
                color = colors.textPrimary,
                modifier = Modifier.padding(start = Spacing.TINY)
            )
        }
    }

    @Composable
    private fun WarningCard() {
        val colors = MaterialTheme.mangalaColors
        GlassCard {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Dimensions.Padding.default),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = MangalaWalletPack.ExportPrivateKey,
                    contentDescription = null,
                    tint = colors.textLink,
                    modifier = Modifier.size(Dimensions.IconSizeNextToText)
                )
                Spacer(modifier = Modifier.width(Spacing.TINY))
                Text(
                    text = MR.strings.message_export_private_key_show_private_key.desc().localized(),
                    style = MangalaTypography.Size12Regular(),
                    color = colors.textSecondary
                )
            }
        }
    }

    @Composable
    private fun PrivateKeyCard(
        privateKey: String,
        isVisible: Boolean,
        isLoading: Boolean,
        isError: Boolean,
        onToggleVisibility: () -> Unit,
        onCopy: () -> Unit
    ) {
        val colors = MaterialTheme.mangalaColors
        GlassCard {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Dimensions.Padding.default),
                verticalArrangement = Arrangement.spacedBy(Spacing.XSMALL)
            ) {
                Text(
                    text = MR.strings.all_private_key.desc().localized(),
                    style = MangalaTypography.Size10SemiBold(),
                    color = colors.textSecondary
                )
                Text(
                    text = privateKey,
                    style = MangalaTypography.Size12SemiBold(),
                    color = colors.textPrimary,
                    modifier = if (isVisible || isLoading || isError) {
                        Modifier
                    } else {
                        Modifier.blur(Spacing.TINY)
                    }
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding(),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.TINY)
                ) {
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(Dimensions.Padding.xsmall))
                            .background(colors.bgInnerCard.copy(alpha = 0.5f))
                            .border(
                                width = Dimensions.Width.xSmall / 4,
                                color = colors.border.copy(alpha = 0.35f),
                                shape = RoundedCornerShape(Dimensions.Padding.xsmall)
                            )
                            .clickable(onClick = onToggleVisibility)
                            .padding(
                                horizontal = Dimensions.Padding.xsmall,
                                vertical = Dimensions.Padding.small
                            ),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = if (isVisible) MangalaWalletPack.Hide else MangalaWalletPack.Show,
                            contentDescription = null,
                            tint = colors.textPrimary,
                            modifier = Modifier.size(Dimensions.IconButtonSize14)
                        )
                        Spacer(modifier = Modifier.width(Spacing.STINY))
                        Text(
                            text = if (isVisible) MR.strings.button_export_private_key_hide.desc().localized() else MR.strings.button_export_private_key_show.desc().localized(),
                            style = MangalaTypography.Size12SemiBold(),
                            color = colors.textPrimary
                        )
                    }

                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(Dimensions.Padding.xsmall))
                            .background(
                                Brush.linearGradient(
                                    listOf(colors.textLink, colors.bgBadge)
                                )
                            )
                            .clickable(
                                enabled = isLoading.not() && privateKey.isNotBlank(),
                                onClick = onCopy
                            )
                            .padding(
                                horizontal = Dimensions.Padding.xsmall,
                                vertical = Dimensions.Padding.small
                            ),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = MangalaWalletPack.IcCopy,
                            contentDescription = null,
                            tint = colors.textPrimary,
                            modifier = Modifier.size(Dimensions.IconButtonSize14)
                        )
                        Spacer(modifier = Modifier.width(Spacing.STINY))
                        Text(
                            text = MR.strings.button_export_private_key_copy.desc().localized(),
                            style = MangalaTypography.Size12SemiBold(),
                            color = colors.textPrimary
                        )
                    }
                }
            }
        }
    }

    @Composable
    private fun GlassCard(content: @Composable () -> Unit) {
        val colors = MaterialTheme.mangalaColors
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(CornerRadius.BottomSheet))
                .background(
                    Brush.verticalGradient(
                        listOf(colors.bgInnerCard.copy(alpha = 0.24f), colors.bgInnerCard.copy(alpha = 0.14f))
                    )
                )
                .border(
                    width = Dimensions.Width.xSmall / 4,
                    color = colors.border.copy(alpha = 0.35f),
                    shape = RoundedCornerShape(CornerRadius.BottomSheet)
                )
        ) {
            content()
        }
    }
}
