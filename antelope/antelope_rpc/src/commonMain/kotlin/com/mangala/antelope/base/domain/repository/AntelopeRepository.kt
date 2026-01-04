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
import com.mangala.antelope.base.domain.model.ResourceProviderResponse
import com.mangala.wallet.model.blockchain.BlockchainType
import com.mangala.wallet.remote.di.ApiResponse
import com.mangala.wallet.remote.network.CustomError

interface AntelopeRepository {

    suspend fun getBlock(blockchainType: BlockchainType, request: GetBlockRequest): ApiResponse<GetBlockResponse, CustomError>

    suspend fun getBlockInfo(blockchainType: BlockchainType, request: GetBlockInfoRequest): ApiResponse<GetBlockInfoResponse, CustomError>

    suspend fun getInfo(blockchainType: BlockchainType): ApiResponse<GetInfoResponse, CustomError>

    suspend fun pushTransaction(blockchainType: BlockchainType, request: PushTransactionRequest): ApiResponse<PushTransactionResponse, ChainError>

    suspend fun sendTransaction(blockchainType: BlockchainType, request: SendTransactionRequest): ApiResponse<Unit, CustomError>

    suspend fun getBlockHeaderState(blockchainType: BlockchainType, request: GetBlockHeaderStateRequest): ApiResponse<GetBlockHeaderStateResponse, CustomError>

    suspend fun pushTransactions(blockchainType: BlockchainType, request: PushTransactionsRequest): ApiResponse<Unit, CustomError>

    suspend fun getAbi(blockchainType: BlockchainType,  request: GetAccountRequest): ApiResponse<GetAbiResponse, CustomError>

    suspend fun getRawAbi(blockchainType: BlockchainType, accountName: String): ApiResponse<GetRawAbiResponse, CustomError>

    suspend fun getAccount(blockchainType: BlockchainType, accountName: String): ApiResponse<GetAccountResponse, CustomError>

    suspend fun getCurrencyBalance(blockchainType: BlockchainType, request: GetCurrencyBalanceRequest): ApiResponse<List<String>, CustomError>

    suspend fun getCurrencyStats(blockchainType: BlockchainType, request: GetCurrencyStatsRequest): ApiResponse<GetCurrencyStatsResponse?, ChainError>

    suspend fun getRequiredKeys(blockchainType: BlockchainType, request: GetRequiredKeysRequest): ApiResponse<Any, CustomError>

    suspend fun getProducers(
        blockchainType: BlockchainType,
        request: GetProducersRequest
    ): ApiResponse<GetProducersResponse, CustomError>

    suspend fun getRawCodeAndAbi(
        blockchainType: BlockchainType,
        request: GetRawCodeAndAbiRequest
    ): ApiResponse<GetRawCodeAndAbiResponse, CustomError>

    suspend fun getScheduledTransactions(
        blockchainType: BlockchainType,
        request: GetScheduledTransactionsRequest
    ): ApiResponse<GetScheduledTransactionsResponse, CustomError>

    suspend fun getTableByScope(
        blockchainType: BlockchainType,
        request: GetTableByScopeRequest
    ): ApiResponse<GetTableByScopeResponse, CustomError>

    suspend fun getTableRows(
        blockchainType: BlockchainType,
        request: GetTableRowsRequest
    ): ApiResponse<GetTableRowsResponse, CustomError>

    suspend fun getTableRowsMultisigs(
        blockchainType: BlockchainType,
        request: GetTableRowsMultisigsRequest
    ): ApiResponse<GetTableRowsMultisigsResponse, CustomError>

    suspend fun getTableRowsMultisigsProposals(
        blockchainType: BlockchainType,
        request: GetTableRowsMultisigsRequest
    ): ApiResponse<GetMultisigProposalTableRowResponse, CustomError>

    suspend fun getCode(
        blockchainType: BlockchainType,
        request: GetCodeRequest
    ): ApiResponse<GetCodeResponse, CustomError>

    suspend fun getActivatedProtocolFeatures(
        blockchainType: BlockchainType,
        request: GetActivatedProtocolFeaturesRequest
    ): ApiResponse<GetActivatedProtocolFeaturesResponse, CustomError>

    suspend fun getTransactionStatus(
        blockchainType: BlockchainType,
        id: String
    ): ApiResponse<GetTransactionStatusResponse, CustomError>

    suspend fun sendTransaction2(blockchainType: BlockchainType, request: GetTransactionStatusResponse): ApiResponse<Unit, CustomError>

    suspend fun computeTransaction(blockchainType: BlockchainType, request: ComputeTransactionRequest): ApiResponse<ComputeTransactionResponse, ChainError>

    suspend fun getCodeHash(blockchainType: BlockchainType, accountName: String): ApiResponse<GetCodeHashResponse, CustomError>

    suspend fun getTransactionId(blockchainType: BlockchainType, request: GetTransactionIdRequest): ApiResponse<String, CustomError>

    suspend fun getProducerSchedule(blockchainType: BlockchainType, ): ApiResponse<GetProducerScheduleResponse, CustomError>

    suspend fun sendReadOnlyTransaction(blockchainType: BlockchainType, request: SendReadOnlyTransactionRequest): ApiResponse<Unit, CustomError>

    suspend fun pushBlock(blockchainType: BlockchainType, request: PushBlockRequest): ApiResponse<Unit, CustomError>

    // Resource Provider
    suspend fun requestTransaction(
        blockchainType: BlockchainType,
        packedTrx: String,
        signerActor: String,
        signerPermission: String
    ): Result<ResourceProviderResponse>

    suspend fun getActionsPaging(
        blockchainType: BlockchainType,
        request: GetActionsPagingRequest
    ): ApiResponse<GetActionsPagingResponse, CustomError>
}