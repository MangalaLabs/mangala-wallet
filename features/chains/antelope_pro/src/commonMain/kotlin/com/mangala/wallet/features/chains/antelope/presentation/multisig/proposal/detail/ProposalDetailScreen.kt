package com.mangala.wallet.features.chains.antelope.presentation.multisig.proposal.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.Button
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.koin.getScreenModel
import com.mangala.antelope.base.api.model.GetMultisigProposalTableRowResponse
import com.mangala.wallet.features.chains.antelope_base.domain.PROPOSAL_APPROVED_STATUS
import com.mangala.wallet.features.chains.antelope_base.domain.PROPOSAL_PENDING_STATUS
import com.mangala.wallet.features.chains.antelope_base.domain.model.actions.abis.AntelopeActionAbi
import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.ui.component.MaxSizeBox
import com.mangala.wallet.ui.utils.collectAsStateMultiplatform
import com.mangala.wallet.ui.utils.screenmodel.BaseScreen
import com.mangala.wallet.utils.analytics.MangalaAnalytics
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.desc.desc
import dev.icerock.moko.resources.format
import org.koin.core.parameter.parameterArrayOf

class ProposalDetailScreen(
    private val proposal: GetMultisigProposalTableRowResponse.ProposalRow,
    private val proposer: String,
) : BaseScreen<ProposalDetailScreenModel>() {

    override val screenName: String = MangalaAnalytics.Screens.ANTELOPE_MULTISIG_PROPOSAL_DETAILS
    override val screenClassName: String = ProposalDetailScreen::class.simpleName.orEmpty()

    @Composable
    override fun createScreenModel(): ProposalDetailScreenModel =
        getScreenModel<ProposalDetailScreenModel> { parameterArrayOf(proposer, proposal) }

    @Composable
    override fun ScreenContent(screenModel: ProposalDetailScreenModel) {
        MaxSizeBox(Modifier.background(Color.White).windowInsetsPadding(WindowInsets.safeDrawing)) {
            when (val uiState = screenModel.uiState.collectAsStateMultiplatform().value) {
                is ProposalDetailScreenUiState.Loading -> {
                }

                is ProposalDetailScreenUiState.Success -> {
                    Box(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                        Text(MR.strings.message_proposal_detail_screen_success.format(uiState.data).localized())
                    }
                }

                is ProposalDetailScreenUiState.LoadDataSuccess -> {

                    val proposalDetail = uiState.data

                    ProposalDetailScreen(
                        accountsImported = proposalDetail.accountsImported,
                        permissionsImported = proposalDetail.permissionsImported,
                        proposer = proposer,
                        proposalName = proposal.proposalName.toString(),
                        approvalStatus = proposalDetail.approvalStatus,
                        expirationDate = proposalDetail.expirationDateFormatted,
                        proposalDetail = proposalDetail,
                        authorizationDetails = proposalDetail.actionProposalDetails,
                        screenModel = screenModel
                    )
                }

                is ProposalDetailScreenUiState.Error -> {
                    Box(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                        Text(MR.strings.all_error.format(uiState.message).localized())
                    }
                }
            }
        }

    }




    @Composable
    fun ProposalDetailScreen(
        accountsImported: List<String>,
        permissionsImported: List<String>,
        proposer: String,
        proposalName: String,
        approvalStatus: String,
        expirationDate: String,
        proposalDetail: ProposalDetail,
        authorizationDetails: List<ActionProposalDetail>,
        screenModel: ProposalDetailScreenModel,
    ) {
        var accountExpanded by remember { mutableStateOf(false) }
        var permissionExpanded by remember { mutableStateOf(false) }
        LazyColumn(modifier = Modifier.padding(16.dp)) {
            item {
                Row(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(MR.strings.all_proposer.desc().localized(), style = MaterialTheme.typography.h6)
                        Text(proposer, style = MaterialTheme.typography.body1)
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(MR.strings.message_proposal_detail_screen_proposal_name.desc().localized(), style = MaterialTheme.typography.h6)
                        Text(proposalName, style = MaterialTheme.typography.body1)
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(MR.strings.message_proposal_detail_screen_approval_status.desc().localized(), style = MaterialTheme.typography.h6)
                        Text(approvalStatus, style = MaterialTheme.typography.body1)
                    }
                }

                Box {
                    OutlinedTextField(
                        value = proposalDetail.accountExecuted,
                        onValueChange = { },
                        label = { Text(text = MR.strings.label_proposal_detail_screen_account_execute.desc().localized(), color = Color.Black) },
                        modifier = Modifier.fillMaxWidth(),
                        trailingIcon = {
                            IconButton(onClick = { accountExpanded = !accountExpanded }) {
                                Icon(
                                    Icons.Default.ArrowDropDown,
                                    contentDescription = "Dropdown",
                                    tint = Color.Black
                                )
                            }
                        },
                        colors = TextFieldDefaults.textFieldColors(
                            backgroundColor = Color(0xFFEEEEEE),
                            focusedIndicatorColor = Color.Blue,
                            unfocusedIndicatorColor = Color.Gray
                        ),
                        readOnly = true
                    )
                    DropdownMenu(
                        expanded = accountExpanded,
                        onDismissRequest = { accountExpanded = false }
                    ) {
                        accountsImported.forEach { account ->
                            DropdownMenuItem(onClick = {
                                screenModel.updateAccountExecuted(account, proposalDetail)
                                accountExpanded = false
                            }) {
                                Text(account)
                            }
                        }
                    }
                }


                Box {
                    OutlinedTextField(
                        value = proposalDetail.permissionExecuted,
                        onValueChange = { },
                        label = { Text(text = MR.strings.label_proposal_detail_screen_permission_execute.desc().localized(), color = Color.Black) },
                        modifier = Modifier.fillMaxWidth(),
                        trailingIcon = {
                            IconButton(onClick = { permissionExpanded = !permissionExpanded }) {
                                Icon(
                                    Icons.Default.ArrowDropDown,
                                    contentDescription = "Dropdown",
                                    tint = Color.Black
                                )
                            }
                        },
                        colors = TextFieldDefaults.textFieldColors(
                            backgroundColor = Color(0xFFEEEEEE),
                            focusedIndicatorColor = Color.Blue,
                            unfocusedIndicatorColor = Color.Gray
                        ),
                        readOnly = true
                    )
                    DropdownMenu(
                        expanded = permissionExpanded,
                        onDismissRequest = { permissionExpanded = false }
                    ) {
                        permissionsImported.forEach { permission ->
                            DropdownMenuItem(onClick = {
                                screenModel.updatePermissionExecuted(permission, proposalDetail)
                                permissionExpanded = false
                            }) {
                                Text(permission)
                            }
                        }
                    }
                }

                Row {
                    Button(onClick = {
                        screenModel.approveProposal(
                            proposer,
                            proposalName,
                            proposalDetail.permissionExecuted,
                            proposalDetail.accountExecuted
                        )
                    }) {
                        Text(MR.strings.button_proposal_detail_screen_approval.desc().localized())
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Button(onClick = {
                        screenModel.executeProposal(
                            proposer,
                            proposalName,
                            proposalDetail.permissionExecuted,
                            proposalDetail.accountExecuted
                        )
                    }) {
                        Text(MR.strings.button_proposal_detail_screen_execute.desc().localized())
                    }

                    Spacer(modifier = Modifier.width(12.dp))
                    Button(onClick = {
                        screenModel.cancelProposal(
                            proposer,
                            proposalName,
                            proposalDetail.permissionExecuted,
                            proposalDetail.accountExecuted
                        )
                    }) {
                        Text(MR.strings.all_cancel.desc().localized())
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Row {
                    Button(onClick = {
                        screenModel.unApproveProposal(
                            proposer,
                            proposalName,
                            proposalDetail.permissionExecuted,
                            proposalDetail.accountExecuted
                        )
                    }) {
                        Text(MR.strings.button_proposal_detail_screen_unapprove.desc().localized())
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Button(onClick = { /*TODO*/ }) {
                        Text(MR.strings.all_copy.desc().localized())
                    }

                }


                Text(MR.strings.message_proposal_detail_screen_multisig_tx.desc().localized(), style = MaterialTheme.typography.h6)
                Spacer(modifier = Modifier.height(8.dp))
                Text(MR.strings.message_proposal_detail_screen_expiration.format(expirationDate).localized(), style = MaterialTheme.typography.body2)
                Spacer(modifier = Modifier.height(8.dp))
                AuthorizationDetailSection(authorizationDetails)
                Spacer(modifier = Modifier.height(16.dp))
                Text(MR.strings.message_proposal_detail_screen_requested_approvals.desc().localized(), style = MaterialTheme.typography.h6)
                Spacer(modifier = Modifier.height(8.dp))
                ApprovalTable(
                    screenModel.convertToApprovalList(
                        proposal.requestedProposal,
                        proposal.providedApprovals
                    )
                )
            }
        }
    }

    @Composable
    fun AuthorizationDetailSection(details: List<ActionProposalDetail>) {
        Column(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
            details.forEach { detail ->
                Row {
                    Text(detail.actor, modifier = Modifier.weight(1f))
                    Text(detail.permission, modifier = Modifier.weight(1f))
                    Text(detail.action, modifier = Modifier.weight(1f))
                }
                Column(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                    detail.dataDecoded.forEach { actionAbi ->
                        AntelopeActionView(actionAbi)
                    }
                }
            }
        }
    }


    @Composable
    fun ApprovalTable(approvals: List<Approval>) {
        Column(modifier = Modifier.fillMaxWidth()) {
            approvals.forEachIndexed { index, approval ->
                Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                    Text("${index + 1}", modifier = Modifier.weight(1f))
                    Text(approval.actor, modifier = Modifier.weight(2f))
                    Text(approval.permission, modifier = Modifier.weight(2f))
                    val statusColor = when (approval.status) {
                        PROPOSAL_PENDING_STATUS -> Color.Yellow
                        PROPOSAL_APPROVED_STATUS -> Color.Green
                        else -> Color.Gray
                    }

                    Text(
                        text = approval.status,
                        color = statusColor,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }

    @Composable
    fun AntelopeActionView(antelopeActionAbi: AntelopeActionAbi) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "${antelopeActionAbi.fieldName} (${antelopeActionAbi.fieldType})",
                style = MaterialTheme.typography.h6
            )

            Spacer(modifier = Modifier.height(8.dp))
            if (!antelopeActionAbi.isArrayObject) {
                val fieldValue =
                    antelopeActionAbi.mapValue[antelopeActionAbi.fieldName]?.joinToString(", ")
                val defaultValue = MR.strings.message_proposal_detail_screen_n_a.desc().localized()
                Text(
                    text = MR.strings.message_proposal_detail_screen_value.format(fieldValue ?: defaultValue).localized(),
                    style = MaterialTheme.typography.body1
                )
            }

            antelopeActionAbi.subFields.forEach { subField ->
                Spacer(modifier = Modifier.height(8.dp))
                SubFieldView(subField, antelopeActionAbi.level + 1)
            }
        }
    }

    @Composable
    fun SubFieldView(subField: AntelopeActionAbi, indentLevel: Int) {
        Column(modifier = Modifier.padding(start = (indentLevel * 16).dp)) {
            Text(
                text = "${subField.fieldName} (${subField.fieldType})",
                style = MaterialTheme.typography.body2
            )

            Text(MR.strings.message_proposal_detail_screen_array_size.format(subField.arraySize).localized())

            if (!subField.isArrayObject) {

                val subFieldValue = subField.mapValue[subField.fieldName]?.joinToString(", ")
                val defaultValue = MR.strings.message_proposal_detail_screen_n_a.desc().localized()
                Text(
                    text = MR.strings.message_proposal_detail_screen_value.format(subFieldValue ?: defaultValue).localized(),
                    style = MaterialTheme.typography.caption
                )
            }

            subField.subFields.forEach {
                Spacer(modifier = Modifier.height(8.dp))
                SubFieldView(it, indentLevel + 1)
            }
        }
    }
}