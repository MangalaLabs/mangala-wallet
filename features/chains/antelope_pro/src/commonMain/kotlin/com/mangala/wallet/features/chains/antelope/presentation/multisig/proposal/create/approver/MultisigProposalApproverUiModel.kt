package com.mangala.wallet.features.chains.antelope.presentation.multisig.proposal.create.approver

import com.mangala.wallet.features.chains.antelope_base.domain.model.multisig.MultisigAction
import com.mangala.wallet.features.chains.antelope_base.domain.model.multisig.MultisigActionAuthorization

data class MultisigProposalApproverUiModel(
    val actions: List<MultisigAction>,
    val approvers: Map<MultisigActionAuthorization, List<MultisigActionAuthorization>>,
    val listApprover: List<Approver> = emptyList(),
) {
    val isButtonEnabled: Boolean
        get() = listApprover.all { it.isValidApprover() }

    private fun Approver.isValidApprover(): Boolean {
        return key.authorization.authorizationName.isNotBlank() && listItem.all { it.isValidItem() } && listItem.isNotEmpty()
    }

    private fun AuthorItem.isValidItem(): Boolean {
        return authorization.authorizationName.isNotBlank() &&
                authorization.permissionName.isNotBlank() &&
                !firstInputLoading && !secondInputLoading &&
                firstInputError.isNullOrBlank() && secondInputError.isNullOrBlank()
                && weight > 0
    }

}

data class Approver(
    val key: Author,
    val listItem: List<AuthorItem> = emptyList(),
    val numberAccounts: Int,
    val threshold: Int,
){
    val sumWeight = listItem.distinct().sumBy { it.weight }
}

data class Author(
    val authorization: MultisigActionAuthorization,
    val authorIndex: Int,
    val actionName: String
)

data class AuthorItem(
    val authorization: MultisigActionAuthorization,
    val weight: Int,
    val firstInputLoading: Boolean = false,
    val firstInputError: String? = null,
    val secondInputLoading: Boolean = false,
    val secondInputError: String? = null,
    val listActor: List<String> = emptyList(),
    val listPermission: List<String> = emptyList()
)
