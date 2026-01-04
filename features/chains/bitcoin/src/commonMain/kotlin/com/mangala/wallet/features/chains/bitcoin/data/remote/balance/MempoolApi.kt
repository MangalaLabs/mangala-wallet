package com.mangala.wallet.features.chains.bitcoin.data.remote.balance

import com.mangala.wallet.features.chains.bitcoin.data.remote.balance.response.MempoolGetBalanceResponse
import com.mangala.wallet.features.chains.bitcoin.data.remote.balance.response.MempoolUtxoResponseItem
import com.mangala.wallet.features.chains.bitcoin.data.remote.fee.response.MempoolRecommendedFeesResponse
import com.mangala.wallet.features.chains.bitcoin.data.remote.transaction.response.BitcoinTransactionResponse
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Headers
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.Path
import de.jensklingenberg.ktorfit.http.Query

interface MempoolApi {

    @GET("{network}api/address/{address}")
    suspend fun getBalance(
        @Path("network") network: String,
        @Path("address") address: String
    ): MempoolGetBalanceResponse

    @GET("{network}api/address/{address}/utxo")
    suspend fun getUtxo(
        @Path("network") network: String,
        @Path("address") address: String
    ): List<MempoolUtxoResponseItem>

    @GET("{network}api/tx/{txId}")
    suspend fun getTransaction(
        @Path("network") network: String,
        @Path("txId") txId: String
    ): BitcoinTransactionResponse
    
    @GET("{network}api/address/{address}/txs")
    suspend fun getTransactionsByAddress(
        @Path("network") network: String,
        @Path("address") address: String
    ): List<BitcoinTransactionResponse>
    
    @GET("{network}api/address/{address}/txs")
    suspend fun getTransactionsByAddressAfterTxid(
        @Path("network") network: String,
        @Path("address") address: String,
        @Query("after_txid") afterTxid: String
    ): List<BitcoinTransactionResponse>

    @POST("{network}api/tx")
    @Headers("Content-Type: text/plain")
    suspend fun sendTransaction(
        @Path("network") network: String,
        @Body transactionHex: String
    ): String
    
    @GET("api/v1/fees/recommended")
    suspend fun getRecommendedFees(): MempoolRecommendedFeesResponse
}