package com.mangala.wallet.features.chains.antelope.presentation.multisig.proposal.create.action

import androidx.compose.ui.util.fastAny
import cafe.adriel.voyager.core.model.screenModelScope
import com.mangala.antelope.base.api.model.GetCurrencyStatsRequest
import com.mangala.antelope.base.domain.usecase.GetCurrencyStatsUseCase
import com.mangala.wallet.antelope_balance.BalanceFormatter
import com.mangala.wallet.domain.datastore.usecases.GetSelectedNetworkUseCase
import com.mangala.wallet.domain.token.price.usecases.GetNativeCoinUseCase
import com.mangala.wallet.features.chains.antelope.presentation.multisig.proposal.create.getNameErrorMessage
import com.mangala.wallet.features.chains.antelope.presentation.multisig.proposal.create.validateNameGetErrorMessage
import com.mangala.wallet.features.chains.antelope_base.domain.CONTRACT_EOSIO
import com.mangala.wallet.features.chains.antelope_base.domain.model.actions.abis.FlattenedActionFields
import com.mangala.wallet.features.chains.antelope_base.domain.model.multisig.MultisigAction
import com.mangala.wallet.features.chains.antelope_base.domain.model.multisig.MultisigActionAuthorization
import com.mangala.wallet.features.chains.antelope_base.domain.model.proposal.ActionAbi
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.account.GetAccountInfoUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.account.GetAccountsByQueryUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.actions.GetActionAbi
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.actions.GetActionAbiByContractAndActionNameUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.actions.GetActionsByContractUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.actions.GetFlattenedActionAbiArrayElementUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.multisigs.proposal.GetContractNamesUseCase
import com.mangala.wallet.features.chains.antelope_base.presentation.utils.AntelopeDataFieldValidationUtils
import com.mangala.wallet.features.chains.antelope_base.presentation.utils.AntelopeDataFieldValidator
import com.mangala.wallet.model.blockchain.BlockchainNetworkData
import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.ui.utils.WrappedStringResource
import com.mangala.wallet.ui.utils.screenmodel.BaseScreenModel
import com.mangala.wallet.utils.ext.format
import com.mangala.wallet.utils.ext.formatWithThousandSeparator
import com.mangala.wallet.utils.formatAmountInput
import com.memtrip.eos.chain.actions.transaction.account.actions.AntelopePrimitiveDataTypes
import com.memtrip.eos.chain.actions.transaction.account.actions.AntelopePrimitiveDataTypes.Companion.ASSET_TYPE_DELIMITER
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

