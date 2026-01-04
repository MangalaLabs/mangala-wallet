package com.mangala.wallet.remote.provider.covalenthq

import com.mangala.wallet.model.provider.covalenthq.CovalenthqResponse
import com.mangala.wallet.model.provider.covalenthq.GetNftsForAddressResponse
import com.mangala.wallet.model.provider.covalenthq.GetPaginatedCovalenthqTransactionsForAddressResponse
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Path
import de.jensklingenberg.ktorfit.http.Query

interface CovalenthqApi {

    @GET("{chainNetWork}/address/{address}/balances_v2/")
    suspend fun getBalanceByNetWorkAndAddress(
        @Path("chainNetWork") chainNetwork: String,
        @Path("address") address: String,
    ): CovalenthqResponse

    @GET("{chainName}/address/{walletAddress}/transactions_v3/")
    suspend fun getLatestTransactionsForAddress(
        @Path("chainName") chainName: String,
        @Path("walletAddress") walletAddress: String,
    ): GetPaginatedCovalenthqTransactionsForAddressResponse

    @GET("{chainName}/address/{walletAddress}/transactions_v3/page/{page}/")
    suspend fun getPaginatedTransactionsForAddress(
        @Path("chainName") chainName: String,
        @Path("walletAddress") walletAddress: String,
        @Path("page") page: Int,
    ): GetPaginatedCovalenthqTransactionsForAddressResponse

    @GET("{chainName}/address/{walletAddress}/balances_nft/")
    suspend fun getNftsForAddress(
        @Path("chainName") chainName: String,
        @Path("walletAddress") walletAddress: String,
        @Query("with-uncached") withUncached: Boolean,
    ): GetNftsForAddressResponse
}