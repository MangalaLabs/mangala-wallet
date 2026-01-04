package com.mangala.wallet.features.chains.antelope.presentation.manageaccount

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.registry.ScreenRegistry
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.mangala.wallet.common.mokoresources.CornerRadius
import com.mangala.wallet.common.mokoresources.Dimensions
import com.mangala.wallet.common.mokoresources.Spacing
import com.mangala.wallet.common.mokoresources.icons.MangalaWalletPack
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.Add
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.AntelopeAccountVisualize
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.Delete
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.Exclamation
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.ExportPrivateKey
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.MoreHorizontalCircle
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.Wallet
import com.mangala.wallet.features.chains.antelope_base.domain.model.AntelopeAccount
import com.mangala.wallet.model.account.domain.eos.AccountNameType
import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.ui.MangalaCommonDialog
import com.mangala.wallet.ui.PullRefreshState
import com.mangala.wallet.ui.SharedScreen
import com.mangala.wallet.ui.TextDescription2
import com.mangala.wallet.ui.TextNormal
import com.mangala.wallet.ui.component.AddAccountWalletImage
import com.mangala.wallet.ui.component.MangalaWalletIconButton
import com.mangala.wallet.ui.component.MangalaWalletTopBarCenteredTitle
import com.mangala.wallet.ui.component.MaxSizeBox
import com.mangala.wallet.ui.component.MaxSizeColumn
import com.mangala.wallet.ui.component.MaxWidthColumn
import com.mangala.wallet.ui.component.MaxWidthRow
import com.mangala.wallet.ui.component.OnboardingGradientBackground
import com.mangala.wallet.ui.modifier.roundedCornerItemShape
import com.mangala.wallet.ui.theme.mangalaColors
import com.mangala.wallet.ui.utils.collectAsStateMultiplatform
import com.mangala.wallet.ui.utils.screenmodel.BaseScreen
import com.mangala.wallet.utils.analytics.MangalaAnalytics
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.desc.desc
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ManageAntelopeAccountScreen : BaseScreen<ManageAntelopeAccountScreenModel>() {

    override val screenName: String = MangalaAnalytics.Screens.ANTELOPE_MANAGE_ACCOUNT
    override val screenClassName: String = ManageAntelopeAccountScreen::class.simpleName.orEmpty()

    @Composable
    override fun createScreenModel(): ManageAntelopeAccountScreenModel =
        getScreenModel<ManageAntelopeAccountScreenModel>()

    override val isBottomBarVisible: Boolean
        get() = false

    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    override fun ScreenContent(screenModel: ManageAntelopeAccountScreenModel) {
        val navigator = LocalNavigator.currentOrThrow
        val scope = rememberCoroutineScope()

        val modalBottomSheetState = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)
        val selectedAccountName = remember { mutableStateOf<AntelopeAccount?>(null) }
        val isOpenConfirmDeleteDialog = remember { mutableStateOf(false) }

        val uiState = screenModel.uiState.collectAsStateMultiplatform().value

        if (isOpenConfirmDeleteDialog.value) {
            MangalaCommonDialog(
                title = MR.strings.title_delete_antelope_account_confirm_dialog.desc().localized(),
                message = MR.strings.message_delete_antelope_account_confirm_dialog.desc()
                    .localized(),
                positiveButtonText = MR.strings.all_delete.desc().localized().uppercase(),
                negativeButtonText = MR.strings.all_cancel.desc().localized().uppercase(),
                onNegativeClick = {
                    selectedAccountName.value = null
                    isOpenConfirmDeleteDialog.value = false
                },
                onPositiveClick = {
                    isOpenConfirmDeleteDialog.value = false
                    selectedAccountName.value?.let {
                        screenModel.deleteAccount(it.accountName)
                        selectedAccountName.value = null
                    }
                }
            )
        }

        val isLoading = screenModel.isLoading.collectAsStateMultiplatform().value

        ModalBottomSheetLayout(
            sheetState = modalBottomSheetState,
            sheetShape = RoundedCornerShape(
                topStart = CornerRadius.Medium,
                topEnd = CornerRadius.Medium
            ),
            sheetBackgroundColor = Color.Transparent,
            sheetContent = {
                BottomSheetManageAccountContent(
                    isTempAccount = selectedAccountName.value?.isTemp ?: false,
                    onClickDeleteAccount = {
                        scope.launch {
                            modalBottomSheetState.hide()
                            isOpenConfirmDeleteDialog.value = true
                        }
                    },
                    onClickExportPrivateKey = {
                        scope.launch {
                            modalBottomSheetState.hide()
                            selectedAccountName.value?.let { account ->
                                val guideBackupAntelopeAccountScreen = ScreenRegistry.get(
                                    SharedScreen.GuideBackupAntelopeAccountScreen(
                                        accountName = account.accountName
                                    )
                                )
                                selectedAccountName.value = null
                                navigator.push(guideBackupAntelopeAccountScreen)
                            }
                        }
                    },
                    onClickRetryCreateAccount = {
                        scope.launch {
                            selectedAccountName.value?.accountName?.let {
                                modalBottomSheetState.hide()
                                delay(100)
                                val screen = ScreenRegistry.get(
                                    SharedScreen.IapCreateAccountScreen(
                                        accountNameWithSuffix = it,
                                        accountNameType = AccountNameType.getAccountNameType(it).name,
                                        skipToCreateAccountStep = false,
                                        retryCreateAccountName = true,
                                        purchaseToken = null,
                                        purchaseId = null
                                    )
                                )

                                navigator.push(screen)
                            }
                        }
                    }
                )
            }
        ) {
            val pullRefreshState = PullRefreshState(
                isRefreshing = isLoading,
                onRefresh = {}
            )

            OnboardingGradientBackground {
                MaxSizeColumn(
                    modifier = Modifier.windowInsetsPadding(WindowInsets.safeDrawing),
                ) {
                    MangalaWalletTopBarCenteredTitle(
                        title = MR.strings.all_manage_accounts.desc().localized(),
                        onBackClicked = { navigator.pop() },
                        trailingButton = {
                            IconButton(
                                onClick = {
                                    val screen = ScreenRegistry.get(SharedScreen.ImportPrivateKeyScreen)
                                    navigator.push(screen)
                                }
                            ) {
                                Icon(
                                    MangalaWalletPack.Add,
                                    contentDescription = null,
                                    tint = MaterialTheme.mangalaColors.iconPrimary
                                )
                            }
                        }
                    )

                    MaxSizeBox {
                        val toastMessage = screenModel.toastMessage

                        toastMessage.value?.desc()?.localized()
                            ?.let { screenModel.displayToast(it) }

                        when (uiState) {
                            is ManageAntelopeAccountScreenUiState.Success -> {
                                ManageAccountScreen(
                                    antelopeAccountNames = uiState.accounts
                                ) { account ->
                                    selectedAccountName.value = account
                                    scope.launch {
                                        modalBottomSheetState.show()
                                    }
                                }
                            }

                            is ManageAntelopeAccountScreenUiState.Loading -> {
                                CircularProgressIndicator(
                                    modifier = Modifier.align(Alignment.Center),
                                    color = MaterialTheme.mangalaColors.iconPrimary,
                                )
                            }

                            is ManageAntelopeAccountScreenUiState.NoAccount -> {
                                AddAccountWalletImage(
                                    onClickButtonAdd = {
                                        val screen =
                                            ScreenRegistry.get(SharedScreen.AddWalletScreen)
                                        navigator.push(screen)
                                    },
                                    textButton = MR.strings.message_wallet_add_account.desc()
                                        .localized(),
                                    textMessage = MR.strings.message_wallet_no_accounts_found.desc()
                                        .localized()
                                )
                            }
                        }

                        PullRefreshIndicator(
                            refreshing = isLoading,
                            pullRefreshState,
                            modifier = Modifier.align(Alignment.TopCenter)
                        )
                    }
                }
            }
        }
    }

    @Composable
    private fun ManageAccountScreen(
        antelopeAccountNames: List<AntelopeAccount>,
        onClickManageAccount: (AntelopeAccount) -> Unit,
    ) {
        if (antelopeAccountNames.isNotEmpty()) {
            LazyColumn(
                modifier = Modifier.padding(Dimensions.Padding.default).fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(1.dp)
            ) {
                items(
                    items = antelopeAccountNames,
                    key = { it.accountName }
                ) {
                    AccountItem(
                        shape = roundedCornerItemShape(
                            antelopeAccountNames, antelopeAccountNames.indexOf(it)
                        ),
                        account = it,
                        onClickManageAccount = {
                            onClickManageAccount(it)
                        }
                    )
                }
            }
        }
    }

    @Composable
    private fun AccountItem(
        account: AntelopeAccount,
        shape: Shape,
        onClickManageAccount: () -> Unit,
    ) {
        MaxWidthRow(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .background(
                    color = MaterialTheme.mangalaColors.bgInnerCard,
                    shape = shape
                )
                .padding(Dimensions.Padding.default),
        ) {
            Icon(
                MangalaWalletPack.AntelopeAccountVisualize,
                contentDescription = null,
                tint = Color.Unspecified
            )

            Spacer(modifier = Modifier.width(Spacing.XSMALL))

            TextNormal(
                text = account.accountName,
                color = MaterialTheme.mangalaColors.textPrimary
            )

            if (account.isTemp) {
                Spacer(modifier = Modifier.width(Spacing.XTINY))
                Icon(
                    imageVector = MangalaWalletPack.Exclamation,
                    contentDescription = null,
                    tint = Color.Unspecified,
                    modifier = Modifier.size(16.dp),
                )
            }
            Spacer(modifier = Modifier.weight(1f))

            MangalaWalletIconButton(
                icon = MangalaWalletPack.MoreHorizontalCircle,
                onClick = onClickManageAccount,
                tint = Color.Unspecified
            )
        }
    }

    @Composable
    private fun BottomSheetManageAccountContent(
        isTempAccount: Boolean = false,
        onClickDeleteAccount: () -> Unit,
        onClickExportPrivateKey: () -> Unit,
        onClickRetryCreateAccount: () -> Unit
    ) {
        MaxWidthColumn(
            modifier = Modifier
                .background(MaterialTheme.mangalaColors.bg)
                .padding(
                    horizontal = Dimensions.Padding.double,
                )
                .navigationBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(Spacing.TINY))

            Box(
                modifier = Modifier
                    .width(Dimensions.Width.xxLarge)
                    .height(Dimensions.Height.xSmall)
                    .background(
                        color = MaterialTheme.mangalaColors.border,
                        shape = RoundedCornerShape(100.dp)
                    )
            )

            Spacer(modifier = Modifier.height(Spacing.XBASE))

            if (isTempAccount) {
                BottomSheetManageAccountItem(
                    optionIcon = MangalaWalletPack.Wallet,
                    optionName = MR.strings.button_wallet_main_retry_create.desc().localized(),
                    onClick = onClickRetryCreateAccount
                )
            } else {
                BottomSheetManageAccountItem(
                    optionIcon = MangalaWalletPack.ExportPrivateKey,
                    optionName = MR.strings.all_antelope_export_private_key.desc().localized(),
                    onClick = onClickExportPrivateKey
                )
            }


            Spacer(modifier = Modifier.height(Spacing.MEDIUM))

            BottomSheetManageAccountItem(
                optionIcon = MangalaWalletPack.Delete,
                optionName = MR.strings.title_manage_antelope_account_bottomsheet_delete_account.desc()
                    .localized(),
                textColor = MaterialTheme.mangalaColors.buttonDestructiveContainer,
                iconTint = MaterialTheme.mangalaColors.buttonDestructiveContent,
                iconBackground = MaterialTheme.mangalaColors.buttonDestructiveContainer,
                onClick = onClickDeleteAccount
            )

            Spacer(modifier = Modifier.height(Spacing.XXXBASE))
        }
    }

    @Composable
    private fun BottomSheetManageAccountItem(
        optionIcon: ImageVector,
        optionName: String,
        textColor: Color = MaterialTheme.mangalaColors.textPrimary,
        iconTint: Color = MaterialTheme.mangalaColors.iconPrimary,
        iconBackground: Color = MaterialTheme.mangalaColors.bgInnerCard,
        onClick: () -> Unit
    ) {
        MaxWidthRow(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable(onClick = onClick)
        ) {
            Icon(
                optionIcon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier
                    .background(
                        color = iconBackground,
                        shape = RoundedCornerShape(CornerRadius.Tiny)
                    )
                    .padding(Dimensions.Padding.small)
            )
            Spacer(modifier = Modifier.width(Spacing.XSMALL))
            TextDescription2(
                text = optionName,
                color = textColor
            )
        }
    }
}

