package com.mangala.wallet.features.chains.evmcompatible.data.remote.provider.infura

import com.mangala.wallet.features.chains.evmcompatible.data.model.provider.infura.NodeRequest
import com.mangala.wallet.model.provider.JsonRpcNodeResponse
import de.jensklingenberg.ktorfit.http.*

interface NodeApi {
    @POST
    suspend fun getData(@Url url: String, @Body body: NodeRequest): JsonRpcNodeResponse

    @POST
    suspend fun getDataByString(@Url url: String, @Body body: String): String

}