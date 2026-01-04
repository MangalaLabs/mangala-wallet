package com.mangala.contract.wizard.domain.repository

import com.mangala.contract.wizard.data.model.ContractWizardRequest
import com.mangala.contract.wizard.data.model.ContractWizardResponse
import com.mangala.contract.wizard.data.model.DeployContractWizardResponse

interface ContractWizardRepository {
    suspend fun createContractWizard(body: ContractWizardRequest): String
    suspend fun deployContractWizard(body: String): DeployContractWizardResponse
}