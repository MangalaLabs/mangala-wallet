package com.mangala.wallet.features.chains.antelope.presentation.multisig.proposal.create

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.AlertDialog
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.LocalMinimumInteractiveComponentEnforcement
import androidx.compose.material.Text
import androidx.compose.material3.BottomSheetDefaults.DragHandle
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.annotation.ExperimentalVoyagerApi
import cafe.adriel.voyager.core.registry.ScreenRegistry
import cafe.adriel.voyager.koin.getNavigatorScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.bottomSheet.LocalBottomSheetNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.mangala.wallet.common.mokoresources.ColorsNew
import com.mangala.wallet.common.mokoresources.CornerRadius
import com.mangala.wallet.common.mokoresources.Dimensions
import com.mangala.wallet.common.mokoresources.Spacing
import com.mangala.wallet.common.mokoresources.icons.MangalaWalletPack
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.ArrowRightWithStem
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.Calendar
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.Delete
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.EditNew
import com.mangala.wallet.features.chains.antelope.presentation.multisig.proposal.create.action.MultisigProposalActionScreen
import com.mangala.wallet.features.chains.antelope.presentation.multisig.proposal.create.approver.MultisigProposalApproverScreen
import com.mangala.wallet.features.chains.antelope.presentation.multisig.proposal.create.selectAccPer.SelectAccountPermissionScreen
import com.mangala.wallet.features.chains.antelope.presentation.multisig.proposal.ui.DeleteConfirmationDialog
import com.mangala.wallet.features.chains.antelope.presentation.multisig.proposal.ui.ProposalInputLabel
import com.mangala.wallet.features.chains.antelope.presentation.multisig.proposal.ui.ProposalTextButton
import com.mangala.wallet.features.chains.antelope.presentation.multisig.proposal.ui.ProposalTextInputField
import com.mangala.wallet.features.chains.antelope.presentation.multisig.proposal.ui.ProposalTopAppBar
import com.mangala.wallet.features.chains.antelope_base.presentation.ui.AntelopeResourceProviderFeeDialog
import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.ui.SharedScreen
import com.mangala.wallet.ui.component.ExecuteTransactionSuccess
import com.mangala.wallet.ui.component.MangalaBottomSheetNavigator
import com.mangala.wallet.ui.component.MangalaButtonSize
import com.mangala.wallet.ui.component.MangalaConfirmationDialog
import com.mangala.wallet.ui.component.MangalaGradientButton
import com.mangala.wallet.ui.component.MangalaTextButton
import com.mangala.wallet.ui.component.MangalaWalletSearchBarDefaults
import com.mangala.wallet.ui.component.MangalaWalletSearchBarWithBorder
import com.mangala.wallet.ui.component.MaxSizeColumn
import com.mangala.wallet.ui.component.MaxWidthColumn
import com.mangala.wallet.ui.component.VerticalSpacer
import com.mangala.wallet.ui.theme.MangalaTypography
import com.mangala.wallet.ui.theme.mangalaColors
import com.mangala.wallet.ui.utils.collectAsStateMultiplatform
import com.mangala.wallet.ui.utils.resolve
import com.mangala.wallet.ui.utils.screenmodel.BaseScreen
import com.mangala.wallet.utils.FormatStyle
import com.mangala.wallet.utils.analytics.MangalaAnalytics
import com.mangala.wallet.utils.formatDate
import com.mangala.wallet.utils.formatTime
import com.mangala.wallet.utils.getShortMonthName
import com.mangala.wallet.utils.isNotNullOrBlank
import dev.darkokoa.datetimewheelpicker.WheelDatePicker
import dev.darkokoa.datetimewheelpicker.WheelTimePicker
import dev.darkokoa.datetimewheelpicker.core.WheelPickerDefaults
import dev.darkokoa.datetimewheelpicker.core.format.DateOrder
import dev.darkokoa.datetimewheelpicker.core.format.MonthDisplayStyle
import dev.darkokoa.datetimewheelpicker.core.format.dateFormatter
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.desc.desc
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime

class CreateNewProposalScreen : BaseScreen<CreateNewProposalScreenModel>() {

    override val screenName: String = MangalaAnalytics.Screens.ANTELOPE_MULTISIG_CREATE_NEW_PROPOSAL
    override val screenClassName: String = CreateNewProposalScreen::class.simpleName.orEmpty()

