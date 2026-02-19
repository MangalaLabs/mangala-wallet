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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.input.ImeAction
import cafe.adriel.voyager.core.registry.ScreenRegistry
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.mangala.wallet.common.mokoresources.CornerRadius
import com.mangala.wallet.common.mokoresources.Dimensions
import com.mangala.wallet.common.mokoresources.Spacing
import com.mangala.wallet.common.mokoresources.icons.MangalaWalletPack
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.Add
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.ArrowLeft
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.ExportPrivateKey
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.Hide
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.IcCopy
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.IcEdit
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.IcWarning
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.Reorder
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.Security
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.Show
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.Trash
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.WalletReal
import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.ui.SharedScreen
import com.mangala.wallet.ui.HIDDEN_BALANCE_STRING
import com.mangala.wallet.ui.component.OnboardingGradientBackground
import com.mangala.wallet.ui.theme.MangalaTypography
import com.mangala.wallet.ui.theme.mangalaColors
import com.mangala.wallet.ui.utils.PNL_DECIMAL_PLACES
import com.mangala.wallet.ui.utils.collectAsStateMultiplatform
import com.mangala.wallet.ui.utils.screenmodel.BaseScreen
import com.mangala.wallet.utils.formattedAddress
import com.mangala.wallet.utils.analytics.MangalaAnalytics
import com.mangala.wallet.utils.ext.formatCompact
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.desc.desc
import kotlinx.coroutines.delay
import org.koin.core.parameter.parametersOf

internal class WalletDetailsScreen(private val walletId: String) : BaseScreen<WalletDetailsScreenModel>() {
    private companion object {
        const val COPY_MESSAGE_DURATION_MS = 1200L
    }

    override val screenName: String = MangalaAnalytics.Screens.EVM_WALLET_DETAILS
    override val screenClassName: String = WalletDetailsScreen::class.simpleName.orEmpty()
    override val isBottomBarVisible = false

    @Composable
    override fun createScreenModel(): WalletDetailsScreenModel = getScreenModel(
        parameters = { parametersOf(walletId) }
    )

    @Composable
    override fun ScreenContent(screenModel: WalletDetailsScreenModel) {
        val navigator = LocalNavigator.currentOrThrow
        val walletName by screenModel._walletName.collectAsStateMultiplatform()
        val uiModel by screenModel._uiModel.collectAsStateMultiplatform()

        WalletDetails(
            uiModel = uiModel,
            walletName = walletName,
            onBackClicked = { navigator.pop() },
            onNameChanged = screenModel::updateWalletName,
            onClickAddAccount = {
                val unlockPinScreen = ScreenRegistry.get(
                    SharedScreen.UnlockPinScreen(
                        onUnlockSuccess = {
                            val addAccountScreen = ScreenRegistry.get(
                                SharedScreen.EvmCreateAccountScreen(
                                    isPinVerified = true,
                                    walletId = walletId
                                )
                            )
                            // Replace unlock screen to keep a clean back stack:
                            // WalletDetails -> AddAccount (without stale UnlockPin behind it)
                            navigator.replace(addAccountScreen)
                        }
                    )
                )
                navigator.push(unlockPinScreen)
            },
            onClickViewPhrase = {
                val unlockPinScreen = ScreenRegistry.get(
                    SharedScreen.UnlockPinScreen(
                        onUnlockSuccess = {
                            val showRecoveryPhraseScreen = ScreenRegistry.get(
                                SharedScreen.ShowRecoveryPhraseScreen(walletId = walletId)
                            )
                            navigator.replace(showRecoveryPhraseScreen)
                        }
                    )
                )
                navigator.push(unlockPinScreen)
            },
            onClickDeleteWallet = {
                screenModel.onClickDeletedWallet(walletId)
                navigator.pop()
            },
            onClickPrivateKey = { accountId ->
                val unlockPinScreen = ScreenRegistry.get(
                    SharedScreen.UnlockPinScreen(
                        onUnlockSuccess = {
                            navigator.replace(
                                ExportPrivateKeyScreen(
                                    walletId = walletId,
                                    accountId = accountId
                                )
                            )
                        }
                    )
                )
                navigator.push(unlockPinScreen)
            }
        )
    }

