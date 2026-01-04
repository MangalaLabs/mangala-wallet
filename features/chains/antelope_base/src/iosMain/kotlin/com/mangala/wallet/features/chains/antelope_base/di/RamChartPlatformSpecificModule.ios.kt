package com.mangala.wallet.features.chains.antelope_base.di

import com.mangala.wallet.features.chains.antelope_base.data.remote.ram.EosEyesApi
import com.mangala.wallet.features.chains.antelope_base.data.remote.ram.EosEyesRemoteDataSource
import com.mangala.wallet.features.chains.antelope_base.data.remote.ram.RamChartRemoteDataSource
import com.mangala.wallet.features.chains.antelope_base.data.remote.ram.createEosEyesApi
import com.mangala.wallet.remote.di.provideKtorfit
import org.koin.dsl.module

internal actual val ramChartRemoteModule = module {
    single<EosEyesApi> {
        provideKtorfit(
            baseUrl = "https://eoseyes.com/",
            enableNetworkLogs = true,
            username = "",
            password = "",
            forceJsonBody = false,
            httpClientEngine = get()
        ).createEosEyesApi()
    }

    single<RamChartRemoteDataSource> { EosEyesRemoteDataSource(get()) }
}
