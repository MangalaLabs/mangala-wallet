package com.mangala.wallet.features.contract_wizard.domain.repository

import com.mangala.wallet.features.contract_wizard.data.model.ContractWizardRequest
import com.mangala.wallet.features.contract_wizard.data.model.DeployContractWizardResponse
import com.mangala.wallet.features.contract_wizard.data.remote.ContractWizardDataSource

class ContractWizardRepositoryImpl(
    private val contractWizardDataSource: ContractWizardDataSource
): ContractWizardRepository {
    override suspend fun createContractWizard(body: ContractWizardRequest): String {
        return contractWizardDataSource.createContractWizard(body)
    }

    override suspend fun deployContractWizard(body: String): DeployContractWizardResponse {
        return contractWizardDataSource.deployContractWizard(body)
    }
}