    @Composable
    private fun WalletDetails(
        uiModel: WalletDetailsScreenUiModel,
        walletName: String,
        onBackClicked: () -> Unit,
        onNameChanged: (String) -> Unit,
        onClickAddAccount: () -> Unit,
        onClickViewPhrase: () -> Unit,
        onClickDeleteWallet: () -> Unit,
        onClickPrivateKey: (String) -> Unit
    ) {
        val colors = MaterialTheme.mangalaColors
        val clipboardManager = LocalClipboardManager.current
        var isPrivateMode by remember { mutableStateOf(true) }
        var isEditingWalletName by remember { mutableStateOf(false) }
        var showDeleteWalletConfirm by remember { mutableStateOf(false) }
        var copiedAddress by remember { mutableStateOf<String?>(null) }

        LaunchedEffect(copiedAddress) {
            if (copiedAddress == null) return@LaunchedEffect
            delay(COPY_MESSAGE_DURATION_MS)
            copiedAddress = null
        }

        OnboardingGradientBackground(circleBackgroundEnabled = true) {
            Box(modifier = Modifier.fillMaxSize()) {
                Column(modifier = Modifier.fillMaxSize()) {
                Header(onBackClicked = onBackClicked)

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = Dimensions.Padding.default),
                    verticalArrangement = Arrangement.spacedBy(Spacing.XSMALL)
                ) {
                    item {
                        IdentityCard(
                            walletName = walletName,
                            onNameChanged = onNameChanged,
                            onClickViewPhrase = onClickViewPhrase,
                            isEditingWalletName = isEditingWalletName,
                            onStartEditWalletName = { isEditingWalletName = true },
                            onDoneEditWalletName = { isEditingWalletName = false }
                        )
                    }

                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(Dimensions.Height.xLarge)
                                .padding(vertical = Spacing.MICRO),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = MR.strings.label_accounts_and_addresses.desc().localized(),
                                style = MangalaTypography.Size10SemiBold(),
                                color = colors.textSecondary
                            )

                            Box(
                                modifier = Modifier
                                    .size(Spacing.BASE)
                                    .clip(RoundedCornerShape(CornerRadius.Tiny))
                                    .background(colors.bgInnerCard.copy(alpha = 0.45f))
                                    .border(
                                        width = Dimensions.Width.xSmall / 4,
                                        color = colors.border.copy(alpha = 0.5f),
                                        shape = RoundedCornerShape(CornerRadius.Tiny)
                                    )
                                    .clickable(onClick = { isPrivateMode = !isPrivateMode }),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (isPrivateMode) MangalaWalletPack.Hide else MangalaWalletPack.Show,
                                    contentDescription = MR.strings.content_description_toggle_balance.desc().localized(),
                                    tint = colors.iconPrimary,
                                    modifier = Modifier.size(Dimensions.IconButtonSize14)
                                )
                            }
                        }
                    }

                    items(uiModel.accounts, key = { it.account.account.id }) { account ->
                        AccountCard(
                            item = account,
                            isPrivateMode = isPrivateMode,
                            onCopyAddress = { address ->
                                clipboardManager.setText(AnnotatedString(address))
                                copiedAddress = address
                            },
                            onClickPrivateKey = { onClickPrivateKey(account.account.account.id) }
                        )
                    }

                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(Spacing.XXMEDIUM))
                                .border(
                                    width = Dimensions.Width.xSmall / 4,
                                    color = colors.border.copy(alpha = 0.5f),
                                    shape = RoundedCornerShape(Spacing.XXMEDIUM)
                                )
                                .clickable(onClick = onClickAddAccount)
                                .padding(
                                    horizontal = Dimensions.Padding.medium,
                                    vertical = Dimensions.Padding.medium
                                ),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(Spacing.XXBASE)
                                    .clip(RoundedCornerShape(CornerRadius.Small))
                                    .background(colors.bgInnerCard.copy(alpha = 0.5f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = MangalaWalletPack.Add,
                                    contentDescription = null,
                                    tint = colors.iconPrimary,
                                    modifier = Modifier.size(Dimensions.IconSizeNextToText)
                                )
                            }
                            Spacer(modifier = Modifier.width(Dimensions.Padding.xsmall))
                            Text(
                                text = MR.strings.all_add_new_account.desc().localized(),
                                style = MangalaTypography.Size14SemiBold(),
                                color = colors.textSecondary
                            )
                        }
                    }

                    if (copiedAddress != null) {
                        item {
                            Text(
                                text = MR.strings.message_address_copied.desc().localized(),
                                style = MangalaTypography.Size12Medium(),
                                color = colors.textLink,
                                modifier = Modifier.padding(
                                    start = Dimensions.Padding.quarter,
                                    top = Dimensions.Padding.quarter
                                )
                            )
                        }
                    }

                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = Spacing.MICRO),
                            verticalArrangement = Arrangement.spacedBy(Spacing.TINY)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(Dimensions.Height.xLarge),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = MR.strings.label_wallet_management.desc().localized(),
                                    style = MangalaTypography.Size10SemiBold(),
                                    color = colors.textSecondary
                                )
                                Spacer(modifier = Modifier.width(Spacing.BASE))
                            }

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .navigationBarsPadding()
                                    .clip(RoundedCornerShape(Dimensions.Padding.medium))
                                    .background(colors.buttonDestructiveContainer.copy(alpha = 0.12f))
                                    .border(
                                        width = Dimensions.Width.xSmall / 4,
                                        color = colors.buttonDestructiveContainer.copy(alpha = 0.35f),
                                        shape = RoundedCornerShape(Dimensions.Padding.medium)
                                    )
                                    .clickable(onClick = { showDeleteWalletConfirm = true })
                                    .padding(vertical = Dimensions.Padding.medium),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = MangalaWalletPack.Trash,
                                    contentDescription = null,
                                    tint = colors.buttonDestructiveContainer,
                                    modifier = Modifier.size(Dimensions.IconSize)
                                )
                                Spacer(modifier = Modifier.width(Spacing.TINY))
                                Text(
                                    text = MR.strings.action_remove_entire_wallet.desc().localized(),
                                    style = MangalaTypography.Size14SemiBold(),
                                    color = colors.buttonDestructiveContainer
                                )
                            }
                            Spacer(modifier = Modifier.height(Spacing.XSMALL))
                        }
                    }
                }
            }

            if (showDeleteWalletConfirm) {
                DeleteWalletConfirmDialog(
                    walletName = walletName,
                    onConfirm = {
                        showDeleteWalletConfirm = false
                        onClickDeleteWallet()
                    },
                    onDismiss = { showDeleteWalletConfirm = false }
                )
            }
        }
    }
    }

    @Composable
    private fun DeleteWalletConfirmDialog(
        walletName: String,
        onConfirm: () -> Unit,
        onDismiss: () -> Unit
    ) {
        val colors = MaterialTheme.mangalaColors

        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.65f))
                    .clickable(onClick = onDismiss)
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Spacing.BASE)
                    .clip(RoundedCornerShape(CornerRadius.BottomSheet))
                    .background(colors.bgInnerCard)
                    .border(
                        width = Dimensions.Width.xSmall / 4,
                        color = colors.border.copy(alpha = 0.45f),
                        shape = RoundedCornerShape(CornerRadius.BottomSheet)
                    )
                    .padding(Dimensions.Padding.large),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(Spacing.XSMALL)
            ) {
                Box(
                    modifier = Modifier
                        .size(Spacing.XBASE)
                        .clip(CircleShape)
                        .background(colors.buttonDestructiveContainer.copy(alpha = 0.14f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = MangalaWalletPack.IcWarning,
                        contentDescription = null,
                        tint = colors.buttonDestructiveContainer,
                        modifier = Modifier.size(Spacing.BASE)
                    )
                }

                Text(
                    text = MR.strings.title_remove_wallet_confirm.desc().localized(),
                    style = MangalaTypography.Size17SemiBold(),
                    color = colors.textPrimary
                )

                Text(
                    text = MR.strings.message_remove_wallet_confirm.desc().localized().replace("%s", walletName),
                    style = MangalaTypography.Size12Medium(),
                    color = colors.textSecondary
                )

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(Spacing.TINY)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(Dimensions.Padding.medium))
                            .background(colors.buttonDestructiveContainer)
                            .clickable(onClick = onConfirm)
                            .padding(vertical = Dimensions.Padding.medium),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = MR.strings.action_yes_remove_it.desc().localized(),
                            style = MangalaTypography.Size14SemiBold(),
                            color = colors.buttonDestructiveContent
                        )
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(Dimensions.Padding.medium))
                            .background(colors.bgInnerCard.copy(alpha = 0.55f))
                            .border(
                                width = Dimensions.Width.xSmall / 4,
                                color = colors.border.copy(alpha = 0.45f),
                                shape = RoundedCornerShape(Dimensions.Padding.medium)
                            )
                            .clickable(onClick = onDismiss)
                            .padding(vertical = Dimensions.Padding.medium),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = MR.strings.all_cancel.desc().localized(),
                            style = MangalaTypography.Size14SemiBold(),
                            color = colors.textPrimary
                        )
                    }
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
                text = MR.strings.title_wallet_detail.desc().localized(),
                style = MangalaTypography.Size17SemiBold(),
                color = colors.textPrimary,
                modifier = Modifier.padding(start = Spacing.TINY)
            )
        }
    }

    @Composable
    private fun IdentityCard(
        walletName: String,
        onNameChanged: (String) -> Unit,
        onClickViewPhrase: () -> Unit,
        isEditingWalletName: Boolean,
        onStartEditWalletName: () -> Unit,
        onDoneEditWalletName: () -> Unit
    ) {
        val colors = MaterialTheme.mangalaColors
        val focusRequester = remember { FocusRequester() }
        val keyboardController = LocalSoftwareKeyboardController.current

        LaunchedEffect(isEditingWalletName) {
            if (isEditingWalletName) focusRequester.requestFocus()
        }

        GlassCard {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Dimensions.Padding.default),
                verticalArrangement = Arrangement.spacedBy(Spacing.XSMALL)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(Spacing.LARGE)
                            .clip(RoundedCornerShape(Dimensions.Padding.medium))
                            .background(
                                Brush.linearGradient(
                                    listOf(colors.textLink, colors.bgBadge)
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = MangalaWalletPack.WalletReal,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(Spacing.XXMEDIUM)
                        )
                    }

                    Spacer(modifier = Modifier.width(Spacing.XSMALL))
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (isEditingWalletName) {
                                BasicTextField(
                                    value = walletName,
                                    onValueChange = onNameChanged,
                                    singleLine = true,
                                    textStyle = MangalaTypography.Size14SemiBold().copy(color = colors.textPrimary),
                                    cursorBrush = SolidColor(colors.textLink),
                                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                                    keyboardActions = KeyboardActions(onDone = {
                                        keyboardController?.hide()
                                        onDoneEditWalletName()
                                    }),
                                    modifier = Modifier
                                        .widthIn(min = Spacing.XXBASE, max = Spacing.HUGE)
                                        .clip(RoundedCornerShape(CornerRadius.Tiny))
                                        .background(colors.bgInnerCard.copy(alpha = 0.5f))
                                        .border(
                                            width = Dimensions.Width.xSmall / 4,
                                            color = colors.textLink.copy(alpha = 0.5f),
                                            shape = RoundedCornerShape(CornerRadius.Tiny)
                                        )
                                        .padding(horizontal = Spacing.TINY, vertical = Spacing.XTINY)
                                        .focusRequester(focusRequester)
                                )
                            } else {
                                Text(
                                    text = walletName,
                                    style = MangalaTypography.Size14SemiBold(),
                                    color = colors.textPrimary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.widthIn(max = Spacing.HUGE)
                                )
                            }
                            Spacer(modifier = Modifier.width(Spacing.STINY))
                            Icon(
                                imageVector = MangalaWalletPack.IcEdit,
                                contentDescription = null,
                                tint = if (isEditingWalletName) colors.textLink else colors.iconSecondary,
                                modifier = Modifier
                                    .size(Dimensions.IconButtonSize14)
                                    .clickable(onClick = onStartEditWalletName)
                            )
                        }
                        Text(
                            text = MR.strings.label_standard_wallet.desc().localized(),
                            style = MangalaTypography.Size10SemiBold(),
                            color = colors.textLink.copy(alpha = 0.8f)
                        )
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            width = Dimensions.Width.xSmall / 4,
                            color = colors.border.copy(alpha = 0.35f),
                            shape = RoundedCornerShape(CornerRadius.Small)
                        )
                        .padding(
                            horizontal = Spacing.XSMALL,
                            vertical = Dimensions.Padding.xsmall
                        ),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = MR.strings.label_recovery_phrase.desc().localized(),
                            style = MangalaTypography.Size10SemiBold(),
                            color = colors.textSecondary.copy(alpha = 0.8f)
                        )
                        Text(
                            text = MR.strings.label_verified_backed_up.desc().localized(),
                            style = MangalaTypography.Size12Medium(),
                            color = colors.textLink
                        )
                    }

                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(Dimensions.Padding.xsmall))
                            .background(colors.bgInnerCard.copy(alpha = 0.5f))
                            .border(
                                width = Dimensions.Width.xSmall / 4,
                                color = colors.border.copy(alpha = 0.35f),
                                shape = RoundedCornerShape(Dimensions.Padding.xsmall)
                            )
                            .clickable(onClick = onClickViewPhrase)
                            .padding(
                                horizontal = Dimensions.Padding.xsmall,
                                vertical = Spacing.STINY
                            ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = MangalaWalletPack.Security,
                            contentDescription = null,
                            tint = colors.textLink,
                            modifier = Modifier.size(Dimensions.IconButtonSize14)
                        )
                        Spacer(modifier = Modifier.width(Spacing.STINY))
                        Text(
                            text = MR.strings.button_view_phrase.desc().localized(),
                            style = MangalaTypography.Size12SemiBold(),
                            color = colors.textPrimary
                        )
                    }
                }

            }
        }
    }

    @Composable
    private fun AccountCard(
        item: AccountItemUiModel,
        isPrivateMode: Boolean,
        onCopyAddress: (String) -> Unit,
        onClickPrivateKey: () -> Unit
    ) {
        val colors = MaterialTheme.mangalaColors
        val address = item.account.account.bip44Address

        GlassCard {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = Dimensions.Padding.medium,
                        vertical = Spacing.XSMALL
                    ),
                verticalArrangement = Arrangement.spacedBy(Spacing.XSMALL)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(Spacing.XXBASE)
                                .clip(RoundedCornerShape(CornerRadius.Small))
                                .background(
                                    Brush.linearGradient(
                                        listOf(colors.textLink, colors.bgBadge)
                                    )
                                )
                        )

                        Spacer(modifier = Modifier.width(Dimensions.Padding.xsmall))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = item.account.account.name,
                                style = MangalaTypography.Size14SemiBold(),
                                color = colors.textPrimary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = address.formattedAddress(leadingCharsCount = 6, trailingCharsCount = 4),
                                    style = MangalaTypography.Size12Regular(),
                                    color = colors.textSecondary.copy(alpha = 0.85f),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )

                                IconButton(
                                    onClick = { onCopyAddress(address) },
                                    modifier = Modifier.size(Dimensions.IconButtonSize)
                                ) {
                                    Icon(
                                        imageVector = MangalaWalletPack.IcCopy,
                                        contentDescription = MR.strings.content_description_copy_address.desc().localized(),
                                        tint = colors.textLink,
                                        modifier = Modifier.size(Spacing.XSMALL)
                                    )
                                }
                            }
                        }
                    }

                    Icon(
                        imageVector = MangalaWalletPack.Reorder,
                        contentDescription = MR.strings.content_description_options.desc().localized(),
                        tint = colors.iconSecondary.copy(alpha = 0.9f),
                        modifier = Modifier.size(Dimensions.IconSize)
                    )
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(Dimensions.Width.xSmall / 4)
                        .background(colors.border.copy(alpha = 0.35f))
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(Dimensions.Padding.quarter)) {
                        Text(
                            text = MR.strings.label_balance_upper.desc().localized(),
                            style = MangalaTypography.Size10SemiBold(),
                            color = colors.textSecondary.copy(alpha = 0.78f)
                        )
                        Text(
                            text = if (isPrivateMode) {
                                HIDDEN_BALANCE_STRING
                            } else if (item.isBalanceLoading) {
                                MR.strings.all_loading.desc().localized()
                            } else {
                                item.currencySymbol + item.totalValueUsd.formatCompact(PNL_DECIMAL_PLACES)
                            },
                            style = MangalaTypography.Size14SemiBold(),
                            color = if (isPrivateMode) colors.textSecondary else colors.textLink,
                            modifier = if (isPrivateMode) Modifier.blur(Spacing.TINY) else Modifier
                        )
                    }

                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(Dimensions.Padding.xsmall))
                            .background(colors.textLink.copy(alpha = 0.1f))
                            .border(
                                width = Dimensions.Width.xSmall / 4,
                                color = colors.textLink.copy(alpha = 0.22f),
                                shape = RoundedCornerShape(Dimensions.Padding.xsmall)
                            )
                            .clickable(onClick = onClickPrivateKey)
                            .padding(
                                horizontal = Dimensions.Padding.xsmall,
                                vertical = Spacing.STINY
                            ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = MangalaWalletPack.ExportPrivateKey,
                            contentDescription = null,
                            tint = colors.textLink,
                            modifier = Modifier.size(Spacing.XSMALL)
                        )
                        Spacer(modifier = Modifier.width(Dimensions.Padding.quarter))
                        Text(
                            text = MR.strings.all_private_key.desc().localized(),
                            style = MangalaTypography.Size10SemiBold(),
                            color = colors.textLink
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
