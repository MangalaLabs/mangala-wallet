package com.mangala.wallet.features.chains.antelope.presentation.multisig.proposal.create

import cafe.adriel.voyager.core.model.screenModelScope
import com.mangala.antelope.base.api.model.ChainException
import com.mangala.antelope.base.domain.model.ResourceProviderResponse
import com.mangala.wallet.domain.blockchain.usecases.GetBlockchainExplorerLinkUseCase
import com.mangala.wallet.domain.datastore.usecases.GetSelectedNetworkUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.model.multisig.MultisigAction
import com.mangala.wallet.features.chains.antelope_base.domain.model.multisig.MultisigActionAuthorization
import com.mangala.wallet.features.chains.antelope_base.domain.model.multisig.MultisigProposal
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.account.GetAccountPermissionsUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.account.GetAccountsUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.multisigs.proposal.CreateProposeTransactionUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.multisigs.proposal.DeleteProposalUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.multisigs.proposal.GetProposalDraftUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.multisigs.proposal.SaveNewProposalDraftUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.multisigs.proposal.UpdateProposalOnSubmitSuccessUseCase
import com.mangala.wallet.features.chains.antelope_base.presentation.BaseAntelopeTransactScreenModel
import com.mangala.wallet.features.chains.antelope_base.presentation.utils.AntelopeDataFieldValidationUtils
import com.mangala.wallet.features.chains.antelope_base.presentation.utils.AntelopeDataFieldValidator
import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.ui.utils.WrappedStringResource
import com.memtrip.eos.chain.actions.transaction.abi.TransactionAuthorizationAbi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.todayIn
import kotlin.time.Duration.Companion.days

