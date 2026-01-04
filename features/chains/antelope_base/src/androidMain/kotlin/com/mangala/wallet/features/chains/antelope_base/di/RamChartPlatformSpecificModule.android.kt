package com.mangala.wallet.features.chains.antelope_base.di

import com.mangala.wallet.features.chains.antelope_base.data.remote.ram.RamChartRemoteDataSource
import com.mangala.wallet.features.chains.antelope_base.data.remote.ram.RamChartRemoteDataSourceImpl
import org.koin.dsl.module

internal actual val ramChartRemoteModule = module {
    single<RamChartRemoteDataSource> { RamChartRemoteDataSourceImpl() }
}