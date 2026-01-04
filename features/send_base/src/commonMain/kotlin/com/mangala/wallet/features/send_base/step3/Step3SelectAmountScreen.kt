package com.mangala.wallet.features.send_base.step3

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.registry.ScreenRegistry
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import com.mangala.wallet.common.mokoresources.CornerRadius
import com.mangala.wallet.common.mokoresources.Dimensions
import com.mangala.wallet.common.mokoresources.FontType
import com.mangala.wallet.common.mokoresources.Spacing
import com.mangala.wallet.model.blockchain.BlockchainType
import com.mangala.wallet.model.blockchain.NetworkType
import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.ui.MangalaCommonDialog
import com.mangala.wallet.ui.SharedScreen
import com.mangala.wallet.ui.TextDescription2
import com.mangala.wallet.ui.TextNormal
import com.mangala.wallet.ui.component.BaseBottomSheet
import com.mangala.wallet.ui.component.BasicTextFieldWithHintAndTrailingIcons
import com.mangala.wallet.ui.component.HorizontalSpacer
import com.mangala.wallet.ui.component.MangalaButtonSize
import com.mangala.wallet.ui.component.MangalaButtonStyle
import com.mangala.wallet.ui.component.MangalaGradientButton
import com.mangala.wallet.ui.component.MangalaWalletTopBarCenteredTitle
import com.mangala.wallet.ui.component.MaxSizeBox
import com.mangala.wallet.ui.component.MaxSizeColumn
import com.mangala.wallet.ui.component.MaxWidthColumn
import com.mangala.wallet.ui.component.MaxWidthRow
import com.mangala.wallet.ui.component.OnboardingGradientBackground
import com.mangala.wallet.ui.component.VerticalSpacer
import com.mangala.wallet.ui.imageloader.LocalImage
import com.mangala.wallet.ui.placeholder.mangalaWalletPlaceholder
import com.mangala.wallet.ui.theme.MangalaTypography
import com.mangala.wallet.ui.theme.mangalaColors
import com.mangala.wallet.ui.utils.collectAsStateMultiplatform
import com.mangala.wallet.ui.utils.screenmodel.BaseScreen
import com.mangala.wallet.utils.analytics.MangalaAnalytics
import com.mangala.wallet.utils.isNotNullOrBlank
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.desc.desc
import org.koin.core.parameter.parametersOf

