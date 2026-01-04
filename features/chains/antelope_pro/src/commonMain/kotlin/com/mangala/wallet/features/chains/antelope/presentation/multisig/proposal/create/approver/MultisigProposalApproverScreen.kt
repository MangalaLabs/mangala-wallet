package com.mangala.wallet.features.chains.antelope.presentation.multisig.proposal.create.approver

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import cafe.adriel.voyager.core.annotation.ExperimentalVoyagerApi
import cafe.adriel.voyager.koin.getNavigatorScreenModel
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import androidx.compose.material3.MaterialTheme
import com.mangala.wallet.common.mokoresources.ColorsNew
import com.mangala.wallet.common.mokoresources.Dimensions
import com.mangala.wallet.common.mokoresources.Dimensions.ButtonIconSize
import com.mangala.wallet.common.mokoresources.FontType
import com.mangala.wallet.common.mokoresources.Spacing
import com.mangala.wallet.features.chains.antelope.presentation.multisig.proposal.create.CreateNewProposalScreenModel
import com.mangala.wallet.features.chains.antelope.presentation.multisig.proposal.create.action.MultisigProposalActionScreen
import com.mangala.wallet.features.chains.antelope.presentation.multisig.proposal.ui.ApproverAuthorizationInput
import com.mangala.wallet.ui.component.MangalaGradientButton
import com.mangala.wallet.features.chains.antelope.presentation.multisig.proposal.ui.ProposalTextButton
import com.mangala.wallet.features.chains.antelope.presentation.multisig.proposal.ui.ProposalTopAppBar
import com.mangala.wallet.features.chains.antelope.presentation.proposal.getAvatarColor
import com.mangala.wallet.features.chains.antelope_base.domain.model.multisig.MultisigActionAuthorization
import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.ui.TextDescription2
import com.mangala.wallet.ui.TextIconAvatar
import com.mangala.wallet.ui.component.MaxSizeColumn
import com.mangala.wallet.ui.component.MaxWidthColumn
import com.mangala.wallet.ui.component.VerticalSpacer
import com.mangala.wallet.ui.theme.MangalaTypography
import com.mangala.wallet.ui.theme.mangalaColors
import com.mangala.wallet.ui.utils.collectAsStateMultiplatform
import com.mangala.wallet.ui.utils.screenmodel.BaseScreen
import com.mangala.wallet.utils.analytics.MangalaAnalytics
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.desc.desc
import kotlinx.coroutines.Dispatchers
import org.koin.core.parameter.parametersOf

