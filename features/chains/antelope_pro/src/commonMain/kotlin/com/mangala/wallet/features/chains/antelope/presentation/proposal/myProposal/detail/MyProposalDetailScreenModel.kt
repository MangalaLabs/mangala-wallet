package com.mangala.wallet.features.chains.antelope.presentation.proposal.myProposal.detail

import cafe.adriel.voyager.core.model.screenModelScope
import com.mangala.antelope.base.api.model.GetMultisigProposalTableRowResponse.ProvidedApprovals
import com.mangala.antelope.base.api.model.GetMultisigProposalTableRowResponse.RequestedProposalRow
import com.mangala.antelope.base.domain.model.ResourceProviderResponse
import com.mangala.wallet.domain.datastore.usecases.GetSelectedNetworkUseCase
import com.mangala.wallet.features.chains.antelope.presentation.multisig.proposal.detail.ActionProposalDetail
import com.mangala.wallet.features.chains.antelope.presentation.multisig.proposal.detail.Approval
import com.mangala.wallet.features.chains.antelope.presentation.multisig.proposal.detail.ProposalDetail
import com.mangala.wallet.features.chains.antelope.presentation.multisig.proposal.detail.State
import com.mangala.wallet.features.chains.antelope_base.domain.APPROVE_PROPOSAL_ACTION
import com.mangala.wallet.features.chains.antelope_base.domain.CANCEL_PROPOSAL_ACTION
import com.mangala.wallet.features.chains.antelope_base.domain.EXECUTE_PROPOSAL_ACTION
import com.mangala.wallet.features.chains.antelope_base.domain.PROPOSAL_APPROVED_STATUS
import com.mangala.wallet.features.chains.antelope_base.domain.PROPOSAL_PENDING_STATUS
import com.mangala.wallet.features.chains.antelope_base.domain.UN_APPROVE_PROPOSAL_ACTION
import com.mangala.wallet.features.chains.antelope_base.domain.model.multisig.MultisigActionAuthorization
import com.mangala.wallet.features.chains.antelope_base.domain.model.proposal.TransactionProposalDecoded
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.account.GetAccountPermissionsUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.account.GetAccountsUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.multisigs.proposal.ApproveProposalUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.multisigs.proposal.CancelProposalUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.multisigs.proposal.DecoderProposalTransactionUseCaseV2
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.multisigs.proposal.ExecuteProposalUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.multisigs.proposal.GetAccountWeightUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.multisigs.proposal.GetExecutableStatusProposalUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.multisigs.proposal.GetProposalDetailUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.multisigs.proposal.GetRequestApprovalProposalDetailUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.multisigs.proposal.UnApproveProposalUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.transaction.BaseTransactUseCase
import com.mangala.wallet.features.chains.antelope_base.presentation.AntelopeTransactionHandler
import com.mangala.wallet.model.blockchain.BlockchainType
import com.mangala.wallet.ui.utils.screenmodel.BaseScreenModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MyProposalDetailScreenModel(
    proposerAccountName: String,
    proposalName: String,
    chainId: String?,
    private val getProposalDetailUseCase: GetProposalDetailUseCase,
    private val decoderProposalTransactionUseCase: DecoderProposalTransactionUseCaseV2,
    private val getSelectedNetworkUseCase: GetSelectedNetworkUseCase,
    private val getRequestApprovalProposalDetailUseCase: GetRequestApprovalProposalDetailUseCase,
    private val getAccountsUseCase: GetAccountsUseCase,
    private val getAccountPermissionsUseCase: GetAccountPermissionsUseCase,
    private val approveProposalUseCase: ApproveProposalUseCase,
    private val cancelProposalUseCase: CancelProposalUseCase,
    private val executeProposalUseCase: ExecuteProposalUseCase,
    private val unApproveProposalUseCase: UnApproveProposalUseCase,
    private val getExecutableStatusProposalUseCase: GetExecutableStatusProposalUseCase,
    private val getAccountWeightUseCase: GetAccountWeightUseCase
) : BaseScreenModel() {
    lateinit var blockchainType: BlockchainType
    private val _uiState =
        MutableStateFlow<MyProposalDetailScreenUiState>(MyProposalDetailScreenUiState.Loaded(ProposalDetail()))
    val uiState: StateFlow<MyProposalDetailScreenUiState> = _uiState.asStateFlow()
    private var _accountExecuted = ""
    private var _permissionExecuted = ""
    private var _proposalAction = ""

    init {
        loadData(proposerAccountName, proposalName, chainId)
    }

    fun updateAccountExecuted(newAccountName: String, proposalDetail: ProposalDetail) {
        _accountExecuted = newAccountName
        screenModelScope.launch {
            val permissionsImported = getAccountPermissionsUseCase(
                accountName = newAccountName,
                blockchainUid = blockchainType.uid
            ).map {
                it.permissionType.permissionName
            }
            _permissionExecuted = permissionsImported.firstOrNull().orEmpty()

            _uiState.value = MyProposalDetailScreenUiState.Loaded(
                proposalDetail.copy(
                    accountExecuted = newAccountName,
                    permissionExecuted = _permissionExecuted
                )
            )
        }
    }

    fun updatePermissionExecuted(newPermissionName: String, proposalDetail: ProposalDetail) {
        _permissionExecuted = newPermissionName
        screenModelScope.launch {
            val permissionsImported = getAccountPermissionsUseCase(
                accountName = _permissionExecuted,
                blockchainUid = blockchainType.uid
            ).map { it.permissionType.permissionName }


            _uiState.value = MyProposalDetailScreenUiState.Loaded(
                proposalDetail.copy(
                    permissionExecuted = _permissionExecuted,
                    permissionsImported = permissionsImported
                )
            )
        }
    }

    fun onConfirmActionProposal(proposalAction: String) {
        _proposalAction = proposalAction
        when (_proposalAction) {
            APPROVE_PROPOSAL_ACTION -> approveHandler.onRequestTransaction()
            UN_APPROVE_PROPOSAL_ACTION -> unApproveHandler.onRequestTransaction()
            CANCEL_PROPOSAL_ACTION -> cancelApproveHandler.onRequestTransaction()
            EXECUTE_PROPOSAL_ACTION -> executeApproveHandler.onRequestTransaction()
        }
    }

    fun onPinPromptShown() {
        when (_proposalAction) {
            APPROVE_PROPOSAL_ACTION -> approveHandler.onPinPromptShown()
            UN_APPROVE_PROPOSAL_ACTION -> unApproveHandler.onPinPromptShown()
            CANCEL_PROPOSAL_ACTION -> cancelApproveHandler.onPinPromptShown()
            EXECUTE_PROPOSAL_ACTION -> executeApproveHandler.onPinPromptShown()
        }
    }

    fun onAuthenticationSuccess(accountExecuted: String) {
        when (_proposalAction) {
            APPROVE_PROPOSAL_ACTION -> approveHandler.onAuthenticationSuccess(accountExecuted)
            UN_APPROVE_PROPOSAL_ACTION -> unApproveHandler.onAuthenticationSuccess(accountExecuted)
            CANCEL_PROPOSAL_ACTION -> cancelApproveHandler.onAuthenticationSuccess(accountExecuted)
            EXECUTE_PROPOSAL_ACTION -> executeApproveHandler.onAuthenticationSuccess(accountExecuted)
        }
    }

    fun onConfirmResourceProviderFee() {
        when (_proposalAction) {
            APPROVE_PROPOSAL_ACTION -> approveHandler.onConfirmResourceProviderFee()
            UN_APPROVE_PROPOSAL_ACTION -> unApproveHandler.onConfirmResourceProviderFee()
            CANCEL_PROPOSAL_ACTION -> cancelApproveHandler.onConfirmResourceProviderFee()
            EXECUTE_PROPOSAL_ACTION -> executeApproveHandler.onConfirmResourceProviderFee()
        }
    }

    fun onDismissTransactionFeeBreakdown() {
        when (_proposalAction) {
            APPROVE_PROPOSAL_ACTION -> approveHandler.onDismissTransactionFeeBreakdown()
            UN_APPROVE_PROPOSAL_ACTION -> unApproveHandler.onDismissTransactionFeeBreakdown()
            CANCEL_PROPOSAL_ACTION -> cancelApproveHandler.onDismissTransactionFeeBreakdown()
            EXECUTE_PROPOSAL_ACTION -> executeApproveHandler.onDismissTransactionFeeBreakdown()
        }
    }

    fun loadData(
        proposerAccountName: String,
        proposalName: String,
        chainId: String?,
    ) {
        screenModelScope.launch {
            blockchainType = if (chainId.isNullOrBlank()) getSelectedNetworkUseCase().blockchainType
            else BlockchainType.fromChainId(chainId)

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
                _accountExecuted = proposerAccountName

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

                val state = if (executableStatus.isExecutable) State.Executable else State.Pending

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
                println("Failed to load proposal data: ${e.message}")
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


    private val approveHandler = object : AntelopeTransactionHandler {
        override fun onRequestTransactionFeeRequired(resourceProviderResponse: ResourceProviderResponse.FeeRequired) {
            val currentState =
                (_uiState.value as? MyProposalDetailScreenUiState.Loaded) ?: return

            _uiState.update {
                currentState.copy(
                    resourceRequiredBreakdown = resourceProviderResponse.feeBreakdown,
                    resourceRequiredTotal = resourceProviderResponse.fee,
                    isLoading = false,
                    buttonEnabled = false
                )
            }
        }

        override fun onRequestTransactionInvalidRequest() {
            val uiState = (_uiState.value as? MyProposalDetailScreenUiState.Loaded) ?: return

            _uiState.value = uiState.copy(
                isLoading = false,
                error = "Invalid request",
                buttonEnabled = true
            )
        }

        override fun onRequestTransactionResourceCovered() {
            val currentState = _uiState.value as? MyProposalDetailScreenUiState.Loaded ?: return
            _uiState.update {
                currentState.copy(
                    promptConfirmTransaction = true,
                    isLoading = false,
                    buttonEnabled = false
                )
            }
        }


        override suspend fun requestTransaction(): Result<ResourceProviderResponse> {
            return approveProposalUseCase.requestApproveProposal(
                blockchainType,
                proposerAccountName,
                proposalName,
                _permissionExecuted,
                _accountExecuted
            )
        }

        override fun onDismissTransactionFeeBreakdown() {
            val currentState =
                (_uiState.value as? MyProposalDetailScreenUiState.Loaded) ?: return

            _uiState.update {
                currentState.copy(
                    resourceRequiredBreakdown = null,
                    resourceRequiredTotal = null,
                    isLoading = false,
                    buttonEnabled = true
                )
            }
        }

        override fun onPinPromptShown() {
            val currentState = _uiState.value as? MyProposalDetailScreenUiState.Loaded ?: return

            _uiState.update {
                currentState.copy(
                    promptConfirmTransaction = false,
                    resourceRequiredBreakdown = null,
                    resourceRequiredTotal = null,
                    buttonEnabled = true
                )
            }
        }

        override fun onConfirmResourceProviderFee() {
            val currentState = _uiState.value as? MyProposalDetailScreenUiState.Loaded ?: return

            _uiState.update {
                currentState.copy(promptConfirmTransaction = true, isLoading = true)
            }
        }

        override suspend fun pushTransactionWithoutResourceProvider(): Result<String> {
            return approveProposalUseCase.pushApproveProposal(
                blockchainType,
                proposerAccountName,
                proposalName,
                _permissionExecuted,
                _accountExecuted
            )
        }

        override fun showLoadingState() {
            val currentState = _uiState.value as? MyProposalDetailScreenUiState.Loaded ?: return

            _uiState.update {
                currentState.copy(isLoading = true, buttonEnabled = false)
            }
        }

        override fun onPushTransactionSuccess(txHash: String) {
            _uiState.update {
                MyProposalDetailScreenUiState.Success(APPROVE_PROPOSAL_ACTION)
            }
        }

        override fun onPushTransactionFail(throwable: Throwable) {
            val currentState = _uiState.value as? MyProposalDetailScreenUiState.Loaded ?: return

            _uiState.update {
                currentState.copy(
                    error = throwable.message,
                    isLoading = false,
                    buttonEnabled = true
                )
            }
        }

        override var resourceProviderResponse: ResourceProviderResponse? = null
        override val transactUseCase: BaseTransactUseCase = approveProposalUseCase
        override val coroutineScope: CoroutineScope = screenModelScope
        override var blockchainUid: String = ""
            get() = this@MyProposalDetailScreenModel.blockchainType.uid

    }

    private val unApproveHandler = object : AntelopeTransactionHandler {
        override fun onRequestTransactionFeeRequired(resourceProviderResponse: ResourceProviderResponse.FeeRequired) {
            val currentState =
                (_uiState.value as? MyProposalDetailScreenUiState.Loaded) ?: return

            _uiState.update {
                currentState.copy(
                    resourceRequiredBreakdown = resourceProviderResponse.feeBreakdown,
                    resourceRequiredTotal = resourceProviderResponse.fee,
                    isLoading = false,
                    buttonEnabled = false
                )
            }
        }

        override fun onRequestTransactionInvalidRequest() {
            val uiState = (_uiState.value as? MyProposalDetailScreenUiState.Loaded) ?: return

            _uiState.value = uiState.copy(
                isLoading = false,
                error = "Invalid request",
                buttonEnabled = true
            )
        }

        override fun onRequestTransactionResourceCovered() {
            val currentState = _uiState.value as? MyProposalDetailScreenUiState.Loaded ?: return

            println(
                "onRequestTransactionResourceCovered called"
            )
            _uiState.update {
                currentState.copy(
                    promptConfirmTransaction = true,
                    isLoading = false,
                    buttonEnabled = false
                )
            }
        }


        override suspend fun requestTransaction(): Result<ResourceProviderResponse> {
            return unApproveProposalUseCase.requestUnApproveProposal(
                blockchainType,
                proposerAccountName,
                proposalName,
                _permissionExecuted,
                _accountExecuted
            )
        }

        override fun onDismissTransactionFeeBreakdown() {
            val currentState =
                (_uiState.value as? MyProposalDetailScreenUiState.Loaded) ?: return

            _uiState.update {
                currentState.copy(
                    resourceRequiredBreakdown = null,
                    resourceRequiredTotal = null,
                    isLoading = false,
                    buttonEnabled = true
                )
            }
        }

        override fun onPinPromptShown() {
            val currentState = _uiState.value as? MyProposalDetailScreenUiState.Loaded ?: return

            _uiState.update {
                currentState.copy(
                    promptConfirmTransaction = false,
                    resourceRequiredBreakdown = null,
                    resourceRequiredTotal = null,
                    buttonEnabled = true
                )
            }
        }

        override fun onConfirmResourceProviderFee() {
            val currentState = _uiState.value as? MyProposalDetailScreenUiState.Loaded ?: return

            _uiState.update {
                currentState.copy(promptConfirmTransaction = true, isLoading = true)
            }
        }

        override suspend fun pushTransactionWithoutResourceProvider(): Result<String> {
            return unApproveProposalUseCase.pushUnApproveProposal(
                blockchainType,
                proposerAccountName,
                proposalName,
                _permissionExecuted,
                _accountExecuted
            )
        }

        override fun showLoadingState() {
            val currentState = _uiState.value as? MyProposalDetailScreenUiState.Loaded ?: return

            _uiState.update {
                currentState.copy(isLoading = true, buttonEnabled = false)
            }
        }

        override fun onPushTransactionSuccess(txHash: String) {
            _uiState.update {
                MyProposalDetailScreenUiState.Success(UN_APPROVE_PROPOSAL_ACTION)
            }
        }

        override fun onPushTransactionFail(throwable: Throwable) {
            val currentState = _uiState.value as? MyProposalDetailScreenUiState.Loaded ?: return

            _uiState.update {
                currentState.copy(
                    error = throwable.message,
                    isLoading = false,
                    buttonEnabled = true
                )
            }
        }

        override var resourceProviderResponse: ResourceProviderResponse? = null
        override val transactUseCase: BaseTransactUseCase = unApproveProposalUseCase
        override val coroutineScope: CoroutineScope = screenModelScope
        override var blockchainUid: String = ""
            get() = this@MyProposalDetailScreenModel.blockchainType.uid

    }

    private val cancelApproveHandler = object : AntelopeTransactionHandler {
        override fun onRequestTransactionFeeRequired(resourceProviderResponse: ResourceProviderResponse.FeeRequired) {
            val currentState =
                (_uiState.value as? MyProposalDetailScreenUiState.Loaded) ?: return

            _uiState.update {
                currentState.copy(
                    resourceRequiredBreakdown = resourceProviderResponse.feeBreakdown,
                    resourceRequiredTotal = resourceProviderResponse.fee,
                    isLoading = false,
                    buttonEnabled = false
                )
            }
        }

        override fun onRequestTransactionInvalidRequest() {
            val uiState = (_uiState.value as? MyProposalDetailScreenUiState.Loaded) ?: return

            _uiState.value = uiState.copy(
                isLoading = false,
                error = "Invalid request",
                buttonEnabled = true
            )
        }

        override fun onRequestTransactionResourceCovered() {
            val currentState = _uiState.value as? MyProposalDetailScreenUiState.Loaded ?: return
            _uiState.update {
                currentState.copy(
                    promptConfirmTransaction = true,
                    isLoading = false,
                    buttonEnabled = false
                )
            }
        }


        override suspend fun requestTransaction(): Result<ResourceProviderResponse> {
            return cancelProposalUseCase.requestCancelProposal(
                blockchainType,
                proposerAccountName,
                proposalName,
                _permissionExecuted,
                _accountExecuted
            )
        }

        override fun onDismissTransactionFeeBreakdown() {
            val currentState =
                (_uiState.value as? MyProposalDetailScreenUiState.Loaded) ?: return

            _uiState.update {
                currentState.copy(
                    resourceRequiredBreakdown = null,
                    resourceRequiredTotal = null,
                    isLoading = false,
                    buttonEnabled = true
                )
            }
        }

        override fun onPinPromptShown() {
            val currentState = _uiState.value as? MyProposalDetailScreenUiState.Loaded ?: return

            _uiState.update {
                currentState.copy(
                    promptConfirmTransaction = false,
                    resourceRequiredBreakdown = null,
                    resourceRequiredTotal = null,
                    buttonEnabled = true
                )
            }
        }

        override fun onConfirmResourceProviderFee() {
            val currentState = _uiState.value as? MyProposalDetailScreenUiState.Loaded ?: return

            _uiState.update {
                currentState.copy(promptConfirmTransaction = true, isLoading = true)
            }
        }

        override suspend fun pushTransactionWithoutResourceProvider(): Result<String> {
            return cancelProposalUseCase.pushCancelProposal(
                blockchainType,
                proposerAccountName,
                proposalName,
                _permissionExecuted,
                _accountExecuted
            )
        }

        override fun showLoadingState() {
            val currentState = _uiState.value as? MyProposalDetailScreenUiState.Loaded ?: return

            _uiState.update {
                currentState.copy(isLoading = true, buttonEnabled = false)
            }
        }

        override fun onPushTransactionSuccess(txHash: String) {
            _uiState.update {
                MyProposalDetailScreenUiState.Success(CANCEL_PROPOSAL_ACTION)
            }
        }

        override fun onPushTransactionFail(throwable: Throwable) {
            val currentState = _uiState.value as? MyProposalDetailScreenUiState.Loaded ?: return

            _uiState.update {
                currentState.copy(
                    error = throwable.message,
                    isLoading = false,
                    buttonEnabled = true
                )
            }
        }

        override var resourceProviderResponse: ResourceProviderResponse? = null
        override val transactUseCase: BaseTransactUseCase = cancelProposalUseCase
        override val coroutineScope: CoroutineScope = screenModelScope
        override var blockchainUid: String = ""
            get() = this@MyProposalDetailScreenModel.blockchainType.uid


    }

    private val executeApproveHandler = object : AntelopeTransactionHandler {
        override fun onRequestTransactionFeeRequired(resourceProviderResponse: ResourceProviderResponse.FeeRequired) {
            val currentState =
                (_uiState.value as? MyProposalDetailScreenUiState.Loaded) ?: return

            _uiState.update {
                currentState.copy(
                    resourceRequiredBreakdown = resourceProviderResponse.feeBreakdown,
                    resourceRequiredTotal = resourceProviderResponse.fee,
                    isLoading = false,
                    buttonEnabled = false
                )
            }
        }

        override fun onRequestTransactionInvalidRequest() {
            val uiState = (_uiState.value as? MyProposalDetailScreenUiState.Loaded) ?: return

            _uiState.value = uiState.copy(
                isLoading = false,
                error = "Invalid request",
                buttonEnabled = true
            )
        }

        override fun onRequestTransactionResourceCovered() {
            val currentState = _uiState.value as? MyProposalDetailScreenUiState.Loaded ?: return

            println(
                "onRequestTransactionResourceCovered called"
            )
            _uiState.update {
                currentState.copy(
                    promptConfirmTransaction = true,
                    isLoading = false,
                    buttonEnabled = false
                )
            }
        }


        override suspend fun requestTransaction(): Result<ResourceProviderResponse> {
            return executeProposalUseCase.requestExecuteProposal(
                blockchainType,
                proposerAccountName,
                proposalName,
                _permissionExecuted,
                _accountExecuted
            )
        }

        override fun onDismissTransactionFeeBreakdown() {
            val currentState =
                (_uiState.value as? MyProposalDetailScreenUiState.Loaded) ?: return

            _uiState.update {
                currentState.copy(
                    resourceRequiredBreakdown = null,
                    resourceRequiredTotal = null,
                    isLoading = false,
                    buttonEnabled = true
                )
            }
        }

        override fun onPinPromptShown() {
            val currentState = _uiState.value as? MyProposalDetailScreenUiState.Loaded ?: return

            _uiState.update {
                currentState.copy(
                    promptConfirmTransaction = false,
                    resourceRequiredBreakdown = null,
                    resourceRequiredTotal = null,
                    buttonEnabled = true
                )
            }
        }

        override fun onConfirmResourceProviderFee() {
            val currentState = _uiState.value as? MyProposalDetailScreenUiState.Loaded ?: return

            _uiState.update {
                currentState.copy(promptConfirmTransaction = true, isLoading = true)
            }
        }

        override suspend fun pushTransactionWithoutResourceProvider(): Result<String> {
            return executeProposalUseCase.pushExecuteProposal(
                blockchainType,
                proposerAccountName,
                proposalName,
                _permissionExecuted,
                _accountExecuted
            )
        }

        override fun showLoadingState() {
            val currentState = _uiState.value as? MyProposalDetailScreenUiState.Loaded ?: return

            _uiState.update {
                currentState.copy(isLoading = true, buttonEnabled = false)
            }
        }

        override fun onPushTransactionSuccess(txHash: String) {
            _uiState.update {
                MyProposalDetailScreenUiState.Success(EXECUTE_PROPOSAL_ACTION)
            }
        }

        override fun onPushTransactionFail(throwable: Throwable) {
            val currentState = _uiState.value as? MyProposalDetailScreenUiState.Loaded ?: return

            _uiState.update {
                currentState.copy(
                    error = throwable.message,
                    isLoading = false,
                    buttonEnabled = true
                )
            }
        }

        override var resourceProviderResponse: ResourceProviderResponse? = null
        override val transactUseCase: BaseTransactUseCase = executeProposalUseCase
        override val coroutineScope: CoroutineScope = screenModelScope
        override var blockchainUid: String = ""
            get() = this@MyProposalDetailScreenModel.blockchainType.uid


    }

}