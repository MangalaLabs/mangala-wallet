package com.mangala.contract.wizard.domain.repository

import com.mangala.contract.wizard.data.model.ContractWizardRequest
import com.mangala.contract.wizard.data.model.ContractWizardResponse
import com.mangala.contract.wizard.data.model.DeployContractWizardResponse
import com.mangala.contract.wizard.data.remote.ContractWizardDataSource

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