package com.mangala.wallet.features.contract_wizard.data.model

import kotlinx.serialization.Serializable

@Serializable
data class DeployContractWizardResponse(
    val deployTx: String?
)