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

package com.mangala.antelope.base.domain.repository

import com.mangala.antelope.base.api.model.ChainError
import com.mangala.antelope.base.api.model.ComputeTransactionRequest
import com.mangala.antelope.base.api.model.ComputeTransactionResponse
import com.mangala.antelope.base.api.model.GetAbiResponse
import com.mangala.antelope.base.api.model.GetAccountRequest
import com.mangala.antelope.base.api.model.GetAccountResponse
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
import com.mangala.antelope.base.api.model.PushBlockRequest
import com.mangala.antelope.base.api.model.PushTransactionRequest
import com.mangala.antelope.base.api.model.PushTransactionResponse
import com.mangala.antelope.base.api.model.PushTransactionsRequest
import com.mangala.antelope.base.api.model.SendReadOnlyTransactionRequest
import com.mangala.antelope.base.api.model.SendTransactionRequest
import com.mangala.antelope.base.api.remote.AntelopeRemoteDataSource
import com.mangala.antelope.base.domain.model.ResourceProviderResponse
import com.mangala.antelope.base.domain.repository.mapper.toGetCurrencyStatsResponse
import com.mangala.antelope.base.domain.repository.mapper.toResourceProviderResponse
import com.mangala.wallet.model.blockchain.BlockchainType
import com.mangala.wallet.remote.di.ApiResponse
import com.mangala.wallet.remote.network.CustomError
import kotlinx.serialization.json.Json

