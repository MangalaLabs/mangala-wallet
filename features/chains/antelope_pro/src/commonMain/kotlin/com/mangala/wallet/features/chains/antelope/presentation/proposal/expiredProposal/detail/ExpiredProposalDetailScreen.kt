package com.mangala.wallet.features.chains.antelope.presentation.proposal.expiredProposal.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
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
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.mangala.wallet.common.mokoresources.ColorsNew
import com.mangala.wallet.common.mokoresources.CornerRadius
import com.mangala.wallet.common.mokoresources.Dimensions
import com.mangala.wallet.common.mokoresources.Spacing
import com.mangala.wallet.features.chains.antelope.presentation.multisig.proposal.detail.ProposalDetail
import com.mangala.wallet.features.chains.antelope.presentation.proposal.ErrorPopup
import com.mangala.wallet.features.chains.antelope.presentation.proposal.ProposalDetailHeader
import com.mangala.wallet.features.chains.antelope.presentation.proposal.myProposal.detail.ActionExecuteSuccessState
import com.mangala.wallet.features.chains.antelope.presentation.proposal.myProposal.detail.MyProposalDetailScreenUiState
import com.mangala.wallet.features.chains.antelope.presentation.proposal.myProposal.detail.ProposalDataDetailScreen
import com.mangala.wallet.features.chains.antelope.presentation.proposal.myProposal.detail.ProposalDetailBottomSheet
import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.ui.LocalBottomNavigationVisibility
import com.mangala.wallet.ui.component.MangalaBottomSheetNavigator
import com.mangala.wallet.ui.component.MaxSizeBox
import com.mangala.wallet.ui.placeholder.mangalaWalletPlaceholder
import com.mangala.wallet.ui.theme.MangalaTypography
import com.mangala.wallet.ui.utils.collectAsStateMultiplatform
import com.mangala.wallet.ui.utils.screenmodel.BaseScreen
import com.mangala.wallet.utils.analytics.MangalaAnalytics
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.desc.desc
import dev.icerock.moko.resources.format
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant
import org.koin.core.parameter.parameterArrayOf

class ExpiredProposalDetailScreen(
    private val proposalName: String,
    private val submitter: String,
    private val chainId: String? = null
) : BaseScreen<ExpiredProposalDetailScreenModel>() {

    override val screenName: String = MangalaAnalytics.Screens.ANTELOPE_MULTISIG_EXPIRED_PROPOSAL
    override val screenClassName: String = ExpiredProposalDetailScreen::class.simpleName.orEmpty()

    @Composable
    override fun createScreenModel(): ExpiredProposalDetailScreenModel =
        getScreenModel<ExpiredProposalDetailScreenModel> {
            parameterArrayOf(submitter, proposalName)
        }

    override val isBottomBarVisible: Boolean
        get() = false

    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    override fun ScreenContent(screenModel: ExpiredProposalDetailScreenModel) {

        LaunchedEffect(submitter, proposalName) {
            screenModel.loadData(
                proposerAccountName = submitter,
                proposalName = proposalName,
                chainId = chainId
            )
        }

        val navigator = LocalNavigator.currentOrThrow
        val uiState = screenModel.uiState.collectAsStateMultiplatform().value
        MangalaBottomSheetNavigator(
            sheetShape = RectangleShape
        ) {
            ExpiredProposalDetailScreen(
                navigator,
                proposalName,
                uiState,
                onBackClicked = { navigator.pop() },
            )
        }
    }

    @Composable
    fun ExpiredProposalDetailScreen(
        navigator: Navigator,
        proposalName: String,
        uiState: MyProposalDetailScreenUiState,
        onBackClicked: () -> Unit
    ) {
        MaxSizeBox(Modifier.background(Color.White).imePadding().navigationBarsPadding()) {
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
                    uiState as? MyProposalDetailScreenUiState.Loaded
                    val uiModel = uiState as? MyProposalDetailScreenUiState.Loaded
                    uiModel?.let {
                        ExpiredProposalScreen(
                            proposalName,
                            it.data,
                            onBackClicked,
                        )
                    }
                }
            }
        }

    }


    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun ExpiredProposalScreen(
        proposalName: String,
        proposalDetail: ProposalDetail,
        onBackClicked: () -> Unit
    ) {
        val scope = rememberCoroutineScope()
        var showBottomSheet by remember { mutableStateOf(false) }
        val modalBottomSheetState = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)

        val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
        val headerBackgroundColor = ColorsNew.stroke


        ModalBottomSheetLayout(
            sheetState = modalBottomSheetState,
            sheetShape = RoundedCornerShape(
                topStart = CornerRadius.Medium,
                topEnd = CornerRadius.Medium
            ),
            sheetContent = {
                if (showBottomSheet) {
                    ProposalDetailBottomSheet(
                        proposalDetail.approvals
                    )
                }
            },
        ) {
            CompositionLocalProvider(LocalBottomNavigationVisibility provides mutableStateOf(true)) {
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
                        ExpiredProposalDetailScreenContent(
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

                    }
                )
            }

            LaunchedEffect(modalBottomSheetState.isVisible) {
                if (!modalBottomSheetState.isVisible) {
                    showBottomSheet = false
                }
            }
        }

    }

    @Composable
    private fun ExpiredProposalDetailScreenContent(
        proposalDetail: ProposalDetail,
        onSeeDetailApprovalsClicked: () -> Unit,
        paddingValues: PaddingValues
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(ColorsNew.background)
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
    fun ApprovalOverview(proposalDetail: ProposalDetail, onSeeDetailApprovalsClicked: () -> Unit) {
        Column(
            modifier = Modifier
                .padding(
                    start = Dimensions.Padding.default,
                    top = Dimensions.Padding.small,
                    end = Dimensions.Padding.default,
                    bottom = Dimensions.Padding.small
                )
                .background(
                    color = ColorsNew.white,
                    shape = RoundedCornerShape(size = Spacing.XSMALL)
                ),
            verticalArrangement = Arrangement.spacedBy(Spacing.SMALL, Alignment.CenterVertically),
            horizontalAlignment = Alignment.Start,
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(Dimensions.Padding.default)
            ) {

                Text(
                    MR.strings.message_proposal_details_expired_approval.desc().localized(),
                    color = ColorsNew.primary_600,
                    style = MangalaTypography.Size13Italic(),
                    modifier = Modifier.mangalaWalletPlaceholder(
                        proposalDetail.expirationDate == Instant.DISTANT_PAST
                    )
                )

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
                        color = ColorsNew.black,
                        modifier = Modifier.weight(1f).mangalaWalletPlaceholder(
                            proposalDetail.expirationDate == Instant.DISTANT_PAST
                        )
                    )

                    TextButton(
                        onClick = { onSeeDetailApprovalsClicked() },
                        colors = ButtonDefaults.textButtonColors(
                            backgroundColor = ColorsNew.white,
                            contentColor = ColorsNew.black
                        )
                    ) {
                        Text(
                            text = MR.strings.label_proposal_details_see_detail.desc().localized(),
                            color = ColorsNew.black,
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
}