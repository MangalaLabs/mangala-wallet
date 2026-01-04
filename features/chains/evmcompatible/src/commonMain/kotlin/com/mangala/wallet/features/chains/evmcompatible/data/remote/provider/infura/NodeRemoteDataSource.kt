package com.mangala.wallet.features.chains.evmcompatible.data.remote.provider.infura

import com.mangala.wallet.features.chains.evmcompatible.data.model.provider.infura.NodeRequest
import com.mangala.wallet.model.provider.JsonRpcNodeResponse


class NodeRemoteDataSource(private val api: NodeApi) {

    suspend fun getData(url: String, body: NodeRequest): JsonRpcNodeResponse {
        return api.getData(url, body)
    }

    suspend fun getDataByString(url: String, body: String): String {
        return api.getDataByString(url, body)
    }
}