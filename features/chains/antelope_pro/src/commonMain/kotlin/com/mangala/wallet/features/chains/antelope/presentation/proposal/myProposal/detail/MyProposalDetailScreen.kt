package com.mangala.wallet.features.chains.antelope.presentation.proposal.myProposal.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.nestedscroll.nestedScroll
import cafe.adriel.voyager.core.registry.ScreenRegistry
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.bottomSheet.LocalBottomSheetNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.mangala.wallet.common.mokoresources.ColorsNew
import com.mangala.wallet.common.mokoresources.CornerRadius
import com.mangala.wallet.common.mokoresources.Dimensions
import com.mangala.wallet.common.mokoresources.Spacing
import com.mangala.wallet.features.chains.antelope.presentation.multisig.proposal.detail.ProposalDetail
import com.mangala.wallet.features.chains.antelope.presentation.multisig.proposal.detail.State
import com.mangala.wallet.features.chains.antelope.presentation.proposal.Constants.BOTTOM_SHEET_SCRIM_ALPHA
import com.mangala.wallet.features.chains.antelope.presentation.proposal.ErrorPopup
import com.mangala.wallet.features.chains.antelope.presentation.proposal.ProposalDetailHeader
import com.mangala.wallet.features.chains.antelope_base.domain.APPROVE_PROPOSAL_ACTION
import com.mangala.wallet.features.chains.antelope_base.domain.CANCEL_PROPOSAL_ACTION
import com.mangala.wallet.features.chains.antelope_base.domain.EXECUTE_PROPOSAL_ACTION
import com.mangala.wallet.features.chains.antelope_base.domain.UN_APPROVE_PROPOSAL_ACTION
import com.mangala.wallet.features.chains.antelope_base.presentation.ui.AntelopeResourceProviderFeeDialog
import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.ui.LocalBottomNavigationVisibility
import com.mangala.wallet.ui.SharedScreen
import com.mangala.wallet.ui.component.MangalaBottomSheetNavigator
import com.mangala.wallet.ui.component.MangalaButton
import com.mangala.wallet.ui.component.MangalaGradientButton
import com.mangala.wallet.ui.component.MangalaOutlinedButtonNew
import com.mangala.wallet.ui.component.MaxSizeBox
import com.mangala.wallet.ui.placeholder.mangalaWalletPlaceholder
import com.mangala.wallet.ui.theme.MangalaTypography
import com.mangala.wallet.ui.theme.mangalaColors
import com.mangala.wallet.ui.utils.collectAsStateMultiplatform
import com.mangala.wallet.ui.utils.screenmodel.BaseScreen
import com.mangala.wallet.utils.analytics.MangalaAnalytics
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.desc.desc
import dev.icerock.moko.resources.format
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant
import org.koin.core.parameter.parameterArrayOf

