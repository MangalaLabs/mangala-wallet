package com.mangala.wallet.features.chains.antelope.presentation.multisig.proposal.list

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.mangala.antelope.base.api.model.GetMultisigProposalTableRowResponse
import com.mangala.wallet.features.chains.antelope.presentation.multisig.proposal.detail.ProposalDetailScreen
import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.ui.MangalaTextButton
import com.mangala.wallet.ui.component.MaxSizeBox
import com.mangala.wallet.ui.utils.collectAsStateMultiplatform
import com.mangala.wallet.ui.utils.screenmodel.BaseScreen
import com.mangala.wallet.utils.analytics.MangalaAnalytics
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.desc.desc
import dev.icerock.moko.resources.format
import org.koin.core.parameter.parameterArrayOf

class ProposalsByProposerScreen(
    private val proposer: String,
) : BaseScreen<ProposalsByProposerScreenModel>() {

    override val screenName: String = MangalaAnalytics.Screens.ANTELOPE_PROPOSALS_BY_PROPOSER
    override val screenClassName: String = ProposalsByProposerScreen::class.simpleName.orEmpty()

    @Composable
    override fun createScreenModel(): ProposalsByProposerScreenModel =
        getScreenModel<ProposalsByProposerScreenModel> { parameterArrayOf(proposer) }

    @Composable
    override fun ScreenContent(screenModel: ProposalsByProposerScreenModel) {
        val navigator = LocalNavigator.currentOrThrow
        MaxSizeBox(Modifier.background(Color.White).windowInsetsPadding(WindowInsets.safeDrawing)) {
            when (val uiState = screenModel.uiState.collectAsStateMultiplatform().value) {
                is ProposalsByProposerScreenUiState.Loading -> {
                    Box(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                        Text(MR.strings.all_loading.desc().localized())
                    }
                }

                is ProposalsByProposerScreenUiState.Success -> {
                    uiState.data.rows?.let { ProposalsByProposerScreen(proposer, it, navigator) }
                }

                is ProposalsByProposerScreenUiState.Error -> {
                    Box(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                        Text(MR.strings.all_error.format(uiState.message).localized())
                    }
                }

            }
        }
    }

    @Composable
    fun ProposalsByProposerScreen(
        proposer: String,
        proposals: List<GetMultisigProposalTableRowResponse.ProposalRow>,
        navigator: Navigator
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                text = MR.strings.message_proposals_by_proposer_screen_proposals.format(proposer).localized(),
                style = MaterialTheme.typography.h6,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            LazyColumn {
                items(proposals.size) { index ->
                    Row {
                        MangalaTextButton(
                            text = proposals[index].proposalName.toString(),
                            color = Color.Blue,
                            onClick = {
                                navigator.push(
                                    ProposalDetailScreen(
                                        proposals[index],
                                        proposer
                                    )
                                )
                            }
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    navigator.pop()
                },
                modifier = Modifier.align(Alignment.End)
            ) {
                Text(MR.strings.all_close.desc().localized())
            }
        }
    }
}