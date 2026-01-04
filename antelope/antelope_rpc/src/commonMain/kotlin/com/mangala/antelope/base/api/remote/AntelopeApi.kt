/*
 * Copyright 2023-2024 Mangala Wallet
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mangala.antelope.base.api.remote

import com.mangala.antelope.base.api.model.BaseGetTableRowsRequest
import com.mangala.antelope.base.api.model.BaseGetTableRowsResponse
import com.mangala.antelope.base.api.model.ComputeTransactionRequest
import com.mangala.antelope.base.api.model.ComputeTransactionResponse
import com.mangala.antelope.base.api.model.GetAbiResponse
import com.mangala.antelope.base.api.model.GetAccountRequest
import com.mangala.antelope.base.api.model.GetAccountResponse
import com.mangala.antelope.base.api.model.GetAccountsByAuthorizersRequest
import com.mangala.antelope.base.api.model.GetAccountsByAuthorizersResponse
import com.mangala.antelope.base.api.model.GetActionsPagingRequest
import com.mangala.antelope.base.api.model.GetActionsPagingResponse
import com.mangala.antelope.base.api.model.GetActivatedProtocolFeaturesRequest
import com.mangala.antelope.base.api.model.GetActivatedProtocolFeaturesResponse
import com.mangala.antelope.base.api.model.GetBlockHeaderStateRequest
import com.mangala.antelope.base.api.model.GetBlockHeaderStateResponse
import com.mangala.antelope.base.api.model.GetBlockInfoRequest
import com.mangala.antelope.base.api.model.GetBlockInfoResponse
import com.mangala.antelope.base.api.model.GetBlockRequest
import com.mangala.antelope.base.api.model.GetBlockResponse
import com.mangala.antelope.base.api.model.GetCodeHashResponse
import com.mangala.antelope.base.api.model.GetCodeRequest
import com.mangala.antelope.base.api.model.GetCodeResponse
import com.mangala.antelope.base.api.model.GetCurrencyBalanceRequest
import com.mangala.antelope.base.api.model.GetCurrencyStatsRequest
import com.mangala.antelope.base.api.model.GetCurrencyStatsResponse
import com.mangala.antelope.base.api.model.GetInfoResponse
import com.mangala.antelope.base.api.model.GetMultisigProposalTableRowResponse
import com.mangala.antelope.base.api.model.GetProducerScheduleResponse
import com.mangala.antelope.base.api.model.GetProducersRequest
import com.mangala.antelope.base.api.model.GetProducersResponse
import com.mangala.antelope.base.api.model.GetRawAbiResponse
import com.mangala.antelope.base.api.model.GetRawCodeAndAbiRequest
import com.mangala.antelope.base.api.model.GetRawCodeAndAbiResponse
import com.mangala.antelope.base.api.model.GetRequiredKeysRequest
import com.mangala.antelope.base.api.model.GetScheduledTransactionsRequest
import com.mangala.antelope.base.api.model.GetScheduledTransactionsResponse
import com.mangala.antelope.base.api.model.GetTableByScopeRequest
import com.mangala.antelope.base.api.model.GetTableByScopeResponse
import com.mangala.antelope.base.api.model.GetTableRowsMultisigsRequest
import com.mangala.antelope.base.api.model.GetTableRowsMultisigsResponse
import com.mangala.antelope.base.api.model.GetTableRowsRequest
import com.mangala.antelope.base.api.model.GetTableRowsResponse
import com.mangala.antelope.base.api.model.GetTransactionIdRequest
import com.mangala.antelope.base.api.model.GetTransactionStatusResponse
import com.mangala.antelope.base.api.model.PowerupState
import com.mangala.antelope.base.api.model.PushBlockRequest
import com.mangala.antelope.base.api.model.PushTransactionRequest
import com.mangala.antelope.base.api.model.PushTransactionResponse
import com.mangala.antelope.base.api.model.PushTransactionsRequest
import com.mangala.antelope.base.api.model.RamMarketRowResponse
import com.mangala.antelope.base.api.model.RexFundRowResponse
import com.mangala.antelope.base.api.model.RexPoolRowResponse
import com.mangala.antelope.base.api.model.RexQueueRowResponse
import com.mangala.antelope.base.api.model.SendReadOnlyTransactionRequest
import com.mangala.antelope.base.api.model.SendTransactionRequest
import com.mangala.antelope.base.api.model.resourceprovider.ResourceProviderRequestTransactionRequest
import com.mangala.antelope.base.api.model.resourceprovider.ResourceProviderRequestTransactionResponse
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.Query
import kotlinx.serialization.json.JsonObject

interface AntelopeApi {

    @POST("v1/chain/get_block")
    suspend fun getBlock(@Body request: GetBlockRequest): GetBlockResponse

    @POST("v1/chain/get_block_info")
    suspend fun getBlockInfo(@Body request: GetBlockInfoRequest): GetBlockInfoResponse

    @POST("v1/chain/get_info")
    suspend fun getInfo(): GetInfoResponse

    @POST("v1/chain/push_transaction")
    suspend fun pushTransaction(@Body request: PushTransactionRequest): PushTransactionResponse

    @POST("v1/chain/push_transaction")
    suspend fun sendTransaction(@Body request: SendTransactionRequest): Unit

    @POST("v1/chain/get_block_header_state")
    suspend fun getBlockHeaderState(@Body request: GetBlockHeaderStateRequest): GetBlockHeaderStateResponse

    @POST("v1/chain/push_transactions")
    suspend fun pushTransactions(@Body request: PushTransactionsRequest): Unit

    @POST("v1/chain/get_abi")
    suspend fun getAbi(@Body request: GetAccountRequest): GetAbiResponse

    @GET("v1/chain/get_raw_abi")
    suspend fun getRawAbi(@Query("account_name") accountName: String): GetRawAbiResponse

    @POST("v1/chain/get_account")
    suspend fun getAccount(@Body account: GetAccountRequest): GetAccountResponse

    @POST("v1/chain/get_currency_balance")
    suspend fun getCurrencyBalance(@Body request: GetCurrencyBalanceRequest): List<String>

    @POST("v1/chain/get_currency_stats")
    suspend fun getCurrencyStats(@Body request: GetCurrencyStatsRequest): JsonObject

    @POST("v1/chain/get_required_keys")
    suspend fun getRequiredKeys(@Body request: GetRequiredKeysRequest): Any

    @POST("v1/chain/get_producers")
    suspend fun getProducers(@Body request: GetProducersRequest): GetProducersResponse

    @POST("v1/chain/get_raw_code_and_abi")
    suspend fun getRawCodeAndAbi(@Body request: GetRawCodeAndAbiRequest): GetRawCodeAndAbiResponse

    @POST("v1/chain/get_scheduled_transactions")
    suspend fun getScheduledTransactions(@Body request: GetScheduledTransactionsRequest): GetScheduledTransactionsResponse

    @POST("v1/chain/get_table_by_scope")
    suspend fun getTableByScope(@Body request: GetTableByScopeRequest): GetTableByScopeResponse

    @POST("v1/chain/get_table_rows")
    suspend fun getTableRows(@Body request: GetTableRowsRequest): GetTableRowsResponse

    @POST("v1/chain/get_table_rows")
    suspend fun getTableRowsRamMarket(@Body request: BaseGetTableRowsRequest): BaseGetTableRowsResponse<RamMarketRowResponse>

    @POST("v1/chain/get_table_rows")
    suspend fun getTableRowsRexPool(@Body request: BaseGetTableRowsRequest): BaseGetTableRowsResponse<RexPoolRowResponse>

    @POST("v1/chain/get_table_rows")
    suspend fun getTableRowsRexFund(@Body request: BaseGetTableRowsRequest): BaseGetTableRowsResponse<RexFundRowResponse>

    @POST("v1/chain/get_table_rows")
    suspend fun getTableRowsRexQueue(@Body request: BaseGetTableRowsRequest): BaseGetTableRowsResponse<RexQueueRowResponse>

    @POST("v1/chain/get_code")
    suspend fun getCode(@Body request: GetCodeRequest): GetCodeResponse

    @POST("v1/chain/get_activated_protocol_features")
    suspend fun getActivatedProtocolFeatures(@Body request: GetActivatedProtocolFeaturesRequest): GetActivatedProtocolFeaturesResponse

    @POST("v1/chain/get_accounts_by_authorizers")
    suspend fun getAccountsByAuthorizers(@Body request: GetAccountsByAuthorizersRequest): GetAccountsByAuthorizersResponse

    @POST("v1/chain/get_transaction_status")
    suspend fun getTransactionStatus(@Query("id") id: String): GetTransactionStatusResponse

    @POST("v1/chain/send_transaction2")
    suspend fun sendTransaction2(@Body request: GetTransactionStatusResponse): Unit

    @POST("v1/chain/compute_transaction")
    suspend fun computeTransaction(@Body request: ComputeTransactionRequest): ComputeTransactionResponse

    @GET("v1/chain/get_code_hash")
    suspend fun getCodeHash(@Query("account_name") accountName: String): GetCodeHashResponse

    @POST("v1/chain/get_transaction_id")
    suspend fun getTransactionId(@Body request: GetTransactionIdRequest): String

    @POST("v1/chain/get_producer_schedule")
    suspend fun getProducerSchedule(): GetProducerScheduleResponse

    @POST("v1/chain/send_read_only_transaction")
    suspend fun sendReadOnlyTransaction(@Body request: SendReadOnlyTransactionRequest): Unit

    @POST("v1/chain/push_block")
    suspend fun pushBlock(@Body request: PushBlockRequest): Unit

    @POST("v1/chain/get_table_rows")
    suspend fun getTableRowsPowerUp(@Body request: BaseGetTableRowsRequest): BaseGetTableRowsResponse<PowerupState>

    @POST("v1/chain/get_table_rows")
    suspend fun getTableRowsMultisigs(@Body request: GetTableRowsMultisigsRequest): GetTableRowsMultisigsResponse

    @POST("v1/chain/get_table_rows")
    suspend fun getTableRowsMultisigsProposals(@Body request: GetTableRowsMultisigsRequest): GetMultisigProposalTableRowResponse

    @POST("v1/resource_provider/request_transaction")
    suspend fun resourceProviderRequestTransaction(@Body request: ResourceProviderRequestTransactionRequest): ResourceProviderRequestTransactionResponse

    @POST("v1/history/get_actions")
    suspend fun getActionsPaging(
        @Body request: GetActionsPagingRequest
    ): GetActionsPagingResponse
}