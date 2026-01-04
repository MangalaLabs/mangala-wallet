package com.mangala.wallet.uniswap.data.di

import com.mangala.wallet.uniswap.data.repository.UniswapRepositoryImpl
import com.mangala.wallet.uniswap.data.services.UniswapService
import com.mangala.wallet.uniswap.domain.repository.UniswapRepository
import org.koin.dsl.module

inline fun uniswapDataModule() = module {
    single<UniswapRepository> { UniswapRepositoryImpl(get()) }
    single { UniswapService() }
}