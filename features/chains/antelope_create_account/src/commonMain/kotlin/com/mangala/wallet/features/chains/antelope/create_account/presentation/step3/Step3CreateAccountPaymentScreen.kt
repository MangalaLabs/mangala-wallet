package com.mangala.wallet.features.chains.antelope.create_account.presentation.step3

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.registry.ScreenRegistry
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.bottomSheet.LocalBottomSheetNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.mangala.wallet.antelope_key_manager.domain.model.AccountKeyPairs
import com.mangala.wallet.features.chains.antelope.create_account.presentation.iap.IapCreateAccountScreen
import com.mangala.wallet.features.chains.antelope.create_account.presentation.ui.ProductAlreadyOwnedDialog
import com.mangala.wallet.features.chains.antelope.create_account.presentation.ui.SelectionOption
import com.mangala.wallet.features.chains.antelope.create_account.presentation.ui.SelectionOptionText
import com.mangala.wallet.features.chains.antelope.create_account.presentation.ui.getAccountTypeText
import com.mangala.wallet.features.chains.antelope_base.presentation.ui.AntelopeResourceProviderFeeDialog
import com.mangala.wallet.model.account.domain.eos.AccountNameType
import com.mangala.wallet.common.mokoresources.Colors
import com.mangala.wallet.common.mokoresources.Dimensions
import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.common.mokoresources.Spacing
import com.mangala.wallet.common.mokoresources.icons.MangalaWalletPack
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.EditCircle
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.InfoCircle
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.TxSwap
import com.mangala.wallet.ui.SharedScreen
import com.mangala.wallet.ui.TextNormal
import com.mangala.wallet.ui.component.CreateImportButton
import com.mangala.wallet.ui.component.GradientBackground
import com.mangala.wallet.ui.component.HorizontalSpacer
import com.mangala.wallet.ui.component.MangalaBottomSheetNavigator
import com.mangala.wallet.ui.component.MangalaButton
import com.mangala.wallet.ui.component.MangalaWalletTopBar
import com.mangala.wallet.ui.component.MaxSizeColumn
import com.mangala.wallet.ui.component.MaxWidthBox
import com.mangala.wallet.ui.component.MaxWidthColumn
import com.mangala.wallet.ui.component.MaxWidthRow
import com.mangala.wallet.ui.component.VerticalSpacer
import com.mangala.wallet.ui.theme.MangalaTypography
import com.mangala.wallet.ui.utils.screenmodel.BaseScreen
import com.mangala.wallet.utils.analytics.MangalaAnalytics
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.desc.desc
import org.koin.core.component.KoinComponent
import org.koin.core.parameter.parametersOf
import kotlin.jvm.Transient

