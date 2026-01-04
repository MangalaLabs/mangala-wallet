package com.mangala.wallet.features.chains.antelope_base.data.remote.esr

import com.mangala.wallet.features.chains.antelope_base.data.remote.esr.model.EsrCallbackRequest
import com.mangala.wallet.remote.di.ApiResponse
import com.mangala.wallet.remote.di.safeApiCall
import com.mangala.wallet.remote.network.CustomError

class EsrRemoteDataSource(private val esrApi: EsrApi) {

    suspend fun postCallback(
        url: String,
        request: EsrCallbackRequest
    ): ApiResponse<Unit, CustomError> {
        return safeApiCall {
            esrApi.postCallback(url, request)
        }
    }
}