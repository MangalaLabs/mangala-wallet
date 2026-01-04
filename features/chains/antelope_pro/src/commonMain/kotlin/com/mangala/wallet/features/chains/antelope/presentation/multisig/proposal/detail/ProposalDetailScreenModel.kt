package com.mangala.wallet.features.chains.antelope.presentation.multisig.proposal.detail

import cafe.adriel.voyager.core.model.screenModelScope
import com.mangala.antelope.base.api.model.GetMultisigProposalTableRowResponse
import com.mangala.antelope.base.api.model.GetTableRowsMultisigsRequest
import com.mangala.wallet.domain.datastore.usecases.GetSelectedNetworkUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.PROPOSAL_APPROVED_STATUS
import com.mangala.wallet.features.chains.antelope_base.domain.PROPOSAL_PENDING_STATUS
import com.mangala.wallet.features.chains.antelope_base.domain.model.proposal.TransactionProposalDecoded
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.account.GetAccountPermissionsUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.account.GetAccountsUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.multisigs.GetTableRowsDataProposalUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.multisigs.proposal.ApproveProposalUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.multisigs.proposal.CancelProposalUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.multisigs.proposal.DecoderProposalTransactionUseCaseV2
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.multisigs.proposal.ExecuteProposalUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.multisigs.proposal.UnApproveProposalUseCase
import com.mangala.wallet.model.blockchain.BlockchainType
import com.mangala.wallet.ui.utils.screenmodel.BaseScreenModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProposalDetailScreenModel(
    private val proposerAccountName: String,
    private val proposal: GetMultisigProposalTableRowResponse.ProposalRow,
    private val getSelectedNetworkUseCase: GetSelectedNetworkUseCase,
    private val approveProposalUseCase: ApproveProposalUseCase,
    private val cancelProposalUseCase: CancelProposalUseCase,
    private val executeProposalUseCase: ExecuteProposalUseCase,
    private val unApproveProposalUseCase: UnApproveProposalUseCase,
    private val getTableRowsDataProposalUseCase: GetTableRowsDataProposalUseCase,
    private val decoderProposalTransactionUseCase: DecoderProposalTransactionUseCaseV2,
    private val getAccountsUseCase: GetAccountsUseCase,
    private val getAccountPermissionsUseCase: GetAccountPermissionsUseCase
) : BaseScreenModel() {

    private lateinit var blockchainType: BlockchainType

    private val _uiState =
        MutableStateFlow<ProposalDetailScreenUiState>(ProposalDetailScreenUiState.Loading)
    val uiState: StateFlow<ProposalDetailScreenUiState> = _uiState.asStateFlow()
    private val _accountExecuted = MutableStateFlow("")
    private val _permissionExecuted = MutableStateFlow("")

    init {
        screenModelScope.launch {
            blockchainType = getSelectedNetworkUseCase().blockchainType
            loadProposalData(blockchainType)
        }
    }

    fun approveProposal(
        proposerAccountName: String,
        proposalName: String,
        accountPermissionExecuted: String,
        accountNameExecuted: String,
    ) = performProposalAction(
        useCase = {
            approveProposalUseCase.pushApproveProposal(
                blockchainType,
                proposerAccountName,
                proposalName,
                accountPermissionExecuted,
                accountNameExecuted
            )
        },
        successMessage = "Proposal approved",
        errorMessage = "Approve proposal failed"
    )

    fun cancelProposal(
        proposerAccountName: String,
        proposalName: String,
        accountPermissionExecuted: String,
        accountNameExecuted: String,
    ) = performProposalAction(
        useCase = {
            cancelProposalUseCase.pushCancelProposal(
                blockchainType,
                proposerAccountName,
                proposalName,
                accountPermissionExecuted,
                accountNameExecuted
            )
        },
        successMessage = "Proposal cancelled",
        errorMessage = "Cancel proposal failed"
    )

    fun executeProposal(
        proposerAccountName: String,
        proposalName: String,
        accountPermissionExecuted: String,
        accountNameExecuted: String,
    ) = performProposalAction(
        useCase = {
            executeProposalUseCase.pushExecuteProposal(
                blockchainType,
                proposerAccountName,
                proposalName,
                accountPermissionExecuted,
                accountNameExecuted
            )
        },
        successMessage = "Proposal executed",
        errorMessage = "Execute proposal failed"
    )

    fun unApproveProposal(
        proposerAccountName: String,
        proposalName: String,
        accountPermissionExecuted: String,
        accountNameExecuted: String,
    ) {
        performProposalAction(
            useCase = {
                unApproveProposalUseCase.pushUnApproveProposal(
                    blockchainType,
                    proposerAccountName,
                    proposalName,
                    accountPermissionExecuted,
                    accountNameExecuted
                )
            },
            successMessage = "Proposal unapproved",
            errorMessage = "Unapprove proposal failed"
        )
    }

    fun updateAccountExecuted(newAccountName: String, proposalDetail: ProposalDetail) {
        _accountExecuted.value = newAccountName
        screenModelScope.launch {
            val permissionsImported = getAccountPermissionsUseCase(newAccountName).map {
                it.permissionType.permissionName
            }
            _permissionExecuted.value = permissionsImported.firstOrNull().orEmpty()

            _uiState.value = ProposalDetailScreenUiState.LoadDataSuccess(
                proposalDetail.copy(
                    accountExecuted = newAccountName,
                    permissionExecuted = permissionsImported.firstOrNull().orEmpty()
                )
            )
        }
    }

    fun updatePermissionExecuted(newPermissionName: String, proposalDetail: ProposalDetail) {
        _permissionExecuted.value = newPermissionName
        _uiState.value = ProposalDetailScreenUiState.LoadDataSuccess(
            proposalDetail.copy(permissionExecuted = newPermissionName)
        )
    }

    private fun performProposalAction(
        useCase: suspend () -> Result<String>,
        successMessage: String,
        errorMessage: String,
    ) {
        screenModelScope.launch {
            val result = useCase()
            _uiState.value = result.fold(
                onSuccess = { ProposalDetailScreenUiState.Success(successMessage) },
                onFailure = { ProposalDetailScreenUiState.Error(it.message ?: errorMessage) }
            )
        }
    }

    private fun loadProposalData(blockchainType: BlockchainType) {
        screenModelScope.launch {
            try {
                val request =
                    createTableRowsRequest(proposerAccountName, proposal.proposalName.toString())
                val result = getTableRowsDataProposalUseCase(blockchainType, request)

                val transactionProposalDecoded = result?.rows?.firstOrNull()?.packedTransaction
                    ?.let { decoderProposalTransactionUseCase(it) }
                    ?: throw IllegalArgumentException("No transaction found")

                val actionProposalDetails = extractActionProposalDetails(transactionProposalDecoded)

                val accountsImported = getAccountsUseCase()

                val accountNamesImported = accountsImported.map { it.accountName }

                val permissionsImported = getAccountPermissionsUseCase(
                    accountNamesImported.firstOrNull().orEmpty()
                ).map {it.permissionType.permissionName}

                _uiState.value = ProposalDetailScreenUiState.LoadDataSuccess(
                    ProposalDetail(
                        approvalStatus = calculateApprovalStatus(proposal),
                        expirationDate = transactionProposalDecoded.expiration,
                        actionProposalDetails = actionProposalDetails,
                        approvals = convertToApprovalList(
                            proposal.requestedProposal,
                            proposal.providedApprovals
                        ),
                        accountsImported = accountNamesImported,
                        permissionsImported = permissionsImported,
                        accountExecuted = accountNamesImported.firstOrNull().orEmpty(),
                        permissionExecuted = permissionsImported.firstOrNull().orEmpty(),
                        actionAbi = transactionProposalDecoded.actions
                    )
                )
            } catch (e: Exception) {
                _uiState.value =
                    ProposalDetailScreenUiState.Error(e.message ?: "Failed to load proposal data")
            }
        }
    }

    private fun createTableRowsRequest(proposerAccountName: String, proposalName: String) =
        GetTableRowsMultisigsRequest(
            code = "eosio.msig",
            scope = proposerAccountName,
            table = "proposal",
            lowerBound = proposalName,
            encodeType = "",
            upperBound = "",
            limit = 1,
            showPayer = false,
            json = true,
            keyType = "",
            indexPosition = "",
            reverse = false
        )

    private fun calculateApprovalStatus(proposal: GetMultisigProposalTableRowResponse.ProposalRow): String {
        val totalApprovals = proposal.providedApprovals.size + proposal.requestedProposal.size
        return "${proposal.providedApprovals.size} / $totalApprovals"
    }

    private fun extractActionProposalDetails(transaction: TransactionProposalDecoded): List<ActionProposalDetail> {
        return transaction.actions.flatMap { action ->
            action.authorization.map { auth ->
                ActionProposalDetail(
                    auth.actor,
                    auth.permission,
                    action.name,
                    "getDataString(action?.data)",
                    action.dataDecoded
                )
            }
        }
    }

    fun convertToApprovalList(
        requestedProposals: List<GetMultisigProposalTableRowResponse.RequestedProposalRow>,
        providedApprovals: List<GetMultisigProposalTableRowResponse.ProvidedApprovals>,
    ): List<Approval> {
        val pendingApprovals = requestedProposals.map { requested ->
            Approval(
                actor = requested.level.actor!!,
                permission = requested.level.permission!!,
                status = PROPOSAL_PENDING_STATUS
            )
        }
        val approvedApprovals = providedApprovals.map { provided ->
            Approval(
                actor = provided.level.actor!!,
                permission = provided.level.permission!!,
                status = PROPOSAL_APPROVED_STATUS
            )
        }
        return pendingApprovals + approvedApprovals
    }
}