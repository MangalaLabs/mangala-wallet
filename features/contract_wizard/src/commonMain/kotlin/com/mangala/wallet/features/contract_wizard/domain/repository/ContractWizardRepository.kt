package com.mangala.wallet.features.contract_wizard.domain.repository

import com.mangala.wallet.features.contract_wizard.data.model.ContractWizardRequest
import com.mangala.wallet.features.contract_wizard.data.model.DeployContractWizardResponse

interface ContractWizardRepository {
    suspend fun createContractWizard(body: ContractWizardRequest): String
    suspend fun deployContractWizard(body: String): DeployContractWizardResponse
}