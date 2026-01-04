package com.mangala.wallet.remote.provider.moralis

import com.mangala.wallet.model.provider.moralis.MoralisBalanceResponse
import com.mangala.wallet.model.provider.moralis.MoralisWalletHistoryResponse
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Header
import de.jensklingenberg.ktorfit.http.Path
import de.jensklingenberg.ktorfit.http.Query

interface MoralisApi {

    @GET("wallets/{address}/history")
    suspend fun getWalletHistory(
        @Path("address") address: String,
        @Query("chain") chain: String,
        @Query("order") order: String = "desc",
        @Query("page") page: Int? = null,
        @Query("page_size") pageSize: Int? = null,
        @Query("cursor") cursor: String? = null,
        @Header("X-API-Key") apiKey: String
    ): MoralisWalletHistoryResponse

    @GET("wallets/{address}/tokens")
    suspend fun getNativeAndErc20Balance(
        @Path("address") address: String,
        @Query("chain") chain: String,
        @Query("page") page: Int? = null,
        @Query("page_size") pageSize: Int? = null,
        @Query("cursor") cursor: String? = null,
        @Header("X-API-Key") apiKey: String
    ): MoralisBalanceResponse

}