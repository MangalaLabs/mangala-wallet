package com.mangala.wallet.features.chains.antelope.presentation.proposal.expiredProposal.detail

import cafe.adriel.voyager.core.model.screenModelScope
import com.mangala.antelope.base.api.model.GetMultisigProposalTableRowResponse.ProvidedApprovals
import com.mangala.antelope.base.api.model.GetMultisigProposalTableRowResponse.RequestedProposalRow
import com.mangala.wallet.domain.datastore.usecases.GetSelectedNetworkUseCase
import com.mangala.wallet.features.chains.antelope.presentation.multisig.proposal.detail.ActionProposalDetail
import com.mangala.wallet.features.chains.antelope.presentation.multisig.proposal.detail.Approval
import com.mangala.wallet.features.chains.antelope.presentation.multisig.proposal.detail.ProposalDetail
import com.mangala.wallet.features.chains.antelope.presentation.multisig.proposal.detail.State
import com.mangala.wallet.features.chains.antelope.presentation.proposal.myProposal.detail.MyProposalDetailScreenUiState
import com.mangala.wallet.features.chains.antelope_base.domain.PROPOSAL_APPROVED_STATUS
import com.mangala.wallet.features.chains.antelope_base.domain.PROPOSAL_PENDING_STATUS
import com.mangala.wallet.features.chains.antelope_base.domain.model.multisig.MultisigActionAuthorization
import com.mangala.wallet.features.chains.antelope_base.domain.model.proposal.TransactionProposalDecoded
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.account.GetAccountPermissionsUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.account.GetAccountsUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.multisigs.proposal.DecoderProposalTransactionUseCaseV2
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.multisigs.proposal.GetAccountWeightUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.multisigs.proposal.GetExecutableStatusProposalUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.multisigs.proposal.GetProposalDetailUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.multisigs.proposal.GetRequestApprovalProposalDetailUseCase
import com.mangala.wallet.model.blockchain.BlockchainType
import com.mangala.wallet.ui.utils.screenmodel.BaseScreenModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock

