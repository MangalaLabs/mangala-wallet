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

package com.mangala.antelope.base.api.remote.tokenbalance.mapper

import com.mangala.antelope.base.api.model.tokenbalance.BloksTokenBalanceResponse
import com.mangala.antelope.base.api.model.tokenbalance.JungleTokenBalanceResponse

fun BloksTokenBalanceResponse.toBaseTokenBalanceResponse() = com.mangala.antelope.base.api.model.tokenbalance.BaseAntelopeTokenBalanceResponse(
    tokens = tokens?.map {
        com.mangala.antelope.base.api.model.tokenbalance.BaseAntelopeTokenBalanceResponse.Token(
            amount = it?.amount,
            contract = it?.contract,
            currency = it?.currency,
            decimals = it?.decimals,
            name = it?.metadata?.name,
            exchangePrice = it?.exchanges?.firstOrNull()?.price,
            exchangeName = it?.exchanges?.firstOrNull()?.name,
            createdAt = it?.metadata?.createdAt,
            logo = it?.metadata?.logo,
            website = it?.metadata?.website,
            key = it?.key
        )
    }
)

fun JungleTokenBalanceResponse.toBaseTokenBalanceResponse() = com.mangala.antelope.base.api.model.tokenbalance.BaseAntelopeTokenBalanceResponse(
    tokens = tokens?.map {
        com.mangala.antelope.base.api.model.tokenbalance.BaseAntelopeTokenBalanceResponse.Token(
            amount = it?.amount,
            contract = it?.contract,
            currency = it?.symbol,
            decimals = it?.precision.toString(),
            name = it?.symbol,
            exchangePrice = 0.0,
            createdAt = "1970-01-01T00:00:00.000Z",
            logo = null,
            website = null,
            key = it?.symbol
        )
    }
)