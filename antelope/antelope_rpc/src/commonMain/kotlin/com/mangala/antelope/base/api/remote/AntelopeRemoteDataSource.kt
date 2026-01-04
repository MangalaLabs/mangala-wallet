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
import com.mangala.antelope.base.api.model.ChainError
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
import com.mangala.wallet.model.blockchain.BlockchainType
import com.mangala.wallet.remote.di.ApiResponse
import com.mangala.wallet.remote.di.safeApiCall
import com.mangala.wallet.remote.di.safeApiCallWithErrorBodyParsing
import com.mangala.wallet.remote.network.CustomError
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject

class AntelopeRemoteDataSource(
    private val jungleApi: AntelopeApi,
    private val mainnetApi: AntelopeApi,
    private val json: Json
) {

    suspend fun getBlock(blockchainType: BlockchainType, request: GetBlockRequest): ApiResponse<GetBlockResponse, CustomError> {
        return safeApiCall { getApi(blockchainType).getBlock(request)}
    }

    suspend fun getBlockInfo(blockchainType: BlockchainType, request: GetBlockInfoRequest): ApiResponse<GetBlockInfoResponse, CustomError> {
        return safeApiCall { getApi(blockchainType).getBlockInfo(request) }
    }

    suspend fun getInfo(blockchainType: BlockchainType): ApiResponse<GetInfoResponse, CustomError> {
        return safeApiCall { getApi(blockchainType).getInfo() }
    }

    suspend fun pushTransaction(blockchainType: BlockchainType, request: PushTransactionRequest): ApiResponse<PushTransactionResponse, ChainError> {
        return safeApiCallWithErrorBodyParsing(json) { getApi(blockchainType).pushTransaction(request) }
    }

    suspend fun sendTransaction(blockchainType: BlockchainType, request: SendTransactionRequest): ApiResponse<Unit, CustomError> {
        return safeApiCall { getApi(blockchainType).sendTransaction(request) }
    }

    suspend fun getBlockHeaderState(blockchainType: BlockchainType, request: GetBlockHeaderStateRequest): ApiResponse<GetBlockHeaderStateResponse, CustomError> {
        return safeApiCall { getApi(blockchainType).getBlockHeaderState(request) }
    }

    suspend fun pushTransactions(blockchainType: BlockchainType, request: PushTransactionsRequest): ApiResponse<Unit, CustomError> {
        return safeApiCall { getApi(blockchainType).pushTransactions(request) }
    }

    suspend fun getAbi(blockchainType: BlockchainType, request: GetAccountRequest): ApiResponse<GetAbiResponse, CustomError> {
        return safeApiCall { getApi(blockchainType).getAbi(request) }
    }

    suspend fun getRawAbi(blockchainType: BlockchainType, accountName: String): ApiResponse<GetRawAbiResponse, CustomError> {
        return safeApiCall { getApi(blockchainType).getRawAbi(accountName) }
    }

    suspend fun getAccount(blockchainType: BlockchainType, request: GetAccountRequest): ApiResponse<GetAccountResponse, CustomError> {
        return safeApiCall { getApi(blockchainType).getAccount(request) }
    }

    suspend fun getCurrencyBalance(blockchainType: BlockchainType, request: GetCurrencyBalanceRequest): ApiResponse<List<String>, CustomError> {
        return safeApiCall { getApi(blockchainType).getCurrencyBalance(request) }
    }

    suspend fun getCurrencyStats(blockchainType: BlockchainType, request: GetCurrencyStatsRequest): ApiResponse<JsonObject, ChainError> {
        return safeApiCall { getApi(blockchainType).getCurrencyStats(request) }
    }

    suspend fun getRequiredKeys(blockchainType: BlockchainType, request: GetRequiredKeysRequest): ApiResponse<Any, CustomError> {
        return safeApiCall { getApi(blockchainType).getRequiredKeys(request) }
    }

    suspend fun getProducers(blockchainType: BlockchainType, request: GetProducersRequest): ApiResponse<GetProducersResponse, CustomError> {
        return safeApiCall { getApi(blockchainType).getProducers(request) }
    }

    suspend fun getRawCodeAndAbi(blockchainType: BlockchainType, request: GetRawCodeAndAbiRequest): ApiResponse<GetRawCodeAndAbiResponse, CustomError> {
        return safeApiCall { getApi(blockchainType).getRawCodeAndAbi(request) }
    }

    suspend fun getScheduledTransactions(blockchainType: BlockchainType, request: GetScheduledTransactionsRequest): ApiResponse<GetScheduledTransactionsResponse, CustomError> {
        return safeApiCall { getApi(blockchainType).getScheduledTransactions(request) }
    }

    suspend fun getTableByScope(blockchainType: BlockchainType, request: GetTableByScopeRequest): ApiResponse<GetTableByScopeResponse, CustomError> {
        return safeApiCall { getApi(blockchainType).getTableByScope(request) }
    }

    suspend fun getTableRows(blockchainType: BlockchainType, request: GetTableRowsRequest): ApiResponse<GetTableRowsResponse, CustomError> {
        return safeApiCall { getApi(blockchainType).getTableRows(request) }
    }

    suspend fun getTableRowsMultisigs(
        blockchainType: BlockchainType,
        request: GetTableRowsMultisigsRequest
    ): ApiResponse<GetTableRowsMultisigsResponse, CustomError> {
        return safeApiCall { getApi(blockchainType).getTableRowsMultisigs(request) }
    }

    suspend fun getTableRowsMultisigsProposals(
        blockchainType: BlockchainType,
        request: GetTableRowsMultisigsRequest
    ): ApiResponse<GetMultisigProposalTableRowResponse, CustomError> {
        return safeApiCall { getApi(blockchainType).getTableRowsMultisigsProposals(request) }
    }

    suspend fun getTableRowsRamMarket(blockchainType: BlockchainType, request: BaseGetTableRowsRequest): ApiResponse<BaseGetTableRowsResponse<RamMarketRowResponse>, CustomError> {
        return safeApiCall { getApi(blockchainType).getTableRowsRamMarket(request) }
    }

    suspend fun getTableRowsRexPool(blockchainType: BlockchainType, request: BaseGetTableRowsRequest): ApiResponse<BaseGetTableRowsResponse<RexPoolRowResponse>, CustomError> {
        return safeApiCall { getApi(blockchainType).getTableRowsRexPool(request) }
    }

    suspend fun getTableRowsRexFund(blockchainType: BlockchainType, request: BaseGetTableRowsRequest): ApiResponse<BaseGetTableRowsResponse<RexFundRowResponse>, CustomError> {
        return safeApiCall { getApi(blockchainType).getTableRowsRexFund(request) }
    }

    suspend fun getTableRowsRexQueue(blockchainType: BlockchainType, request: BaseGetTableRowsRequest): ApiResponse<BaseGetTableRowsResponse<RexQueueRowResponse>, CustomError> {
        return safeApiCall { getApi(blockchainType).getTableRowsRexQueue(request) }
    }

    suspend fun getCode(blockchainType: BlockchainType, request: GetCodeRequest): ApiResponse<GetCodeResponse, CustomError> {
        return safeApiCall { getApi(blockchainType).getCode(request) }
    }

    suspend fun getActivatedProtocolFeatures(blockchainType: BlockchainType, request: GetActivatedProtocolFeaturesRequest): ApiResponse<GetActivatedProtocolFeaturesResponse, CustomError> {
        return safeApiCall { getApi(blockchainType).getActivatedProtocolFeatures(request) }
    }

    suspend fun getAccountsByAuthorizers(blockchainType: BlockchainType, request: GetAccountsByAuthorizersRequest): ApiResponse<GetAccountsByAuthorizersResponse, CustomError> {
        return safeApiCall { getApi(blockchainType).getAccountsByAuthorizers(request) }
    }

    suspend fun getTransactionStatus(blockchainType: BlockchainType, id: String): ApiResponse<GetTransactionStatusResponse, CustomError> {
        return safeApiCall { getApi(blockchainType).getTransactionStatus(id) }
    }

    suspend fun sendTransaction2(blockchainType: BlockchainType, request: GetTransactionStatusResponse): ApiResponse<Unit, CustomError> {
        return safeApiCall { getApi(blockchainType).sendTransaction2(request) }
    }

    suspend fun computeTransaction(blockchainType: BlockchainType, request: ComputeTransactionRequest): ApiResponse<ComputeTransactionResponse, ChainError> {
        return safeApiCallWithErrorBodyParsing(json) { getApi(blockchainType).computeTransaction(request) }
    }

    suspend fun getCodeHash(blockchainType: BlockchainType, accountName: String): ApiResponse<GetCodeHashResponse, CustomError> {
        return safeApiCall { getApi(blockchainType).getCodeHash(accountName) }
    }

    suspend fun getTransactionId(blockchainType: BlockchainType, request: GetTransactionIdRequest): ApiResponse<String, CustomError> {
        return safeApiCall { getApi(blockchainType).getTransactionId(request) }
    }

    suspend fun getProducerSchedule(blockchainType: BlockchainType): ApiResponse<GetProducerScheduleResponse, CustomError> {
        return safeApiCall { getApi(blockchainType).getProducerSchedule() }
    }

    suspend fun sendReadOnlyTransaction(blockchainType: BlockchainType, request: SendReadOnlyTransactionRequest): ApiResponse<Unit, CustomError> {
        return safeApiCall { getApi(blockchainType).sendReadOnlyTransaction(request) }
    }

    suspend fun pushBlock(blockchainType: BlockchainType, request: PushBlockRequest): ApiResponse<Unit, CustomError> {
        return safeApiCall { getApi(blockchainType).pushBlock(request) }
    }

    private fun getApi(blockchainType: BlockchainType): AntelopeApi {
        return when(blockchainType) {
            BlockchainType.EosJungleTestnet -> jungleApi
            BlockchainType.Eos -> mainnetApi
            else -> throw IllegalArgumentException("Unsupported blockchain type: $blockchainType")
        }
    }

    suspend fun getTableRowsPowerUp(
        blockchainType: BlockchainType,
        request: BaseGetTableRowsRequest
    ): ApiResponse<BaseGetTableRowsResponse<PowerupState>, CustomError> {
        return safeApiCall { getApi(blockchainType).getTableRowsPowerUp(request) }
    }

    suspend fun resourceProviderRequestTransaction(
        blockchainType: BlockchainType,
        packedTrx: String,
        signerActor: String,
        signerPermission: String
    ): ApiResponse<ResourceProviderRequestTransactionResponse, ResourceProviderRequestTransactionResponse> {
        return safeApiCall {
            getApi(blockchainType).resourceProviderRequestTransaction(
                ResourceProviderRequestTransactionRequest(
                    ResourceProviderRequestTransactionRequest.PackedTransaction(
                        compression = 0,
                        packedContextFreeData = "00",
                        packedTrx = packedTrx,
                        signatures = emptyList()
                    ),
                    ResourceProviderRequestTransactionRequest.Signer(
                        actor = signerActor,
                        permission = signerPermission
                    )
                ),
            )
        }
    }

    suspend fun getActionsPaging(
        blockchainType: BlockchainType,
        request: GetActionsPagingRequest
    ): ApiResponse<GetActionsPagingResponse, CustomError> {
        return safeApiCall { getApi(blockchainType).getActionsPaging(request) }
    }
}