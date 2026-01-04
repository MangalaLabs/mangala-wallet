package com.mangala.wallet.features.contract_wizard.domain.usecases

import com.mangala.wallet.features.contract_wizard.data.model.ContractWizardRequest
import com.mangala.wallet.features.contract_wizard.domain.repository.ContractWizardRepository

class CreateContractWizardUseCase (private val repository: ContractWizardRepository) {

    suspend operator fun invoke(
        body: ContractWizardRequest
    ): String {
        return repository.createContractWizard(body)
    }
}