class Step3SelectAmountScreen(
    val accountId: String,
    val contactId: Long?,
    val receivingAddress: String?,
    val blockchainUid: String?,
    val amount: String?
) : BaseScreen<Step3SelectAmountScreenModel>() {

    override val screenName: String = MangalaAnalytics.Screens.SEND_TOKEN_SELECT_AMOUNT
    override val screenClassName: String = Step3SelectAmountScreen::class.simpleName.orEmpty()

    override val isBottomBarVisible = false

    @Composable
    override fun createScreenModel(): Step3SelectAmountScreenModel =
        getScreenModel<Step3SelectAmountScreenModel>(
            parameters = {
                parametersOf(
                    accountId,
                    contactId,
                    receivingAddress,
                    blockchainUid,
                    amount
                )
            }
        )

    @Composable
    override fun ScreenContent(screenModel: Step3SelectAmountScreenModel) {
        val parentNavigator = LocalNavigator.current ?: return // parent, non-bottom sheet navigator

        val sendTokenAmountState by screenModel.sendTokenAmountState.collectAsStateMultiplatform()
        sendTokenAmountState?.let { sendTokenAmountState ->
            val step4VerifyAndSendScreen =
                when (BlockchainType.fromUid(blockchainUid.orEmpty()).networkType) {
                    NetworkType.EVM -> {
                        SharedScreen.Step4EvmVerifyAndSendScreen(
                            contactId = sendTokenAmountState.contactId,
                            address = sendTokenAmountState.recipientAddress,
                            blockchainUid = sendTokenAmountState.blockchainUid,
                            tokenId = sendTokenAmountState.tokenId,
                            amount = sendTokenAmountState.amount,
                            accountId = sendTokenAmountState.accountId
                        )
                    }

                    NetworkType.ANTELOPE -> {
                        SharedScreen.Step4AntelopeVerifyAndSendScreen(
                            senderAccount = sendTokenAmountState.accountId,
                            toAccount = sendTokenAmountState.recipientAddress.orEmpty(),
                            blockchainUid = sendTokenAmountState.blockchainUid,
                            tokenSymbol = sendTokenAmountState.tokenId,
                            contactId = sendTokenAmountState.contactId,
                            amount = sendTokenAmountState.amount,
                            memo = sendTokenAmountState.memo ?: ""
                        )
                    }

                    NetworkType.BITCOIN -> {
                        SharedScreen.Step4BitcoinVerifyAndSendScreen(
                            contactId = sendTokenAmountState.contactId,
                            address = sendTokenAmountState.recipientAddress,
                            blockchainUid = sendTokenAmountState.blockchainUid,
                            tokenId = sendTokenAmountState.tokenId,
                            amount = sendTokenAmountState.amount,
                            accountId = sendTokenAmountState.accountId
                        )
                    }

                    else -> throw UnsupportedOperationException("Unsupported network type: ${blockchainUid.orEmpty()}")
                }

            parentNavigator.push(ScreenRegistry.get(step4VerifyAndSendScreen)) // Since we use bottom sheet, we need to push the screen to global navigator
            screenModel.clearState()
        }

        Body(
            screenModel,
            onClickAccountInfo = { selectedAccountId ->
                screenModel.onSelectAccount(selectedAccountId)
            },
            onClickContinue = {
                screenModel.clickContinue()
            },
            onClickBack = {
                parentNavigator.pop()
            },
            onClickCreateAccount = {
                val antelopeCreateAccountScreen =
                    ScreenRegistry.get(SharedScreen.AntelopeCreateAccountV2Screen)
                parentNavigator.push(antelopeCreateAccountScreen)
            },
            onClickImportAccount = {
                val antelopeImportAccountScreen =
                    ScreenRegistry.get(SharedScreen.ImportPrivateKeyScreen)
                parentNavigator.push(antelopeImportAccountScreen)
            }
        )
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun Body(
        screenModel: Step3SelectAmountScreenModel,
        onClickAccountInfo: (accountId: String) -> Unit,
        onClickContinue: () -> Unit,
        onClickBack: () -> Unit,
        onClickCreateAccount: () -> Unit,
        onClickImportAccount: () -> Unit
    ) {
        val keyboardController: SoftwareKeyboardController? =
            LocalSoftwareKeyboardController.current

        val nameDisplay = if (contactId != null) screenModel.selectedContact?.name ?: ""
        else receivingAddress ?: ""

        val contactInput = remember { mutableStateOf(nameDisplay) }

        val selectedAmount = screenModel.selectedAmount.value

        val amountFocusRequester = remember { FocusRequester() }
        val tokenFocusRequester = remember { FocusRequester() }
        val accountFocusRequester = remember { FocusRequester() }
        val memoFocusRequester = remember { FocusRequester() }

        val isTokenFocus = remember { mutableStateOf(false) }
        val isAccountFocus = remember { mutableStateOf(false) }
        val isAmountFocus = remember { mutableStateOf(false) }

        val uiState by screenModel.uiState.collectAsStateMultiplatform()
        val selectTokenUiState = uiState as? Step3SelectAmountScreenUiState.SelectToken
        val selectAccountUiState = remember {
            derivedStateOf {
                uiState as? Step3SelectAmountScreenUiState.SelectAccount
            }
        }
        val currencySymbol = selectTokenUiState?.currencySymbol.orEmpty()

        val isShowNoImportedAccountCTA = remember {
            derivedStateOf {
                selectAccountUiState.value?.isNoImportedAccount == true
            }
        }
        val noImportedAccountCTABottomSheetState = rememberModalBottomSheetState(true)

        if (selectAccountUiState.value?.isTransferToSelf == true) {
            MangalaCommonDialog(
                onClickActionButton = {
                    screenModel.onResetToSelectAccount()
                },
                actionButtonText = MR.strings.all_ok.desc().localized(),
                title = MR.strings.title_send_token_transfer_to_self_alert_dialog.desc()
                    .localized(),
                message = MR.strings.message_send_token_transfer_to_self_alert_dialog.desc()
                    .localized()
            )
        }

        OnboardingGradientBackground {
            MaxSizeColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectTapGestures {
                            keyboardController?.hide()
                        }
                    }
                    .windowInsetsPadding(WindowInsets.safeDrawing)
            ) {
                MangalaWalletTopBarCenteredTitle(
                    title = MR.strings.message_send_token_send_token.desc().localized(),
                    onBackClicked = onClickBack
                )

                BaseBottomSheet(
                    modalSheetState = noImportedAccountCTABottomSheetState,
                    isShowBottomSheet = isShowNoImportedAccountCTA.value,
                    onDismiss = onClickBack
                ) {
                    OnboardingGradientBackground(
                        modifier = Modifier,
                        afterBackgroundModifier = Modifier
                    ) {
                        NoImportedAccountState(
                            onClickCreate = onClickCreateAccount,
                            onClickImport = onClickImportAccount
                        )
                    }
                }

                MaxWidthColumn(Modifier.weight(1f)) {
                    Spacer(modifier = Modifier.height(Spacing.XLARGE))
                    MaxWidthColumn {
                        MaxWidthColumn(Modifier.verticalScroll(rememberScrollState())) {
                            TextNormal(
                                MR.strings.message_send_token_transferring.desc().localized(),
                                modifier = Modifier.fillMaxWidth()
                                    .padding(horizontal = Spacing.SMALL),
                                color = MaterialTheme.mangalaColors.textPrimary,
                                fontWeight = FontWeight.W500
                            )
                            VerticalSpacer(Spacing.SMALL)
                            RecipientText(
                                contactInput = contactInput,
                                onValueChange = { contactInput.value = it },
                                onClickBack = onClickBack
                            )
                            VerticalSpacer(Spacing.SMALL)
                            AccountSearchTextField(
                                uiState.accountQuery,
                                screenModel::filterAccounts,
                                screenModel,
                                keyboardController,
                                uiState.doneSelectAccount,
                                isAccountFocus,
                                accountFocusRequester
                            )
                            if (uiState.doneSelectAccount) {
                                VerticalSpacer(Spacing.SMALL)
                                TokenSearchTextField(
                                    screenModel,
                                    uiState,
                                    keyboardController,
                                    uiState.doneSelectToken,
                                    isTokenFocus,
                                    tokenFocusRequester,
                                    onTokenInputChange = {
                                        screenModel.onUpdateTokenQuery(it)
                                    }
                                )
                                LaunchedEffect(uiState is Step3SelectAmountScreenUiState.SelectToken) {
                                    tokenFocusRequester.requestFocus()
                                    keyboardController?.hide()
                                }
                            }
                            if (uiState.doneSelectToken) {
                                VerticalSpacer(Spacing.SMALL)
                                AmountTextField(
                                    isSufficientBalance = uiState.isAmountError,
                                    validationError = (uiState as? Step3SelectAmountScreenUiState.SelectAmount)?.amountValidationError
                                        ?: (uiState as? Step3SelectAmountScreenUiState.EnterMemo)?.amountValidationError,
                                    selectedAmount = selectedAmount,
                                    doneSelectAmount = uiState.doneSelectAmount,
                                    isAmountFocus = isAmountFocus,
                                    screenModel = screenModel,
                                    keyboardController = keyboardController,
                                    amountFocusRequester = amountFocusRequester
                                )
                                LaunchedEffect(uiState is Step3SelectAmountScreenUiState.SelectAmount) {
                                    amountFocusRequester.requestFocus()
                                }
                            }
                            if (uiState.doneSelectAmount) {
                                VerticalSpacer(Spacing.SMALL)
                                val enterMemoUiState =
                                    uiState as? Step3SelectAmountScreenUiState.EnterMemo
                                MemoTextField(
                                    memo = enterMemoUiState?.memo.orEmpty(),
                                    onValueChange = screenModel::onMemoChange,
                                    focusRequester = memoFocusRequester,
                                    onDoneAction = {
                                        onClickContinue()
                                    }
                                )
                                LaunchedEffect(uiState is Step3SelectAmountScreenUiState.EnterMemo) {
                                    memoFocusRequester.requestFocus()
                                }
                            }
                        }
                        if (selectTokenUiState != null) {
                            VerticalSpacer(Spacing.XXXLARGE)
                            TokenList(
                                uiState = selectTokenUiState,
                                allTokens = selectTokenUiState.tokenList,
                                onTokenSelected = { token ->
                                    screenModel.onSelectToken(token)
                                    keyboardController?.hide()
                                },
                                filter = selectTokenUiState.tokenInput,
                                currencySymbol = currencySymbol
                            )
                        }
                        if (selectAccountUiState.value?.selectedAccount == null) {
                            selectAccountUiState.value?.let {
                                AccountSelector(
                                    it.filteredAccounts,
                                    it.selectedAccount,
                                    onClickAccountInfo
                                )
                            }
                        }
                    }
                }
                if (uiState.doneSelectToken) {
                    MaxWidthRow(Modifier.padding(Dimensions.Padding.default)) {
                        MangalaGradientButton(
                            label = MR.strings.all_continue.desc().localized(),
                            onClick = {
                                if (uiState.doneSelectAmount) {
                                    onClickContinue()
                                } else {
                                    keyboardController?.hide()
                                    if (uiState.isAmountError) {
                                        screenModel.onEnterAmountDone()
                                    }
                                }
                            },
                            enabled = if (uiState.doneSelectAmount && !uiState.isAmountError) {
                                selectedAmount?.isNotEmpty() == true && uiState.buttonEnabled
                            } else {
                                uiState.doneSelectToken && selectedAmount?.isNotEmpty() == true && uiState.isAmountError
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }

    @Composable
    private fun ColumnScope.AccountSelector(
        accounts: List<SelectAmountAccountWrapper>,
        selectedAccount: SelectAmountAccountWrapper?,
        onClickAccountInfo: (accountId: String) -> Unit
    ) {
        Spacer(Modifier.weight(0.4f))
        Column(
            Modifier
                .weight(0.6f)
                .fillMaxWidth()
                .padding(start = Spacing.SMALL, end = Spacing.SMALL)
        ) {
            TextNormal(
                MR.strings.message_send_token_from.desc().localized(),
                color = MaterialTheme.mangalaColors.textPrimary,
                fontWeight = FontWeight.W500
            )
            Spacer(modifier = Modifier.height(Spacing.SMALL))
            if (accounts.isEmpty()) {
                TextNormal(
                    MR.strings.message_send_token_no_matching_accounts.desc().localized(),
                    color = MaterialTheme.mangalaColors.textSecondary
                )
            } else {
                LazyColumn(
                    Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(Spacing.XSMALL)
                ) {
                    items(accounts) {
                        AccountItem(it, selectedAccount == it, onClickAccountInfo)
                    }
                }
            }
        }
    }

    @Composable
    private fun AmountTextField(
        isSufficientBalance: Boolean,
        validationError: AmountValidationError?,
        selectedAmount: String?,
        doneSelectAmount: Boolean,
        isAmountFocus: MutableState<Boolean>,
        screenModel: Step3SelectAmountScreenModel,
        keyboardController: SoftwareKeyboardController?,
        amountFocusRequester: FocusRequester
    ) {
        Column {
            MaxWidthRow(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(start = Spacing.SMALL, end = Spacing.SMALL)
            ) {
                TextNormal(
                    MR.strings.message_send_token_amount.desc().localized(),
                    color = MaterialTheme.mangalaColors.textPrimary,
                    fontWeight = FontWeight.W500
                )
                HorizontalSpacer(Spacing.TINY)
                MaterialTheme(
                    colorScheme = MaterialTheme.colorScheme.copy(primary = MaterialTheme.mangalaColors.iconPrimary)
                ) {
                    val colorText =
                        if ((isSufficientBalance.not() || validationError != null) && selectedAmount.isNotNullOrBlank()) MaterialTheme.mangalaColors.buttonDestructiveContainer else MaterialTheme.mangalaColors.textLink
                    BasicTextFieldWithHintAndTrailingIcons(
                        selectedAmount.orEmpty(),
                        onValueChange = {
                            screenModel.onAmountChange(it) // TODO: Check double .., or invalid character like , when using another language
                            screenModel.checkValidAmount()
                        },
                        textColor = colorText,
                        fontWeight = FontWeight.W500,
                        fontSize = FontType.REGULAR,
                        hint = "........",
                        hintColor = MaterialTheme.mangalaColors.textSecondary,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Decimal,
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = {
                                keyboardController?.hide()
                                if (isSufficientBalance) {
                                    screenModel.onEnterAmountDone()
                                }
                            }
                        ),
                        textFieldModifier = Modifier
                            .onFocusChanged { focusState ->
                                if (focusState.isFocused) {
                                    if (doneSelectAmount) {
                                        screenModel.onResetToSelectAmount()
                                    }
                                    isAmountFocus.value = true
                                }
                            }
                            .fillMaxWidth()
                            .focusRequester(amountFocusRequester),
                        trailingIcon = {
                            Row(verticalAlignment = Alignment.Bottom) {
                                if (selectedAmount?.isNotEmpty() == true) {
                                    IconButton(
                                        onClick = { screenModel.selectedAmount.value = "" },
                                        modifier = Modifier.size(Dimensions.IconButtonSize)
                                    ) {
                                        Icon(
                                            Icons.Default.Clear,
                                            contentDescription = null,
                                            tint = MaterialTheme.mangalaColors.iconPrimary,
                                            modifier = Modifier.size(Dimensions.IconSize)
                                        )
                                    }

                                } else {
                                    Box(
                                        modifier = Modifier.clickable {
                                            screenModel.clickMaxToken()
                                        }
                                    ) {
                                        Text(
                                            text = MR.strings.message_send_token_max.desc()
                                                .localized(),
                                            style = MangalaTypography.Size17SemiBold(),
                                            color = MaterialTheme.mangalaColors.textPrimary,
                                        )
                                    }
                                }
                            }
                        }
                    )
                }
            }

            if (validationError != null && selectedAmount?.isNotEmpty() == true) {
                VerticalSpacer(Spacing.XSMALL)
                Row(
                    modifier = Modifier.padding(start = Spacing.SMALL),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = validationError.errorMessage,
                        color = MaterialTheme.mangalaColors.buttonDestructiveContainer,
                        style = MangalaTypography.Size14Regular(),
                    )
                }
            }
        }
    }

    @Composable
    private fun RecipientText(
        contactInput: MutableState<String>,
        onValueChange: (String) -> Unit,
        onClickBack: () -> Unit
    ) {
        MaxWidthRow(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = Spacing.SMALL)
        ) {
            TextNormal(
                MR.strings.message_send_token_to.desc().localized(),
                color = MaterialTheme.mangalaColors.textPrimary,
                fontWeight = FontWeight.W500
            )
            HorizontalSpacer(Spacing.TINY)
            MaterialTheme(
                colorScheme = MaterialTheme.colorScheme.copy(primary = MaterialTheme.mangalaColors.iconPrimary)
            ) {
                BasicTextFieldWithHintAndTrailingIcons(
                    contactInput.value,
                    onValueChange = onValueChange,
                    textColor = MaterialTheme.mangalaColors.textLink,
                    fontWeight = FontWeight.W500,
                    fontSize = FontType.REGULAR,
                    textFieldModifier = Modifier
                        .onFocusChanged { focusState ->
                            if (focusState.isFocused) {
                                onClickBack()
                            }
                        }
                        .wrapContentWidth(),
                    trailingIcon = {},
                    hint = "",
                    singleLine = false
                )
            }
        }
    }

    @Composable
    private fun MemoTextField(
        memo: String,
        onValueChange: (String) -> Unit,
        focusRequester: FocusRequester,
        onDoneAction: () -> Unit = {}
    ) {
        MaxWidthRow(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = Spacing.SMALL)
        ) {
            TextNormal(
                MR.strings.all_memo.desc().localized(),
                color = MaterialTheme.mangalaColors.textPrimary,
                fontWeight = FontWeight.W500
            )
            HorizontalSpacer(Spacing.TINY)
            MaterialTheme(
                colorScheme = MaterialTheme.colorScheme.copy(primary = MaterialTheme.mangalaColors.iconPrimary)
            ) {
                BasicTextFieldWithHintAndTrailingIcons(
                    memo,
                    onValueChange = onValueChange,
                    textColor = MaterialTheme.mangalaColors.textLink,
                    fontWeight = FontWeight.W500,
                    fontSize = FontType.REGULAR,
                    textFieldModifier = Modifier
                        .wrapContentWidth()
                        .focusRequester(focusRequester),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            focusRequester.freeFocus()
                            onDoneAction()
                        }
                    ),
                    trailingIcon = {
                        Row(verticalAlignment = Alignment.Bottom) {
                            if (memo.isNotEmpty()) {
                                IconButton(
                                    onClick = { onValueChange("") },
                                    modifier = Modifier.size(Dimensions.IconButtonSize)
                                ) {
                                    Icon(
                                        Icons.Default.Clear,
                                        contentDescription = null,
                                        tint = MaterialTheme.mangalaColors.iconPrimary,
                                        modifier = Modifier.size(Dimensions.IconSize)
                                    )
                                }
                            }
                        }
                    },
                    hint = "Optional",
                    hintColor = MaterialTheme.mangalaColors.textSecondary,
                    singleLine = false
                )
            }
        }
    }

    @Composable
    private fun TokenSearchTextField(
        screenModel: Step3SelectAmountScreenModel,
        uiState: Step3SelectAmountScreenUiState,
        keyboardController: SoftwareKeyboardController?,
        doneSelectToken: Boolean,
        isTokenFocus: MutableState<Boolean>,
        tokenFocusRequester: FocusRequester,
        onTokenInputChange: (String) -> Unit
    ) {
        MaxWidthRow(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = Spacing.SMALL)
        ) {
            TextNormal(
                MR.strings.message_send_token_token.desc().localized(),
                color = MaterialTheme.mangalaColors.textPrimary,
                fontWeight = FontWeight.W500
            )
            HorizontalSpacer(Spacing.TINY)
            MaterialTheme(
                colorScheme = MaterialTheme.colorScheme.copy(primary = MaterialTheme.mangalaColors.iconPrimary)
            ) {
                BasicTextFieldWithHintAndTrailingIcons(
                    uiState.tokenQuery,
                    onValueChange = onTokenInputChange,
                    hint = MR.strings.hint_send_token_token_name.desc().localized(),
                    hintColor = MaterialTheme.mangalaColors.textSecondary,
                    textColor = MaterialTheme.mangalaColors.textLink,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = FontType.REGULAR,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Search
                    ),
                    keyboardActions = KeyboardActions(
                        onSearch = {
                            keyboardController?.hide()
                        }
                    ),
                    textFieldModifier = Modifier
                        .fillMaxWidth()
                        .onFocusChanged { focusState ->
                            if (focusState.isFocused) {
                                if (doneSelectToken) {
                                    screenModel.onResetToSelectToken()
                                }
                                isTokenFocus.value = true
                            }
                        }
                        .focusRequester(tokenFocusRequester),
                    trailingIcon = {
                        if (uiState.tokenQuery.isNotEmpty()) {
                            if (isTokenFocus.value) {
                                IconButton(
                                    onClick = {
                                        screenModel.onResetToSelectToken()
                                    },
                                    modifier = Modifier.size(Dimensions.IconButtonSize)
                                ) {
                                    Icon(
                                        Icons.Default.Clear,
                                        contentDescription = null,
                                        tint = MaterialTheme.mangalaColors.iconPrimary,
                                        modifier = Modifier.size(Dimensions.IconSize)
                                    )
                                }
                            }
                        }
                    }
                )
            }
        }
    }

    @Composable
    private fun AccountSearchTextField(
        query: String,
        onQueryChange: (String) -> Unit,
        screenModel: Step3SelectAmountScreenModel,
        keyboardController: SoftwareKeyboardController?,
        doneSelectAccount: Boolean,
        isAccountFocus: MutableState<Boolean>,
        accountFocusRequester: FocusRequester
    ) {
        MaxWidthRow(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = Spacing.SMALL)
        ) {
            TextNormal(
                MR.strings.message_send_token_from.desc().localized(),
                color = MaterialTheme.mangalaColors.textPrimary,
                fontWeight = FontWeight.W500
            )
            HorizontalSpacer(Spacing.TINY)
            MaterialTheme(
                colorScheme = MaterialTheme.colorScheme.copy(primary = MaterialTheme.mangalaColors.iconPrimary)
            ) {
                BasicTextFieldWithHintAndTrailingIcons(
                    query,
                    onValueChange = {
                        onQueryChange(it)
                    },
                    hint = MR.strings.hint_send_token_account_name.desc().localized(),
                    hintColor = MaterialTheme.mangalaColors.textSecondary,
                    textColor = MaterialTheme.mangalaColors.textLink,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = FontType.REGULAR,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Search
                    ),
                    keyboardActions = KeyboardActions(
                        onSearch = {
                            keyboardController?.hide()
                        }
                    ),
                    textFieldModifier = Modifier
                        .fillMaxWidth()
                        .onFocusChanged { focusState ->
                            if (focusState.isFocused) {
                                if (doneSelectAccount) {
                                    screenModel.onResetToSelectAccount()
                                }
                                isAccountFocus.value = true
                            }
                        }
                        .focusRequester(accountFocusRequester),
                    trailingIcon = {
                        if (query.isNotEmpty()) {
                            if (isAccountFocus.value) {
                                IconButton(
                                    onClick = {
                                        screenModel.onResetToSelectAccount()
                                    },
                                    modifier = Modifier.size(Dimensions.IconButtonSize)
                                ) {
                                    Icon(
                                        Icons.Default.Clear,
                                        contentDescription = null,
                                        tint = MaterialTheme.mangalaColors.iconPrimary,
                                        modifier = Modifier.size(Dimensions.IconSize)
                                    )
                                }
                            }
                        }
                    }
                )
            }
        }
    }

    @Composable
    private fun ColumnScope.TokenList(
        uiState: Step3SelectAmountScreenUiState.SelectToken,
        allTokens: List<SelectAmountTokenWrapper>,
        onTokenSelected: (SelectAmountTokenWrapper) -> Unit,
        currencySymbol: String,
        filter: String
    ) {
        val isLoading = uiState.isLoading

        Box(Modifier.weight(1f)) {
            if (isLoading) {
                MaxSizeBox {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .size(20.dp),
                        color = MaterialTheme.mangalaColors.iconPrimary
                    )
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(vertical = Dimensions.Padding.default)
                ) {
                    items(allTokens.size) { index ->
                        TokenItem(
                            allTokens[index],
                            onTokenSelected,
                            filter,
                            currencySymbol,
                            isLoading
                        )
                    }
                }
            }
        }
    }

    @Composable
    private fun TokenItem(
        token: SelectAmountTokenWrapper,
        onTokenSelected: (SelectAmountTokenWrapper) -> Unit,
        filter: String,
        currencySymbol: String,
        isLoading: Boolean
    ) {
        Box(
            modifier = Modifier.fillMaxWidth().clickable {
                onTokenSelected(token)
            }.padding(horizontal = Dimensions.Padding.default)
        ) {
            Row(
                modifier = Modifier.height(65.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                LocalImage(
                    modifier = Modifier.size(32.dp).clip(CircleShape),
                    token.localImage,
                    isLoading = false,
                    placeholderModifier = Modifier.size(32.dp)
                )
                Spacer(Modifier.width(8.dp))
                val text = buildAnnotatedString {
                    val name = token.name
                    val defaultColor = MaterialTheme.mangalaColors.textPrimary
                    val highlightColor = MaterialTheme.mangalaColors.textLink

                    var startIndex = 0
                    val pattern = filter.toRegex(RegexOption.IGNORE_CASE)

                    pattern.findAll(name).forEach { result ->
                        val matchStart = result.range.first
                        val matchEnd = result.range.last
                        if (matchStart > startIndex) {
                            withStyle(style = SpanStyle(color = defaultColor)) {
                                append(name.substring(startIndex, matchStart))
                            }
                        }
                        withStyle(style = SpanStyle(color = highlightColor)) {
                            append(name.substring(matchStart, matchEnd + 1))
                        }
                        startIndex = matchEnd + 1
                    }

                    if (startIndex < name.length) {
                        withStyle(style = SpanStyle(color = defaultColor)) {
                            append(name.substring(startIndex))
                        }
                    }
                }

                //                            Text(
                //                                text = text,
                //                                fontWeight = FontWeight.Normal,
                //                                fontSize = FontType.SMALL,
                //                                fontFamily = fontFamilyResource(MR.fonts.sfpro.sfpro)
                //                            )

                Column {
                    Text(
                        text = text,
                        style = MangalaTypography.Size14Medium(),
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = token.currentPrice(currencySymbol),
                        style = MangalaTypography.Size14Regular(),
                        color = MaterialTheme.mangalaColors.textSecondary,
                    )
                }

                Column(Modifier.weight(1f), horizontalAlignment = Alignment.End) {
                    Text(
                        token.formattedBalance,
                        style = MangalaTypography.Size14SemiBold(),
                        color = MaterialTheme.mangalaColors.textPrimary,
                        textAlign = TextAlign.End,
                        modifier = Modifier.mangalaWalletPlaceholder(
                            isLoading,
                            modifier = Modifier.size(79.dp, 20.dp)
                        )
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        token.formattedValue(currencySymbol),
                        style = MangalaTypography.Size14Regular(),
                        color = MaterialTheme.mangalaColors.textSecondary,
                        textAlign = TextAlign.End,
                        modifier = Modifier.mangalaWalletPlaceholder(
                            isLoading,
                            modifier = Modifier.size(46.dp, 18.dp)
                        )
                    )
                }
            }
        }
    }

    @Composable
    private fun AccountItem(
        account: SelectAmountAccountWrapper,
        isSelected: Boolean,
        onClick: (accountId: String) -> Unit
    ) {
        val borderColor =
            if (isSelected) MaterialTheme.mangalaColors.borderHighlight else SolidColor(
                MaterialTheme.mangalaColors.border
            )
        val shape = remember { RoundedCornerShape(CornerRadius.Medium) }

        MaxWidthColumn(
            verticalArrangement = Arrangement.spacedBy(Spacing.TINY, Alignment.CenterVertically),
            modifier = Modifier
                .border(width = 1.dp, brush = borderColor, shape = shape)
                .clip(shape)
                .clickable {
                    onClick(account.accountId)
                }
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            TextDescription2(
                text = account.accountName,
                color = MaterialTheme.mangalaColors.textPrimary,
                fontWeight = FontWeight.W500
            )
            TextDescription2(
                text = account.formattedAddress,
                color = MaterialTheme.mangalaColors.textSecondary
            )
        }
    }

    @Composable
    private fun NoImportedAccountState(
        onClickCreate: () -> Unit,
        onClickImport: () -> Unit
    ) {
        MaxWidthColumn(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(horizontal = Dimensions.Padding.default)
        ) {
            Spacer(Modifier.height(Spacing.BASE))

            LocalImage(
                imageResource = MR.images.NoContact,
                modifier = Modifier.size(120.dp)
            )

            Spacer(modifier = Modifier.height(Spacing.SMALL))

            Text(
                text = MR.strings.title_send_token_no_imported_account.desc()
                    .localized(),
                style = MangalaTypography.Size17Medium(),
                color = MaterialTheme.mangalaColors.textPrimary
            )

            Spacer(modifier = Modifier.height(Spacing.TINY))

            Text(
                text = MR.strings.message_send_token_no_imported_account.desc()
                    .localized(),
                style = MangalaTypography.Size14Regular(),
                color = MaterialTheme.mangalaColors.textSecondary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(Spacing.BASE))

            MangalaGradientButton(
                label = MR.strings.onboarding_button_create_wallet.desc().localized(),
                onClick = onClickCreate,
                buttonStyle = MangalaButtonStyle.GRADIENT,
                size = MangalaButtonSize.Small,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(Spacing.BASE))

            MangalaGradientButton(
                label = MR.strings.onboarding_button_import_wallet.desc().localized(),
                onClick = onClickImport,
                buttonStyle = MangalaButtonStyle.TRANSPARENT,
                size = MangalaButtonSize.Small,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(Spacing.XBASE))
        }
    }
}