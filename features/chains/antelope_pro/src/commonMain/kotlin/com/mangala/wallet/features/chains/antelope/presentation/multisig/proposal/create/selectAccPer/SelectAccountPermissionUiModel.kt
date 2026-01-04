package com.mangala.wallet.features.chains.antelope.presentation.multisig.proposal.create.selectAccPer

import com.mangala.antelope.base.api.model.Permission

data class SelectAccountPermissionUiModel(
    val accountsImported: List<String> = emptyList(),
    val proposer: String = "",
    val permissionExecute: String = "",
    val permissions: List<String> = emptyList()
) {
    val isButtonEnabled: Boolean
        get() = proposer.isNotBlank() && permissionExecute.isNotBlank()
}