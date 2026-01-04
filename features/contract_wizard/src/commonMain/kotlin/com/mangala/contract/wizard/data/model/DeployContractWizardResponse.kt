package com.mangala.contract.wizard.data.model

import kotlinx.serialization.Serializable

@Serializable
data class DeployContractWizardResponse(
    val deployTx: String?
)