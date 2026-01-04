package com.mangala.wallet.features.chains.antelope.presentation.multisig.proposal.create.action

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.annotation.ExperimentalVoyagerApi
import cafe.adriel.voyager.koin.getNavigatorScreenModel
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.mangala.wallet.common.mokoresources.ColorsNew
import com.mangala.wallet.common.mokoresources.CornerRadius
import com.mangala.wallet.common.mokoresources.Dimensions
import com.mangala.wallet.common.mokoresources.Spacing
import com.mangala.wallet.common.mokoresources.icons.MangalaWalletPack
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.Clear
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.Delete
import com.mangala.wallet.features.chains.antelope.presentation.multisig.proposal.create.CreateNewProposalScreenModel
import com.mangala.wallet.features.chains.antelope.presentation.multisig.proposal.ui.DateTimePickerModalBottomSheet
import com.mangala.wallet.features.chains.antelope.presentation.multisig.proposal.ui.DeleteConfirmationDialog
import com.mangala.wallet.features.chains.antelope.presentation.multisig.proposal.ui.ProposalAuthorizationInput
import com.mangala.wallet.features.chains.antelope.presentation.multisig.proposal.ui.ProposalAutoCompleteTextInputField
import com.mangala.wallet.features.chains.antelope.presentation.multisig.proposal.ui.ProposalDropdownMenu
import com.mangala.wallet.features.chains.antelope.presentation.multisig.proposal.ui.ProposalInputLabel
import com.mangala.wallet.features.chains.antelope.presentation.multisig.proposal.ui.ProposalTextButton
import com.mangala.wallet.features.chains.antelope.presentation.multisig.proposal.ui.ProposalTextInputField
import com.mangala.wallet.features.chains.antelope.presentation.multisig.proposal.ui.ProposalTopAppBar
import com.mangala.wallet.features.chains.antelope_base.domain.model.actions.abis.AntelopeActionAbi
import com.mangala.wallet.features.chains.antelope_base.domain.model.multisig.MultisigActionAuthorization
import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.ui.component.MangalaGradientButton
import com.mangala.wallet.ui.component.MaxSizeColumn
import com.mangala.wallet.ui.component.MaxWidthColumn
import com.mangala.wallet.ui.component.MaxWidthRow
import com.mangala.wallet.ui.component.VerticalSpacer
import com.mangala.wallet.ui.theme.MangalaTypography
import com.mangala.wallet.ui.theme.mangalaColors
import com.mangala.wallet.ui.utils.collectAsStateMultiplatform
import com.mangala.wallet.ui.utils.resolve
import com.mangala.wallet.ui.utils.screenmodel.BaseScreen
import com.mangala.wallet.utils.analytics.MangalaAnalytics
import com.memtrip.eos.chain.actions.transaction.account.actions.AntelopePrimitiveDataTypes
import com.memtrip.eos.chain.actions.transaction.account.actions.AntelopePrimitiveDataTypes.Companion.ASSET_TYPE_DELIMITER
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.desc.desc
import dev.icerock.moko.resources.format
import kotlinx.coroutines.Dispatchers
import kotlinx.datetime.Clock
import org.koin.core.parameter.parametersOf

