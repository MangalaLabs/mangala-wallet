package com.mangala.wallet.features.chains.antelope.presentation.proposal

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import app.cash.paging.LoadStateLoading
import app.cash.paging.compose.collectAsLazyPagingItems
import app.cash.paging.compose.itemKey
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import com.mangala.wallet.common.mokoresources.Dimensions
import com.mangala.wallet.common.mokoresources.Spacing
import com.mangala.wallet.features.chains.antelope.presentation.multisig.proposal.create.CreateNewProposalRootScreen
import com.mangala.wallet.features.chains.antelope.presentation.proposal.approvals.detail.ApprovalProposalDetailScreen
import com.mangala.wallet.features.chains.antelope.presentation.proposal.myProposal.detail.MyProposalDetailScreen
import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.ui.MangalaPullToRefreshBox
import com.mangala.wallet.ui.TextDescription2
import com.mangala.wallet.ui.TextTitle4
import com.mangala.wallet.ui.component.MaxSizeColumn
import com.mangala.wallet.ui.component.MaxWidthRow
import com.mangala.wallet.ui.theme.mangalaColors
import com.mangala.wallet.ui.utils.collectAsStateMultiplatform
import com.mangala.wallet.ui.utils.screenmodel.BaseScreen
import com.mangala.wallet.utils.analytics.MangalaAnalytics
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.desc.desc

class MsigScreen : BaseScreen<ProposalScreenModal>() {
    override val screenName: String = MangalaAnalytics.Screens.ANTELOPE_MULTISIG_MY_PROPOSAL
    override val screenClassName: String = MsigScreen::class.simpleName.orEmpty()

