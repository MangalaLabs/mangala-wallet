package com.mangala.wallet.features.chains.antelope_base.di

import com.mangala.wallet.features.chains.antelope_base.data.local.ram.AntelopeRamChartLocalDataSource
import com.mangala.wallet.features.chains.antelope_base.data.local.ram.AntelopeRamChartLocalDataSourceImpl
import com.mangala.wallet.features.chains.antelope_base.data.repository.ram.AntelopeRamChartRepositoryImpl
import com.mangala.wallet.features.chains.antelope_base.domain.repository.ram.AntelopeRamChartRepository
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.ram.GetRamChartUseCase
import org.koin.core.module.Module
import org.koin.dsl.module

val ramChartModule = module {
    single<AntelopeRamChartLocalDataSource> {
        AntelopeRamChartLocalDataSourceImpl(get())
    }

    includes(ramChartRemoteModule)

    single<AntelopeRamChartRepository> {
        AntelopeRamChartRepositoryImpl(
            get(),
            get(),
        )
    }

    factory { GetRamChartUseCase(get()) }
}

internal expect val ramChartRemoteModule: Module