class MultisigProposalActionScreenModel(
    actionData: MultisigAction?,
    private val getSelectedNetworkUseCase: GetSelectedNetworkUseCase,
    private val getAccountsByQueryUseCase: GetAccountsByQueryUseCase,
    private val getContractNamesUseCase: GetContractNamesUseCase,
    private val getActionsByContractUseCase: GetActionsByContractUseCase,
    private val getActionAbiByContractAndActionNameUseCase: GetActionAbiByContractAndActionNameUseCase,
    private val getActionAbi: GetActionAbi,
    private val getFlattenedActionAbiArrayElementUseCase: GetFlattenedActionAbiArrayElementUseCase,
    private val getAccountInfoUseCase: GetAccountInfoUseCase,
    private val getCurrencyStatsUseCase: GetCurrencyStatsUseCase,
    private val getNativeCoinUseCase: GetNativeCoinUseCase
) : BaseScreenModel() {

    private val _uiState = MutableStateFlow(
        MultisigProposalActionUiModel(
            contractNameFilter = actionData?.contractName.orEmpty(),
            contractNames = emptyList(),
            contractNameSuggestionsLoading = false,
            contractNameError = actionData?.contractName?.validateNameGetErrorMessage(strictLengthValidation = false),
            actionNameFilter = actionData?.actionName.orEmpty(),
            actionNames = emptyList(),
            actionNamesError = null,
            actionNameLoading = false,
            dataFields = actionData?.fields?.actionList?.map { ActionDataFieldUiModel(it, symbolDecimals = it.symbolDecimals) }.orEmpty(),
            dataFieldParentIndexMapping = actionData?.fields?.actionMapIndex.orEmpty(),
            dataFieldsLoading = false,
            authorizations = actionData?.authorizations ?: listOf(
                MultisigActionAuthorization(
                    authorizationName = "",
                    authorizationNameSuggestions = emptyList(),
                    permissionName = "",
                    account = null
                )
            ), // Minimum of 1 item
            dataFieldsLoaded = false
        )
    )
    val uiState = _uiState.asStateFlow()

    private var fetchContractActionsJob: Job? = null
    private var fetchActionsJob: Job? = null
    private var getAuthorizationJob: Job? = null
    private var queryAccountSuggestionsJob: Job? = null
    private var fetchCurrencyStatsJob: Job? = null

    private var network: BlockchainNetworkData? = null

    init {
        screenModelScope.launch {
            network = getSelectedNetworkUseCase()

            val contracts = getContractNamesUseCase().orEmpty()

            _uiState.update {
                it.copy(contractNames = contracts)
            }
        }
    }

    fun onUpdateContractFilter(
        contractName: String,
        isChoosingFromSuggestion: Boolean = false
    ) { // TODO: If is choosing from suggestion then skips the debounce and suggestion load
        val blockchainType = network?.blockchainType
        if (_uiState.value.contractNameFilter == contractName || blockchainType == null) return

        val trimmedContractName =
            if (contractName.length > AntelopeDataFieldValidator.NameValidator.MAX_NAME_LENGTH_STRICT) {
                contractName.trim()
                    .take(AntelopeDataFieldValidator.NameValidator.MAX_NAME_LENGTH_STRICT)
            } else {
                contractName.trim()
            }
        val contractNameError = trimmedContractName.validateNameGetErrorMessage(strictLengthValidation = true)

        _uiState.update {
            it.copy(
                contractNameFilter = trimmedContractName,
                contractNameSuggestionsLoading = contractNameError != null,
                contractNameError = trimmedContractName.validateNameGetErrorMessage(strictLengthValidation = true),
                contractNames = emptyList()
            )
        }

        queryAccountSuggestionsJob?.cancel()
        queryAccountSuggestionsJob = screenModelScope.launch {
            if (trimmedContractName.isBlank() || contractNameError != null) {
                _uiState.update {
                    it.copy(
                        actionNameFilter = "",
                        actionNames = emptyList(),
                        contractNames = emptyList(),
                        dataFields = emptyList(),
                        dataFieldParentIndexMapping = emptyList(),
                        contractNameSuggestionsLoading = false,
                        actionNamesError = null
                    )
                }
                return@launch
            }

            if (isChoosingFromSuggestion) {
                _uiState.update {
                    it.copy(
                        actionNameFilter = "",
                        actionNames = emptyList(),
                        contractNameSuggestionsLoading = false,
                        contractNameError = null,
                        actionNamesError = null
                    )
                }

                getAndShowContractActions(trimmedContractName)
                return@launch
            }

            delay(SMART_CONTRACT_NAME_SUGGESTION_DEBOUNCE)
            val accountsResponse = getAccountsByQueryUseCase(
                blockchainType = blockchainType,
                accountName = trimmedContractName,
                filterByPayer = false
            )
            val accounts = accountsResponse.getOrNull()
            val exception = accountsResponse.exceptionOrNull()

            val contractNameError = if (exception != null && exception !is CancellationException) {
                WrappedStringResource.StringRes(MR.strings.message_multisig_proposal_action_error_contract_name_load_error)
            } else if (accounts?.isEmpty() == true) {
                WrappedStringResource.StringRes(MR.strings.message_multisig_proposal_action_error_contract_name_not_exists)
            } else {
                null
            }

            _uiState.update {
                it.copy(
                    contractNames = accounts.orEmpty(),
                    actionNameFilter = "",
                    actionNames = emptyList(),
                    contractNameSuggestionsLoading = false,
                    contractNameError = contractNameError,
                    actionNamesError = null
                )
            }

            if (accounts?.contains(contractName) == true && contractNameError == null) {
                getAndShowContractActions(contractName)
            }
        }
    }

    private suspend fun getAndShowContractActions(
        contractName: String
    ) {
        _uiState.update {
            it.copy(
                actionNameLoading = true,
                actionNames = emptyList(),
                dataFields = emptyList(),
                dataFieldParentIndexMapping = emptyList(),
                actionNamesError = null
            )
        }
        val actionsResult = getActionsByContractUseCase(contractName, "", false)

        val actions = actionsResult.getOrNull() ?: run {
            _uiState.update {
                it.copy(
                    actionNameLoading = false,
                    actionNamesError = WrappedStringResource.StringRes(MR.strings.message_multisig_proposal_action_error_loading_action)
                )
            }
            return
        }

        val newActionNames = actions.map { it.actionName }
        _uiState.update {
            it.copy(
                actionNameFilter = "",
                actionNames = newActionNames,
                dataFields = emptyList(),
                dataFieldParentIndexMapping = emptyList(),
                actionNameLoading = false,
                actionNamesError = if (newActionNames.isEmpty()) WrappedStringResource.StringRes(MR.strings.message_multisig_proposal_action_no_action) else null
            )
        }
    }

    fun onUpdateActionFilter(actionName: String) {
        if (_uiState.value.actionNameFilter == actionName) return

        val trimmedActionName =
            if (actionName.length > AntelopeDataFieldValidator.NameValidator.MAX_NAME_LENGTH_STRICT) {
                actionName.trim().take(AntelopeDataFieldValidator.NameValidator.MAX_NAME_LENGTH_STRICT)
            } else {
                actionName.trim()
            }
        val actionNameInvalidInputError = trimmedActionName.validateNameGetErrorMessage(strictLengthValidation = true)
        val actionFilterHasError = actionNameInvalidInputError != null

        _uiState.update {
            it.copy(
                actionNameFilter = actionName,
                dataFieldsLoading = actionFilterHasError.not(),
                actionNamesError = actionNameInvalidInputError,
                dataFields = if (actionFilterHasError) emptyList() else it.dataFields
            )
        }

        if (actionFilterHasError) return

        fetchActionsJob?.cancel()
        fetchActionsJob = screenModelScope.launch {
            val currentState = _uiState.value
            val contractName = currentState.contractNameFilter

            if (actionName.isBlank()) {
                _uiState.update {
                    it.copy(
                        dataFields = emptyList(),
                        dataFieldParentIndexMapping = emptyList(),
                        dataFieldsLoading = false,
                        dataFieldsLoaded = false
                    )
                }
                return@launch
            }

            val actionAbi = getActionAbiByContractAndActionNameUseCase(
                contractName,
                actionName,
                false
            ).getOrNull()

            // TODO: Handle loading data error

            if (actionAbi == null) {
                _uiState.update { it.copy(dataFieldsLoading = false) }
                return@launch
            }

            val action = ActionAbi(account = contractName, "", emptyList(), null)

            getActionAbi(
                actionAbi = actionAbi,
                action = action
            ).let { actionAbiMap ->
                _uiState.update {
                    val networkUid = network?.blockchainType?.uid ?: return@launch
                    val nativeToken = getNativeCoinUseCase(networkUid)
                    val nativeTokenSymbol = getNativeCoinUseCase(networkUid).reference

                    val isNativeContract = currentState.contractNameFilter == CONTRACT_EOSIO

                    it.copy(
                        dataFields = actionAbiMap.actionMapField[action]?.map {
                            val shouldPrefillAssetSymbolName =
                                isNativeContract && it.baseType == AntelopePrimitiveDataTypes.ASSET.value

                            val dataField = if (shouldPrefillAssetSymbolName) {
                                it.copy(value = " $nativeTokenSymbol")
                            } else {
                                it
                            }

                            ActionDataFieldUiModel(
                                dataField,
                                symbolNameInputDisabled = shouldPrefillAssetSymbolName,
                                symbolDecimals = if (shouldPrefillAssetSymbolName) nativeToken.decimals?.toInt() else null
                            )
                        } ?: emptyList(),
                        dataFieldParentIndexMapping = actionAbiMap.actionMapIndex,
                        dataFieldsLoading = false,
                        dataFieldsLoaded = true
                    )
                }
            }
        }
    }

    fun onAddArrayElement(index: Int) {
        screenModelScope.launch {
            val currentState = _uiState.value
            val dataFields = currentState.dataFields
            val parentIndexMapping = currentState.dataFieldParentIndexMapping
            val parentAction = dataFields.getOrNull(index)

            parentAction?.let {
                val listToAdd = getFlattenedActionAbiArrayElementUseCase.addArrayElement(
                    actionBaseName = it.field.baseType,
                    accountName = it.field.accountName,
                    index,
                    it.field.level + 1
                )
                val actions = dataFields.toMutableList()
                actions.addAll(
                    index + 1,
                    listToAdd.actionList.map { ActionDataFieldUiModel(it) }
                ) // index + 1 to add after the parent

                val indicesBeforeInsertionIndex = parentIndexMapping.take(index + 1)
                val indicesAfterInsertionIndex =
                    parentIndexMapping.subList(index + 1, parentIndexMapping.size).toMutableList()
                indicesAfterInsertionIndex.forEachIndexed { i, oldIndex ->
                    if (indicesAfterInsertionIndex[i] > index) {
                        indicesAfterInsertionIndex[i] = oldIndex + listToAdd.actionList.size
                    }
                }
                val updatedIndices =
                    indicesBeforeInsertionIndex + listToAdd.actionMapIndex + indicesAfterInsertionIndex

                _uiState.update {
                    it.copy(
                        dataFields = actions,
                        dataFieldParentIndexMapping = updatedIndices
                    )
                }
            }
        }
    }

    fun onRemoveArrayElement(index: Int) {
        val currentState = _uiState.value
        val actionAbiList = currentState.dataFields
        val parentIndexMapping = currentState.dataFieldParentIndexMapping
        val parentAction = actionAbiList.getOrNull(index)

        if (parentAction?.field?.isArrayElement != true) return

        parentAction.let {
            val subfieldElementsCount = parentAction.field.totalSubfieldElementsCount

            val actionsBeforeInsertionIndex = actionAbiList.take(index)
            val actionsAfterInsertionIndex =
                actionAbiList.subList(index + 1 + subfieldElementsCount, actionAbiList.size)
            val updatedList = actionsBeforeInsertionIndex + actionsAfterInsertionIndex

            val indicesBeforeInsertionIndex = parentIndexMapping.take(index)
            val indicesAfterInsertionIndex = parentIndexMapping.subList(
                index + 1 + subfieldElementsCount,
                parentIndexMapping.size
            ).toMutableList()
            indicesAfterInsertionIndex.forEachIndexed { i, oldIndex ->
                if (indicesAfterInsertionIndex[i] > index) {
                    indicesAfterInsertionIndex[i] =
                        oldIndex - subfieldElementsCount - 1 // Removing the object tag as well
                }
            }
            val updatedIndices = indicesBeforeInsertionIndex + indicesAfterInsertionIndex

            _uiState.update {
                it.copy(
                    dataFields = updatedList,
                    dataFieldParentIndexMapping = updatedIndices
                )
            }
        }
    }

    fun onSetOptionalValue(index: Int) {
        screenModelScope.launch {
            val currentState = _uiState.value
            val dataFields = currentState.dataFields
            val parentIndexMapping = currentState.dataFieldParentIndexMapping
            val parentAction = dataFields.getOrNull(index)

            parentAction?.let {
                val actions = dataFields.toMutableList()
                if (it.field.isOptionalValueSet) return@let // Shouldn't add the optional value element again

                val listToAdd = getFlattenedActionAbiArrayElementUseCase.setOptionalValue(
                    actionBaseName = it.field.baseType,
                    accountName = it.field.accountName,
                    index,
                    it.field.level
                )
                actions[index] = it.copy(field = it.field.copy(isOptionalValueSet = true, totalSubfieldElementsCount = listToAdd.actionList.size))
                actions.addAll(
                    index + 1,
                    listToAdd.actionList.map { ActionDataFieldUiModel(it) }
                ) // index + 1 to add after the parent

                val indicesBeforeInsertionIndex = parentIndexMapping.take(index + 1)
                val indicesAfterInsertionIndex =
                    parentIndexMapping.subList(index + 1, parentIndexMapping.size).toMutableList()
                indicesAfterInsertionIndex.forEachIndexed { i, oldIndex ->
                    if (indicesAfterInsertionIndex[i] > index) {
                        indicesAfterInsertionIndex[i] = oldIndex + listToAdd.actionList.size
                    }
                }
                val updatedIndices =
                    indicesBeforeInsertionIndex + listToAdd.actionMapIndex + indicesAfterInsertionIndex

                _uiState.update {
                    it.copy(
                        dataFields = actions,
                        dataFieldParentIndexMapping = updatedIndices
                    )
                }
            }
        }
    }

    fun onSetVariantTypeIndex(index: Int, variantIndex: Int) {
        screenModelScope.launch {
            val currentState = _uiState.value
            val dataFields = currentState.dataFields
            val parentIndexMapping = currentState.dataFieldParentIndexMapping
            val parentAction = dataFields.getOrNull(index)

            if (parentAction?.field?.isVariant != true) return@launch
            if (parentAction.field.isOptional && parentAction.field.isOptionalValueSet.not()) return@launch
            if (parentAction.field.isExtension && parentAction.field.isOptionalValueSet.not()) return@launch

            val variantTypes = parentAction.field.fieldType.split(",")
            val selectedVariantType = variantTypes.getOrNull(variantIndex) ?: return@launch

            val variantItem = dataFields.getOrNull(index + 1) ?: return@launch
            if (variantItem.field.fieldType == selectedVariantType) return@launch

            val subfieldElementsCount = parentAction.field.totalSubfieldElementsCount

            val actionsBeforeInsertionIndex = dataFields.take(index + 1)
            val actionsAfterInsertionIndex =
                dataFields.subList(index + 2 + subfieldElementsCount, dataFields.size)
            val listToAdd = getFlattenedActionAbiArrayElementUseCase.getVariantElement(
                actionBaseName = selectedVariantType,
                accountName = parentAction.field.accountName,
                variantTypeIndex = variantIndex,
                index = index,
                level = parentAction.field.level
            )
            val updatedList = actionsBeforeInsertionIndex + listToAdd.actionList.map { ActionDataFieldUiModel(it) } + actionsAfterInsertionIndex

            val indicesBeforeInsertionIndex = parentIndexMapping.take(index)
            val indicesAfterInsertionIndex =
                parentIndexMapping.subList(index + 1, parentIndexMapping.size).toMutableList()
            indicesAfterInsertionIndex.forEachIndexed { i, oldIndex ->
                if (indicesAfterInsertionIndex[i] > index) {
                    indicesAfterInsertionIndex[i] = oldIndex + listToAdd.actionList.size
                }
            }
            val updatedIndices = indicesBeforeInsertionIndex + listToAdd.actionMapIndex + indicesAfterInsertionIndex

            _uiState.update {
                it.copy(
                    dataFields = updatedList,
                    dataFieldParentIndexMapping = updatedIndices
                )
            }
        }
    }

    fun onUnsetOptionalValue(index: Int) {
        val currentState = _uiState.value
        val actionAbiList = currentState.dataFields
        val parentIndexMapping = currentState.dataFieldParentIndexMapping
        val parentAction = actionAbiList.getOrNull(index)

        if (parentAction?.field?.isOptional != true && parentAction?.field?.isExtension != true) return
        if (parentAction.field.isOptionalValueSet.not()) return

        parentAction.let {
            val subfieldElementsCount = parentAction.field.totalSubfieldElementsCount

            val actionsBeforeInsertionIndex = actionAbiList.take(index + 1).toMutableList() // index + 1 to exclude the tag itself from removal
            actionsBeforeInsertionIndex[actionsBeforeInsertionIndex.lastIndex] = actionsBeforeInsertionIndex[actionsBeforeInsertionIndex.lastIndex].copy(
                field = actionsBeforeInsertionIndex[actionsBeforeInsertionIndex.lastIndex].field.copy(isOptionalValueSet = false)
            )

            val actionsAfterInsertionIndex =
                actionAbiList.subList(index + 1 + subfieldElementsCount, actionAbiList.size)
            val updatedList = actionsBeforeInsertionIndex + actionsAfterInsertionIndex

            val indicesBeforeInsertionIndex = parentIndexMapping.take(index + 1) // index + 1 to exclude the tag itself from removal
            val indicesAfterInsertionIndex = parentIndexMapping.subList(
                index + 1 + subfieldElementsCount,
                parentIndexMapping.size
            ).toMutableList()
            indicesAfterInsertionIndex.forEachIndexed { i, oldIndex ->
                if (indicesAfterInsertionIndex[i] > index) {
                    indicesAfterInsertionIndex[i] =
                        oldIndex - subfieldElementsCount
                }
            }
            val updatedIndices = indicesBeforeInsertionIndex + indicesAfterInsertionIndex

            _uiState.update {
                it.copy(
                    dataFields = updatedList,
                    dataFieldParentIndexMapping = updatedIndices
                )
            }
        }
    }

    fun onUpdateTimestampFieldValue(index: Int, value: Long) {
        val currentState = _uiState.value
        val item = currentState.dataFields.getOrNull(index) ?: return

        val dataType = AntelopePrimitiveDataTypes.fromValue(item.field.baseType) ?: return

        val valueString = when (dataType) {
            AntelopePrimitiveDataTypes.TIME_POINT -> {
                (value * 1000).toString()
            }
            AntelopePrimitiveDataTypes.TIME_POINT_SEC -> {
                (value / 1000).toString()
            }
            AntelopePrimitiveDataTypes.BLOCK_TIMESTAMP_TYPE -> {
                // https://github.com/wharfkit/antelope/blob/a22e2829638c82dfa463d82ced066820b182d5db/src/chain/time.ts#L152
                val calculatedValue = (value - 946684800000).toDouble() / 500
                (calculatedValue.roundToInt()).toString()
            }
            else -> {
                return
            }
        }

        onUpdateFieldValue(index, valueString)
    }

    fun onUpdateFieldValue(index: Int, value: String) {
        val currentState = _uiState.value
        val actionAbiList = currentState.dataFields.toMutableList()

        if (index !in actionAbiList.indices) return

        val fieldBeingUpdated = actionAbiList[index]
        val originalValue = fieldBeingUpdated.field.value

        val dataType = AntelopePrimitiveDataTypes.fromValue(fieldBeingUpdated.field.baseType)
            ?: AntelopePrimitiveDataTypes.STRING

        val trimmedValue = when (dataType) {
            AntelopePrimitiveDataTypes.EXTENDED_ASSET,
            AntelopePrimitiveDataTypes.STRING -> value

            AntelopePrimitiveDataTypes.ASSET -> {
                val originalValueParts = originalValue.split(ASSET_TYPE_DELIMITER)
                val originalNumberPart = originalValueParts.getOrNull(0)?.trim().orEmpty()

                val parts = value.split(ASSET_TYPE_DELIMITER)
                val numberPart = parts.getOrNull(0)?.trim().orEmpty()
                val symbolPart = parts.getOrNull(1)?.trim().orEmpty()

                numberPart.formatAmountInput(
                    oldValue = originalNumberPart,
                    maxDecimals = fieldBeingUpdated.symbolDecimals ?: Int.MAX_VALUE
                ) + ASSET_TYPE_DELIMITER + symbolPart
            }

            AntelopePrimitiveDataTypes.NAME -> {
                value.trim().take(AntelopeDataFieldValidator.NameValidator.MAX_NAME_LENGTH)
            }

            else -> {
                value.trim()
            }
        }

        val errorMessage: InputFieldError? = getErrorMessage(dataType, fieldBeingUpdated, trimmedValue)

        actionAbiList[index] = fieldBeingUpdated.copy(
            field = fieldBeingUpdated.field.copy(value = trimmedValue),
            errorMessage = errorMessage
        )

        _uiState.update {
            it.copy(dataFields = actionAbiList.toList())
        }

        if (currentState.contractNameFilter != CONTRACT_EOSIO && dataType == AntelopePrimitiveDataTypes.ASSET) {
            val originalAssetSymbolValue = originalValue.split(ASSET_TYPE_DELIMITER).getOrNull(1)?.trim()
            val newAssetSymbolValue = trimmedValue.split(ASSET_TYPE_DELIMITER).getOrNull(1)?.trim()
            val hasAssetSymbolChanged = originalAssetSymbolValue != newAssetSymbolValue

            if (hasAssetSymbolChanged.not() || newAssetSymbolValue.isNullOrBlank()) return

            fetchCurrencyStatsJob?.cancel()
            fetchCurrencyStatsJob = screenModelScope.launch {
                val updatedActionAbiList = _uiState.value.dataFields.toMutableList()
                val updatedAction = updatedActionAbiList.getOrNull(index) ?: return@launch

                updatedActionAbiList[index] = updatedAction.copy(
                    symbolInfoLoading = true,
                    symbolDecimals = null
                )
                _uiState.update {
                    it.copy(dataFields = updatedActionAbiList.toList())
                }

                delay(ASSET_FETCH_DEBOUNCE)
                val blockchainType = network?.blockchainType ?: return@launch
                val currencyStats = getCurrencyStatsUseCase(
                    blockchainType = blockchainType,
                    request = GetCurrencyStatsRequest(
                        code = _uiState.value.contractNameFilter,
                        symbol = newAssetSymbolValue
                    ),
                ).getOrNull()

                if (currencyStats == null) {
                    updatedActionAbiList[index] = updatedAction.copy(
                        symbolDecimals = null,
                        symbolInfoLoading = false,
                        errorMessage = InputFieldError.TwoFieldsInput(
                            (updatedAction.errorMessage as? InputFieldError.TwoFieldsInput)?.firstFieldErrorMessage,
                            WrappedStringResource.StringRes(MR.strings.message_multisig_proposal_action_name_asset_symbol_not_found)
                        )
                    )

                    _uiState.update {
                        it.copy(dataFields = updatedActionAbiList.toList())
                    }
                    return@launch
                }

                val symbolDecimals =
                    BalanceFormatter.deserializeOrNull(currencyStats.maxSupply.orEmpty())?.precision

                val updatedErrorMessage = getErrorMessage(
                    dataType,
                    updatedAction,
                    updatedAction.field.value,
                    symbolDecimals
                )

                val originalValueParts = originalValue.split(ASSET_TYPE_DELIMITER)
                val originalNumberPart = originalValueParts.getOrNull(0)?.trim().orEmpty()

                val parts = value.split(ASSET_TYPE_DELIMITER)
                val numberPart = parts.getOrNull(0)?.trim().orEmpty()
                val symbolPart = parts.getOrNull(1)?.trim().orEmpty()

                val newValue = numberPart.formatAmountInput(
                    oldValue = originalNumberPart,
                    maxDecimals = symbolDecimals ?: Int.MAX_VALUE
                ) + ASSET_TYPE_DELIMITER + symbolPart

                updatedActionAbiList[index] = updatedAction.copy(
                    field = updatedAction.field.copy(value = newValue),
                    symbolDecimals = symbolDecimals,
                    symbolInfoLoading = false,
                    errorMessage = updatedErrorMessage
                )

                _uiState.update {
                    it.copy(dataFields = updatedActionAbiList.toList())
                }
            }
        }
    }

    fun onAddAuthorization() {
        _uiState.update {
            val oldList = it.authorizations.toMutableList()
            oldList.add(
                MultisigActionAuthorization(
                    authorizationName = "",
                    authorizationNameSuggestions = emptyList(),
                    permissionName = "",
                    account = null
                )
            )

            it.copy(authorizations = oldList.toList())
        }
    }

    fun onRemoveAuthorization(index: Int) {
        _uiState.update {
            val oldList = it.authorizations.toMutableList()

            if (oldList.size <= 1) {
                it
            } else {
                oldList.removeAt(index)

                it.copy(authorizations = oldList.toList())
            }
        }
    }

    fun onAuthorizationNameChange(index: Int, value: String, isChoosingFromSuggestion: Boolean) {
        getAuthorizationJob?.cancel()

        val trimmedAuthorizationName =
            if (value.length > AntelopeDataFieldValidator.NameValidator.MAX_NAME_LENGTH_STRICT) {
                value.trim().take(AntelopeDataFieldValidator.NameValidator.MAX_NAME_LENGTH_STRICT)
            } else {
                value.trim()
            }
        val authorizationNameInvalidInputError = trimmedAuthorizationName.validateNameGetErrorMessage(strictLengthValidation = true)

        _uiState.update {
            val oldList = it.authorizations.toMutableList()
            val oldItem = oldList.getOrNull(index) ?: return@update it

            oldList[index] = oldItem.copy(
                authorizationName = trimmedAuthorizationName,
                authorizationNameSuggestions = emptyList(),
                authorizationNameSuggestionsLoading = isChoosingFromSuggestion.not() && authorizationNameInvalidInputError == null && trimmedAuthorizationName.isNotBlank(),
                authorizationNameError = authorizationNameInvalidInputError,
                account = null,
                permissionName = ""
            )

            it.copy(authorizations = oldList.toList())
        }

        if (authorizationNameInvalidInputError != null) return

        if (isChoosingFromSuggestion.not()) {
            getAuthorizationJob = screenModelScope.launch {
                delay(AUTHORIZATION_THRESHOLD_FETCH_DEBOUNCE)
                val blockchainType = network?.blockchainType ?: return@launch
                val accountsResult = getAccountsByQueryUseCase(
                    blockchainType = blockchainType,
                    accountName = trimmedAuthorizationName,
                    filterByPayer = false
                )

                val accounts = accountsResult.getOrNull()
                val accountsResultException = accountsResult.exceptionOrNull()

                if (accounts?.size == 1 && accounts.firstOrNull() == trimmedAuthorizationName) {
                    _uiState.update {
                        val oldList = it.authorizations.toMutableList()
                        val oldItem = oldList.getOrNull(index) ?: return@update it

                        oldList[index] = oldItem.copy(
                            authorizationNameSuggestions = emptyList(),
                            authorizationNameSuggestionsLoading = false,
                            authorizationNameError = null
                        )

                        it.copy(authorizations = oldList.toList())
                    }

                    fetchAccountInfoAndUpdate(accountName = trimmedAuthorizationName, authorizationIndex = index)
                    return@launch
                }

                _uiState.update {
                    val oldList = it.authorizations.toMutableList()
                    val oldItem = oldList.getOrNull(index) ?: return@update it

                    val authorizationNameLoadingError =
                        if (accountsResultException != null && accountsResultException !is CancellationException) {
                            WrappedStringResource.StringRes(MR.strings.message_multisig_proposal_action_error_authorization_name_load_error)
                        } else if (accounts?.isEmpty() == true) {
                            WrappedStringResource.StringRes(MR.strings.message_multisig_proposal_action_error_authorization_no_authorization)
                        } else {
                            null
                        }

                    oldList[index] = oldItem.copy(
                        authorizationNameSuggestions = accounts.orEmpty(),
                        authorizationNameSuggestionsLoading = false,
                        authorizationNameError = authorizationNameLoadingError
                    )

                    it.copy(authorizations = oldList.toList())
                }
            }
        } else {
            fetchAccountInfoAndUpdate(accountName = trimmedAuthorizationName, authorizationIndex = index)
            // TODO: We can validate by asking the user to select first before showing the permission text field
        }
    }

    fun onAuthorizationPermissionNameChange(
        index: Int,
        value: String,
        isChoosingFromSuggestion: Boolean
    ) {
        val trimmedAuthorizationPermissionName =
            if (value.length > AntelopeDataFieldValidator.NameValidator.MAX_NAME_LENGTH) {
                value.trim().take(AntelopeDataFieldValidator.NameValidator.MAX_NAME_LENGTH)
            } else {
                value.trim()
            }
        var authorizationNameInvalidInputError = trimmedAuthorizationPermissionName.validateNameGetErrorMessage()

        if (authorizationNameInvalidInputError == null) {
            if (value.isBlank()) {
                authorizationNameInvalidInputError = WrappedStringResource.StringRes(MR.strings.message_multisig_proposal_no_permission_name_not_found)
            } else {
                val authorizationAccount = _uiState.value.authorizations.getOrNull(index)?.account

                if (authorizationAccount != null && authorizationAccount.permissions.none { it.permissionType.permissionName == trimmedAuthorizationPermissionName }) {
                    authorizationNameInvalidInputError = WrappedStringResource.StringRes(
                        MR.strings.message_multisig_proposal_permission_name_not_found,
                        authorizationAccount.accountName,
                        trimmedAuthorizationPermissionName
                    )
                }
            }
        }

        _uiState.update {
            val oldList = it.authorizations.toMutableList()

            oldList[index] = oldList[index].copy(
                permissionName = trimmedAuthorizationPermissionName,
                accountLoadingError = authorizationNameInvalidInputError
            )

            it.copy(
                authorizations = oldList.toList()
            )
        }
    }

    fun reconstructMultisigActionData(): MultisigAction {
        val localUiState = _uiState.value

        return MultisigAction(
            contractName = localUiState.contractNameFilter,
            actionName = localUiState.actionNameFilter,
            fields = FlattenedActionFields(
                localUiState.dataFields.map {
                    if (it.field.baseType == AntelopePrimitiveDataTypes.ASSET.value) {
                        if (it.symbolDecimals == null) return@map it.field

                        return@map it.field.copy(symbolDecimals = it.symbolDecimals)
                    }

                    return@map it.field
                },
                localUiState.dataFieldParentIndexMapping
            ),
            authorizations = localUiState.authorizations
        )
    }

    private fun fetchAccountInfoAndUpdate(accountName: String, authorizationIndex: Int) {
        screenModelScope.launch {
            _uiState.update {
                val oldList = it.authorizations.toMutableList()
                val oldItem = oldList.getOrNull(authorizationIndex) ?: return@update it
                oldList[authorizationIndex] = oldItem.copy(
                    account = null,
                    accountLoading = true
                )

                it.copy(authorizations = oldList.toList())
            }
            // TODO: Memcache this
            val accountResult = getAccountInfoUseCase.withResult(accountName)
            val account = accountResult.getOrNull()
            val accountLoadingException = accountResult.exceptionOrNull()

            val accountLoadingError =
                if (accountLoadingException != null && accountLoadingException !is CancellationException) {
                    WrappedStringResource.StringRes(MR.strings.message_multisig_proposal_action_error_authorization_permission_loading_error)
                } else null

            _uiState.update {
                val oldList = it.authorizations.toMutableList()
                val oldItem = oldList.getOrNull(authorizationIndex) ?: return@update it
                oldList[authorizationIndex] = oldItem.copy(
                    account = account,
                    accountLoading = false,
                    accountLoadingError = accountLoadingError
                )

                it.copy(authorizations = oldList.toList())
            }
        }
    }

    private fun getErrorMessage(
        dataType: AntelopePrimitiveDataTypes,
        fieldBeingUpdated: ActionDataFieldUiModel,
        trimmedValue: String,
        precision: Int? = fieldBeingUpdated.symbolDecimals
    ): InputFieldError? {
        val validator = AntelopeDataFieldValidationUtils.getValidator(
            dataType,
            strictValidationForName = false,
            validPrecision = precision
        )
        val validationResult = if (trimmedValue.isEmpty()) Result.success(Unit) else validator.getValidationResult(trimmedValue)

        val errorMessage: InputFieldError? = if (validationResult.isFailure) {
            when (validator) {
                is AntelopeDataFieldValidator.AssetValidator -> {
                    when (val exception = validationResult.exceptionOrNull()) {
                        is AntelopeDataFieldValidator.DataFieldException.TwoFieldsException -> {
                            val firstExceptionMessage = exception.field1Exception?.resolveAssetValidatorErrorMessage(precision)
                            val secondExceptionMessage = exception.field2Exception?.resolveAssetValidatorErrorMessage(precision)

                            InputFieldError.TwoFieldsInput(
                                firstExceptionMessage,
                                secondExceptionMessage
                            )
                        }

                        else -> InputFieldError.SingleInput(
                            WrappedStringResource.StringRes(MR.strings.message_multisig_proposal_action_name_invalid_asset)
                        )
                    }
                }

                AntelopeDataFieldValidator.BoolValidator -> null // Boolean are inputted via dropdown, so no need for validation
                is AntelopeDataFieldValidator.DecimalNumberValidator -> {
                    InputFieldError.SingleInput(
                        WrappedStringResource.StringRes(
                            MR.strings.message_multisig_proposal_action_name_invalid_number,
                            validator.minValue.format(),
                            validator.maxValue.format()
                        )
                    )
                }

                is AntelopeDataFieldValidator.HexadecimalStringValidator -> {
                    val bytesLength = validator.bytesLength

                    if (bytesLength != null) {
                        InputFieldError.SingleInput(
                            WrappedStringResource.StringRes(
                                MR.strings.message_multisig_proposal_action_name_invalid_hex_string_with_length,
                                bytesLength.toString(),
                                (bytesLength * 2).toString()
                            )
                        )
                    } else {
                        InputFieldError.SingleInput(
                            WrappedStringResource.StringRes(MR.strings.message_multisig_proposal_action_name_invalid_hex_string)
                        )
                    }
                }

                is AntelopeDataFieldValidator.IntegerNumberValidator -> {
                    InputFieldError.SingleInput(
                        WrappedStringResource.StringRes(
                            MR.strings.message_multisig_proposal_action_name_invalid_number,
                            validator.minValue.formatWithThousandSeparator(),
                            validator.maxValue.formatWithThousandSeparator()
                        )
                    )
                }

                is AntelopeDataFieldValidator.NameValidator -> {
                    val exception = validationResult.exceptionOrNull()

                    InputFieldError.SingleInput(
                        exception?.getNameErrorMessage(strictLengthValidation = false)
                    )
                }

                AntelopeDataFieldValidator.PublicKeyValidator -> {
                    InputFieldError.SingleInput(
                        WrappedStringResource.StringRes(MR.strings.message_multisig_proposal_action_name_invalid_public_key)
                    )
                }

                AntelopeDataFieldValidator.SignatureValidator -> {
                    InputFieldError.SingleInput(
                        WrappedStringResource.StringRes(MR.strings.message_multisig_proposal_action_name_invalid_signature)
                    )
                }

                AntelopeDataFieldValidator.StringValidator -> {
                    InputFieldError.SingleInput(
                        WrappedStringResource.StringRes(MR.strings.message_multisig_proposal_action_name_invalid_string)
                    )
                }

                AntelopeDataFieldValidator.SymbolCodeValidator -> {
                    InputFieldError.SingleInput(
                        WrappedStringResource.StringRes(MR.strings.message_multisig_proposal_action_name_invalid_asset_code)
                    )
                }

                AntelopeDataFieldValidator.SymbolValidator -> {
                    when (val exception = validationResult.exceptionOrNull()) {
                        is AntelopeDataFieldValidator.DataFieldException.TwoFieldsException -> {
                            val firstExceptionMessage = exception.field1Exception?.resolveSymbolValidatorErrorMessage()
                            val secondExceptionMessage = exception.field2Exception?.resolveSymbolValidatorErrorMessage()

                            InputFieldError.TwoFieldsInput(
                                firstExceptionMessage,
                                secondExceptionMessage
                            )
                        }

                        else -> InputFieldError.SingleInput(
                            WrappedStringResource.StringRes(MR.strings.message_multisig_proposal_action_name_invalid_symbol)
                        )
                    }
                }

                is AntelopeDataFieldValidator.ExtendedAssetValidator -> {
                    when (val exception = validationResult.exceptionOrNull()) {
                        is AntelopeDataFieldValidator.DataFieldException.ThreeFieldsException -> {
                            val firstExceptionMessage = exception.compoundFieldException?.field1Exception?.resolveExtendedAssetValidatorErrorMessage(precision)
                            val secondExceptionMessage = exception.compoundFieldException?.field2Exception?.resolveExtendedAssetValidatorErrorMessage(precision)
                            val thirdExceptionMessage = exception.additionalFieldException?.resolveExtendedAssetValidatorErrorMessage(precision)

                            InputFieldError.ThreeFieldsInput(
                                firstExceptionMessage,
                                secondExceptionMessage,
                                thirdExceptionMessage
                            )
                        }

                        else -> InputFieldError.ThreeFieldsInput(
                            WrappedStringResource.StringRes(MR.strings.message_multisig_proposal_action_name_invalid_asset),
                            WrappedStringResource.StringRes(MR.strings.message_multisig_proposal_action_name_invalid_asset),
                            WrappedStringResource.StringRes(MR.strings.message_multisig_proposal_action_name_invalid_asset),
                        )
                    }
                }

                AntelopeDataFieldValidator.ExtendedSymbolValidator -> {
                    when (val exception = validationResult.exceptionOrNull()) {
                        is AntelopeDataFieldValidator.DataFieldException.ThreeFieldsException -> {
                            val firstExceptionMessage = exception.compoundFieldException?.field1Exception?.resolveExtendedSymbolValidatorErrorMessage()
                            val secondExceptionMessage = exception.compoundFieldException?.field2Exception?.resolveExtendedSymbolValidatorErrorMessage()
                            val thirdExceptionMessage = exception.additionalFieldException?.resolveExtendedSymbolValidatorErrorMessage()

                            InputFieldError.ThreeFieldsInput(
                                firstExceptionMessage,
                                secondExceptionMessage,
                                thirdExceptionMessage
                            )
                        }

                        else -> InputFieldError.ThreeFieldsInput(
                            WrappedStringResource.StringRes(MR.strings.message_multisig_proposal_action_name_invalid_extended_symbol),
                            WrappedStringResource.StringRes(MR.strings.message_multisig_proposal_action_name_invalid_extended_symbol),
                            WrappedStringResource.StringRes(MR.strings.message_multisig_proposal_action_name_invalid_extended_symbol),
                        )
                    }
                }
            }
        } else {
            null
        }
        return errorMessage
    }

    private fun Throwable.resolveSymbolValidatorErrorMessage(): WrappedStringResource {
        return if (this is AntelopeDataFieldValidator.SymbolValidator.InvalidSymbolException) {
            when (this) {
                AntelopeDataFieldValidator.SymbolValidator.InvalidSymbolException.InvalidSymbolFormat -> {
                    WrappedStringResource.StringRes(MR.strings.message_multisig_proposal_action_name_invalid_symbol_format)
                }
                AntelopeDataFieldValidator.SymbolValidator.InvalidSymbolException.InvalidSymbolPrecision -> {
                    WrappedStringResource.StringRes(MR.strings.message_multisig_proposal_action_name_invalid_symbol_precision)
                }
                AntelopeDataFieldValidator.SymbolValidator.InvalidSymbolException.InvalidSymbolValue -> {
                    WrappedStringResource.StringRes(MR.strings.message_multisig_proposal_action_name_invalid_symbol_name)
                }
                AntelopeDataFieldValidator.SymbolValidator.InvalidSymbolException.MissingSymbolName -> {
                    WrappedStringResource.StringRes(MR.strings.message_multisig_proposal_action_name_missing_symbol_name)
                }
                AntelopeDataFieldValidator.SymbolValidator.InvalidSymbolException.MissingSymbolPrecision -> {
                    WrappedStringResource.StringRes(MR.strings.message_multisig_proposal_action_name_missing_symbol_precision)
                }
            }
        } else {
            return WrappedStringResource.StringRes(MR.strings.message_multisig_proposal_action_name_invalid_symbol)
        }
    }

    private fun Throwable.resolveAssetValidatorErrorMessage(precision: Int?): WrappedStringResource {
        return if (this is AntelopeDataFieldValidator.AssetValidator.InvalidAssetException) {
            when (this) {
                AntelopeDataFieldValidator.AssetValidator.InvalidAssetException.InvalidAssetFormat -> {
                    WrappedStringResource.StringRes(MR.strings.message_multisig_proposal_action_name_invalid_asset)
                }
                AntelopeDataFieldValidator.AssetValidator.InvalidAssetException.MissingAssetSymbol -> {
                    WrappedStringResource.StringRes(MR.strings.message_multisig_proposal_action_name_missing_asset_symbol)
                }
                AntelopeDataFieldValidator.AssetValidator.InvalidAssetException.MissingAssetValue -> {
                    WrappedStringResource.StringRes(MR.strings.message_multisig_proposal_action_name_missing_asset_value)
                }
                AntelopeDataFieldValidator.AssetValidator.InvalidAssetException.InvalidValueFormat -> {
                    WrappedStringResource.StringRes(MR.strings.message_multisig_proposal_action_name_invalid_asset_value_format)
                }
                AntelopeDataFieldValidator.AssetValidator.InvalidAssetException.InvalidValuePrecision -> {
                    WrappedStringResource.StringRes(MR.strings.message_multisig_proposal_action_name_invalid_asset_value_precision, precision.toString())
                }
                AntelopeDataFieldValidator.AssetValidator.InvalidAssetException.InvalidSymbol -> {
                    WrappedStringResource.StringRes(MR.strings.message_multisig_proposal_action_name_invalid_asset_symbol)
                }
            }
        } else {
            return WrappedStringResource.StringRes(MR.strings.message_multisig_proposal_action_name_invalid_asset)
        }
    }

    private fun Throwable.resolveExtendedSymbolValidatorErrorMessage(): WrappedStringResource {
        return when (this) {
            is AntelopeDataFieldValidator.ExtendedSymbolValidator.InvalidExtendedSymbolException -> {
                when (this) {
                    AntelopeDataFieldValidator.ExtendedSymbolValidator.InvalidExtendedSymbolException.MissingContractName -> {
                        WrappedStringResource.StringRes(MR.strings.message_multisig_proposal_action_name_missing_contract_name)
                    }
                }
            }

            is AntelopeDataFieldValidator.SymbolValidator.InvalidSymbolException -> {
                return this.resolveSymbolValidatorErrorMessage()
            }

            is AntelopeDataFieldValidator.NameValidator.InvalidNameException -> {
                return this.getNameErrorMessage(strictLengthValidation = true)
            }

            else -> {
                return WrappedStringResource.StringRes(MR.strings.message_multisig_proposal_action_name_invalid_extended_symbol)
            }
        }
    }

    private fun Throwable.resolveExtendedAssetValidatorErrorMessage(precision: Int?): WrappedStringResource {
        return when (this) {
            is AntelopeDataFieldValidator.ExtendedAssetValidator.InvalidExtendedAssetException -> {
                when (this) {
                    AntelopeDataFieldValidator.ExtendedAssetValidator.InvalidExtendedAssetException.MissingContractName -> {
                        WrappedStringResource.StringRes(MR.strings.message_multisig_proposal_action_name_missing_contract_name)
                    }
                }
            }

            is AntelopeDataFieldValidator.AssetValidator.InvalidAssetException -> {
                return this.resolveAssetValidatorErrorMessage(precision)
            }

            is AntelopeDataFieldValidator.NameValidator.InvalidNameException -> {
                return this.getNameErrorMessage(strictLengthValidation = true)
            }

            else -> {
                return WrappedStringResource.StringRes(MR.strings.message_multisig_proposal_action_name_invalid_asset)
            }
        }
    }

    companion object {
        private const val SMART_CONTRACT_NAME_SUGGESTION_DEBOUNCE = 300L
        private const val AUTHORIZATION_THRESHOLD_FETCH_DEBOUNCE = 300L
        private const val ASSET_FETCH_DEBOUNCE = 300L
    }
}