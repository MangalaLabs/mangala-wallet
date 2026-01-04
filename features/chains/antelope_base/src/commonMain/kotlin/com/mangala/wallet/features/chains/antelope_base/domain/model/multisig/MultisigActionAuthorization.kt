package com.mangala.wallet.features.chains.antelope_base.domain.model.multisig

import com.mangala.wallet.features.chains.antelope_base.domain.model.AntelopeAccount
import com.mangala.wallet.features.chains.antelope_base.domain.model.AntelopeAccountPermission
import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.ui.utils.WrappedStringResource
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class MultisigActionAuthorization(
    val authorizationName: String,
    @Transient val authorizationNameSuggestions: List<String> = emptyList(), // autocomplete list
    @Transient val authorizationNameSuggestionsLoading: Boolean = false,
    @Transient val authorizationNameError: WrappedStringResource? = null,
    val permissionName: String,
    @Transient val account: AntelopeAccount? = null,
    @Transient val accountLoading: Boolean = false,
    @Transient val accountLoadingError: WrappedStringResource? = null
) {
    @Transient val permissions: List<AntelopeAccountPermission>? = account?.permissions // autocomplete list
    @Transient val threshold: Int? = permissions?.find { it.permissionType.permissionName.equals(permissionName, ignoreCase = true) }?.requiredAuth?.threshold
    @Transient val thresholdFormatted: WrappedStringResource? = if (threshold == null) null else WrappedStringResource.StringRes(
        MR.strings.message_multisig_proposal_authorization_threshold, threshold) // TODO: Refactor - move all of this to an UiModel

    @Transient val formatted = "$authorizationName@$permissionName"
    @Transient val filteredPermissions = permissions
        ?.filter { it.permissionType.permissionName.contains(permissionName, ignoreCase = true) }
        ?.map { it.permissionType.permissionName }
}
