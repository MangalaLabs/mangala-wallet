package com.mangala.wallet.features.chains.antelope_base.domain.model.multisig

import com.mangala.wallet.features.chains.antelope_base.domain.model.actions.abis.FlattenedActionFields
import kotlinx.serialization.Serializable

@Serializable
data class MultisigAction(
    val contractName: String,
    val actionName: String,
    val fields: FlattenedActionFields,
    val authorizations: List<MultisigActionAuthorization>
) {
    val formattedValue = "$contractName:$actionName"
}