class CreateNewProposalScreenModel(
    // Params from Screen
    private val proposalName: String?,
    private val accountName: String?,
    // Usecases
    private val getSelectedNetworkUseCase: GetSelectedNetworkUseCase,
    private val createProposeTransactionUseCase: CreateProposeTransactionUseCase,
    private val getBlockchainExplorerLinkUseCase: GetBlockchainExplorerLinkUseCase,
    private val saveNewProposalDraftUseCase: SaveNewProposalDraftUseCase,
    private val getProposalDraftUseCase: GetProposalDraftUseCase,
    private val getAccountsUseCase: GetAccountsUseCase,
    private val getAccountPermissionsUseCase: GetAccountPermissionsUseCase,
    private val deleteProposalUseCase: DeleteProposalUseCase,
    private val updateProposalOnSubmitSuccessUseCase: UpdateProposalOnSubmitSuccessUseCase
) : BaseAntelopeTransactScreenModel(
    transactUseCase = createProposeTransactionUseCase,
    blockchainUid = ""
) {

    private val _uiState = MutableStateFlow(
        CreateNewProposalUiModel(
            proposalName = proposalName.orEmpty(),
            proposalNameErrorMessage = proposalName?.validateNameGetErrorMessage(),
            expirationTimestamp = (Clock.System.now() + 10.days).toEpochMilliseconds(),
            minSelectableDate = getMinSelectableDateTimestamp(),
            proposerName = "",
            proposerPermissionName = "",
            actions = emptyList(),
            approvers = emptyMap(),
            isLoadedFromDraft = false,
            isDraftExists = false
        )
    )
    val uiState: StateFlow<CreateNewProposalUiModel> = _uiState.asStateFlow()

    val onSaveDone: Channel<Unit> = Channel()
    val onShouldPromptSaveDraft: Channel<Boolean> = Channel()

    private var loadedDraft: MultisigProposal? = null

    init {
        screenModelScope.launch {
            blockchainUid = getSelectedNetworkUseCase().blockChainUid

            if (proposalName != null && accountName != null) {
                // Load cached draft
                val data = getProposalDraftUseCase(
                    blockchainType = blockchainType,
                    proposalName = proposalName,
                    proposerAccountName = accountName
                )

                if (data != null) {
                    loadedDraft = data
                    _uiState.update {
                        it.copy(
                            proposalName = data.proposalName,
                            expirationTimestamp = data.expirationTimestamp,
                            proposerName = data.proposerName,
                            proposerPermissionName = data.proposerPermissionName,
                            actions = data.actions,
                            approvers = data.approvers,
                            isLoadedFromDraft = true
                        )
                    }
                }
            } else {
                val accounts = getAccountsUseCase(blockchainType = blockchainType)
                val defaultAccount = accountName ?: accounts.firstOrNull()?.accountName.orEmpty()
                val defaultPermission =
                    getAccountPermissionsUseCase(defaultAccount).firstOrNull()?.permissionType?.permissionName.orEmpty()

                // Setting default account so we always have key to save the draft in cache
                _uiState.update {
                    it.copy(
                        proposerName = defaultAccount,
                        proposerPermissionName = defaultPermission
                    )
                }
            }
        }
    }

    fun getActionByIndex(index: Int?): MultisigAction? {
        return index?.let { _uiState.value.actions.getOrNull(it) }
    }

    fun onProposalNameChange(proposalName: String) {
        val trimmedProposalName =
            if (proposalName.length > AntelopeDataFieldValidator.NameValidator.MAX_NAME_LENGTH) {
                proposalName.trim().take(AntelopeDataFieldValidator.NameValidator.MAX_NAME_LENGTH)
            } else {
                proposalName.trim()
            }

        _uiState.update {
            it.copy(
                proposalName = trimmedProposalName,
                proposalNameErrorMessage = trimmedProposalName.validateNameGetErrorMessage()
            )
        }
    }

    fun onSelectExpirationDate(expirationDateTimestamp: Long) {
        _uiState.update { it.copy(expirationTimestamp = expirationDateTimestamp) }
    }

    fun onUpdateProposerNameAndPermission(proposerName: String, proposerPermission: String) {
        _uiState.update {
            it.copy(
                proposerName = proposerName,
                proposerPermissionName = proposerPermission
            )
        }
    }

    fun onConfirmUpdateAction(actionIndex: Int?, action: MultisigAction) {
        _uiState.update {
            val currentList = it.actions.toMutableList()

            if (actionIndex != null) {
                currentList[actionIndex] = action
            } else {
                currentList.add(action)
            }

            // TODO: Update approvers, taking into account values updated by users

            it.copy(actions = currentList)
        }
    }

    fun onDeleteAction(actionIndex: Int?) {
        _uiState.update {
            val currentList = it.actions.toMutableList()

            if (actionIndex != null) {
                currentList.removeAt(actionIndex)
            }

            it.copy(actions = currentList)
        }
    }

    fun onConfirmUpdateApprovers(approvers: Map<MultisigActionAuthorization, List<MultisigActionAuthorization>>) {
        _uiState.update {
            it.copy(approvers = approvers)
        }
    }

    fun onConsumeTxHash() {
        _uiState.update {
            it.copy(txHash = null, blockExplorerUrl = null)
        }
    }

    fun onSaveDraft() {
        screenModelScope.launch {
            saveNewProposalDraftUseCase(
                proposalId = loadedDraft?.id,
                blockchainType = blockchainType,
                proposalName = _uiState.value.proposalName,
                expirationTimestamp = _uiState.value.expirationTimestamp,
                proposerAccountName = _uiState.value.proposerName,
                proposerAccountPermission = _uiState.value.proposerPermissionName,
                actions = _uiState.value.actions,
                approvers = _uiState.value.approvers
            )
            onSaveDone.trySend(Unit)
        }
    }

    fun shouldPromptSaveDraft(): Boolean {
        val uiModel = _uiState.value
        val loadedDraft = this.loadedDraft

        val (shouldPromptSaveDraft, isDraftExists) = with(uiModel) {
            if (loadedDraft != null) {
                val shouldPromptSave = loadedDraft.proposalName != proposalName ||
                        loadedDraft.expirationTimestamp != expirationTimestamp ||
                        loadedDraft.proposerName != proposerName ||
                        loadedDraft.proposerPermissionName != proposerPermissionName ||
                        loadedDraft.actions != actions ||
                        loadedDraft.approvers != approvers &&
                        txHash == null // Prevents navigation after transaction is submitted

                val isDraftWithSameNameAlreadyExists =
                    saveNewProposalDraftUseCase.isDraftProposalNameExists(
                        blockchainType = blockchainType,
                        proposalName = uiModel.proposalName,
                        proposerAccountName = uiModel.proposerName
                    ) && uiModel.proposalName != loadedDraft.proposalName

                shouldPromptSave to isDraftWithSameNameAlreadyExists
            } else {
                val shouldPromptSave =
                    (proposalName.isNotBlank() || actions.isNotEmpty() || approvers.isNotEmpty()) && txHash == null  // Prevents navigation after transaction is submitted

                val isDraftExists = saveNewProposalDraftUseCase.isDraftProposalNameExists(
                    blockchainType,
                    uiModel.proposalName,
                    uiModel.proposerName
                )

                shouldPromptSave to isDraftExists
            }
        }

        _uiState.update { it.copy(isDraftExists = isDraftExists) }

        return shouldPromptSaveDraft
    }

    fun onDeleteDraftProposal() {
        screenModelScope.launch {
            deleteProposalUseCase(
                blockchainType = blockchainType,
                proposalName = _uiState.value.proposalName,
                proposerAccountName = _uiState.value.proposerName
            )
            onSaveDone.trySend(Unit)
        }
    }

    fun onDismissTransactionError() {
        _uiState.update {
            it.copy(error = null)
        }
    }

    override fun onRequestTransactionFeeRequired(resourceProviderResponse: ResourceProviderResponse.FeeRequired) {
        _uiState.update {
            it.copy(
                resourceRequiredBreakdown = resourceProviderResponse.feeBreakdown,
                resourceRequiredTotal = resourceProviderResponse.fee,
                isLoading = false,
                transactionReadyToSubmit = false
            )
        }
    }

    override fun onRequestTransactionInvalidRequest() {
        _uiState.update {
            it.copy(
                isLoading = false,
                error = WrappedStringResource.StringRes(
                    MR.strings.message_create_new_proposal_error_unknown
                ),
                transactionReadyToSubmit = true
            )
        }
    }

    override fun onRequestTransactionResourceCovered() {
        _uiState.update {
            it.copy(
                promptConfirmTransaction = true,
                isLoading = false,
                transactionReadyToSubmit = false
            )
        }
    }

    override suspend fun requestTransaction(): Result<ResourceProviderResponse> {
        val uiModel = _uiState.value

        return createProposeTransactionUseCase.requestCreateProposal(
            blockchainType,
            uiModel.proposerName,
            uiModel.proposalName,
            convertToProposeRequestedAbi(uiModel.approvers),
            uiModel.proposerPermissionName,
            uiModel.actions,
            uiModel.expirationTimestamp
        )
    }

    override fun onDismissTransactionFeeBreakdown() {
        _uiState.update {
            it.copy(
                resourceRequiredBreakdown = null,
                resourceRequiredTotal = null,
                isLoading = false,
                transactionReadyToSubmit = true
            )
        }
    }

    override fun onPinPromptShown() {
        _uiState.update {
            it.copy(
                promptConfirmTransaction = false,
                resourceRequiredBreakdown = null,
                resourceRequiredTotal = null,
                transactionReadyToSubmit = true
            )
        }
    }

    override fun onConfirmResourceProviderFee() {
        _uiState.update {
            it.copy(promptConfirmTransaction = true, isLoading = true)
        }
    }

    override suspend fun pushTransactionWithoutResourceProvider(): Result<String> {
        val uiModel = _uiState.value

        return createProposeTransactionUseCase.pushCreateProposal(
            blockchainType,
            uiModel.proposerName,
            uiModel.proposalName,
            convertToProposeRequestedAbi(uiModel.approvers),
            uiModel.proposerPermissionName,
            uiModel.actions,
            uiModel.expirationTimestamp
        )
    }

    override fun showLoadingState() {
        _uiState.update {
            it.copy(isLoading = true, transactionReadyToSubmit = false)
        }
    }

    override fun onPushTransactionSuccess(txHash: String) {
        screenModelScope.launch {
            val currentState = _uiState.value

            updateProposalOnSubmitSuccessUseCase(
                blockchainType = blockchainType,
                proposalName = currentState.proposalName,
                draftProposal = loadedDraft,
                proposer = currentState.proposerName,
                expiresAt = currentState.expirationTimestamp,
                actions = currentState.actions
            )

            _uiState.update {
                val blockExplorerUrl = getBlockchainExplorerLinkUseCase.getTxLink(
                    blockchainUid = blockchainUid,
                    txHash = txHash
                )

                it.copy(txHash = txHash, blockExplorerUrl = blockExplorerUrl)
            }
        }
    }

    override fun onPushTransactionFail(throwable: Throwable) {
        _uiState.update {
            val errorMessage = if (throwable is ChainException) {
                when (val errorMessage =
                    throwable.chainError.error.details.firstOrNull()?.message) {
                    "assertion failure with message: proposal with the same name exists" -> WrappedStringResource.StringRes(
                        MR.strings.message_create_new_proposal_error_proposal_with_same_name,
                        it.proposalName
                    )

                    else -> WrappedStringResource.StringRes(
                        MR.strings.message_create_new_proposal_error,
                        throwable.chainError.error.details.joinToString("\n")
                    )
                }
            } else if (throwable is IllegalArgumentException) {
                WrappedStringResource.StringRes(
                    MR.strings.message_create_new_proposal_error_input
                )
            } else {
                WrappedStringResource.StringRes(
                    MR.strings.message_create_new_proposal_error_unknown
                )
            }

            it.copy(error = errorMessage, isLoading = false, transactionReadyToSubmit = true)
        }
    }

    private fun convertToProposeRequestedAbi(
        approvers: Map<MultisigActionAuthorization, List<MultisigActionAuthorization>>
    ): List<TransactionAuthorizationAbi> {
        // Flatten the map into a single list of TransactionAuthorizationAbi
        return approvers.flatMap { (key, values) ->
            values.map { value ->
                TransactionAuthorizationAbi(
                    actor = key.authorizationName,
                    permission = value.permissionName
                )
            }
        }
    }


    private fun getMinSelectableDateTimestamp(): Long {
        val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
        return today.atStartOfDayIn(TimeZone.UTC).toEpochMilliseconds()
    }
}