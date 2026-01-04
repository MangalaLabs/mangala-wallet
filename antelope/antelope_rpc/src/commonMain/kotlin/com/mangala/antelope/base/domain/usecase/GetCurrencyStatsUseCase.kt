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

import com.mangala.antelope.base.api.model.GetCurrencyStatsRequest
import com.mangala.antelope.base.api.model.GetCurrencyStatsResponse
import com.mangala.antelope.base.domain.repository.AntelopeRepository
import com.mangala.antelope.base.domain.utils.toResult
import com.mangala.wallet.model.blockchain.BlockchainType

class GetCurrencyStatsUseCase(private val repository: AntelopeRepository) {
    suspend operator fun invoke(
        blockchainType: BlockchainType,
        request: GetCurrencyStatsRequest
    ): Result<GetCurrencyStatsResponse> {
        val result = repository.getCurrencyStats(blockchainType, request).toResult().map {
            if (it == null) {
                return Result.failure(Exception("Symbol not found"))
            }

            it
        }

        return result
    }
}