package com.mangala.wallet.features.contract_wizard.domain.usecases

import com.mangala.wallet.features.contract_wizard.data.model.DeployContractWizardResponse
import com.mangala.wallet.features.contract_wizard.domain.repository.ContractWizardRepository

class DeployContractWizardUseCase (private val repository: ContractWizardRepository) {

    suspend operator fun invoke(
        body: String
    ): DeployContractWizardResponse {
        return repository.deployContractWizard(body)
    }
}