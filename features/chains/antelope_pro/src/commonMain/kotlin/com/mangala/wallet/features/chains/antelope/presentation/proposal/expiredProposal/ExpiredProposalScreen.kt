package com.mangala.wallet.features.chains.antelope.presentation.proposal.expiredProposal

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.mangala.wallet.common.mokoresources.ColorsNew
import com.mangala.wallet.common.mokoresources.Dimensions
import com.mangala.wallet.common.mokoresources.Spacing
import com.mangala.wallet.features.chains.antelope.presentation.proposal.ProposalCard
import com.mangala.wallet.features.chains.antelope.presentation.proposal.ProposalUiModel
import com.mangala.wallet.features.chains.antelope.presentation.proposal.expiredProposal.detail.ExpiredProposalDetailScreen
import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.ui.component.MangalaWalletSearchBarWithBorder
import com.mangala.wallet.ui.component.MaxSizeColumn
import com.mangala.wallet.ui.component.MaxWidthRow
import com.mangala.wallet.ui.utils.screenmodel.BaseScreen
import com.mangala.wallet.utils.analytics.MangalaAnalytics
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.desc.desc
import kotlinx.datetime.Instant

internal class ExpiredProposalScreen : BaseScreen<ExpiredProposalScreenModal>() {

    override val screenName: String = MangalaAnalytics.Screens.ANTELOPE_MULTISIG_EXPIRED_PROPOSAL
    override val screenClassName: String = ExpiredProposalScreen::class.simpleName.orEmpty()

    @Composable
    override fun createScreenModel(): ExpiredProposalScreenModal = getScreenModel()

    @Composable
    override fun ScreenContent(screenModel: ExpiredProposalScreenModal) {
        MyProposalScreenContent()
    }

    @Composable
    private fun MyProposalScreenContent() {
        val navigator = LocalNavigator.currentOrThrow.parent?.parent
        val temp = remember {
            listOf(
                ProposalUiModel(
                    proposalName = "Proposal 1",
                    state = ProposalUiModel.State.Pending,
                    action = "Buy ram",
                    submitter = "taman.man",
                    expiredDate = Instant.parse("2024-12-24T01:00:00Z"),
                ),
                ProposalUiModel(
                    proposalName = "Proposal 2",
                    state = ProposalUiModel.State.Pending,
                    action = "Buy ram",
                    submitter = "taman.man",
                    expiredDate = Instant.parse("2024-12-24T00:00:00Z"),
                ),
                ProposalUiModel(
                    proposalName = "Proposal 3",
                    state = ProposalUiModel.State.Executable,
                    action = "Buy ram",
                    submitter = "taman.man",
                    expiredDate = Instant.parse("2024-12-23T00:00:00Z"),
                ),
            )
        }
        MaxSizeColumn(
            modifier = Modifier
                .background(color = ColorsNew.background)
                .padding(Dimensions.Padding.default)
        ) {
            MaxWidthRow(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                MangalaWalletSearchBarWithBorder(
                    query = "",
                    onQueryChange = {},
                    placeholder = MR.strings.place_holder_proposal_search_bar.desc().localized()
                )

//            Spacer(modifier = Modifier.width(Spacing.TINY))
//
//            MangalaWalletIconButton(
//                icon = MangalaWalletPack.Filter,
//                tint = ColorsNew.primary_500,
//                onClick = {}
//            )
            }

            Spacer(modifier = Modifier.height(Spacing.BASE))

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(Spacing.TINY)
            ) {
                items(
                    items = temp,
                    key = { it.proposalName }
                ) {
                    ProposalCard(
                        proposal = it,
                        shouldShowSubmitterInfo = true,
                        onClick = {
                            navigator?.push(
                                ExpiredProposalDetailScreen(
                                    it.proposalName,
                                    it.submitter
                                )
                            )
                        }
                    )
                }
            }
        }
    }
}
