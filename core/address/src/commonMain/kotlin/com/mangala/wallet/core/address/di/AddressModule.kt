package com.mangala.wallet.core.address.di

import com.mangala.wallet.core.address.domain.usecases.DeriveBitcoinAddressUseCase
import com.mangala.wallet.core.address.domain.usecases.DeriveEthereumAddressUseCase
import org.koin.dsl.module

val addressModule = module {
    factory { DeriveEthereumAddressUseCase() }
    factory { DeriveBitcoinAddressUseCase() }
}