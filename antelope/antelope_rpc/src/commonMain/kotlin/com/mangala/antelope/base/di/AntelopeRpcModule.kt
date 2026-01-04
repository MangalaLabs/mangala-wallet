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

package com.mangala.antelope.base.di

import com.mangala.antelope.base.api.remote.AntelopeApi
import com.mangala.antelope.base.api.remote.AntelopeRemoteDataSource
import com.mangala.antelope.base.domain.repository.AntelopeRepository
import com.mangala.antelope.base.domain.repository.AntelopeRepositoryImpl
import com.mangala.antelope.base.domain.usecase.ComputeTransactionUseCase
import com.mangala.antelope.base.domain.usecase.GetAbiUseCase
import com.mangala.antelope.base.domain.usecase.GetActivatedProtocolFeaturesUseCase
import com.mangala.antelope.base.domain.usecase.GetBlockHeaderStateUseCase
import com.mangala.antelope.base.domain.usecase.GetBlockInfoUseCase
import com.mangala.antelope.base.domain.usecase.GetBlockUseCase
import com.mangala.antelope.base.domain.usecase.GetCodeHashUseCase
import com.mangala.antelope.base.domain.usecase.GetCodeUseCase
import com.mangala.antelope.base.domain.usecase.GetCurrencyBalanceAntelopeUseCase
import com.mangala.antelope.base.domain.usecase.GetCurrencyBalanceUseCase
import com.mangala.antelope.base.domain.usecase.GetCurrencyStatsUseCase
import com.mangala.antelope.base.domain.usecase.GetInfoUseCase
import com.mangala.antelope.base.domain.usecase.GetProducersUseCase
import com.mangala.antelope.base.domain.usecase.GetRawCodeAndAbiUseCase
import com.mangala.antelope.base.domain.usecase.GetRequiredKeysUseCase
import com.mangala.antelope.base.domain.usecase.GetScheduledTransactionsUseCase
import com.mangala.antelope.base.domain.usecase.GetTableRowsUseCase
import com.mangala.antelope.base.domain.usecase.GetTransactionIdUseCase
import com.mangala.antelope.base.domain.usecase.GetTransactionStatusUseCase
import com.mangala.antelope.base.domain.usecase.PushTransactionUseCase
import com.mangala.antelope.base.domain.usecase.SearchAccountByQueryUseCase
import com.mangala.antelope.base.domain.usecase.SendReadOnlyTransactionUseCase
import com.mangala.antelope.base.domain.usecase.SendTransaction2UseCase
import com.mangala.antelope.base.domain.usecase.SendTransactionUseCase
import com.mangala.wallet.remote.di.provideKtorfit
import com.mangala.wallet.utils.di.IGNORE_UNKNOWN_KEY_JSON
import org.koin.core.module.dsl.factoryOf
import org.koin.core.qualifier.named
import org.koin.dsl.module


fun antelopeRpcModule() = module{
    single<AntelopeApi>(named(AntelopeApiQualifier.EOS_JUNGLE_API_QUALIFIER)) {
        provideKtorfit(
            baseUrl = "https://jungle4.greymass.com/",
            enableNetworkLogs = true,
            username = "",
            password = "",
            forceJsonBody = true,
            httpClientEngine = get()
        ).create()
    }
    single<AntelopeApi>(named(AntelopeApiQualifier.EOS_MAINNET_API_QUALIFIER)) {
        provideKtorfit(
            baseUrl = "https://eos.greymass.com/",
            enableNetworkLogs = true,
            username = "",
            password = "",
            forceJsonBody = true,
            httpClientEngine = get()
        ).create()
    }
    single<AntelopeRemoteDataSource> {
        AntelopeRemoteDataSource(
            get(named(AntelopeApiQualifier.EOS_JUNGLE_API_QUALIFIER)),
            get(named(AntelopeApiQualifier.EOS_MAINNET_API_QUALIFIER)),
            get(named(IGNORE_UNKNOWN_KEY_JSON))
        )
    }

    single<AntelopeRepository> { AntelopeRepositoryImpl(get(), get(named(IGNORE_UNKNOWN_KEY_JSON))) }
    factoryOf(::ComputeTransactionUseCase)
    factoryOf(::GetAbiUseCase)
    factoryOf(::GetActivatedProtocolFeaturesUseCase)
    factoryOf(::GetBlockHeaderStateUseCase)
    factoryOf(::GetBlockInfoUseCase)
    factoryOf(::GetBlockUseCase)
    factoryOf(::GetCodeHashUseCase)
    factoryOf(::GetCodeUseCase)
    factoryOf(::GetCurrencyBalanceUseCase)
    factoryOf(::GetCurrencyBalanceAntelopeUseCase)
    factoryOf(::GetCurrencyStatsUseCase)
    factoryOf(::GetInfoUseCase)
    factoryOf(::GetProducersUseCase)
    factoryOf(::GetRawCodeAndAbiUseCase)
    factoryOf(::GetRequiredKeysUseCase)
    factoryOf(::GetScheduledTransactionsUseCase)
    factoryOf(::SearchAccountByQueryUseCase)
    factoryOf(::GetTableRowsUseCase)
    factoryOf(::GetTransactionIdUseCase)
    factoryOf(::GetTransactionStatusUseCase)
    factoryOf(::PushTransactionUseCase)
    factoryOf(::SendReadOnlyTransactionUseCase)
    factoryOf(::SendTransaction2UseCase)
    factoryOf(::SendTransactionUseCase)
}

enum class AntelopeApiQualifier {
    EOS_JUNGLE_API_QUALIFIER,
    EOS_MAINNET_API_QUALIFIER
}