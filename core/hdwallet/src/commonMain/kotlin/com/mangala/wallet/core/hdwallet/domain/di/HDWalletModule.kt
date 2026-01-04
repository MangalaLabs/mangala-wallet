package com.mangala.wallet.core.hdwallet.domain.di
import com.mangala.wallet.core.hdwallet.domain.usecases.GenerateHDKeyUseCase
import org.koin.dsl.module
fun hdWalletModule() = module{
    factory { GenerateHDKeyUseCase() }
}