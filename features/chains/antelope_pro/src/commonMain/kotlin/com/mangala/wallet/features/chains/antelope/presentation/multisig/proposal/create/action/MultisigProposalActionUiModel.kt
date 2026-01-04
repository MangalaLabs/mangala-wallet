package com.mangala.wallet.features.chains.antelope.presentation.multisig.proposal.create.action

import androidx.compose.ui.text.input.KeyboardType
import com.mangala.wallet.features.chains.antelope_base.domain.model.multisig.MultisigActionAuthorization
import com.mangala.wallet.features.chains.antelope_base.domain.model.actions.abis.AntelopeActionAbi
import com.mangala.wallet.ui.utils.WrappedStringResource
import com.memtrip.eos.chain.actions.transaction.abi.ActionAbi
import com.memtrip.eos.chain.actions.transaction.account.actions.AntelopePrimitiveDataTypes

data class MultisigProposalActionUiModel(
    val contractNameFilter: String,
    val contractNameSuggestionsLoading: Boolean,
    val contractNameError: WrappedStringResource?,
    val contractNames: List<String>,
    val actionNameFilter: String,
    val actionNameLoading: Boolean,
    val actionNames: List<String>,
    val actionNamesError: WrappedStringResource?,
    val dataFieldsLoading: Boolean,
    val dataFields: List<ActionDataFieldUiModel>,
    val dataFieldsLoaded: Boolean,
    val dataFieldParentIndexMapping: List<Int>,
    val authorizations: List<MultisigActionAuthorization>
) {
    // We don't check if data fields are all filled in because some fields may be optional (e.g memo)
    val buttonEnabled = contractNameFilter.isNotBlank()
            && actionNameFilter.isNotBlank()
            && dataFields.all {
                if (it.field.isPrimitive.not()) return@all true

                val dataType = AntelopePrimitiveDataTypes.fromValue(it.field.baseType)

                if (dataType != AntelopePrimitiveDataTypes.STRING) {
                    if (dataType == AntelopePrimitiveDataTypes.ASSET) {
                        // In case we prefill the asset name (for system contract), will need to validate the asset value again
                        it.field.value.split(" ").getOrNull(0)?.isNotBlank() == true && it.errorMessage == null
                    } else {
                        it.field.value.isNotBlank() && it.errorMessage == null
                    }
                } else {
                    it.errorMessage == null
                }
            } // Validate all fields filled in
            && authorizations.all {
                it.permissionName.isNotBlank() && it.authorizationName.isNotBlank() && it.accountLoadingError == null && it.authorizationNameError == null
            }

    val filteredActionNames = actionNames.filter { it.contains(actionNameFilter, ignoreCase = true) }
}