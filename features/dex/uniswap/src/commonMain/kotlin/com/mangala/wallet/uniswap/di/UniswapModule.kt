package com.mangala.wallet.uniswap.di

import com.mangala.wallet.uniswap.data.di.uniswapDataModule
import com.mangala.wallet.uniswap.domain.di.uniswapDomainModule
import org.koin.dsl.module

inline fun uniswapModule() = module {
    includes(uniswapDataModule(), uniswapDomainModule())
}