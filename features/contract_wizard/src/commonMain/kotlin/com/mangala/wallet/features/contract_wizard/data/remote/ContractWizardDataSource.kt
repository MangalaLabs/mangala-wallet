package com.mangala.wallet.features.contract_wizard.data.remote

import com.mangala.wallet.features.contract_wizard.data.model.ContractWizardRequest
import com.mangala.wallet.features.contract_wizard.data.model.DeployContractWizardResponse
import com.mangala.wallet.features.contract_wizard.data.remote.ContractWizardApi

class ContractWizardDataSource(private val api: ContractWizardApi) {

    suspend fun createContractWizard(body: ContractWizardRequest): String {
        return api.createContractWizard(body)
    }

    suspend fun deployContractWizard(body: String): DeployContractWizardResponse {
        return api.deployContractWizard(body)
    }
}