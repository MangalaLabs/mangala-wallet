package com.mangala.wallet.features.contract_wizard.data.model

import kotlinx.serialization.Serializable

@Serializable
data class ContractWizardRequest(
    val metadata: Metadata?,
    val features: Features?,
    val upgradeable: String?
)

@Serializable
data class Metadata(
    val name: String?,
    val symbol: String?,
    val premint: String?
)

@Serializable
data class Features(
    val mintable: Boolean?,
    val burnable: Boolean?,
    val pauseable: Boolean?,
    val permit: Boolean?,
    val flashmint: Boolean?,
    val snapshots: Boolean?
)