class AntelopeRepositoryImpl(
    private val antelopeRemoteDataSource: AntelopeRemoteDataSource,
    private val json: Json
) :
    AntelopeRepository {

    override suspend fun getBlock(
        blockchainType: BlockchainType,
        request: GetBlockRequest
    ): ApiResponse<GetBlockResponse, CustomError> {
        return antelopeRemoteDataSource.getBlock(blockchainType, request)
    }

    override suspend fun getBlockInfo(
        blockchainType: BlockchainType,
        request: GetBlockInfoRequest
    ): ApiResponse<GetBlockInfoResponse, CustomError> {
        return antelopeRemoteDataSource.getBlockInfo(blockchainType, request)
    }

    override suspend fun getInfo(blockchainType: BlockchainType): ApiResponse<GetInfoResponse, CustomError> {
        return antelopeRemoteDataSource.getInfo(blockchainType)
    }

    override suspend fun pushTransaction(
        blockchainType: BlockchainType,
        request: PushTransactionRequest
    ): ApiResponse<PushTransactionResponse, ChainError> {
        return antelopeRemoteDataSource.pushTransaction(blockchainType, request)
    }

    override suspend fun sendTransaction(
        blockchainType: BlockchainType,
        request: SendTransactionRequest
    ): ApiResponse<Unit, CustomError> {
        return antelopeRemoteDataSource.sendTransaction(blockchainType, request)
    }

    override suspend fun getBlockHeaderState(
        blockchainType: BlockchainType,
        request: GetBlockHeaderStateRequest
    ): ApiResponse<GetBlockHeaderStateResponse, CustomError> {
        return antelopeRemoteDataSource.getBlockHeaderState(blockchainType, request)
    }

    override suspend fun pushTransactions(
        blockchainType: BlockchainType,
        request: PushTransactionsRequest
    ): ApiResponse<Unit, CustomError> {
        return antelopeRemoteDataSource.pushTransactions(blockchainType, request)
    }

    override suspend fun getAbi(
        blockchainType: BlockchainType,
        request: GetAccountRequest
    ): ApiResponse<GetAbiResponse, CustomError> {
        return antelopeRemoteDataSource.getAbi(blockchainType, request)
    }

    override suspend fun getRawAbi(
        blockchainType: BlockchainType,
        accountName: String
    ): ApiResponse<GetRawAbiResponse, CustomError> {
        return antelopeRemoteDataSource.getRawAbi(blockchainType, accountName)
    }

    override suspend fun getAccount(
        blockchainType: BlockchainType,
        accountName: String
    ): ApiResponse<GetAccountResponse, CustomError> {
        return antelopeRemoteDataSource.getAccount(blockchainType, GetAccountRequest(accountName))
    }

    override suspend fun getCurrencyBalance(
        blockchainType: BlockchainType,
        request: GetCurrencyBalanceRequest
    ): ApiResponse<List<String>, CustomError> {
        return antelopeRemoteDataSource.getCurrencyBalance(blockchainType, request)
    }

    override suspend fun getCurrencyStats(
        blockchainType: BlockchainType,
        request: GetCurrencyStatsRequest
    ): ApiResponse<GetCurrencyStatsResponse?, ChainError> {
        return antelopeRemoteDataSource.getCurrencyStats(blockchainType, request).map { it.toGetCurrencyStatsResponse(json) }
    }

    override suspend fun getRequiredKeys(
        blockchainType: BlockchainType,
        request: GetRequiredKeysRequest
    ): ApiResponse<Any, CustomError> {
        return antelopeRemoteDataSource.getRequiredKeys(blockchainType, request)
    }

    override suspend fun getProducers(
        blockchainType: BlockchainType,
        request: GetProducersRequest
    ): ApiResponse<GetProducersResponse, CustomError> {
        return antelopeRemoteDataSource.getProducers(blockchainType, request)
    }

    override suspend fun getRawCodeAndAbi(
        blockchainType: BlockchainType,
        request: GetRawCodeAndAbiRequest
    ): ApiResponse<GetRawCodeAndAbiResponse, CustomError> {
        return antelopeRemoteDataSource.getRawCodeAndAbi(blockchainType, request)
    }

    override suspend fun getScheduledTransactions(
        blockchainType: BlockchainType,
        request: GetScheduledTransactionsRequest
    ): ApiResponse<GetScheduledTransactionsResponse, CustomError> {
        return antelopeRemoteDataSource.getScheduledTransactions(blockchainType, request)
    }

    override suspend fun getTableByScope(
        blockchainType: BlockchainType,
        request: GetTableByScopeRequest
    ): ApiResponse<GetTableByScopeResponse, CustomError> {
        return antelopeRemoteDataSource.getTableByScope(blockchainType, request)
    }

    override suspend fun getTableRows(
        blockchainType: BlockchainType,
        request: GetTableRowsRequest
    ): ApiResponse<GetTableRowsResponse, CustomError> {
        return antelopeRemoteDataSource.getTableRows(blockchainType, request)
    }

    override suspend fun getTableRowsMultisigs(
        blockchainType: BlockchainType,
        request: GetTableRowsMultisigsRequest
    ): ApiResponse<GetTableRowsMultisigsResponse, CustomError> {
        return antelopeRemoteDataSource.getTableRowsMultisigs(blockchainType, request)
    }

    override suspend fun getTableRowsMultisigsProposals(
        blockchainType: BlockchainType,
        request: GetTableRowsMultisigsRequest
    ): ApiResponse<GetMultisigProposalTableRowResponse, CustomError> {
        return antelopeRemoteDataSource.getTableRowsMultisigsProposals(blockchainType, request)
    }

    override suspend fun getCode(
        blockchainType: BlockchainType,
        request: GetCodeRequest
    ): ApiResponse<GetCodeResponse, CustomError> {
        return antelopeRemoteDataSource.getCode(blockchainType, request)
    }

    override suspend fun getActivatedProtocolFeatures(
        blockchainType: BlockchainType,
        request: GetActivatedProtocolFeaturesRequest
    ): ApiResponse<GetActivatedProtocolFeaturesResponse, CustomError> {
        return antelopeRemoteDataSource.getActivatedProtocolFeatures(blockchainType, request)
    }

    override suspend fun getTransactionStatus(
        blockchainType: BlockchainType,
        id: String
    ): ApiResponse<GetTransactionStatusResponse, CustomError> {
        return antelopeRemoteDataSource.getTransactionStatus(blockchainType, id)
    }

    override suspend fun sendTransaction2(
        blockchainType: BlockchainType,
        request: GetTransactionStatusResponse
    ): ApiResponse<Unit, CustomError> {
        return antelopeRemoteDataSource.sendTransaction2(blockchainType, request)
    }

    override suspend fun computeTransaction(
        blockchainType: BlockchainType,
        request: ComputeTransactionRequest
    ): ApiResponse<ComputeTransactionResponse, ChainError> {
        return antelopeRemoteDataSource.computeTransaction(blockchainType, request)
    }

    override suspend fun getCodeHash(
        blockchainType: BlockchainType,
        accountName: String
    ): ApiResponse<GetCodeHashResponse, CustomError> {
        return antelopeRemoteDataSource.getCodeHash(blockchainType, accountName)
    }

    override suspend fun getTransactionId(
        blockchainType: BlockchainType,
        request: GetTransactionIdRequest
    ): ApiResponse<String, CustomError> {
        return antelopeRemoteDataSource.getTransactionId(blockchainType, request)
    }

    override suspend fun getProducerSchedule(blockchainType: BlockchainType): ApiResponse<GetProducerScheduleResponse, CustomError> {
        return antelopeRemoteDataSource.getProducerSchedule(blockchainType)
    }

    override suspend fun sendReadOnlyTransaction(
        blockchainType: BlockchainType,
        request: SendReadOnlyTransactionRequest
    ): ApiResponse<Unit, CustomError> {
        return antelopeRemoteDataSource.sendReadOnlyTransaction(blockchainType, request)
    }

    override suspend fun pushBlock(
        blockchainType: BlockchainType,
        request: PushBlockRequest
    ): ApiResponse<Unit, CustomError> {
        return antelopeRemoteDataSource.pushBlock(blockchainType, request)
    }

    override suspend fun requestTransaction(
        blockchainType: BlockchainType,
        packedTrx: String,
        signerActor: String,
        signerPermission: String
    ): Result<ResourceProviderResponse> {
        val response = antelopeRemoteDataSource.resourceProviderRequestTransaction(
            blockchainType,
            packedTrx,
            signerActor,
            signerPermission
        )

        return when (response) {
            is ApiResponse.Success -> {
                Result.success(response.body.toResourceProviderResponse(json))
            }

            else -> {
                when (response) {
                    is ApiResponse.Error.CustomError -> {
                        // 4xx errors can indicate different cases that we need to handle in upper layers
                        // https://wharfkit.com/docs/utilities/resource-provider-spec#api-response
                        // Need to return error body to upper layers
                        return response.errorBody?.let {
                            Result.success(it.toResourceProviderResponse(json))
                        } ?: return Result.failure(Exception("Invalid error response"))
                    }

                    else -> {
                        Result.failure(Exception("Invalid ApiResponse"))
                    }
                }
            }
        }
    }

    override suspend fun getActionsPaging(
        blockchainType: BlockchainType,
        request: GetActionsPagingRequest
    ): ApiResponse<GetActionsPagingResponse, CustomError> {
        return antelopeRemoteDataSource.getActionsPaging(blockchainType = blockchainType, request = request)
    }
}