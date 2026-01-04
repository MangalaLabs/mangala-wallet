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

import com.mangala.antelope.base.api.model.ChainError
import com.mangala.antelope.base.api.model.ChainException
import com.mangala.antelope.base.api.model.PushTransactionRequest
import com.mangala.antelope.base.domain.repository.AntelopeRepository
import com.mangala.wallet.model.blockchain.BlockchainType
import com.mangala.wallet.remote.di.ApiResponse

class PushTransactionUseCase(private val repository: AntelopeRepository) {

    @Deprecated("Use withResult instead, and then remove this function after migrated all")
    suspend operator fun invoke(blockchainType: BlockchainType, request: PushTransactionRequest): String? {
        val result = repository.pushTransaction(blockchainType, request)
        if (result is ApiResponse.Success) {
            return result.body.transactionId
        }
        else if (result is ApiResponse.Error) {
            return result.toString()
        }
        return null
    }

    suspend fun withResult(blockchainType: BlockchainType, request: PushTransactionRequest): Result<String> {
        return when (val result = repository.pushTransaction(blockchainType, request)) {
            is ApiResponse.Success -> {
                Result.success(result.body.transactionId.orEmpty())
            }

            is ApiResponse.Error -> {
                if (result is ApiResponse.Error.CustomError && result.errorBody is ChainError) {
                    Result.failure(ChainException(result.errorBody!!))
                } else {
                    Result.failure(Exception(result.toString()))
                }
            }

            else -> Result.failure(Exception(result.toString()))
        }
    }
}