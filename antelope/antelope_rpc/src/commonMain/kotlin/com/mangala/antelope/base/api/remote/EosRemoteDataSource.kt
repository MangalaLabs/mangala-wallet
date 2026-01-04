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

import com.mangala.antelope.base.api.model.GetActionsResponse
import com.mangala.antelope.base.api.model.msig.ListProposalResponse
import com.mangala.wallet.model.blockchain.BlockchainType
import com.mangala.wallet.remote.di.ApiResponse
import com.mangala.wallet.remote.di.safeApiCall
import com.mangala.wallet.remote.network.CustomError

class EosRemoteDataSource(
    private val jungleApi: EosApi,
    private val mainnetApi: EosApi
) {
    suspend fun getActions(
        blockchainType: BlockchainType,
        accountName: String?,
        filter: String?,
        skip: Int?,
        limit: Int?,
        sort: String,
        transferTo: String?,
        transferFrom: String?,
        after: String?,
        before: String?,
        receiptsReceiver: String? = null,
    ): ApiResponse<GetActionsResponse, CustomError> {
        return safeApiCall {
            getApi(blockchainType).getActionsFilter(
                accountName = accountName,
                filter = filter,
                skip = skip,
                limit = limit,
                sort = sort,
                transferTo = transferTo,
                transferFrom = transferFrom,
                after = after,
                before = before,
                receiptsReceiver = receiptsReceiver
            )
        }
    }

    suspend fun getListProposals(
        blockchainType: BlockchainType,
        proposer: String?,
        requested: String?,
        executed: Boolean,
        skip: Int?,
        limit: Int?,
    ): ApiResponse<ListProposalResponse, CustomError> = safeApiCall {
        getApi(blockchainType).getListProposals(
            proposer = proposer,
            requested = requested,
            executed = executed,
            skip = skip,
            limit = limit
        )
    }

    private fun getApi(blockchainType: BlockchainType): EosApi {
        return when (blockchainType) {
            BlockchainType.EosJungleTestnet -> jungleApi
            BlockchainType.Eos -> mainnetApi
            else -> throw IllegalArgumentException("Unsupported blockchain type: $blockchainType")
        }
    }
}