class MultisigProposalActionScreen(
    private val actionIndex: Int? = null // Null for create new, index for edit
) : BaseScreen<CreateNewProposalScreenModel>() {

    override val screenName: String = MangalaAnalytics.Screens.ANTELOPE_MULTISIG_PROPOSAL_ACTION
    override val screenClassName: String = MultisigProposalActionScreen::class.simpleName.orEmpty()

    override val isBottomBarVisible: Boolean = false

    @OptIn(ExperimentalVoyagerApi::class)
    @Composable
    override fun createScreenModel(): CreateNewProposalScreenModel {
        val navigator = LocalNavigator.currentOrThrow
        return navigator.getNavigatorScreenModel<CreateNewProposalScreenModel>()
    }

    @Composable
    override fun ScreenContent(screenModel: CreateNewProposalScreenModel) {
        val navigator = LocalNavigator.currentOrThrow
        val localScreenModel: MultisigProposalActionScreenModel = getScreenModel(
            parameters = {
                parametersOf(screenModel.getActionByIndex(actionIndex))
            }
        )

        val uiState by screenModel.uiState.collectAsStateMultiplatform(Dispatchers.Main.immediate)
        val localUiState by localScreenModel.uiState.collectAsStateMultiplatform(Dispatchers.Main.immediate)

        MultisigProposalActionScreen(
            localUiState,
            onBackPressed = {
                navigator.pop()
            },
            onUpdateContractFilter = { value: String, isChoosingFromSuggestion: Boolean ->
                localScreenModel.onUpdateContractFilter(value, isChoosingFromSuggestion)
            },
            onUpdateActionFilter = {
                localScreenModel.onUpdateActionFilter(it)
            },
            onAddArrayElement = {
                localScreenModel.onAddArrayElement(it)
            },
            onRemoveArrayElement = {
                localScreenModel.onRemoveArrayElement(it)
            },
            onSetOptionalValue = {
                localScreenModel.onSetOptionalValue(it)
            },
            onUnsetOptionalValue = {
                localScreenModel.onUnsetOptionalValue(it)
            },
            onUpdateFieldValue = { index: Int, value: String ->
                localScreenModel.onUpdateFieldValue(index, value)
            },
            onSetVariantTypeIndex = { index: Int, typeIndex: Int ->
                localScreenModel.onSetVariantTypeIndex(index, typeIndex)
            },
            onUpdateTimestampFieldValue = { index: Int, value: Long ->
                localScreenModel.onUpdateTimestampFieldValue(index, value)
            },
            onAuthorizationNameChange = { index: Int, value: String, isChoosingFromSuggestion: Boolean ->
                localScreenModel.onAuthorizationNameChange(index, value, isChoosingFromSuggestion)
            },
            onAuthorizationPermissionNameChange = { index: Int, value: String, isChoosingFromSuggestion: Boolean ->
                localScreenModel.onAuthorizationPermissionNameChange(
                    index,
                    value,
                    isChoosingFromSuggestion
                )
            },
            onAddAuthorization = {
                localScreenModel.onAddAuthorization()
            },
            onRemoveAuthorization = {
                localScreenModel.onRemoveAuthorization(it)
            },
            onConfirmUpdateAction = {
                screenModel.onConfirmUpdateAction(
                    actionIndex,
                    localScreenModel.reconstructMultisigActionData()
                )
                navigator.pop()
            },
            onDeleteAction = {
                screenModel.onDeleteAction(actionIndex)
                navigator.pop()
            }
        )
    }

    @Composable
    fun MultisigProposalActionScreen(
        localUiState: MultisigProposalActionUiModel,
        onBackPressed: () -> Unit,
        onUpdateContractFilter: (value: String, isChoosingFromSuggestion: Boolean) -> Unit,
        onUpdateActionFilter: (String) -> Unit,
        onAddArrayElement: (index: Int) -> Unit,
        onRemoveArrayElement: (index: Int) -> Unit,
        onSetOptionalValue: (index: Int) -> Unit,
        onUnsetOptionalValue: (index: Int) -> Unit,
        onUpdateFieldValue: (index: Int, value: String) -> Unit,
        onSetVariantTypeIndex: (index: Int, typeIndex: Int) -> Unit,
        onUpdateTimestampFieldValue: (index: Int, value: Long) -> Unit,
        onAddAuthorization: () -> Unit,
        onRemoveAuthorization: (index: Int) -> Unit,
        onAuthorizationNameChange: (index: Int, value: String, isChoosingFromSuggestion: Boolean) -> Unit,
        onAuthorizationPermissionNameChange: (index: Int, value: String, isChoosingFromSuggestion: Boolean) -> Unit,
        onConfirmUpdateAction: () -> Unit,
        onDeleteAction: () -> Unit
    ) {
        val keyboardController = LocalSoftwareKeyboardController.current
        val focusManager = LocalFocusManager.current
        var showDeleteConfirmation by remember { mutableStateOf(false) }
        var showDatePicker by remember { mutableStateOf<Pair<Int?, Long?>>(null to null) } // First: Index of field, second: timestamp

        if (showDatePicker.first != null) {
            DateTimePickerModalBottomSheet(
                initialTimestamp = showDatePicker.second ?: Clock.System.now().toEpochMilliseconds(),
                onSelectExpirationDate = {
                    val index = showDatePicker.first

                    if (index != null) {
                        onUpdateTimestampFieldValue(index, it)
                    }
                    showDatePicker = null to null
                },
                onDismissRequest = {
                    showDatePicker = null to null
                }
            )
        }

        if (showDeleteConfirmation) {
            DeleteActionConfirmationDialog(
                onDismiss = {
                    showDeleteConfirmation = false
                },
                onDeleteAction = onDeleteAction
            )
        }

        MaxSizeColumn(
            Modifier
                .background(MaterialTheme.mangalaColors.bg)
                .pointerInput(Unit) {
                    detectTapGestures {
                        keyboardController?.hide()
                        focusManager.clearFocus()
                    }
                },
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            ProposalTopAppBar(
                onBackPressed = onBackPressed,
                label = MR.strings.title_multisig_action.desc().localized(),
                trailingIcon = {
                    if (actionIndex != null) {
                        IconButton(
                            onClick = {
                                showDeleteConfirmation = true
                            }
                        ) {
                            Icon(
                                imageVector = MangalaWalletPack.Delete,
                                contentDescription = MR.strings.all_delete.desc().localized(),
                                modifier = Modifier.size(24.dp),
                                tint = ColorsNew.error_600
                            )
                        }
                    }
                }
            )

            LazyColumn(
                Modifier
                    .weight(1f)
                    .padding(horizontal = Dimensions.Padding.default),
            ) {
                item {
                    VerticalSpacer(Spacing.BASE)
                    ProposalAutoCompleteTextInputField(
                        label = MR.strings.label_multisig_action_smart_contract_name.desc()
                            .localized(),
                        placeholder = MR.strings.hint_multisig_action_smart_contract_name.desc()
                            .localized(),
                        query = localUiState.contractNameFilter,
                        suggestions = localUiState.contractNames,
                        onQueryChange = { value: String, isChoosingFromSuggestion: Boolean ->
                            onUpdateContractFilter(value, isChoosingFromSuggestion)
                        },
                        trailingIcon = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (localUiState.contractNameSuggestionsLoading) {
                                    CircularProgressIndicator(
                                        color = MaterialTheme.mangalaColors.iconPrimary,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                                if (localUiState.contractNameFilter.isNotBlank()) {
                                    IconButton(
                                        onClick = {
                                            onUpdateContractFilter("", false)
                                        }
                                    ) {
                                        Icon(
                                            imageVector = MangalaWalletPack.Clear,
                                            contentDescription = "Delete",
                                            modifier = Modifier.size(24.dp),
                                            tint = MaterialTheme.mangalaColors.iconPrimary
                                        )
                                    }
                                }
                            }
                        },
                        isError = localUiState.contractNameError != null,
                        errorText = localUiState.contractNameError?.resolve()
                    )
                    if (localUiState.contractNameFilter.isNotBlank()) {
                        VerticalSpacer(Spacing.SMALL)
                        ProposalAutoCompleteTextInputField(
                            label = MR.strings.label_multisig_action_action_name.desc().localized(),
                            placeholder = MR.strings.hint_multisig_action_action_name.desc()
                                .localized(),
                            query = localUiState.actionNameFilter,
                            suggestions = localUiState.filteredActionNames,
                            onQueryChange = { value: String, isChoosingFromSuggestion: Boolean ->
                                onUpdateActionFilter(value)
                            },
                            trailingIcon = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    if (localUiState.actionNameLoading) {
                                        CircularProgressIndicator(
                                            color = MaterialTheme.mangalaColors.iconPrimary,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                    if (localUiState.actionNameFilter.isNotBlank()) {
                                        IconButton(
                                            onClick = {
                                                onUpdateActionFilter("")
                                            }
                                        ) {
                                            Icon(
                                                imageVector = MangalaWalletPack.Clear,
                                                contentDescription = "Delete",
                                                modifier = Modifier.size(24.dp),
                                                tint = MaterialTheme.mangalaColors.iconPrimary
                                            )
                                        }
                                    }
                                }
                            },
                            isError = localUiState.actionNamesError != null,
                            errorText = localUiState.actionNamesError?.resolve()
                        )
                    }
                }
                if (localUiState.dataFields.isNotEmpty() || localUiState.dataFieldsLoading) {
                    ListSeparatorItem()
                }
                if (localUiState.dataFieldsLoading) {
                    item {
                        CircularProgressIndicator(
                            color = MaterialTheme.mangalaColors.iconPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
                itemsIndexed(localUiState.dataFields) { index, actionAbi ->
                    ActionDataField(
                        actionAbi,
                        isLastItem = index == localUiState.dataFields.size - 1,
                        onAddArrayElement = {
                            onAddArrayElement(index)
                        },
                        onRemoveArrayElement = {
                            onRemoveArrayElement(index)
                        },
                        onSetOptionalValue = {
                            onSetOptionalValue(index)
                        },
                        onUnsetOptionalValue = {
                            onUnsetOptionalValue(index)
                        },
                        onUpdateFieldValue = {
                            onUpdateFieldValue(index, it)
                        },
                        onShowDatePicker = {
                            showDatePicker = index to actionAbi.valueAsInstant?.toEpochMilliseconds()
                        },
                        onSetVariantTypeIndex = {
                            onSetVariantTypeIndex(index, it)
                        }
                    )
                }
                if (localUiState.dataFieldsLoaded || actionIndex != null) {
                    // Only shows the authorization section after user has selected an action
                    ListSeparatorItem()
                    item {
                        AuthorizationSectionHeader()
                        VerticalSpacer(Spacing.XSMALL)
                    }
                    itemsIndexed(localUiState.authorizations) { index, multisigActionAuthorization ->
                        AuthorizationItem(
                            multisigActionAuthorization,
                            shouldShowRemoveButton = localUiState.authorizations.size > 1,
                            onAuthorizationNameChange = { value, isChoosingFromSuggestion ->
                                onAuthorizationNameChange(index, value, isChoosingFromSuggestion)
                            },
                            onAuthorizationPermissionNameChange = { value, isChoosingFromSuggestion ->
                                onAuthorizationPermissionNameChange(
                                    index,
                                    value,
                                    isChoosingFromSuggestion
                                )
                            },
                            onRemoveAuthorization = {
                                onRemoveAuthorization(index)
                            },
                            isLastItem = index == localUiState.authorizations.size - 1
                        )
                        if (index != localUiState.authorizations.size - 1) {
                            VerticalSpacer(Spacing.XSMALL)
                        }
                    }
                    item {
                        VerticalSpacer(Spacing.SMALL)
                        ProposalTextButton(
                            onClick = {
                                onAddAuthorization()
                            },
                            text = "Add authorization"
                        )
                        VerticalSpacer(Spacing.SMALL)
                    }
                }
            }
            MaxWidthColumn(
                Modifier
                    .background(MaterialTheme.mangalaColors.bgInnerCard)
                    .navigationBarsPadding()
                    .imePadding()
                    .padding(horizontal = Dimensions.Padding.default)
            ) {
                VerticalSpacer(Spacing.SMALL)
                MangalaGradientButton(
                    label = MR.strings.all_done.desc().localized(),
                    onClick = {
                        onConfirmUpdateAction()
                    },
                    enabled = localUiState.buttonEnabled,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }

    private fun LazyListScope.ListSeparatorItem() {
        item {
            VerticalSpacer(Spacing.SMALL)
            HorizontalDivider(Modifier.fillMaxWidth(), 1.dp, MaterialTheme.mangalaColors.border)
            VerticalSpacer(Spacing.SMALL)
        }
    }

    @Composable
    private fun AuthorizationItem(
        authorization: MultisigActionAuthorization,
        shouldShowRemoveButton: Boolean = true,
        isLastItem: Boolean,
        onAuthorizationNameChange: (value: String, isChoosingFromSuggestion: Boolean) -> Unit,
        onAuthorizationPermissionNameChange: (value: String, isChoosingFromSuggestion: Boolean) -> Unit,
        onRemoveAuthorization: () -> Unit
    ) {
        ProposalAuthorizationInput(
            firstInputLabel = "Authorization name",
            firstInputValue = authorization.authorizationName,
            firstInputPlaceholder = "Enter authorization name",
            onFirstInputValueChange = { value, isChoosingFromSuggestion ->
                onAuthorizationNameChange(value, isChoosingFromSuggestion)
            },
            firstInputSuggestions = authorization.authorizationNameSuggestions,
            firstInputLoading = authorization.authorizationNameSuggestionsLoading,
            firstInputError = authorization.authorizationNameError,
            secondInputLabel = "Permission",
            secondInputValue = authorization.permissionName,
            secondInputPlaceholder = "Enter permission name",
            onSecondInputValueChange = { value, isChoosingFromSuggestion ->
                onAuthorizationPermissionNameChange(value, isChoosingFromSuggestion)
            },
            secondInputSuggestions = authorization.filteredPermissions.orEmpty(),
            subtitle = authorization.thresholdFormatted?.resolve(),
            shouldShowRemoveButton = shouldShowRemoveButton,
            onDelete = onRemoveAuthorization,
            isLastItem = isLastItem,
            secondInputError = authorization.accountLoadingError,
            secondInputLoading = authorization.accountLoading,
            secondInputVisible = authorization.account != null
        )
    }

    @OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
    @Composable
    private fun ActionDataField(
        uiModel: ActionDataFieldUiModel,
        isLastItem: Boolean,
        onAddArrayElement: () -> Unit,
        onRemoveArrayElement: () -> Unit,
        onSetOptionalValue: () -> Unit,
        onUnsetOptionalValue: () -> Unit,
        onUpdateFieldValue: (value: String) -> Unit,
        onShowDatePicker: () -> Unit,
        onSetVariantTypeIndex: (typeIndex: Int) -> Unit
    ) {
        val actionAbi = uiModel.field
        Column(modifier = Modifier.padding(start = Spacing.BASE.times(actionAbi.level))) {
            if (actionAbi.isPrimitive.not() || (actionAbi.isArrayElement && actionAbi.isBaseTypePrimitive)) {
                MaxWidthRow(horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(
                        text = "${actionAbi.fieldName} (${actionAbi.fieldType})",
                        style = MangalaTypography.Size13Medium(),
                        modifier = Modifier.weight(1f)
                    )
                    if (actionAbi.isArray) {
                        Text(
                            text = "Add new",
                            style = MangalaTypography.Size13Regular(),
                            modifier = Modifier.clickable {
                                onAddArrayElement()
                            }
                        )
                    } else if (actionAbi.isOptional || actionAbi.isExtension) {
                        if (actionAbi.isOptionalValueSet) {
                            Text(
                                text = "Unset value",
                                style = MangalaTypography.Size13Regular(),
                                color = ColorsNew.error_600,
                                modifier = Modifier.clickable {
                                    onUnsetOptionalValue()
                                }
                            )
                        } else {
                            Text(
                                text = "Set value",
                                style = MangalaTypography.Size13Regular(),
                                modifier = Modifier.clickable {
                                    onSetOptionalValue()
                                }
                            )
                        }
                    } else if (actionAbi.isArrayElement) {
                        Text(
                            text = "Delete",
                            style = MangalaTypography.Size13Regular(),
                            color = ColorsNew.error_600,
                            modifier = Modifier.clickable {
                                onRemoveArrayElement()
                            }
                        )
                    }
                }
            }

            if (actionAbi.isVariant) {
                var isVariantTypeChooserExpanded = remember { mutableStateOf(false) }

                ExposedDropdownMenuBox(
                    expanded = isVariantTypeChooserExpanded.value,
                    onExpandedChange = { isVariantTypeChooserExpanded.value = it }
                ) {
                    Row(
                        modifier = Modifier
                            .background(
                                color = MaterialTheme.mangalaColors.bgInnerCard,
                                shape = RoundedCornerShape(CornerRadius.Medium)
                            )
                            .padding(
                                horizontal = Dimensions.Padding.half,
                                vertical = Dimensions.Padding.quarter
                            )
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = actionAbi.fieldName,
                            style = MangalaTypography.Size13Medium()
                        )
                    }

                    ExposedDropdownMenu(
                        expanded = isVariantTypeChooserExpanded.value,
                        onDismissRequest = {
                            isVariantTypeChooserExpanded.value = false
                        },
                        containerColor = MaterialTheme.mangalaColors.bgInnerCard,
                        shape = RoundedCornerShape(CornerRadius.Medium),
                        matchTextFieldWidth = false,
                    ) {
                        actionAbi.variantTypes?.forEachIndexed { index, option ->
                            val onClickModifier = remember {
                                Modifier.clickable {
                                    isVariantTypeChooserExpanded.value = false
                                    onSetVariantTypeIndex(index)
                                }
                            }

                            Row(
                                modifier = onClickModifier
                                    .background(MaterialTheme.mangalaColors.bgInnerCard)
                                    .padding(horizontal = Dimensions.Padding.default, vertical = Dimensions.Padding.half)
                            ) {
                                Text(
                                    text = option,
                                    style = MangalaTypography.Size14Regular(),
                                    color = MaterialTheme.mangalaColors.textPrimary,
                                )
                            }
                        }
                    }
                }
            }

            if (actionAbi.isPrimitive) {
                when (actionAbi.baseType) {
                    AntelopePrimitiveDataTypes.ASSET.value -> {
                        AssetInputField(
                            uiModel = uiModel,
                            onUpdateFieldValue = onUpdateFieldValue
                        )
                    }

                    AntelopePrimitiveDataTypes.BOOL.value -> {
                        BoolInputDropdown(
                            uiModel = uiModel,
                            onUpdateFieldValue = onUpdateFieldValue
                        )
                    }

                    AntelopePrimitiveDataTypes.SYMBOL.value -> {
                        SymbolInputField(
                            uiModel = uiModel,
                            onUpdateFieldValue = onUpdateFieldValue
                        )
                    }

                    AntelopePrimitiveDataTypes.TIME_POINT_SEC.value,
                    AntelopePrimitiveDataTypes.TIME_POINT.value,
                    AntelopePrimitiveDataTypes.BLOCK_TIMESTAMP_TYPE.value -> {
                        DateTimeInputField(
                            uiModel = uiModel,
                            onClick = {
                                onShowDatePicker()
                            }
                        )
                    }

                    AntelopePrimitiveDataTypes.EXTENDED_ASSET.value -> {
                        ExtendedAssetInputField(
                            uiModel = uiModel,
                            onUpdateFieldValue = onUpdateFieldValue
                        )
                    }

                    AntelopePrimitiveDataTypes.EXTENDED_SYMBOL.value -> {
                        ExtendedSymbolInputField(
                            uiModel = uiModel,
                            onUpdateFieldValue = onUpdateFieldValue
                        )
                    }

                    else -> {
                        val errorMessage = uiModel.errorMessage as? InputFieldError.SingleInput

                        ProposalTextInputField(
                            label = if (actionAbi.isOptional.not()) {
                                MR.strings.message_multisig_proposal_field_name_header.format(
                                    actionAbi.fieldName,
                                    actionAbi.fieldType
                                ).localized()
                            } else {
                                MR.strings.message_multisig_proposal_field_name_header_optional.format(
                                    actionAbi.fieldName,
                                    actionAbi.fieldType
                                ).localized()
                            },
                            query = actionAbi.value,
                            onQueryChange = {
                                onUpdateFieldValue(it)
                            },
                            placeholder = actionAbi.getDataTypeHint(),
                            modifier = Modifier.fillMaxWidth(),
                            requiredInput = AntelopePrimitiveDataTypes.fromValue(actionAbi.baseType) != AntelopePrimitiveDataTypes.STRING,
                            keyboardOptions = KeyboardOptions.Default.copy(
                                capitalization = KeyboardCapitalization.None,
                                autoCorrectEnabled = false,
                                keyboardType = actionAbi.getKeyboardType(),
                                imeAction = ImeAction.Next
                            ),
                            isError = uiModel.errorMessage != null,
                            errorText = errorMessage?.errorMessage?.resolve()
                        )
                    }
                }
            }
        }
    }

    @Composable
    fun AuthorizationSectionHeader() {
        MaxWidthRow {
            Text(
                modifier = Modifier.weight(1f),
                text = MR.strings.label_proposals_detail_authorization.desc().localized(),
                style = MangalaTypography.Size14Medium(),
                color = MaterialTheme.mangalaColors.textPrimary
            )
        }
    }

    @Composable
    fun BoolInputDropdown(
        uiModel: ActionDataFieldUiModel,
        onUpdateFieldValue: (value: String) -> Unit
    ) {
        val actionAbi = uiModel.field

        ProposalDropdownMenu(
            label = "${actionAbi.fieldName} (${actionAbi.fieldType})",
            placeholder = actionAbi.getDataTypeHint(),
            value = if (actionAbi.value == true.toString()) true.toString() else false.toString(),
            items = listOf(
                true.toString(),
                false.toString()
            ),
            onValueChange = {
                if (it == true.toString()) {
                    onUpdateFieldValue(true.toString())
                } else {
                    onUpdateFieldValue(false.toString())
                }
            },
            requiredInput = true
        )
    }

    @Composable
    fun DateTimeInputField(
        uiModel: ActionDataFieldUiModel,
        onClick: () -> Unit
    ) {
        val actionAbi = uiModel.field

        ProposalTextInputField(
            label = "${actionAbi.fieldName} (${actionAbi.fieldType})",
            query = uiModel.valueAsFormattedLocalDateTime.orEmpty(),
            onQueryChange = {},
            placeholder = actionAbi.getDataTypeHint(),
            modifier = Modifier.clickable(onClick = onClick),
            enabled = false,
            requiredInput = true,
            keyboardOptions = KeyboardOptions.Default.copy(
                capitalization = KeyboardCapitalization.None,
                autoCorrectEnabled = false,
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next
            ),
            isError = uiModel.errorMessage != null,
            errorText = (uiModel.errorMessage as? InputFieldError.SingleInput)?.errorMessage?.resolve()
        )
    }

    @Composable
    fun ExtendedSymbolInputField(
        uiModel: ActionDataFieldUiModel,
        onUpdateFieldValue: (value: String) -> Unit
    ) {
        val actionAbi = uiModel.field
        val errorMessage = uiModel.errorMessage as? InputFieldError.ThreeFieldsInput

        MaxWidthColumn {
            ProposalInputLabel("${actionAbi.fieldName} (${actionAbi.fieldType})", true)
            MaxWidthRow(horizontalArrangement = Arrangement.spacedBy(Spacing.SMALL)) {
                ProposalTextInputField(
                    label = MR.strings.label_multisig_proposal_action_symbol_precision.desc().localized(),
                    query = actionAbi.value
                        .split(AntelopePrimitiveDataTypes.EXTENDED_TYPE_DELIMITER)
                        .getOrNull(0)
                        ?.split(AntelopePrimitiveDataTypes.SYMBOL_TYPE_DELIMITER)
                        ?.getOrNull(0).orEmpty(),
                    onQueryChange = {
                        onUpdateFieldValue(it + AntelopePrimitiveDataTypes.SYMBOL_TYPE_DELIMITER + actionAbi.value.split(AntelopePrimitiveDataTypes.EXTENDED_TYPE_DELIMITER).getOrNull(0)?.split(AntelopePrimitiveDataTypes.SYMBOL_TYPE_DELIMITER)?.getOrNull(1).orEmpty() + AntelopePrimitiveDataTypes.EXTENDED_TYPE_DELIMITER + actionAbi.value.split(AntelopePrimitiveDataTypes.EXTENDED_TYPE_DELIMITER).getOrNull(1).orEmpty())
                    },
                    placeholder = MR.strings.hint_multisig_proposal_action_symbol_precision.desc().localized(),
                    modifier = Modifier.fillMaxWidth(),
                    requiredInput = true,
                    keyboardOptions = KeyboardOptions.Default.copy(
                        capitalization = KeyboardCapitalization.None,
                        autoCorrectEnabled = false,
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next
                    ),
                    containerModifier = Modifier.weight(2f),
                    isError = uiModel.errorMessage != null,
                    errorText = errorMessage?.firstFieldErrorMessage?.resolve()
                )
                ProposalTextInputField(
                    label = MR.strings.label_multisig_proposal_action_symbol_name.desc().localized(),
                    query = actionAbi.value
                        .split(AntelopePrimitiveDataTypes.EXTENDED_TYPE_DELIMITER)
                        .getOrNull(0)
                        ?.split(AntelopePrimitiveDataTypes.SYMBOL_TYPE_DELIMITER)
                        ?.getOrNull(1)
                        .orEmpty(),
                    onQueryChange = {
                        onUpdateFieldValue(actionAbi.value.split(AntelopePrimitiveDataTypes.EXTENDED_TYPE_DELIMITER).getOrNull(0)?.split(AntelopePrimitiveDataTypes.SYMBOL_TYPE_DELIMITER)?.getOrNull(0).orEmpty() + AntelopePrimitiveDataTypes.SYMBOL_TYPE_DELIMITER + it + AntelopePrimitiveDataTypes.EXTENDED_TYPE_DELIMITER + actionAbi.value.split(AntelopePrimitiveDataTypes.EXTENDED_TYPE_DELIMITER).getOrNull(1).orEmpty())
                    },
                    placeholder = MR.strings.hint_multisig_proposal_action_symbol_name.desc().localized(),
                    modifier = Modifier.fillMaxWidth(),
                    requiredInput = true,
                    keyboardOptions = KeyboardOptions.Default.copy(
                        capitalization = KeyboardCapitalization.Characters,
                        autoCorrectEnabled = false,
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next
                    ),
                    containerModifier = Modifier.weight(1f),
                    isError = uiModel.errorMessage != null,
                    errorText = errorMessage?.secondFieldErrorMessage?.resolve()
                )
            }

            ProposalTextInputField(
                label = MR.strings.label_multisig_proposal_action_extended_contract_name.desc().localized(),
                query = actionAbi.value
                    .split(AntelopePrimitiveDataTypes.EXTENDED_TYPE_DELIMITER)
                    .getOrNull(1)
                    .orEmpty(),
                onQueryChange = {
                    onUpdateFieldValue(actionAbi.value.split(AntelopePrimitiveDataTypes.EXTENDED_TYPE_DELIMITER).getOrNull(0) + AntelopePrimitiveDataTypes.EXTENDED_TYPE_DELIMITER + it)
                },
                placeholder = MR.strings.hint_multisig_proposal_action_name_type.desc().localized(),
                modifier = Modifier.fillMaxWidth(),
                requiredInput = true,
                keyboardOptions = KeyboardOptions.Default.copy(
                    capitalization = KeyboardCapitalization.None,
                    autoCorrectEnabled = false,
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                ),
                isError = uiModel.errorMessage != null,
                errorText = errorMessage?.thirdFieldErrorMessage?.resolve()
            )
        }
    }

    @Composable
    fun ExtendedAssetInputField(
        uiModel: ActionDataFieldUiModel,
        onUpdateFieldValue: (value: String) -> Unit
    ) {
        val actionAbi = uiModel.field
        val errorMessage = uiModel.errorMessage as? InputFieldError.ThreeFieldsInput

        MaxWidthColumn {
            ProposalInputLabel("${actionAbi.fieldName} (${actionAbi.fieldType})", true)
            MaxWidthRow(horizontalArrangement = Arrangement.spacedBy(Spacing.SMALL)) {
                ProposalTextInputField(
                    label = MR.strings.label_multisig_proposal_action_asset_amount.desc().localized(),
                    query = actionAbi.value
                        .split(AntelopePrimitiveDataTypes.EXTENDED_TYPE_DELIMITER)
                        .getOrNull(0)
                        ?.split(AntelopePrimitiveDataTypes.ASSET_TYPE_DELIMITER)
                        ?.getOrNull(0)
                        .orEmpty(),
                    onQueryChange = {
                        onUpdateFieldValue(
                            it + AntelopePrimitiveDataTypes.ASSET_TYPE_DELIMITER + actionAbi.value.split(
                                AntelopePrimitiveDataTypes.EXTENDED_TYPE_DELIMITER
                            ).getOrNull(0)?.split(AntelopePrimitiveDataTypes.ASSET_TYPE_DELIMITER)
                                ?.getOrNull(1)
                                .orEmpty() + AntelopePrimitiveDataTypes.EXTENDED_TYPE_DELIMITER + actionAbi.value.split(
                                AntelopePrimitiveDataTypes.EXTENDED_TYPE_DELIMITER
                            ).getOrNull(1).orEmpty()
                        )
                    },
                    placeholder = MR.strings.hint_multisig_proposal_action_asset_amount.desc().localized(),
                    modifier = Modifier.fillMaxWidth(),
                    requiredInput = false,
                    keyboardOptions = KeyboardOptions.Default.copy(
                        capitalization = KeyboardCapitalization.None,
                        autoCorrectEnabled = false,
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next
                    ),
                    containerModifier = Modifier.weight(2f),
                    isError = uiModel.errorMessage != null,
                    errorText = errorMessage?.firstFieldErrorMessage?.resolve()
                )
                ProposalTextInputField(
                    label = MR.strings.label_multisig_proposal_action_asset_name.desc().localized(),
                    query = actionAbi.value
                        .split(AntelopePrimitiveDataTypes.EXTENDED_TYPE_DELIMITER)
                        .getOrNull(0)
                        ?.split(AntelopePrimitiveDataTypes.ASSET_TYPE_DELIMITER)
                        ?.getOrNull(1)
                        .orEmpty(),
                    onQueryChange = {
                        onUpdateFieldValue(
                            actionAbi.value.split(AntelopePrimitiveDataTypes.EXTENDED_TYPE_DELIMITER)
                                .getOrNull(0)
                                ?.split(AntelopePrimitiveDataTypes.ASSET_TYPE_DELIMITER)
                                ?.getOrNull(0)
                                .orEmpty() + AntelopePrimitiveDataTypes.ASSET_TYPE_DELIMITER + it + AntelopePrimitiveDataTypes.EXTENDED_TYPE_DELIMITER + actionAbi.value.split(
                                AntelopePrimitiveDataTypes.EXTENDED_TYPE_DELIMITER
                            ).getOrNull(1).orEmpty()
                        )
                    },
                    placeholder = MR.strings.hint_multisig_proposal_action_asset_name.desc().localized(),
                    modifier = Modifier.fillMaxWidth(),
                    requiredInput = false,
                    keyboardOptions = KeyboardOptions.Default.copy(
                        capitalization = KeyboardCapitalization.Characters,
                        autoCorrectEnabled = false,
                        keyboardType = actionAbi.getKeyboardType(),
                        imeAction = ImeAction.Next
                    ),
                    containerModifier = Modifier.weight(1f),
                    isError = uiModel.errorMessage != null,
                    errorText = errorMessage?.secondFieldErrorMessage?.resolve()
                )
            }

            ProposalTextInputField(
                label = MR.strings.label_multisig_proposal_action_extended_contract_name.desc().localized(),
                query = actionAbi.value
                    .split(AntelopePrimitiveDataTypes.EXTENDED_TYPE_DELIMITER)
                    .getOrNull(1)
                    .orEmpty(),
                onQueryChange = {
                    onUpdateFieldValue(actionAbi.value.split(AntelopePrimitiveDataTypes.EXTENDED_TYPE_DELIMITER).getOrNull(0) + AntelopePrimitiveDataTypes.EXTENDED_TYPE_DELIMITER + it)
                },
                placeholder = MR.strings.hint_multisig_proposal_action_name_type.desc().localized(),
                modifier = Modifier.fillMaxWidth(),
                requiredInput = false, // Some fields might not be required (e.g memo), we can't know for sure
                keyboardOptions = KeyboardOptions.Default.copy(
                    capitalization = KeyboardCapitalization.None,
                    autoCorrectEnabled = false,
                    keyboardType = actionAbi.getKeyboardType(),
                    imeAction = ImeAction.Next
                ),
                isError = uiModel.errorMessage != null,
                errorText = errorMessage?.thirdFieldErrorMessage?.resolve()
            )
        }
    }

    @Composable
    fun SymbolInputField(
        uiModel: ActionDataFieldUiModel,
        onUpdateFieldValue: (value: String) -> Unit
    ) {
        val actionAbi = uiModel.field
        val errorText = uiModel.errorMessage as? InputFieldError.TwoFieldsInput

        MaxWidthColumn {
            ProposalInputLabel("${actionAbi.fieldName} (${actionAbi.fieldType})", true)
            MaxWidthRow(horizontalArrangement = Arrangement.spacedBy(Spacing.SMALL)) {
                ProposalTextInputField(
                    label = MR.strings.label_multisig_proposal_action_symbol_precision.desc().localized(),
                    query = actionAbi.value.split(AntelopePrimitiveDataTypes.SYMBOL_TYPE_DELIMITER).getOrNull(0).orEmpty(),
                    onQueryChange = {
                        onUpdateFieldValue(it + AntelopePrimitiveDataTypes.SYMBOL_TYPE_DELIMITER + actionAbi.value.split(AntelopePrimitiveDataTypes.SYMBOL_TYPE_DELIMITER).getOrNull(1).orEmpty())
                    },
                    placeholder = MR.strings.hint_multisig_proposal_action_symbol_precision.desc().localized(),
                    modifier = Modifier.fillMaxWidth(),
                    requiredInput = false,
                    keyboardOptions = KeyboardOptions.Default.copy(
                        capitalization = KeyboardCapitalization.None,
                        autoCorrectEnabled = false,
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next
                    ),
                    containerModifier = Modifier.weight(1f),
                    isError = uiModel.errorMessage != null,
                    errorText = errorText?.firstFieldErrorMessage?.resolve()
                )
                ProposalTextInputField(
                    label = MR.strings.label_multisig_proposal_action_symbol_name.desc().localized(),
                    query = actionAbi.value.split(AntelopePrimitiveDataTypes.SYMBOL_TYPE_DELIMITER).getOrNull(1).orEmpty(),
                    onQueryChange = {
                        onUpdateFieldValue(actionAbi.value.split(AntelopePrimitiveDataTypes.SYMBOL_TYPE_DELIMITER).getOrNull(0).orEmpty() + AntelopePrimitiveDataTypes.SYMBOL_TYPE_DELIMITER + it)
                    },
                    placeholder = MR.strings.hint_multisig_proposal_action_symbol_name.desc().localized(),
                    modifier = Modifier.fillMaxWidth(),
                    requiredInput = false,
                    keyboardOptions = KeyboardOptions.Default.copy(
                        capitalization = KeyboardCapitalization.Characters,
                        autoCorrectEnabled = false,
                        keyboardType = actionAbi.getKeyboardType(),
                        imeAction = ImeAction.Next
                    ),
                    containerModifier = Modifier.weight(2f),
                    isError = uiModel.errorMessage != null,
                    errorText = errorText?.secondFieldErrorMessage?.resolve()
                )
            }
        }
    }

    @Composable
    fun AssetInputField(
        uiModel: ActionDataFieldUiModel,
        onUpdateFieldValue: (value: String) -> Unit
    ) {
        val actionAbi = uiModel.field
        val errorMessage = uiModel.errorMessage as? InputFieldError.TwoFieldsInput

        MaxWidthColumn {
            ProposalInputLabel("${actionAbi.fieldName} (${actionAbi.fieldType})", true)
            MaxWidthRow(horizontalArrangement = Arrangement.spacedBy(Spacing.SMALL)) {
                ProposalTextInputField(
                    label = MR.strings.label_multisig_proposal_action_asset_amount.desc().localized(),
                    query = actionAbi.value.split(ASSET_TYPE_DELIMITER).getOrNull(0).orEmpty(),
                    onQueryChange = {
                        onUpdateFieldValue(it + ASSET_TYPE_DELIMITER + actionAbi.value.split(ASSET_TYPE_DELIMITER).getOrNull(1).orEmpty())
                    },
                    placeholder = MR.strings.hint_multisig_proposal_action_asset_amount.desc().localized(),
                    modifier = Modifier.fillMaxWidth(),
                    requiredInput = false,
                    keyboardOptions = KeyboardOptions.Default.copy(
                        capitalization = KeyboardCapitalization.None,
                        autoCorrectEnabled = false,
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next
                    ),
                    containerModifier = Modifier.weight(2f),
                    isError = uiModel.errorMessage != null,
                    errorText = errorMessage?.firstFieldErrorMessage?.resolve()
                )
                ProposalTextInputField(
                    label = MR.strings.label_multisig_proposal_action_asset_name.desc().localized(),
                    query = actionAbi.value.split(ASSET_TYPE_DELIMITER).getOrNull(1).orEmpty(),
                    onQueryChange = {
                        onUpdateFieldValue(actionAbi.value.split(ASSET_TYPE_DELIMITER).getOrNull(0).orEmpty() + ASSET_TYPE_DELIMITER + it)
                    },
                    placeholder = MR.strings.hint_multisig_proposal_action_asset_name.desc().localized(),
                    modifier = Modifier.fillMaxWidth(),
                    requiredInput = false,
                    keyboardOptions = KeyboardOptions.Default.copy(
                        capitalization = KeyboardCapitalization.Characters,
                        autoCorrectEnabled = false,
                        keyboardType = actionAbi.getKeyboardType(),
                        imeAction = ImeAction.Next
                    ),
                    containerModifier = Modifier.weight(1f),
                    isError = uiModel.errorMessage != null,
                    errorText = errorMessage?.secondFieldErrorMessage?.resolve(),
                    trailingIcon = {
                        if (uiModel.symbolInfoLoading) {
                            CircularProgressIndicator(
                                color = MaterialTheme.mangalaColors.iconPrimary,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    },
                    enabled = uiModel.symbolNameInputDisabled.not()
                )
            }
            uiModel.symbolDecimals?.let {
                Text(
                    "Maximum $it decimal place(s)",
                    style = MangalaTypography.Size12Regular(),
                    color = MaterialTheme.mangalaColors.textSecondary,
                )
            }
        }
    }

    @Composable
    private fun DeleteActionConfirmationDialog(
        onDismiss: () -> Unit,
        onDeleteAction: () -> Unit
    ) {
        DeleteConfirmationDialog(
            title = MR.strings.title_multisig_proposal_action_delete_confirmation.desc()
                .localized(),
            description = MR.strings.message_multisig_proposal_action_delete_confirmation_description.desc()
                .localized(),
            onDismiss = onDismiss,
            onDeleteAction = onDeleteAction
        )
    }

    @Composable
    private fun AntelopeActionAbi.getDataTypeHint(): String {
        return when (AntelopePrimitiveDataTypes.fromValue(this.baseType)) {
            AntelopePrimitiveDataTypes.BOOL -> MR.strings.hint_multisig_proposal_action_bool_type.desc().localized()
            AntelopePrimitiveDataTypes.INT8 -> MR.strings.hint_multisig_proposal_action_int_type.format("-128", "127").localized()
            AntelopePrimitiveDataTypes.UINT8 -> MR.strings.hint_multisig_proposal_action_int_type.format("0", "255").localized()
            AntelopePrimitiveDataTypes.INT16 -> MR.strings.hint_multisig_proposal_action_int_type.format("-32768", "32767").localized()
            AntelopePrimitiveDataTypes.UINT16 -> MR.strings.hint_multisig_proposal_action_int_type.format("0", "65535").localized()
            AntelopePrimitiveDataTypes.INT32, AntelopePrimitiveDataTypes.VARINT32 -> MR.strings.hint_multisig_proposal_action_int_type.format("-2147483648", "2147483647").localized()
            AntelopePrimitiveDataTypes.UINT32, AntelopePrimitiveDataTypes.VARUINT32 -> MR.strings.hint_multisig_proposal_action_int_type.format("0", "4294967295").localized()
            AntelopePrimitiveDataTypes.INT64 -> MR.strings.hint_multisig_proposal_action_int_type.format("-9223372036854775808", "9223372036854775807").localized()
            AntelopePrimitiveDataTypes.UINT64 -> MR.strings.hint_multisig_proposal_action_int_type.format("0", "18446744073709551615").localized()
            AntelopePrimitiveDataTypes.INT128 -> MR.strings.hint_multisig_proposal_action_int_type.format("-170141183460469231731687303715884105728", "170141183460469231731687303715884105727").localized()
            AntelopePrimitiveDataTypes.UINT128 -> MR.strings.hint_multisig_proposal_action_int_type.format("0", "340282366920938463463374607431768211455").localized()
            AntelopePrimitiveDataTypes.FLOAT32, AntelopePrimitiveDataTypes.FLOAT64, AntelopePrimitiveDataTypes.FLOAT128 -> MR.strings.hint_multisig_proposal_action_float_type.desc().localized()
            AntelopePrimitiveDataTypes.TIME_POINT -> MR.strings.hint_multisig_proposal_action_time_point_type.desc().localized()
            AntelopePrimitiveDataTypes.TIME_POINT_SEC, AntelopePrimitiveDataTypes.BLOCK_TIMESTAMP_TYPE -> MR.strings.hint_multisig_proposal_action_time_point_sec_type.desc().localized()
            AntelopePrimitiveDataTypes.NAME -> MR.strings.hint_multisig_proposal_action_name_type.desc().localized()
            AntelopePrimitiveDataTypes.BYTES -> MR.strings.hint_multisig_proposal_action_hex_string_type.desc().localized()
            AntelopePrimitiveDataTypes.STRING -> MR.strings.hint_multisig_proposal_action_string_type.desc().localized()
            AntelopePrimitiveDataTypes.CHECKSUM160 -> MR.strings.hint_multisig_proposal_action_checksum160_type.desc().localized()
            AntelopePrimitiveDataTypes.CHECKSUM256 -> MR.strings.hint_multisig_proposal_action_checksum256_type.desc().localized()
            AntelopePrimitiveDataTypes.CHECKSUM512 -> MR.strings.hint_multisig_proposal_action_checksum512_type.desc().localized()
            AntelopePrimitiveDataTypes.PUBLIC_KEY -> MR.strings.hint_multisig_proposal_action_public_key_type.desc().localized()
            AntelopePrimitiveDataTypes.SIGNATURE -> MR.strings.hint_multisig_proposal_action_signature_type.desc().localized()
            AntelopePrimitiveDataTypes.SYMBOL_CODE -> MR.strings.hint_multisig_proposal_action_symbol_code_type.desc().localized()
            else -> ""
        }
    }

    private fun AntelopeActionAbi.getKeyboardType(): KeyboardType {
        return if (AntelopePrimitiveDataTypes.fromValue(this.baseType)?.isNumericKeyboardInputType == true) {
            KeyboardType.Number
        } else {
            KeyboardType.Text
        }
    }
}