class ExpiredProposalDetailScreenModel(
    proposerAccountName: String,
    proposalName: String,
    private val getProposalDetailUseCase: GetProposalDetailUseCase,
    private val decoderProposalTransactionUseCase: DecoderProposalTransactionUseCaseV2,
    private val getSelectedNetworkUseCase: GetSelectedNetworkUseCase,
    private val getRequestApprovalProposalDetailUseCase: GetRequestApprovalProposalDetailUseCase,
    private val getAccountsUseCase: GetAccountsUseCase,
    private val getAccountPermissionsUseCase: GetAccountPermissionsUseCase,
    private val getExecutableStatusProposalUseCase: GetExecutableStatusProposalUseCase,
    private val getAccountWeightUseCase: GetAccountWeightUseCase
) : BaseScreenModel() {
    lateinit var blockchainType: BlockchainType
    private val _uiState =
        MutableStateFlow<MyProposalDetailScreenUiState>(MyProposalDetailScreenUiState.Loaded(ProposalDetail()))
    val uiState: StateFlow<MyProposalDetailScreenUiState> = _uiState.asStateFlow()
    private var _accountExecuted = ""
    private var _permissionExecuted = ""

    init {
        loadData(proposerAccountName, proposalName, null)
    }

    fun loadData(
        proposerAccountName: String,
        proposalName: String,
        chainId: String?
    ) {
        screenModelScope.launch {
            blockchainType = getSelectedNetworkUseCase().blockchainType

            try {
                val proposalDetailDeferred = async {
                    getProposalDetailUseCase(blockchainType, proposerAccountName, proposalName)
                }
                val requestApprovalDeferred = async {
                    getRequestApprovalProposalDetailUseCase(proposerAccountName, proposalName, blockchainType)
                }
                val accountsImportedDeferred = async {
                    getAccountsUseCase()
                }

                val proposalDetailResult = proposalDetailDeferred.await()

                val transactionProposalDecoded = proposalDetailResult?.rows?.firstOrNull()?.packedTransaction?.let {
                    decoderProposalTransactionUseCase(it)
                } ?: run {
                    _uiState.value = MyProposalDetailScreenUiState.ExecutedError("Transaction executed")
                    return@launch
                }

                val accountsImported = accountsImportedDeferred.await()
                val accountNamesImported = accountsImported.map { it.accountName }
                _accountExecuted = accountNamesImported.firstOrNull().orEmpty()

                val permissionsImported = getAccountPermissionsUseCase(
                    proposerAccountName
                ).map { it.permissionType.permissionName }

                _permissionExecuted = permissionsImported.firstOrNull().orEmpty()

                val requestApproval = requestApprovalDeferred.await()

                val requestedApprovals =
                    requestApproval?.rows?.getOrNull(0)?.requestedProposal.orEmpty()
                val providedApprovals =
                    requestApproval?.rows?.getOrNull(0)?.providedApprovals.orEmpty()

                val actionProposalDetails = extractActionProposalDetails(transactionProposalDecoded)

                val accountWeight = getAccountWeightUseCase(
                    blockchainType,
                    actionProposalDetails.firstOrNull()?.authorizations?.firstOrNull()?.authorizationName.orEmpty(),
                    actionProposalDetails.firstOrNull()?.authorizations?.firstOrNull()?.permissionName.orEmpty(),
                )

                val executableStatus = getExecutableStatusProposalUseCase(
                    providedApprovals.map {
                        MultisigActionAuthorization(
                            authorizationName = it.level.actor.orEmpty(),
                            authorizationNameSuggestions = emptyList(),
                            permissionName = it.level.permission.orEmpty(),
                            account = null
                        )
                    },
                    accountWeight
                )

                var state = if (executableStatus.isExecutable) State.Executable else State.Pending

                if (transactionProposalDecoded.expiration < Clock.System.now()) {
                    state = State.Expired
                }

                _uiState.update {
                    MyProposalDetailScreenUiState.Loaded(
                        ProposalDetail(
                            expirationDate = transactionProposalDecoded.expiration,
                            actionProposalDetails = actionProposalDetails,
                            approvals = convertToApprovalList(
                                requestedApprovals,
                                providedApprovals,
                                accountWeight.accountWeightMap
                            ),
                            accountsImported = accountNamesImported,
                            permissionsImported = permissionsImported,
                            accountExecuted = _accountExecuted,
                            permissionExecuted = _permissionExecuted,
                            isApproved = isApproved(proposerAccountName, providedApprovals),
                            isRequestedApproval = isRequestedApproval(
                                proposerAccountName,
                                requestedApprovals
                            ),
                            approvedCount = providedApprovals.size,
                            requestedApprovalCount = requestedApprovals.size,
                            state = state
                        )
                    )
                }

            } catch (e: Exception) {
                _uiState.value =
                    MyProposalDetailScreenUiState.Error(e.message ?: "Failed to load proposal data")
            }
        }
    }

    private fun isApproved(submitter: String, providedApprovals: List<ProvidedApprovals>): Boolean {
        return providedApprovals.any { approval ->
            approval.level.actor == submitter
        }
    }

    private fun isRequestedApproval(
        submitter: String,
        requestedProposal: List<RequestedProposalRow>
    ): Boolean {
        return requestedProposal.any { request ->
            request.level.actor == submitter
        }
    }

    private fun convertToApprovalList(
        requestedProposals: List<RequestedProposalRow>,
        providedApprovals: List<ProvidedApprovals>,
        accountWeightMap: Map<String?, Long>
    ): List<Approval> {
        val pendingApprovals = requestedProposals.map { requested ->
            val actor = requested.level.actor.orEmpty()
            val permission = requested.level.permission.orEmpty()
            val accountPermission = "$actor@$permission"
            Approval(
                actor = requested.level.actor.orEmpty(),
                permission = requested.level.permission.orEmpty(),
                status = PROPOSAL_PENDING_STATUS,
                weight = accountWeightMap[accountPermission] ?: 0
            )
        }
        val approvedApprovals = providedApprovals.map { provided ->
            val actor = provided.level.actor.orEmpty()
            val permission = provided.level.permission.orEmpty()
            val accountPermission = "$actor@$permission"
            Approval(
                actor = provided.level.actor.orEmpty(),
                permission = provided.level.permission.orEmpty(),
                status = PROPOSAL_APPROVED_STATUS,
                weight = accountWeightMap[accountPermission] ?: 0
            )
        }
        return pendingApprovals + approvedApprovals
    }


    private fun extractActionProposalDetails(transaction: TransactionProposalDecoded): List<ActionProposalDetail> {
        return transaction.actions.map { action ->
            val authorizations = action.authorization.map { auth ->
                MultisigActionAuthorization(
                    authorizationName = auth.actor,
                    permissionName = auth.permission,
                    account = null,
                    authorizationNameSuggestions = emptyList()
                )
            }

            ActionProposalDetail(
                authorizations = authorizations,
                action = action.name,
                dataDecoded = action.dataDecoded
            )
        }
    }
}