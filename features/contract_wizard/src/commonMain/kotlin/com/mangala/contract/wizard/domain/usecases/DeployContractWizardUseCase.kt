package com.mangala.contract.wizard.domain.usecases

import com.ionspin.kotlin.bignum.integer.BigInteger
import com.ionspin.kotlin.bignum.integer.toBigInteger
import com.mangala.contract.wizard.data.model.ContractWizardRequest
import com.mangala.contract.wizard.data.model.ContractWizardResponse
import com.mangala.contract.wizard.data.model.DeployContractWizardResponse
import com.mangala.contract.wizard.domain.repository.ContractWizardRepository

class DeployContractWizardUseCase (private val repository: ContractWizardRepository) {

    suspend operator fun invoke(
        body: String
    ): DeployContractWizardResponse {
        return repository.deployContractWizard(body)
    }
}