package com.mangala.wallet.features.chains.antelope.presentation.multisig.proposal.create.selectAccPer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.annotation.ExperimentalVoyagerApi
import cafe.adriel.voyager.koin.getNavigatorScreenModel
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.mangala.wallet.common.mokoresources.Dimensions
import com.mangala.wallet.common.mokoresources.Spacing
import com.mangala.wallet.features.chains.antelope.presentation.multisig.proposal.create.CreateNewProposalScreenModel
import com.mangala.wallet.ui.component.MangalaGradientButton
import com.mangala.wallet.features.chains.antelope.presentation.multisig.proposal.ui.ProposalDropdownMenu
import com.mangala.wallet.features.chains.antelope.presentation.multisig.proposal.ui.ProposalTopAppBar
import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.ui.component.MaxSizeBox
import com.mangala.wallet.ui.component.MaxSizeColumn
import com.mangala.wallet.ui.theme.mangalaColors
import com.mangala.wallet.ui.utils.collectAsStateMultiplatform
import com.mangala.wallet.ui.utils.screenmodel.BaseScreen
import com.mangala.wallet.utils.analytics.MangalaAnalytics
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.desc.desc
import org.koin.core.parameter.parametersOf


class SelectAccountPermissionScreen : BaseScreen<CreateNewProposalScreenModel>() {

    override val screenName: String = MangalaAnalytics.Screens.ANTELOPE_MULTISIG_PROPOSAL_PERMISSION
    override val screenClassName: String = SelectAccountPermissionScreen::class.simpleName.orEmpty()

    @OptIn(ExperimentalVoyagerApi::class)
    @Composable
    override fun createScreenModel(): CreateNewProposalScreenModel {
        val navigator = LocalNavigator.currentOrThrow
        return navigator.getNavigatorScreenModel<CreateNewProposalScreenModel>()

    }

    override val isBottomBarVisible: Boolean = false

    @Composable
    override fun ScreenContent(screenModel: CreateNewProposalScreenModel) {

        val navigator = LocalNavigator.currentOrThrow
        val localScreenModel: SelectAccountPermissionScreenModel = getScreenModel(parameters = {
            parametersOf(
                screenModel.uiState.value.proposerName,
                screenModel.uiState.value.proposerPermissionName
            )
        })
        val uiState by localScreenModel.uiState.collectAsStateMultiplatform()

        MaxSizeBox(
            modifier = Modifier
                .background(MaterialTheme.mangalaColors.bg)
                .navigationBarsPadding()
        ) {

            when (uiState) {
                is SelectAccountPermissionUiState.Loading -> {
                    Text("Loading")
                }

                is SelectAccountPermissionUiState.Success -> {
                    val successState = uiState as? SelectAccountPermissionUiState.Success
                    successState?.data?.let { data ->
                        SelectAccountPermission(
                            proposers = data.accountsImported,
                            proposerSelected = data.proposer,
                            permissionExecute = data.permissionExecute,
                            onPermissionExecute = localScreenModel::updatePermission,
                            onProposerChange = localScreenModel::updateProposer,
                            onNavigateBack = {
                                navigator.pop()
                            },
                            onSubmit = {
                                screenModel.onUpdateProposerNameAndPermission(
                                    data.proposer,
                                    data.permissionExecute
                                )
                                navigator.pop()
                            },
                            permissions = data.permissions,
                            uiModel = data
                        )
                    }
                }

                else -> {
                    Text("Loading")
                }
            }
        }
    }

    @Composable
    fun SelectAccountPermission(
        proposerSelected: String,
        proposers: List<String>,
        permissionExecute: String,
        onProposerChange: (String) -> Unit,
        onPermissionExecute: (String) -> Unit,
        onNavigateBack: () -> Unit,
        onSubmit: () -> Unit,
        permissions: List<String>,
        uiModel: SelectAccountPermissionUiModel
    ) {

        MaxSizeColumn {

            ProposalTopAppBar(
                onBackPressed = onNavigateBack,
                label = MR.strings.title_multisig_account_and_permission_screen_top_bar.desc()
                    .localized()
            )

            Column(
                Modifier.background(MaterialTheme.mangalaColors.bg)
                    .padding(horizontal = Dimensions.Padding.default).fillMaxHeight()
            ) {
                Spacer(modifier = Modifier.height(Spacing.BASE))

                ProposalDropdownMenu(
                    label = MR.strings.title_multisig_account_and_permission_screen_account_name.desc()
                        .localized(),
                    value = proposerSelected,
                    items = proposers,
                    onValueChange = onProposerChange,
                    placeholder = MR.strings.title_multisig_account_and_permission_screen_account_name.desc()
                        .localized()
                )

                Spacer(
                    modifier = Modifier.height(Spacing.SMALL)
                )

                ProposalDropdownMenu(
                    label = MR.strings.title_multisig_account_and_permission_screen_permission.desc()
                        .localized(),
                    value = permissionExecute,
                    items = permissions,
                    onValueChange = onPermissionExecute,
                    placeholder = MR.strings.title_multisig_account_and_permission_screen_permission.desc()
                        .localized()
                )

                Spacer(modifier = Modifier.weight(1f))

                MangalaGradientButton(
                    label = MR.strings.all_done.desc().localized(),
                    onClick = onSubmit,
                    enabled = uiModel.isButtonEnabled,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}