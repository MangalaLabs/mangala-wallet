package com.mangala.wallet.uniswap.domain.di

import com.mangala.wallet.uniswap.domain.usecase.GetSwapTradeDataUseCase
import com.mangala.wallet.uniswap.domain.usecase.GetSwapTransactionDataUseCase
import org.koin.dsl.module

inline fun uniswapDomainModule() = module {
    single { GetSwapTradeDataUseCase(get()) }
    single { GetSwapTransactionDataUseCase(get()) }
}