class MyProposalDetailScreen(
    private val proposalName: String,
    private val submitter: String,
    private val chainId: String? = null
) : BaseScreen<MyProposalDetailScreenModel>() {

    override val key: ScreenKey
        get() = "ProposalDetailScreen"

    override val screenName: String = MangalaAnalytics.Screens.ANTELOPE_MULTISIG_PROPOSAL_DETAILS
    override val screenClassName: String = MyProposalDetailScreen::class.simpleName.orEmpty()

    @Composable
    override fun createScreenModel(): MyProposalDetailScreenModel =
        getScreenModel<MyProposalDetailScreenModel> {
            parameterArrayOf(submitter, proposalName, chainId)
        }

    override val isBottomBarVisible: Boolean
        get() = false

    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    override fun ScreenContent(screenModel: MyProposalDetailScreenModel) {
        val navigator = LocalNavigator.currentOrThrow
        val uiState = screenModel.uiState.collectAsStateMultiplatform().value
        MangalaBottomSheetNavigator(
            sheetShape = RectangleShape
        ) {
            val bottomSheetNavigator = LocalBottomSheetNavigator.current
            LaunchedEffect((uiState as? MyProposalDetailScreenUiState.Loaded)?.promptConfirmTransaction) {
                if ((uiState as? MyProposalDetailScreenUiState.Loaded)?.promptConfirmTransaction == true) {
                    val unlockPinScreen = ScreenRegistry.get(
                        SharedScreen.UnlockPinScreen(
                            SharedScreen.UnlockPinScreen.VERIFY_SEND_TRANSACTION,
                            onUnlockSuccess = {
                                screenModel.onAuthenticationSuccess(uiState.data.accountExecuted)
                                bottomSheetNavigator.hide()
                            },
                            antelopeAccountName = null
                        )
                    )
                    bottomSheetNavigator.show(unlockPinScreen)
                    screenModel.onPinPromptShown()
                }
            }

            if ((uiState as? MyProposalDetailScreenUiState.Loaded)?.resourceRequiredBreakdown != null) {
                val uiModel = uiState as? MyProposalDetailScreenUiState.Loaded

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
            MyProposalDetailScreen(
                navigator,
                proposalName,
                uiState,
                onBackClicked = { navigator.pop() },
                screenModel = screenModel
            )
        }
    }

    @Composable
    fun MyProposalDetailScreen(
        navigator: Navigator,
        proposalName: String,
        uiState: MyProposalDetailScreenUiState,
        onBackClicked: () -> Unit,
        screenModel: MyProposalDetailScreenModel
    ) {

        MaxSizeBox(
            modifier = Modifier
                .background(MaterialTheme.mangalaColors.bg)
                .safeDrawingPadding()
        ) {
            when (uiState) {
                is MyProposalDetailScreenUiState.Success -> {
                    ActionExecuteSuccessState(
                        uiState.txHash,
                        onContinueTransaction = {
                            navigator.pop()
                        },
                        onBackHome = {
                            navigator.popUntilRoot()
                        }
                    )
                }

                is MyProposalDetailScreenUiState.ExecutedError -> {
                    ErrorPopup(
                        onDismiss = { navigator.pop() },
                        message = MR.strings.message_proposal_details_transaction_executed.desc()
                            .localized()
                    )
                }

                is MyProposalDetailScreenUiState.Error -> {
                    ErrorPopup(
                        onDismiss = { navigator.pop() },
                        message = MR.strings.message_proposal_details_error_message.desc()
                            .localized()
                    )
                }

                else -> {
                    val uiModel = uiState as? MyProposalDetailScreenUiState.Loaded
                    uiModel?.let {
                        MyProposalScreen(
                            proposalName,
                            it.data,
                            onBackClicked,
                            screenModel
                        )
                    }
                }
            }
        }

    }


    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun MyProposalScreen(
        proposalName: String,
        proposalDetail: ProposalDetail,
        onBackClicked: () -> Unit,
        screenModel: MyProposalDetailScreenModel
    ) {
        val scope = rememberCoroutineScope()
        var showBottomSheet by remember { mutableStateOf(false) }
        val modalBottomSheetState = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)
        var showDialog by remember { mutableStateOf(false) }
        var actionProposal by remember { mutableStateOf("") }

        val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
        val headerBackgroundColor =
            if (proposalDetail.isExecutable) ColorsNew.success_100 else ColorsNew.warning_100


        ModalBottomSheetLayout(
            sheetState = modalBottomSheetState,
            sheetShape = RoundedCornerShape(
                topStart = CornerRadius.Medium,
                topEnd = CornerRadius.Medium
            ),
            sheetBackgroundColor = MaterialTheme.mangalaColors.bg,
            sheetContent = {
                if (showBottomSheet) {
                    ProposalDetailBottomSheet(
                        proposalDetail.approvals
                    )
                }
            },
            scrimColor = Color.Black.copy(alpha = BOTTOM_SHEET_SCRIM_ALPHA)
        ) {
            val bottomNavigationState = remember { mutableStateOf(true) }
            CompositionLocalProvider(LocalBottomNavigationVisibility provides bottomNavigationState) {
                Scaffold(
                    modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
                    topBar = {
                        ProposalDetailHeader(
                            proposalName,
                            proposalDetail,
                            onBackClicked,
                            MR.strings.title_proposal_detail_proposal.desc().localized(),
                            headerBackgroundColor,
                            scrollBehavior,
                        )
                    },
                    content = { innerPadding ->
                        MyProposalDetailScreenContent(
                            proposalDetail,
                            {
                                scope.launch {
                                    modalBottomSheetState.show()
                                    showBottomSheet = true
                                }
                            },
                            innerPadding
                        )
                    },
                    bottomBar = {
                        ProposalButtonBar(
                            onApproveClick = {
                                showDialog = true
                                actionProposal = APPROVE_PROPOSAL_ACTION
                            },
                            onUnApproveClick = {
                                showDialog = true
                                actionProposal = UN_APPROVE_PROPOSAL_ACTION
                            },
                            onExecuteClick = {
                                showDialog = true
                                actionProposal = EXECUTE_PROPOSAL_ACTION
                            },
                            onCancelClick = {
                                showDialog = true
                                actionProposal = CANCEL_PROPOSAL_ACTION
                            },
                            state = proposalDetail.state,
                            isApproved = proposalDetail.isApproved,
                            isRequestedApproval = proposalDetail.isRequestedApproval,
                            expirationDate = proposalDetail.expirationDate
                        )
                    }
                )
            }

            LaunchedEffect(modalBottomSheetState.isVisible) {
                if (!modalBottomSheetState.isVisible) {
                    showBottomSheet = false
                }
            }


            val (dialogMessage, titleDialog) = getDialogMessages(actionProposal)
            ConfirmationDialog(
                showDialog = showDialog,
                title = titleDialog,
                dialogMessage = dialogMessage,
                permissions = proposalDetail.permissionsImported,
                permissionSelected = proposalDetail.permissionExecuted,
                onConfirm = {
                    screenModel.onConfirmActionProposal(actionProposal)
                    showDialog = false
                },
                onDismiss = {
                    showDialog = false
                },
                onPermissionExecute = {
                    screenModel.updatePermissionExecuted(it, proposalDetail)
                }
            )
        }

    }

    @Composable
    private fun MyProposalDetailScreenContent(
        proposalDetail: ProposalDetail,
        onSeeDetailApprovalsClicked: () -> Unit,
        paddingValues: PaddingValues
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.mangalaColors.bg)
        ) {
            item {
                Column(modifier = Modifier.padding(vertical = Dimensions.Padding.default)) {
                    ApprovalOverview(proposalDetail, onSeeDetailApprovalsClicked)
                    ProposalDataDetailScreen(proposalDetail.actionProposalDetails)
                }
            }
        }
    }

    @Composable
    fun ApprovalOverview(
        proposalDetail: ProposalDetail,
        onSeeDetailApprovalsClicked: () -> Unit
    ) {
        Column(
            modifier = Modifier
                .padding(
                    start = Dimensions.Padding.default,
                    top = Dimensions.Padding.small,
                    end = Dimensions.Padding.default,
                    bottom = Dimensions.Padding.small
                )
                .background(
                    color = MaterialTheme.mangalaColors.bgInnerCard,
                    shape = RoundedCornerShape(size = Spacing.XSMALL)
                ),
            verticalArrangement = Arrangement.spacedBy(Spacing.SMALL, Alignment.CenterVertically),
            horizontalAlignment = Alignment.Start,
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(Dimensions.Padding.default)
            ) {
                if (proposalDetail.isExecutable) {
                    Text(
                        MR.strings.message_proposal_details_execute_approval.desc().localized(),
                        color = MaterialTheme.mangalaColors.textSecondary,
                        style = MangalaTypography.Size13Italic(),
                        modifier = Modifier.weight(1f).mangalaWalletPlaceholder(
                            proposalDetail.expirationDate == Instant.DISTANT_PAST
                        )
                    )
                } else {
                    Text(
                        MR.strings.message_proposal_details_waiting_approval_1.desc().localized(),
                        color = MaterialTheme.mangalaColors.textSecondary,
                        style = MangalaTypography.Size13Italic(),
                        modifier = Modifier.weight(1f).mangalaWalletPlaceholder(
                            proposalDetail.expirationDate == Instant.DISTANT_PAST
                        )
                    )
                    Text(
                        MR.strings.message_proposal_details_waiting_approval_2.desc().localized(),
                        color = MaterialTheme.mangalaColors.textSecondary,
                        style = MangalaTypography.Size13Italic(),
                        modifier = Modifier.weight(1f).mangalaWalletPlaceholder(
                            proposalDetail.expirationDate == Instant.DISTANT_PAST
                        )
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = MR.strings.message_proposal_details_list_approver.format(
                            proposalDetail.approvedCount,
                            proposalDetail.totalApprovals
                        ).localized(),
                        color = MaterialTheme.mangalaColors.textPrimary,
                        modifier = Modifier.weight(1f).mangalaWalletPlaceholder(
                            proposalDetail.expirationDate == Instant.DISTANT_PAST
                        )
                    )

                    TextButton(
                        onClick = { onSeeDetailApprovalsClicked() },
                        colors = ButtonDefaults.textButtonColors(
                            backgroundColor = MaterialTheme.mangalaColors.bgInnerCard,
                            contentColor = MaterialTheme.mangalaColors.textPrimary
                        )
                    ) {
                        Text(
                            text = MR.strings.label_proposal_details_see_detail.desc().localized(),
                            color = MaterialTheme.mangalaColors.textPrimary,
                            style = MangalaTypography.Size14Regular(),
                            modifier = Modifier.mangalaWalletPlaceholder(
                                proposalDetail.expirationDate == Instant.DISTANT_PAST
                            )
                        )
                    }
                }
            }

        }
    }

    @Composable
    private fun ProposalButtonBar(
        onExecuteClick: () -> Unit,
        onApproveClick: () -> Unit,
        onUnApproveClick: () -> Unit,
        onCancelClick: () -> Unit,
        state: State,
        isApproved: Boolean,
        isRequestedApproval: Boolean,
        expirationDate: Instant,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = MaterialTheme.mangalaColors.bgInnerCard)
                .padding(
                    horizontal = Dimensions.Padding.default,
                    vertical = Dimensions.Padding.default
                )
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().mangalaWalletPlaceholder(
                    expirationDate == Instant.DISTANT_PAST
                ),
                horizontalArrangement = Arrangement.spacedBy(Spacing.SMALL),
                verticalAlignment = Alignment.CenterVertically
            ) {
                MangalaOutlinedButtonNew(
                    label = MR.strings.all_cancel.desc().localized(),
                    onClick = onCancelClick,
                    modifier = Modifier
                        .weight(1f),
                )

                when {
                    isApproved -> MangalaButton(
                        modifier = Modifier.weight(1f),
                        label = MR.strings.button_proposal_details_unapprove.desc().localized(),
                        onClick = onUnApproveClick,
                        backgroundColor = MaterialTheme.mangalaColors.buttonDestructiveContainer,
                        contentColor = MaterialTheme.mangalaColors.buttonDestructiveContent,
                    )

                    isRequestedApproval -> MangalaGradientButton(
                        label = MR.strings.button_proposal_details_approve.desc().localized(),
                        onClick = onApproveClick,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            if (isRequestedApproval || isApproved) {
                Spacer(modifier = Modifier.height(Spacing.SMALL))
                MangalaGradientButton(
                    label = MR.strings.button_proposal_details_execute.desc().localized(),
                    onClick = { onExecuteClick() },
                    enabled = state == State.Executable,
                    modifier = Modifier.fillMaxWidth()
                )
            }

        }
    }

    @Composable
    private fun getDialogMessages(action: String): Pair<String, String> {
        return when (action) {
            EXECUTE_PROPOSAL_ACTION -> MR.strings.message_proposal_details_confirm_execute_proposal.desc()
                .localized() to
                    MR.strings.title_proposal_details_execute_proposal.desc().localized()

            APPROVE_PROPOSAL_ACTION -> MR.strings.message_proposal_details_confirm_approve_proposal.desc()
                .localized() to
                    MR.strings.title_proposal_details_approve_proposal.desc().localized()

            UN_APPROVE_PROPOSAL_ACTION -> MR.strings.message_proposal_details_confirm_unapprove_proposal.desc()
                .localized() to
                    MR.strings.title_proposal_details_unapprove_proposal.desc().localized()

            CANCEL_PROPOSAL_ACTION -> MR.strings.message_proposal_details_confirm_cancel_proposal.desc()
                .localized() to
                    MR.strings.title_proposal_details_cancel_proposal.desc().localized()

            else -> "" to ""
        }
    }
}

