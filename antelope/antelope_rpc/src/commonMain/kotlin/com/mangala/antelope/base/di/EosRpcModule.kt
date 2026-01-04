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

import com.mangala.antelope.base.api.remote.EosApi
import com.mangala.antelope.base.api.remote.EosRemoteDataSource
import com.mangala.wallet.remote.di.provideKtorfit
import org.koin.core.qualifier.named
import org.koin.dsl.module

fun eosRpcModule() = module {
    single<EosApi>(named(EosApiQualifier.EOS_JUNGLE_API_QUALIFIER)) {
        provideKtorfit(
            baseUrl = "https://jungle4.cryptolions.io/v2/",
            enableNetworkLogs = true,
            username = "",
            password = "",
            forceJsonBody = false,
            httpClientEngine = get()
        ).create()
    }
    single<EosApi>(named(EosApiQualifier.EOS_MAINNET_API_QUALIFIER)) {
        provideKtorfit(
            baseUrl = "https://hyperion.paycash.online/v2/",
            enableNetworkLogs = true,
            username = "",
            password = "",
            forceJsonBody = false,
            httpClientEngine = get()
        ).create()
    }
    single<EosRemoteDataSource> {
        EosRemoteDataSource(
            jungleApi = get(named(EosApiQualifier.EOS_JUNGLE_API_QUALIFIER)),
            mainnetApi = get(named(EosApiQualifier.EOS_MAINNET_API_QUALIFIER))
        )
    }

}

private enum class EosApiQualifier {
    EOS_JUNGLE_API_QUALIFIER,
    EOS_MAINNET_API_QUALIFIER
}
