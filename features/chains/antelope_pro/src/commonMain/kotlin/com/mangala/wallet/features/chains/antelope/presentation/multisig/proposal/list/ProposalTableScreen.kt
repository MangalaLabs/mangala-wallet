package com.mangala.wallet.features.chains.antelope.presentation.multisig.proposal.list

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.mangala.antelope.base.api.model.GetTableByScopeResponse
import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.ui.component.MaxSizeBox
import com.mangala.wallet.ui.utils.collectAsStateMultiplatform
import com.mangala.wallet.ui.utils.screenmodel.BaseScreen
import com.mangala.wallet.utils.analytics.MangalaAnalytics
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.desc.desc
import dev.icerock.moko.resources.format
import org.koin.core.parameter.parameterArrayOf

class ProposalTableScreen() : BaseScreen<ProposalTableScreenModel>() {

    override val screenName: String = MangalaAnalytics.Screens.ANTELOPE_PROPOSAL_TABLE
    override val screenClassName: String = ProposalTableScreen::class.simpleName.orEmpty()

    @Composable
    override fun createScreenModel(): ProposalTableScreenModel =
        getScreenModel<ProposalTableScreenModel> { parameterArrayOf() }

    @Composable
    override fun ScreenContent(screenModel: ProposalTableScreenModel) {
        val navigator = LocalNavigator.currentOrThrow

        MaxSizeBox(Modifier.background(Color.White).windowInsetsPadding(WindowInsets.safeDrawing)) {
            when (val uiState = screenModel.uiState.collectAsStateMultiplatform().value) {
                is ProposalTableScreenUiState.Loading -> {
                    Box(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                        Text(MR.strings.all_loading.desc().localized())
                    }
                }

                is ProposalTableScreenUiState.Success -> {
                    ProposalsScreen(uiState.data, screenModel, navigator)
                }

                is ProposalTableScreenUiState.Error -> {
                    Box(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                        Text(MR.strings.all_error.format(uiState.message).localized())
                    }
                }

                is ProposalTableScreenUiState.LoadProposalRows -> {

                }
            }
        }
    }


    @Composable
    fun ProposalsScreen(
        getTableByScopeResponse: GetTableByScopeResponse,
        screenModel: ProposalTableScreenModel,
        navigator: Navigator,
    ) {
        val lazyListState = rememberLazyListState()

        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            TextField(
                value = "",
                onValueChange = {},
                label = { Text(MR.strings.label_proposals_by_proposer_screen_search_by_proposer.desc().localized()) },
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
            )

            Row(modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)) {
                Text(MR.strings.all_proposer.desc().localized(), modifier = Modifier.weight(1f))
                Text(MR.strings.message_proposals_by_proposer_screen_of_proposals.desc().localized(), modifier = Modifier.weight(1f))
                Text(MR.strings.all_proposal.desc().localized(), modifier = Modifier.weight(1f))
            }

            LazyColumn(
                state = lazyListState,
                modifier = Modifier.fillMaxSize()
            ) {
                items(getTableByScopeResponse.rows ?: emptyList()) { proposal ->
                    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                        Text(proposal.scope.toString(), modifier = Modifier.weight(1f))
                        Text(proposal.count.toString(), modifier = Modifier.weight(1f))
                        TextButton(
                            onClick = {
                                navigator.push(
                                    ProposalsByProposerScreen(
                                        proposer = proposal.scope.toString(),
                                    )
                                )
                            }
                        ) {
                            Text(color = Color.Gray, text = MR.strings.message_proposals_by_proposer_screen_view.desc().localized())
                        }
                    }
                }
            }

            val lastVisibleItemIndex = lazyListState.layoutInfo.visibleItemsInfo.lastOrNull()?.index
            val totalItems = getTableByScopeResponse.rows?.size ?: 0

            if (lastVisibleItemIndex == totalItems - 1 && totalItems > 0) {
                LaunchedEffect(Unit) {
                    screenModel.getTableRows()
                }
            }
        }
    }
}