class Step3CreateAccountPaymentScreen(
    private val initialAccountName: String,
    private val initialAccountSuffix: String?,
    private val initialAccountType: AccountNameType,
    val eosOwnerPrivateKey: String? = null,
    val eosActivePrivateKey: String? = null
) : BaseScreen<Step3CreateAccountPaymentScreenModel>() {

    override val screenName: String = MangalaAnalytics.Screens.ANTELOPE_CREATE_ACCOUNT_PAYMENT
    override val screenClassName: String = Step3CreateAccountPaymentScreen::class.simpleName.orEmpty()

    @Composable
    override fun createScreenModel(): Step3CreateAccountPaymentScreenModel {
        return getScreenModel<Step3CreateAccountPaymentScreenModel> {
            parametersOf(
                initialAccountName,
                initialAccountSuffix,
                initialAccountType,
                eosOwnerPrivateKey,
                eosActivePrivateKey
            )
        }
    }

    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    override fun ScreenContent(
        screenModel: Step3CreateAccountPaymentScreenModel,
    ) {
        val navigator = LocalNavigator.currentOrThrow
        val uiState = screenModel.uiState.collectAsState().value

        MangalaBottomSheetNavigator(
            sheetShape = RectangleShape
        ) {
            val bottomNavigator = LocalBottomSheetNavigator.current

            LaunchedEffect(uiState) {
                if (uiState is Step3CreateAccountPaymentUiState.Ready && uiState.createAccountState == CreateAccountState.Created) {
                    onAccountCreated(uiState, navigator)
                    screenModel.onConsumeAccountCreatedState()
                }
            }

            LaunchedEffect((uiState as? Step3CreateAccountPaymentUiState.Ready)?.promptConfirmTransaction) {
                if ((uiState as? Step3CreateAccountPaymentUiState.Ready)?.promptConfirmTransaction == true) {
                    val unlockPinScreen = ScreenRegistry.get(
                        SharedScreen.UnlockPinScreen(
                            SharedScreen.UnlockPinScreen.VERIFY_SEND_TRANSACTION,
                            onUnlockSuccess = {
                                screenModel.onAuthenticationSuccess(uiState.selectedAccount?.accountName.orEmpty())
                                bottomNavigator.hide()
                            },
                            antelopeAccountName = null
                        )
                    )
                    bottomNavigator.show(unlockPinScreen)
                    screenModel.onPinPromptShown()
                }
            }

            if ((uiState as? Step3CreateAccountPaymentUiState.Ready)?.resourceRequiredBreakdown != null) {
                val uiModel = uiState as? Step3CreateAccountPaymentUiState.Ready

                AntelopeResourceProviderFeeDialog(
                    feeBreakdown = uiModel?.resourceRequiredBreakdown,
                    resourceRequiredTotal = uiModel?.resourceRequiredTotal,
                    onClick = {
                        screenModel.onConfirmResourceProviderFee()
                    },
                    onDismiss = {
                        screenModel.onDismissTransactionFeeBreakdown()
                    }
                )
            }

            if (uiState is Step3CreateAccountPaymentUiState.Ready && uiState.iapProductAlreadyOwnedDialog != null) {
                val accountName = uiState.iapProductAlreadyOwnedDialog.accountName

                ProductAlreadyOwnedDialog(
                    accountName,
                    onDismiss = {
                        screenModel.onDismissProductAlreadyOwnedDialog()
                    },
                    onConfirmCreate = {
                        val readyUiState = uiState as? Step3CreateAccountPaymentUiState.Ready

                        readyUiState?.let {
                            screenModel.onDismissProductAlreadyOwnedDialog()

                            navigator.push(
                                IapCreateAccountScreen(
                                    accountNameWithSuffix = readyUiState.accountNameWithSuffix,
                                    accountNameType = readyUiState.accountNameType.name,
                                    skipToCreateAccountStep = true,
                                    purchaseToken = null
                                )
                            )
                        }
                    }
                )
            }

            screenModel.navigateToCreateIapScreen.collectAsState(null).value?.let { paymentInfo ->
                val readyUiState = uiState as? Step3CreateAccountPaymentUiState.Ready

                readyUiState?.let {
                    navigator.push(
                        IapCreateAccountScreen(
                            accountNameWithSuffix = readyUiState.accountNameWithSuffix,
                            accountNameType = readyUiState.accountNameType.name,
                            skipToCreateAccountStep = false,
                            purchaseToken = paymentInfo.purchaseToken,
                            purchaseId = paymentInfo.orderId
                        )
                    )
                }
            }

            screenModel.onStartPurchaseFlow.collectAsState(null).value?.let {
                screenModel.purchaseManager.launchPurchaseFlow(
                    it,
                    screenModel.getObfuscatedProfileId()
                )
                screenModel.onRequestIapPurchaseInitiated()
            }

            Step3CreateAccountPaymentScreen(
                uiState,
                navigator,
                onClickChangePaymentAccount = {
                    val selectedAccountName =
                        (uiState as? Step3CreateAccountPaymentUiState.Ready)?.selectedAccount?.accountName
                            ?: return@Step3CreateAccountPaymentScreen

                    val screen = ScreenRegistry.get(
                        SharedScreen.SelectPaymentAccountScreen(selectedAccountName) { newAccountName ->
                            screenModel.onPaymentAccountChange(newAccountName)
                            bottomNavigator.hide()
                        }
                    )
                    bottomNavigator.show(screen)
                },
                onClickChangeAccountName = {
                    val uiState = uiState as? Step3CreateAccountPaymentUiState.Ready
                    val accountName =
                        uiState?.accountNameWithoutSuffix ?: return@Step3CreateAccountPaymentScreen
                    val accountNameType = uiState.accountNameType

                    val screen = ScreenRegistry.get(
                        SharedScreen.SelectAccountNameBottomSheetScreen(
                            initialAccountName = accountName,
                            accountNameSuffix = uiState.accountNameSuffix,
                            accountNameType
                        ) { newAccountName ->
                            screenModel.onAccountNameChange(newAccountName)
                            bottomNavigator.hide()
                        }
                    )
                    bottomNavigator.show(screen)
                },
                onClickPaymentOption = { paymentOption ->
                    screenModel.onClickPaymentOption(paymentOption)
                },
                onClickCreateAccount = {
                    val readyUiState = uiState as? Step3CreateAccountPaymentUiState.Ready
                    val paymentOption =
                        readyUiState?.selectedPaymentOption

                    screenModel.onClickCreateAccount()

                    when (paymentOption) {
                        PaymentOption.IN_APP_PURCHASE -> {
                            screenModel.onRequestIapPurchase()
                        }

                        PaymentOption.EXISTING_IMPORTED_ACCOUNT -> {
                            screenModel.onRequestTransaction()
                        }

                        PaymentOption.ASK_A_FRIEND_TO_CREATE -> {
                            val accountName = readyUiState.accountNameWithSuffix

                            val screen = ScreenRegistry.get(
                                SharedScreen.CreateByFriendBottomSheetScreen(
                                    accountName,
                                    onAccountCreated = {
                                        onAccountCreated(readyUiState, navigator)
                                    })
                            )
                            bottomNavigator.show(screen)
                        }

                        PaymentOption.FREE -> TODO()
                        PaymentOption.PAY_WITH_CRYPTO -> {
                            val uiState = uiState as? Step3CreateAccountPaymentUiState.Ready
                            val accountName =
                                uiState?.accountNameWithSuffix ?: return@Step3CreateAccountPaymentScreen
                            val accountNameType = uiState.accountNameType

                            val screen = ScreenRegistry.get(
                                SharedScreen.ChangeNetworkForPaymentScreen(
                                    accountName = accountName,
                                    accountNameType = accountNameType,
                                    eosOwnerPrivateKey = uiState.eosOwnerPrivateKey,
                                    eosActivePrivateKey = uiState.eosActivePrivateKey
                                )
                            )
                            navigator.push(screen)
                        }

                        null -> TODO()
                    }
                },
                onUpdateAccountType = {
                    (uiState as? Step3CreateAccountPaymentUiState.Ready)?.accountNameType?.let {
                        val screen = ScreenRegistry.get(
                            SharedScreen.SelectAccountTypeScreen(it) { newAccountType ->
                                screenModel.onAccountTypeChange(newAccountType)
                                bottomNavigator.hide()
                            }
                        )
                        bottomNavigator.show(screen)
                    }
                }
            )
        }
    }

    private fun onAccountCreated(
        uiState: Step3CreateAccountPaymentUiState.Ready,
        navigator: Navigator
    ) {
        val blockchainType = uiState.blockchainType
        val accountName = uiState.accountNameWithSuffix

        val nextSharedScreen = if (uiState.isPinSetup) {
            SharedScreen.BackupWalletAlertScreen(
                blockchainUid = blockchainType.uid,
                antelopeAccountName = accountName
            )
        } else {
            SharedScreen.SetupPinScreen(
                blockchainUid = blockchainType.uid,
                antelopeAccountName = accountName,
                pinCase = SharedScreen.SetupPinScreen.SetupPinScreenCase.CREATE_NEW_WALLET.name
            )
        }

        val nextScreen = ScreenRegistry.get(nextSharedScreen)

        navigator.replaceAll(nextScreen)
    }

    @Composable
    private fun Step3CreateAccountPaymentScreen(
        uiState: Step3CreateAccountPaymentUiState,
        navigator: Navigator,
        onClickChangePaymentAccount: () -> Unit,
        onClickChangeAccountName: () -> Unit,
        onClickPaymentOption: (PaymentOption) -> Unit,
        onClickCreateAccount: () -> Unit,
        onUpdateAccountType: () -> Unit
    ) {
        GradientBackground {
            MaxSizeColumn(verticalArrangement = Arrangement.SpaceBetween) {
                MaxWidthColumn {
                    MangalaWalletTopBar(
                        modifier = Modifier.background(Color.Transparent),
                        text = "",
                        onBackClicked = { navigator.pop() }
                    )
                    when (uiState) {
                        is Step3CreateAccountPaymentUiState.Loading -> {}
                        is Step3CreateAccountPaymentUiState.Ready -> {
                            CreateAccountPaymentScreenData(
                                uiState,
                                onClickChangePaymentAccount,
                                onClickChangeAccountName,
                                onClickPaymentOption,
                                onUpdateAccountType
                            )
                        }
                    }
                }
                MangalaButton(
                    label = MR.strings.button_step_3_create_account_payment_create.desc().localized(),
                    onClick = onClickCreateAccount,
                    enabled = uiState.confirmButtonEnabled,
                    modifier = Modifier.padding(horizontal = Dimensions.Padding.default).fillMaxWidth(),
                    style = MangalaTypography.Size17Medium(),
                    disabledBackgroundColor = Color.White,
                    disabledContentColor = Colors.mistGray
                )
            }
        }
    }

    @Composable
    fun CreateAccountPaymentScreenData(
        uiState: Step3CreateAccountPaymentUiState.Ready,
        onClickChangePaymentAccount: () -> Unit,
        onClickChangeAccountName: () -> Unit,
        onClickPaymentOption: (PaymentOption) -> Unit,
        onUpdateAccountType: () -> Unit
    ) {
        MaxWidthBox {
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = Colors.main1Text
                )
            }

            MaxWidthColumn(
                modifier = Modifier.verticalScroll(rememberScrollState())
                    .padding(horizontal = Dimensions.Padding.default),
            ) {
                VerticalSpacer(Spacing.XXXBASE)
                InfoText(
                    MR.strings.label_step_3_create_account_payment_account_type.desc()
                        .localized(),
                    getAccountTypeText(uiState.accountNameType),
                    MangalaWalletPack.EditCircle
                ) {
                    onUpdateAccountType()
                }
                InfoText(
                    label = MR.strings.label_step_3_create_account_payment_account_name.desc()
                        .localized(),
                    value = uiState.accountNameWithSuffix, icon = MangalaWalletPack.EditCircle,
                    leadingIcon = if (uiState.accountNameError) MangalaWalletPack.InfoCircle else null,
                    color = if (uiState.accountNameError) Colors.red else Colors.second,
                ) {
                    onClickChangeAccountName()
                }
                val (feeLabel, priceLabel) = if (uiState.iapProduct != null && uiState.selectedPaymentOption == PaymentOption.IN_APP_PURCHASE) {
                    MR.strings.label_step_3_create_account_payment_account_fee.desc()
                        .localized() to uiState.iapProduct.formattedPrice.orEmpty()
                } else {
                    "   " to "   "
                }
                InfoText(feeLabel, priceLabel, icon = null) {

                }
                VerticalSpacer(Spacing.XBASE)
                Text(
                    text = MR.strings.label_step_3_create_account_payment_account_payment_method.desc()
                        .localized(),
                    fontSize = 16.sp,
                    color = Color.Black,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
                CreateAccountPaymentOptions(
                    uiState,
                    onClickPaymentOption,
                    onClickChangePaymentAccount
                )
                val text = when (uiState.createAccountState) {
                    CreateAccountState.Created -> "Account created"
                    CreateAccountState.Creating -> "Creating account"
                    is CreateAccountState.Error -> uiState.createAccountState.errorMessageString.desc()
                        .localized()

                    CreateAccountState.NotCreated -> null
                }
                text?.let {
                    Text(
                        text,
                        color = if (uiState.createAccountState is CreateAccountState.Error) Colors.red else Colors.main1Text
                    )
                }
            }
        }
    }

    @Composable
    private fun CreateAccountPaymentOptions(
        uiState: Step3CreateAccountPaymentUiState.Ready,
        onClickPaymentOption: (PaymentOption) -> Unit,
        onClickChangePaymentAccount: () -> Unit
    ) {
        if (uiState.availablePaymentOptions.contains(PaymentOption.EXISTING_IMPORTED_ACCOUNT)) {
            VerticalSpacer(Spacing.SMALL)
            SelectionOption(
                selected = uiState.selectedPaymentOption == PaymentOption.EXISTING_IMPORTED_ACCOUNT,
                onClick = {
                    if (uiState.accounts.isNotEmpty()) {
                        onClickPaymentOption(PaymentOption.EXISTING_IMPORTED_ACCOUNT)
                    }
                },
                paddingEnd = 0.dp,
                paddingTop = 0.dp,
                paddingBottom = 0.dp,
                enabled = uiState.payWithExistingAccountEnabled
            ) {
                MaxWidthRow(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(Dimensions.Padding.quarter)) {
                        SelectionOptionText(
                            text = MR.strings.button_step_3_create_account_payment_pay_with_account.desc()
                                .localized(),
                            modifier = Modifier.padding(vertical = Dimensions.Padding.half)
                        )
                        SelectionOptionText(
                            text = uiState.selectedAccount?.accountName.orEmpty(),
                            color = Colors.second,
                            modifier = Modifier.padding(vertical = Dimensions.Padding.half)
                        )
                    }
                    IconButton(onClickChangePaymentAccount) {
                        Icon(
                            imageVector = MangalaWalletPack.TxSwap,
                            contentDescription = null,
                            tint = Colors.second,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
        if (uiState.availablePaymentOptions.contains(PaymentOption.IN_APP_PURCHASE)) {
            VerticalSpacer(Spacing.SMALL)
            SelectionOption(
                MR.strings.button_step_3_create_account_payment_in_app_purchase.desc()
                    .localized(),
                selected = uiState.selectedPaymentOption == PaymentOption.IN_APP_PURCHASE,
                enabled = uiState.iapPaymentEnabled
            ) {
                onClickPaymentOption(PaymentOption.IN_APP_PURCHASE)
            }
        }
        if (uiState.availablePaymentOptions.contains(PaymentOption.ASK_A_FRIEND_TO_CREATE)) {
            VerticalSpacer(Spacing.SMALL)
            SelectionOption(
                MR.strings.button_step_3_create_account_payment_ask_a_friend.desc()
                    .localized(),
                selected = uiState.selectedPaymentOption == PaymentOption.ASK_A_FRIEND_TO_CREATE
            ) {
                onClickPaymentOption(PaymentOption.ASK_A_FRIEND_TO_CREATE)
            }
        }
//        if (uiState.availablePaymentOptions.contains(PaymentOption.FREE)) {
//            VerticalSpacer(Spacing.SMALL)
//            SelectionOption(
//                MR.strings.button_step_3_create_account_payment_free.desc()
//                    .localized(),
//                selected = uiState.selectedPaymentOption == PaymentOption.FREE
//            ) {
//                onClickPaymentOption(PaymentOption.FREE)
//            }
//        }
        if (uiState.availablePaymentOptions.contains(PaymentOption.PAY_WITH_CRYPTO)) {
            VerticalSpacer(Spacing.SMALL)
            SelectionOption(
                MR.strings.title_create_eos_account_paid_with_crypto.desc()
                    .localized(),
                selected = uiState.selectedPaymentOption == PaymentOption.PAY_WITH_CRYPTO
            ) {
                onClickPaymentOption(PaymentOption.PAY_WITH_CRYPTO)
            }
        }
    }

    @OptIn(ExperimentalLayoutApi::class)
    @Composable
    fun InfoText(
        label: String?,
        value: String?,
        icon: ImageVector?,
        color: Color = Colors.second,
        leadingIcon: ImageVector? = null,
        onClick: () -> Unit,
    ) {
        FlowRow {
            CreateAccountPaymentScreenText(
                modifier = Modifier.align(Alignment.CenterVertically),
                text = label.orEmpty()
            )
            HorizontalSpacer(Dimensions.Padding.quarter)
            Row(
                Modifier.align(Alignment.CenterVertically),
                verticalAlignment = Alignment.CenterVertically
            ) {
                leadingIcon?.let {
                    Icon(leadingIcon, contentDescription = null)
                    HorizontalSpacer(Spacing.XTINY)
                }
                CreateAccountPaymentScreenText(
                    value.orEmpty(),
                    color = color,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
                HorizontalSpacer(Dimensions.Padding.quarter)
                icon?.let {
                    IconButton(onClick, modifier = Modifier.align(Alignment.CenterVertically)) {
                        Icon(icon, contentDescription = null)
                    }
                }
            }
        }
    }

    @Composable
    fun CreateAccountPaymentScreenText(
        text: String, color: Color = Colors.main1Text, modifier: Modifier = Modifier
    ) {
        TextNormal(
            text = text, fontWeight = FontWeight.Medium, color = color, modifier = modifier
        )
    }

    override val isBottomBarVisible: Boolean = false
}