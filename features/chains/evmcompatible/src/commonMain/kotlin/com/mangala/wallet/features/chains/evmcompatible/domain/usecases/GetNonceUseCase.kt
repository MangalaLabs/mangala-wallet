package com.mangala.wallet.features.chains.evmcompatible.domain.usecases

import com.mangala.wallet.features.chains.evmcompatible.core.hexStringToLongOrNull
import com.mangala.wallet.model.provider.JsonRpcNodeResponse
import com.mangala.wallet.features.chains.evmcompatible.domain.repository.NodeRepository
import com.mangala.wallet.features.chains.evmcompatible.model.Address
import com.mangala.wallet.features.chains.evmcompatible.model.DefaultBlockParameter
import kotlinx.serialization.json.Json

class GetNonceUseCase(
    private val nodeRepository: NodeRepository,
    private val parsingJson: Json
) {
    @Deprecated("Use getNonceLong instead as the results are already parsed")
    suspend operator fun invoke(url: String, id: Int, address: Address, defaultBlockParameter: DefaultBlockParameter) =
        nodeRepository.getNonce(url, id, address, defaultBlockParameter)

    suspend fun getNonceLong(
        url: String,
        id: Int,
        address: Address,
        defaultBlockParameter: DefaultBlockParameter = DefaultBlockParameter.Pending
    ): Long? {
        val response = nodeRepository.getNonce(url, id, address, defaultBlockParameter)
        val jsonRpcNodeResponse = parsingJson.decodeFromString(JsonRpcNodeResponse.serializer(), response)
        return jsonRpcNodeResponse.result?.hexStringToLongOrNull()
    }
}