    override val isBottomBarVisible: Boolean = false

    @OptIn(ExperimentalVoyagerApi::class)
    @Composable
    override fun createScreenModel(): CreateNewProposalScreenModel {
        val navigator = LocalNavigator.currentOrThrow
        return navigator.getNavigatorScreenModel<CreateNewProposalScreenModel>()
    }

    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    override fun ScreenContent(screenModel: CreateNewProposalScreenModel) {
        val navigator = LocalNavigator.currentOrThrow

        val keyboardController = LocalSoftwareKeyboardController.current
        val focusManager = LocalFocusManager.current

        var showDraftDialog by remember { mutableStateOf(false) }
        var showDeleteConfirmation by remember { mutableStateOf(false) }

        LaunchedEffect(true) {
            screenModel.onSaveDone.receiveAsFlow().collectLatest {
                navigator.popUntilRoot()
            }
        }

        LaunchedEffect(true) {
            onBackPressedCallback = {
                if (screenModel.shouldPromptSaveDraft() && showDraftDialog.not()) {
                    showDraftDialog = true
                    false
                } else {
                    true
                }
            }
        }

        val uiState by screenModel.uiState.collectAsStateMultiplatform(Dispatchers.Main.immediate)

        if (showDraftDialog) {
            MangalaConfirmationDialog(
                title = uiState.saveDraftTitle.desc().localized(),
                description = uiState.saveDraftText.resolve(),
                positiveButtonText = uiState.saveDraftPositiveButtonText.desc().localized(),
                negativeButtonText = uiState.saveDraftNegativeButtonText.desc().localized(),
                onDismiss = {
                    showDraftDialog = false
                },
                onNegativeButton = {
                    showDraftDialog = false
                    navigator.popUntilRoot()
                },
                onPositiveAction = {
                    showDraftDialog = false
                    screenModel.onSaveDraft()
                }
            )
        }

        if (showDeleteConfirmation) {
            DeleteConfirmationDialog(
                title = MR.strings.title_multisig_proposal_delete_draft_confirmation.desc()
                    .localized(),
                description = MR.strings.message_multisig_proposal_delete_draft_confirmation_description.desc()
                    .localized(),
                onDismiss = {
                    showDeleteConfirmation = false
                },
                onDeleteAction = {
                    screenModel.onDeleteDraftProposal()
                    showDeleteConfirmation = false
                }
            )
        }

        if (uiState.error != null) {
            AlertDialog(
                onDismissRequest = { screenModel.onDismissTransactionError() },
                title = {
                    Text(
                        text = MR.strings.title_create_new_proposal_error.desc().localized(),
                        style = MangalaTypography.Size17SemiBold(),
                        color = MaterialTheme.mangalaColors.textPrimary
                    )
                },
                text = {
                    Text(
                        text = uiState.error?.resolve().orEmpty(),
                        style = MangalaTypography.Size13Regular(),
                        color = MaterialTheme.mangalaColors.textSecondary
                    )
                },
                confirmButton = {
                    MangalaGradientButton(
                        label = MR.strings.all_ok.desc().localized(),
                        onClick = {
                            screenModel.onDismissTransactionError()
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                shape = RoundedCornerShape(CornerRadius.Medium),
                modifier = Modifier.padding(Dimensions.Padding.default),
                backgroundColor = MaterialTheme.mangalaColors.bgInnerCard,
            )
        }

        MangalaBottomSheetNavigator(
            sheetShape = RectangleShape
        ) {
            val bottomSheetNavigator = LocalBottomSheetNavigator.current

            LaunchedEffect((uiState).promptConfirmTransaction) {
                if ((uiState).promptConfirmTransaction) {
                    val unlockPinScreen = ScreenRegistry.get(
                        SharedScreen.UnlockPinScreen(
                            SharedScreen.UnlockPinScreen.VERIFY_SEND_TRANSACTION,
                            onUnlockSuccess = {
                                bottomSheetNavigator.hide()
                                screenModel.onAuthenticationSuccess(uiState.proposerName)
                            },
                            antelopeAccountName = null
                        )
                    )
                    bottomSheetNavigator.show(unlockPinScreen)
                    screenModel.onPinPromptShown()
                }
            }

            if ((uiState).resourceRequiredBreakdown != null) {
                AntelopeResourceProviderFeeDialog(
                    feeBreakdown = uiState.resourceRequiredBreakdown,
                    resourceRequiredTotal = uiState.resourceRequiredTotal,
                    onClick = {
                        keyboardController?.hide()
                        focusManager.clearFocus()
                        screenModel.onConfirmResourceProviderFee()
                    },
                    onDismiss = {
                        screenModel.onDismissTransactionFeeBreakdown()
                    }
                )
            }

            CreateNewProposalScreen(
                uiState = uiState,
                onBackPressed = {
                    if (screenModel.shouldPromptSaveDraft() && showDraftDialog.not()) {
                        showDraftDialog = true
                    } else {
                        navigator.popUntilRoot()
                    }
                },
                onProposalNameChange = {
                    screenModel.onProposalNameChange(it)
                },
                onSelectExpirationDate = {
                    screenModel.onSelectExpirationDate(it)
                },
                onClickAccountAndPermission = {
                    navigator.push(SelectAccountPermissionScreen())
                },
                onClickAddNewAction = {
                    navigator.push(MultisigProposalActionScreen())
                },
                onClickUpdateAction = {
                    navigator.push(MultisigProposalActionScreen(it))
                },
                onClickApprover = {
                    navigator.push(MultisigProposalApproverScreen())
                },
                onClickSubmit = {
                    keyboardController?.hide()
                    focusManager.clearFocus()
                    screenModel.onRequestTransaction()
                },
                onBackToHome = {
                    screenModel.onConsumeTxHash()
                    navigator.popUntilRoot()
                },
                onClickDelete = {
                    showDeleteConfirmation = true
                }
            )
        }
    }

    @Composable
    fun CreateNewProposalScreen(
        uiState: CreateNewProposalUiModel,
        onBackPressed: () -> Unit,
        onProposalNameChange: (String) -> Unit,
        onSelectExpirationDate: (Long) -> Unit,
        onClickAccountAndPermission: () -> Unit,
        onClickAddNewAction: () -> Unit,
        onClickUpdateAction: (index: Int) -> Unit,
        onClickApprover: () -> Unit,
        onClickSubmit: () -> Unit,
        onBackToHome: () -> Unit,
        onClickDelete: () -> Unit
    ) {
        val keyboardController = LocalSoftwareKeyboardController.current
        val focusManager = LocalFocusManager.current
        val uriHandler = LocalUriHandler.current

        MaxSizeColumn(
            Modifier
                .background(MaterialTheme.mangalaColors.bg)
                .pointerInput(Unit) {
                    detectTapGestures {
                        keyboardController?.hide()
                        focusManager.clearFocus()
                    }
                },
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            if (uiState.txHash.isNotNullOrBlank()) {
                ExecuteTransactionSuccess(
                    onClickBack = onBackPressed,
                    textTitle = MR.strings.message_transfer_ram_success.desc().localized(),
                    bottomButton = {
                        MangalaGradientButton(
                            label = "Back to Multisig",
                            onClick = onBackToHome,
                            enabled = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        VerticalSpacer(Spacing.SMALL)
                        MangalaTextButton(
                            modifier = Modifier.fillMaxWidth(),
                            label = MR.strings.all_view_on_block_explorer.desc().localized(),
                            onClick = {
                                val blockExplorerUrl =
                                    (uiState as? CreateNewProposalUiModel)?.blockExplorerUrl.orEmpty()
                                if (blockExplorerUrl.isNotBlank()) {
                                    uriHandler.openUri(blockExplorerUrl)
                                }
                            },
                            style = MangalaTypography.Size14Medium(),
                            size = MangalaButtonSize.Big,
                        )
                    }
                )
            } else {
                CreateNewProposalScreenDataState(
                    uiState = uiState,
                    onBackPressed = onBackPressed,
                    onProposalNameChange = onProposalNameChange,
                    onSelectExpirationDate = onSelectExpirationDate,
                    onClickAccountAndPermission = onClickAccountAndPermission,
                    onClickAddNewAction = onClickAddNewAction,
                    onClickUpdateAction = onClickUpdateAction,
                    onClickApprover = onClickApprover,
                    onClickSubmit = onClickSubmit,
                    onClickDelete = onClickDelete
                )
            }
        }
    }

    @OptIn(
        ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class,
    )
    @Composable
    fun ColumnScope.CreateNewProposalScreenDataState(
        uiState: CreateNewProposalUiModel,
        onBackPressed: () -> Unit,
        onProposalNameChange: (String) -> Unit,
        onSelectExpirationDate: (Long) -> Unit,
        onClickAccountAndPermission: () -> Unit,
        onClickAddNewAction: () -> Unit,
        onClickUpdateAction: (index: Int) -> Unit,
        onClickApprover: () -> Unit,
        onClickSubmit: () -> Unit,
        onClickDelete: () -> Unit
    ) {
        var isDatePickerVisible by remember { mutableStateOf(false) }
        val sheetState = rememberModalBottomSheetState(
            skipPartiallyExpanded = true
        )

        if (isDatePickerVisible) {
            ModalBottomSheet(
                sheetState = sheetState,
                onDismissRequest = { isDatePickerVisible = false },
                containerColor = MaterialTheme.mangalaColors.bg,
                dragHandle = {
                    DragHandle(
                        color = MaterialTheme.mangalaColors.border,
                        width = 32.dp,
                        height = 4.dp,
                        shape = RoundedCornerShape(CornerRadius.Medium)
                    )
                },
            ) {
                MaxWidthColumn(
                    Modifier.padding(Dimensions.Padding.default),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    var isSelectingDate by remember { mutableStateOf(false) }
                    var selectedLocalDate by remember(uiState.expirationTimestamp) {
                        mutableStateOf(
                            Instant.fromEpochMilliseconds(uiState.expirationTimestamp)
                                .toLocalDateTime(
                                    TimeZone.currentSystemDefault()
                                ).date
                        )
                    }
                    var selectedLocalTime by remember(uiState.expirationTimestamp) {
                        mutableStateOf(
                            Instant.fromEpochMilliseconds(uiState.expirationTimestamp)
                                .toLocalDateTime(
                                    TimeZone.currentSystemDefault()
                                ).time
                        )
                    }
                    val selectedLocalDateFormatted by remember(selectedLocalDate) {
                        mutableStateOf(selectedLocalDate.formatDate(style = FormatStyle.MEDIUM))
                    }
                    val selectedLocalTimeFormatted by remember(selectedLocalTime) {
                        mutableStateOf(selectedLocalTime.formatTime())
                    }

                    Text("Choose expired date", style = MangalaTypography.Size14Medium())
                    VerticalSpacer(Spacing.BASE)
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(Spacing.XSMALL),
                        verticalArrangement = Arrangement.spacedBy(Spacing.TINY)
                    ) {
                        DateTimeCategoryChip(
                            text = selectedLocalDateFormatted,
                            isSelected = isSelectingDate,
                            onClick = {
                                isSelectingDate = true
                            }
                        )
                        DateTimeCategoryChip(
                            text = selectedLocalTimeFormatted,
                            isSelected = !isSelectingDate,
                            onClick = {
                                isSelectingDate = false
                            }
                        )
                    }
                    VerticalSpacer(Spacing.XXMEDIUM)
                    if (isSelectingDate) {
                        WheelDatePicker(
                            startDate = selectedLocalDate,
                            minDate = uiState.minSelectableLocalDate,
                            dateFormatter = remember(Locale.current) {
                                dateFormatter(
                                    DateOrder.match(
                                        Locale.current
                                    ),
                                    monthDisplayStyle = MonthDisplayStyle.FULL,
                                    formatMonth = { month, monthDisplayStyle ->
                                        month.number.getShortMonthName()
                                    }
                                )
                            },
                            selectorProperties = WheelPickerDefaults.selectorProperties(
                                color = MaterialTheme.mangalaColors.buttonNeutralContainer.copy(
                                    alpha = 0.3f
                                ),
                                shape = RoundedCornerShape(CornerRadius.Tiny),
                                border = null
                            ),
                            rowCount = 3,
                        ) {
                            selectedLocalDate = it
                        }
                    } else {
                        WheelTimePicker(
                            startTime = selectedLocalTime,
                            minTime = uiState.minSelectableLocalTime,
                            selectorProperties = WheelPickerDefaults.selectorProperties(
                                color = MaterialTheme.mangalaColors.buttonNeutralContainer.copy(
                                    alpha = 0.3f
                                ),
                                shape = RoundedCornerShape(CornerRadius.Tiny),
                                border = null
                            ),
                            rowCount = 3
                        ) { snappedTime ->
                            selectedLocalTime = snappedTime
                        }
                    }
                    VerticalSpacer(Spacing.XBASE)
                    MangalaGradientButton(
                        label = MR.strings.all_confirm.desc().localized(),
                        onClick = {
                            onSelectExpirationDate(
                                LocalDateTime(
                                    selectedLocalDate,
                                    selectedLocalTime
                                ).toInstant(TimeZone.currentSystemDefault()).toEpochMilliseconds()
                            )
                            isDatePickerVisible = false
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        ProposalTopAppBar(
            onBackPressed = onBackPressed,
            label = if (uiState.isLoadedFromDraft) {
                MR.strings.title_create_new_proposal_update_draft.desc().localized()
            } else {
                MR.strings.title_create_new_proposal.desc().localized()
            },
            trailingIcon = {
                if (uiState.isLoadedFromDraft) {
                    IconButton(
                        onClick = onClickDelete
                    ) {
                        Icon(
                            imageVector = MangalaWalletPack.Delete,
                            contentDescription = MR.strings.all_delete.desc().localized(),
                            modifier = Modifier.size(24.dp),
                            tint = ColorsNew.error_600
                        )
                    }
                }
            }
        )
        MaxWidthColumn(
            Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = Dimensions.Padding.default)
        ) {
            VerticalSpacer(Spacing.BASE)
            ProposalTextInputField(
                label = MR.strings.label_create_new_proposal_proposal_name.desc().localized(),
                query = uiState.proposalName,
                onQueryChange = onProposalNameChange,
                placeholder = MR.strings.hint_create_new_proposal_proposal_name.desc().localized(),
                isError = uiState.proposalNameErrorMessage != null,
                errorText = uiState.proposalNameErrorMessage?.resolve()
            )
            VerticalSpacer(Spacing.SMALL)
            ProposalTextInputField(
                label = MR.strings.label_create_new_proposal_expiration_date.desc().localized(),
                query = uiState.expirationDateTimeFormatted,
                onQueryChange = {},
                placeholder = "",
                modifier = Modifier.clickable { isDatePickerVisible = true },
                enabled = false,
                trailingIcon = {
                    Icon(
                        imageVector = MangalaWalletPack.Calendar,
                        contentDescription = "Arrow Right",
                        tint = MaterialTheme.mangalaColors.iconPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            )
            VerticalSpacer(Spacing.SMALL)
            ProposalTextInputField(
                label = MR.strings.label_create_new_proposal_account_and_permission.desc()
                    .localized(),
                query = uiState.formattedSelectedAccountAndPermission.orEmpty(),
                onQueryChange = {},
                placeholder = MR.strings.hint_create_new_proposal_account_and_permission.desc()
                    .localized(),
                modifier = Modifier.clickable { onClickAccountAndPermission() },
                enabled = false,
                trailingIcon = {
                    Icon(
                        imageVector = if (uiState.formattedSelectedAccountAndPermission.isNotNullOrBlank()) MangalaWalletPack.EditNew else MangalaWalletPack.ArrowRightWithStem,
                        contentDescription = null,
                        tint = MaterialTheme.mangalaColors.iconPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            )
            VerticalSpacer(Spacing.SMALL)
            ProposalInputLabel(
                MR.strings.label_create_new_proposal_action.desc().localized(),
                requiredInput = true
            )
            VerticalSpacer(Spacing.XTINY)
            if (uiState.actions.isEmpty()) {
                MangalaWalletSearchBarWithBorder(
                    query = "",
                    placeholder = MR.strings.hint_create_new_proposal_action.desc().localized(),
                    onQueryChange = {},
                    leadingIcon = null,
                    enabled = false,
                    modifier = Modifier.clickable { onClickAddNewAction() },
                    trailingIcon = {
                        CompositionLocalProvider(
                            LocalMinimumInteractiveComponentEnforcement provides false,
                            LocalMinimumInteractiveComponentSize provides 0.dp
                        ) {
                            Icon(
                                imageVector = MangalaWalletPack.ArrowRightWithStem,
                                contentDescription = null,
                                tint = MaterialTheme.mangalaColors.iconPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    },
                    colors = MangalaWalletSearchBarDefaults.searchBarColors(
                        textColor = MaterialTheme.mangalaColors.textPrimary,
                        focusedBorderColor = MaterialTheme.mangalaColors.border,
                        unfocusedBorderColor = MaterialTheme.mangalaColors.border,
                        backgroundColor = MaterialTheme.mangalaColors.bgInnerCard,
                        placeholderColor = MaterialTheme.mangalaColors.textSecondary,
                    ),
                )
            } else {
                uiState.actions.forEachIndexed { index, multisigAction ->
                    ActionItem(
                        value = multisigAction.formattedValue,
                        onClick = { onClickUpdateAction(index) }
                    )
                    if (index < uiState.actions.size - 1) {
                        VerticalSpacer(Spacing.XTINY)
                    }
                }
                VerticalSpacer(Spacing.XSMALL)
                ProposalTextButton(
                    onClick = onClickAddNewAction,
                    text = "Add new action",
                )
            }
            VerticalSpacer(Spacing.SMALL)
            ProposalTextInputField(
                label = MR.strings.label_create_new_proposal_approver.desc().localized(),
                query = uiState.approvers
                    .flatMap { (key, value) ->
                        listOf(key.formatted) + value.map { it.formatted }
                    }.joinToString(separator = ", "),
                onQueryChange = {},
                placeholder = MR.strings.hint_create_new_proposal_approver.desc().localized(),
                modifier = Modifier.clickable { onClickApprover() },
                enabled = false,
                trailingIcon = {
                    Icon(
                        imageVector = if (uiState.approvers.isNotEmpty()) MangalaWalletPack.EditNew else MangalaWalletPack.ArrowRightWithStem,
                        contentDescription = null,
                        tint = MaterialTheme.mangalaColors.iconPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            )
        }
        MaxWidthColumn(
            Modifier
                .background(MaterialTheme.mangalaColors.bgInnerCard)
                .navigationBarsPadding()
                .imePadding()
                .padding(horizontal = Dimensions.Padding.default)
        ) {
            VerticalSpacer(Spacing.SMALL)
            MangalaGradientButton(
                label = uiState.buttonText,
                onClick = {
                    onClickSubmit()
                },
                enabled = uiState.isButtonEnabled,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }

    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    fun ActionItem(
        value: String,
        onClick: () -> Unit
    ) {
        MangalaWalletSearchBarWithBorder(
            query = value,
            placeholder = "",
            onQueryChange = {},
            leadingIcon = null,
            enabled = false,
            modifier = Modifier.clickable(onClick = onClick),
            trailingIcon = {
                CompositionLocalProvider(
                    LocalMinimumInteractiveComponentEnforcement provides false,
                    LocalMinimumInteractiveComponentSize provides 0.dp
                ) {
                    Icon(
                        imageVector = MangalaWalletPack.EditNew,
                        contentDescription = null,
                        tint = MaterialTheme.mangalaColors.iconPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            },
            colors = MangalaWalletSearchBarDefaults.searchBarColors(
                textColor = MaterialTheme.mangalaColors.textPrimary,
                focusedBorderColor = MaterialTheme.mangalaColors.border,
                unfocusedBorderColor = MaterialTheme.mangalaColors.border,
                backgroundColor = MaterialTheme.mangalaColors.bgInnerCard,
                placeholderColor = MaterialTheme.mangalaColors.textSecondary,
            ),
        )
    }

    @Composable
    fun DateTimeCategoryChip(
        text: String,
        isSelected: Boolean,
        onClick: () -> Unit,
        modifier: Modifier = Modifier
    ) {
        val backgroundColor =
            if (isSelected) MaterialTheme.mangalaColors.bgBadge else MaterialTheme.mangalaColors.bgAlpha
        val textColor =
            if (isSelected) MaterialTheme.mangalaColors.textOnBadge else MaterialTheme.mangalaColors.textSecondary

        Box(
            Modifier.background(backgroundColor, shape = RoundedCornerShape(CornerRadius.Medium))
                .clickable(onClick = onClick).padding(
                    horizontal = Dimensions.Padding.small,
                    vertical = Dimensions.Padding.quarter
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                color = textColor,
                style = MangalaTypography.Size13Medium()
            )
        }
    }
}