class MultisigProposalApproverScreen(

) : BaseScreen<CreateNewProposalScreenModel>() {

    override val isBottomBarVisible: Boolean = false
    override val screenName: String = MangalaAnalytics.Screens.ANTELOPE_MULTISIG_PROPOSAL_ACTION
    override val screenClassName: String = MultisigProposalActionScreen::class.simpleName.orEmpty()

    @OptIn(ExperimentalVoyagerApi::class)
    @Composable
    override fun createScreenModel(): CreateNewProposalScreenModel {
        val navigator = LocalNavigator.currentOrThrow
        return navigator.getNavigatorScreenModel<CreateNewProposalScreenModel>()
    }

    @Composable
    override fun ScreenContent(screenModel: CreateNewProposalScreenModel) {
        val navigator = LocalNavigator.currentOrThrow

        val uiState by screenModel.uiState.collectAsStateMultiplatform(Dispatchers.Main.immediate)

        val localScreenModel: MultisigProposalApproverScreenModel = getScreenModel(parameters = {
            parametersOf(
                uiState.actions,
                uiState.approvers,
            )

        })
        val localUiState =
            localScreenModel.uiState.collectAsStateMultiplatform(Dispatchers.Main.immediate)

        MultisigProposalApproverScreen(
            uiState = localUiState.value,
            onConfirmUpdateApprovers = {
                screenModel.onConfirmUpdateApprovers(
                    localUiState.value.listApprover.associate { approver ->
                        approver.key.authorization to approver.listItem.map { it.authorization }
                    }
                )
                navigator.pop()
            },
            onNavigateBack = { navigator.pop() },
            onAddAuthorization = { approverOder ->
                localScreenModel.onAddAuthorization(approverOder)
            },
            onRemoveAuthorization = { authorIndex, approverIndex, author ->
                localScreenModel.onRemoveAuthorization(
                    authorIndex,
                    approverIndex,
                    author
                )
            },
            onAuthorizationNameChange = { approverOrder, index, value, authorItem, author ->
                localScreenModel.onAuthorizationNameChange(
                    approverOrder,
                    index,
                    value,
                    authorItem,
                    author
                )
            },
            onPermissionNameChange = { approverOrder, index, value, authorItem, author ->
                localScreenModel.onPermissionNameChange(
                    approverOrder,
                    index,
                    value,
                    authorItem,
                    author
                )
            },
            onUpdateBothInputValues = { newActor, newPermission, approverOrder, index, authorItem ->
                localScreenModel.onUpdateBothInputs(
                    newActor,
                    newPermission,
                    approverOrder,
                    index,
                    authorItem
                )
            }
        )
    }

    @Composable
    fun MultisigProposalApproverScreen(
        uiState: MultisigProposalApproverUiModel,
        onConfirmUpdateApprovers: () -> Unit,
        onNavigateBack: () -> Unit,
        onAddAuthorization: (Int) -> Unit,
        onRemoveAuthorization: (Int, Int, MultisigActionAuthorization) -> Unit,
        onAuthorizationNameChange: (Int, Int, String, AuthorItem, MultisigActionAuthorization) -> Unit,
        onPermissionNameChange: (Int, Int, String, AuthorItem, MultisigActionAuthorization) -> Unit,
        onUpdateBothInputValues: (String, String, Int, Int, AuthorItem) -> Unit
    ) {
        MaxSizeColumn {
            ProposalTopAppBar(
                onBackPressed = onNavigateBack,
                label = MR.strings.title_multisig_approver_screen_top_bar.desc()
                    .localized()
            )
            ApprovalScreen(
                uiModel = uiState,
                onConfirmUpdateApprovers = {
                    onConfirmUpdateApprovers()
                },
                onRemoveAuthorization = onRemoveAuthorization,
                onAddAuthorization = onAddAuthorization,
                onAuthorizationNameChange = onAuthorizationNameChange,
                onPermissionNameChange = onPermissionNameChange,
                onUpdateBothInputValues = onUpdateBothInputValues
            )
        }
    }

    @Composable
    fun ApprovalScreen(
        uiModel: MultisigProposalApproverUiModel,
        onConfirmUpdateApprovers: () -> Unit,
        onAddAuthorization: (Int) -> Unit,
        onRemoveAuthorization: (Int, Int, MultisigActionAuthorization) -> Unit,
        onAuthorizationNameChange: (Int, Int, String, AuthorItem, MultisigActionAuthorization) -> Unit,
        onPermissionNameChange: (Int, Int, String, AuthorItem, MultisigActionAuthorization) -> Unit,
        onUpdateBothInputValues: (String, String, Int, Int, AuthorItem) -> Unit
    ) {
        MaxSizeColumn(
            Modifier
                .fillMaxWidth()
                .background(MaterialTheme.mangalaColors.bg),
            verticalArrangement = Arrangement.SpaceBetween

        ) {

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = Dimensions.Padding.default),
            ) {
                item {
                    Spacer(modifier = Modifier.height(Spacing.BASE))
                }

                itemsIndexed(uiModel.listApprover) { index, approverEntry ->
                    Column {
                        ApproverItem(
                            approver = approverEntry,
                            authorIndex = index,
                            onAddAuthorization = onAddAuthorization,
                            onRemoveAuthorization = onRemoveAuthorization,
                            onAuthorizationNameChange = onAuthorizationNameChange,
                            onPermissionNameChange = onPermissionNameChange,
                            onUpdateBothInputValues = onUpdateBothInputValues
                        )
                    }
                }

            }

            MaxWidthColumn(
                Modifier
                    .background(MaterialTheme.mangalaColors.bgInnerCard)
                    .navigationBarsPadding()
                    .imePadding()
                    .padding(horizontal = Dimensions.Padding.default),
            ) {
                VerticalSpacer(Spacing.SMALL)
                MangalaGradientButton(
                    label = MR.strings.all_submit.desc().localized(),
                    onClick = {
                        onConfirmUpdateApprovers()
                    },
                    enabled = uiModel.isButtonEnabled,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }

    @Composable
    fun ApproverItem(
        approver: Approver,
        authorIndex: Int,
        onAddAuthorization: (Int) -> Unit,
        onRemoveAuthorization: (Int, Int, MultisigActionAuthorization) -> Unit,
        onAuthorizationNameChange: (Int, Int, String, AuthorItem, MultisigActionAuthorization) -> Unit,
        onPermissionNameChange: (Int, Int, String, AuthorItem, MultisigActionAuthorization) -> Unit,
        onUpdateBothInputValues: (String, String, Int, Int, AuthorItem) -> Unit
    ) {
        ApproverLabel(
            submitter = approver.key.authorization.authorizationName,
            permissionName = approver.key.authorization.permissionName,
            totalWeight = approver.sumWeight,
            threshold = approver.threshold
        )

        Spacer(modifier = Modifier.height(Spacing.XSMALL))

        Text(
            text = "Action: ${approver.key.actionName}",
            style = MangalaTypography.Size13Medium(),
            color = ColorsNew.primary_500
        )

        Spacer(modifier = Modifier.height(Spacing.XSMALL))

        Column(modifier = Modifier.fillMaxWidth()) {
            approver.listItem.forEachIndexed { index, author ->
                ApproverAuthorizationInput(
                    firstInputLabel = "Approver",
                    firstInputValue = author.authorization.authorizationName,
                    firstInputPlaceholder = "Enter approver",
                    onFirstInputValueChange = { value ->
                        onAuthorizationNameChange(
                            authorIndex,
                            index,
                            value,
                            author,
                            approver.key.authorization
                        )
                    },
                    firstInputLoading = author.firstInputLoading,
                    firstInputError = author.firstInputError,
                    secondInputLabel = "Permission",
                    secondInputValue = author.authorization.permissionName,
                    secondInputPlaceholder = "Enter weight",
                    onSecondInputValueChange = { value ->
                        onPermissionNameChange(
                            authorIndex,
                            index,
                            value,
                            author,
                            approver.key.authorization
                        )
                    },
                    secondInputLoading = author.secondInputLoading,
                    secondInputError = author.secondInputError,
                    subtitle = "Weight ${approver.listItem[index].weight}",
                    shouldShowRemoveButton = approver.listItem.size > 1,
                    onDelete = {
                        onRemoveAuthorization(
                            authorIndex,
                            index,
                            approver.key.authorization
                        )
                    },
                    suggestionsActor = author.listActor.distinct(),
                    suggestionsPermission = author.listPermission.distinct(),
                    onUpdateBothInputValues = { newActor, newPermission ->
                        onUpdateBothInputValues(
                            newActor,
                            newPermission,
                            authorIndex,
                            index,
                            author,
                        )
                    }
                )
                Spacer(modifier = Modifier.height(Spacing.XSMALL))
            }
        }

        if(approver.listItem.size < approver.numberAccounts) {
            ProposalTextButton(
                onClick = {
                    onAddAuthorization(authorIndex)
                },
                text = MR.strings.title_multisig_approver_screen_text_underline.desc().localized()
            )
        }

        Spacer(modifier = Modifier.height(Spacing.BASE))
    }


    @Composable
    fun ApproverLabel(
        submitter: String,
        permissionName: String,
        totalWeight: Int,
        threshold: Int
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row {
                val submitterAvatarLetter = remember { submitter.trim().firstOrNull() } ?: 'A'
                TextIconAvatar(
                    modifier = Modifier
                        .size(ButtonIconSize)
                        .background(
                            color = getAvatarColor(submitterAvatarLetter),
                            shape = CircleShape
                        )
                        .align(Alignment.CenterVertically),
                    text = submitterAvatarLetter.toString().uppercase(),
                    color = ColorsNew.white,
                    style = TextStyle(
                        fontSize = FontType.SMALL,
                        fontWeight = FontWeight.Normal,
                        lineHeight = FontType.SMALL,
                        textAlign = TextAlign.Center
                    )
                )

                Spacer(modifier = Modifier.width(Spacing.XTINY))

                TextDescription2(
                    text = "$submitter@${permissionName}",
                    fontWeight = FontWeight.Medium,
                    fontSize = FontType.SMALL,
                    color = MaterialTheme.mangalaColors.textPrimary
                )
            }

            TextDescription2(
                text = "Total weight $totalWeight/$threshold",
                fontWeight = FontWeight.Medium,
                fontSize = FontType.SMALL,
                color = if (totalWeight >= threshold) MaterialTheme.mangalaColors.textPrimary else ColorsNew.error_600
            )
        }
    }

}