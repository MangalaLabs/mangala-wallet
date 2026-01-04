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

package com.mangala.antelope.base.domain.usecase

import com.mangala.antelope.base.api.model.ChainException
import com.mangala.antelope.base.api.model.ComputeTransactionRequest
import com.mangala.antelope.base.api.model.ComputeTransactionResponse
import com.mangala.antelope.base.api.model.tryMapToNotEnoughResourceException
import com.mangala.antelope.base.domain.repository.AntelopeRepository
import com.mangala.wallet.model.blockchain.BlockchainType
import com.mangala.wallet.remote.di.ApiResponse

class ComputeTransactionUseCase(private val repository: AntelopeRepository) {

    suspend operator fun invoke(
        blockchainType: BlockchainType,
        request: ComputeTransactionRequest.Transaction
    ): Result<String> {
        return invoke(blockchainType, ComputeTransactionRequest(request))
    }

    suspend operator fun invoke(blockchainType: BlockchainType, request: ComputeTransactionRequest): Result<String> {
        val result = repository.computeTransaction(blockchainType, request)
        if (result is ApiResponse.Success) {
            return Result.success(result.body.transactionId.orEmpty())
        } else if (result is ApiResponse.Error.CustomError) {
            val chainException = result.errorBody?.let { ChainException(it).tryMapToNotEnoughResourceException() }
            return Result.failure(chainException ?: Exception())
        }

        return Result.failure(Exception())
    }
}