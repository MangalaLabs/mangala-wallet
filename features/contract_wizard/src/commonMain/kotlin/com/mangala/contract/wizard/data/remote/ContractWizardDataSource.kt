package com.mangala.contract.wizard.data.remote

import com.mangala.contract.wizard.data.model.ContractWizardRequest
import com.mangala.contract.wizard.data.model.ContractWizardResponse
import com.mangala.contract.wizard.data.model.DeployContractWizardResponse

class ContractWizardDataSource(private val api: ContractWizardApi) {

    suspend fun createContractWizard(body: ContractWizardRequest): String {
        return api.createContractWizard(body)
    }

    suspend fun deployContractWizard(body: String): DeployContractWizardResponse {
        return api.deployContractWizard(body)
    }
}