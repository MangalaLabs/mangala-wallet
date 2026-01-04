package com.mangala.wallet.remote.provider.eosEVM

import com.mangala.wallet.model.provider.eosEVM.EosEvmNativeCoinBalanceResponse
import com.mangala.wallet.model.provider.eosEVM.EosEvmTokenBalanceResponse
import com.mangala.wallet.model.provider.eosEVM.GetPaginatedEosEvmTokenTransferForAddressResponse
import com.mangala.wallet.model.provider.eosEVM.GetPaginatedEosEvmTransactionsForAddressResponse
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Query

interface EosEvmApi {
    @GET("api?module=account&action=tokenlist")
    suspend fun getBalanceByAddress(
        @Query("address") address: String,
    ): EosEvmTokenBalanceResponse

    @GET("api?module=account&action=balance")
    suspend fun getNativeCoinBalanceByAddress(
        @Query("address") address: String,
    ): EosEvmNativeCoinBalanceResponse

    @GET("api?module=account&action=txlist")
    suspend fun getPaginatedTransactionsForAddress(
        @Query("address") address: String,
        @Query("page") page: Int,
        @Query("offset") offset: Int,
    ): GetPaginatedEosEvmTransactionsForAddressResponse

    @GET("api?module=account&action=tokentx")
    suspend fun getPaginatedTokenTransferForAddress(
        @Query("address") address: String,
        @Query("page") page: Int,
        @Query("offset") offset: Int,
    ): GetPaginatedEosEvmTokenTransferForAddressResponse
}