    @Composable
    override fun createScreenModel(): ProposalScreenModal = getScreenModel()

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun ScreenContent(screenModel: ProposalScreenModal) {
        val localNavigator = LocalNavigator.current
        val currentSelectedTab = remember { mutableStateOf(MsigScreenTab.PROPOSALS) }

        val filterState = screenModel.filterState.collectAsStateMultiplatform()
        val myProposalList = screenModel.myProposalPaginated.collectAsLazyPagingItems()
        val approvalList = screenModel.approvalPaginated.collectAsLazyPagingItems()

        val availableAccountNames = screenModel.availableAccountNames.collectAsStateMultiplatform()
        val selectedAccountName = screenModel.selectedAccountName.collectAsStateMultiplatform()

        val isRefreshing = remember {
            derivedStateOf {
                when (currentSelectedTab.value) {
                    MsigScreenTab.PROPOSALS -> myProposalList.loadState.refresh is LoadStateLoading
                    MsigScreenTab.APPROVALS -> approvalList.loadState.refresh is LoadStateLoading
                    else -> false
                }
            }
        }

        MangalaPullToRefreshBox(
            isRefreshing = isRefreshing.value,
            onRefresh = {
                when (currentSelectedTab.value) {
                    MsigScreenTab.PROPOSALS -> myProposalList.refresh()
                    MsigScreenTab.APPROVALS -> approvalList.refresh()
                    else -> Unit
                }
            }
        ) {
            MaxSizeColumn(
                modifier = Modifier
                    .background(MaterialTheme.mangalaColors.bg)
                    .safeDrawingPadding()
            ) {
                Spacer(modifier = Modifier.height(Spacing.SMALL))

                MaxWidthRow(
                    modifier = Modifier
                        .padding(horizontal = Dimensions.Padding.default),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextTitle4(
                        text = MR.strings.all_proposal.desc().localized(),
                        color = MaterialTheme.mangalaColors.textPrimary,
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    TextDescription2(
                        text = MR.strings.button_proposal_create_proposal.desc().localized(),
                        color = MaterialTheme.mangalaColors.textPrimary,
                        modifier = Modifier.clickable {
                            localNavigator?.push(CreateNewProposalRootScreen())
                        }
                    )
                }

                Spacer(modifier = Modifier.height(Spacing.SMALL))

                MaxWidthRow(
                    modifier = Modifier
                        .padding(horizontal = Dimensions.Padding.default)
                ) {
                    MsigTabItem(
                        modifier = Modifier.weight(1f),
                        title = MR.strings.label_proposal_tab_my_proposal.desc().localized(),
                        isSelected = currentSelectedTab.value == MsigScreenTab.PROPOSALS,
                        onClick = {
                            currentSelectedTab.value = MsigScreenTab.PROPOSALS
                        }
                    )

                    Spacer(modifier = Modifier.width(Spacing.XTINY))

                    MsigTabItem(
                        modifier = Modifier.weight(1f),
                        title = MR.strings.label_proposal_tab_approvals.desc().localized(),
                        isSelected = currentSelectedTab.value == MsigScreenTab.APPROVALS,
                        onClick = {
                            currentSelectedTab.value = MsigScreenTab.APPROVALS
                        }
                    )
                }

                MaxSizeColumn(
                    modifier = Modifier
                        .background(color = MaterialTheme.mangalaColors.bg)
                        .padding(Dimensions.Padding.default)
                ) {
                    Spacer(modifier = Modifier.height(Spacing.BASE))

                    MaxWidthRow(
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        LazyRow(
                            modifier = Modifier.weight(1f),
                            horizontalArrangement = Arrangement.spacedBy(Spacing.XSMALL)
                        ) {
                            items(2) {
                                ProposalFilterStateChip(
                                    title = when (it) {
                                        0 -> MR.strings.label_proposal_open_state.desc().localized()
                                        1 -> MR.strings.label_proposal_done_state.desc().localized()
                                        else -> ""
                                    },
                                    isSelected = it == filterState.value,
                                    onClick = {
                                        screenModel.onSelectFilterState(it)
                                    }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(Spacing.XSMALL))

                        ProposalSelectAccountDropdown(
                            availableAccountNames = availableAccountNames.value,
                            selectedAccountName = selectedAccountName.value,
                            onSelectAccount = screenModel::onSelectAccountName
                        )
                    }

                    Spacer(modifier = Modifier.height(Spacing.SMALL))

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        verticalArrangement = Arrangement.spacedBy(Spacing.TINY)
                    ) {
                        when (currentSelectedTab.value) {
                            MsigScreenTab.PROPOSALS -> {
                                items(
                                    count = myProposalList.itemCount,
                                    key = myProposalList.itemKey { it.proposalName }
                                ) { index ->
                                    myProposalList[index]?.let {
                                        ProposalCard(
                                            proposal = it,
                                            shouldShowSubmitterInfo = false,
                                            onClick = {
                                                val screen =
                                                    if (it.state == ProposalUiModel.State.Draft) {
                                                        CreateNewProposalRootScreen(
                                                            it.proposalName,
                                                            it.submitter
                                                        )
                                                    } else {
                                                        MyProposalDetailScreen(
                                                            proposalName = it.proposalName,
                                                            submitter = it.submitter
                                                        )
                                                    }

                                                localNavigator?.push(screen)
                                            }
                                        )
                                    }
                                }
                            }

                            MsigScreenTab.APPROVALS -> {
                                items(
                                    count = approvalList.itemCount,
                                    key = approvalList.itemKey { it.proposalName }
                                ) { index ->
                                    approvalList[index]?.let {
                                        ProposalCard(
                                            proposal = it,
                                            shouldShowSubmitterInfo = true,
                                            onClick = {
                                                localNavigator?.push(
                                                    ApprovalProposalDetailScreen(
                                                        it.proposalName,
                                                        it.submitter
                                                    )
                                                )
                                            }
                                        )
                                    }
                                }
                            }

                            else -> Unit
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun MsigTabItem(
        modifier: Modifier = Modifier,
        title: String,
        isSelected: Boolean,
        onClick: () -> Unit
    ) {
        Column(
            modifier = modifier.clickable(onClick = onClick),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(Spacing.XTINY))

            TextDescription2(
                text = title,
                color = if (isSelected) MaterialTheme.mangalaColors.textPrimary else MaterialTheme.mangalaColors.textSecondary,
                fontWeight = FontWeight.Medium,
            )

            Spacer(modifier = Modifier.height(Spacing.TINY))

            if (isSelected) HorizontalDivider(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.mangalaColors.iconPrimary
            )
        }
    }
}