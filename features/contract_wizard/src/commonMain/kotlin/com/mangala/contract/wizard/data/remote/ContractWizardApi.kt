package com.mangala.contract.wizard.data.remote

import com.mangala.contract.wizard.data.model.ContractWizardRequest
import com.mangala.contract.wizard.data.model.ContractWizardResponse
import com.mangala.contract.wizard.data.model.DeployContractWizardResponse
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.Headers
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.Url

interface ContractWizardApi {

    @POST("api/erc721")
    @Headers("Content-Type: application/json", "Accept: application/json")
    suspend fun createContractWizard(@Body body: ContractWizardRequest): String

    @POST("api/deployTx")
    @Headers("Content-Type: application/json", "Accept: application/json")
    suspend fun deployContractWizard(@Body body: String): DeployContractWizardResponse

}