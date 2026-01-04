package com.mangala.wallet.features.chains.antelope.ram.di

import cafe.adriel.voyager.core.registry.screenModule
import com.mangala.wallet.features.chains.antelope.ram.domain.usecase.BuySellRamUseCase
import com.mangala.wallet.features.chains.antelope.ram.domain.usecase.GenerateBuyRamSignRequestUseCase
import com.mangala.wallet.features.chains.antelope.ram.domain.usecase.GenerateSellRamSignRequestUseCase
import com.mangala.wallet.features.chains.antelope.ram.domain.usecase.GenerateTransferRamSignRequestUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.ram.GetRamPriceUseCase
import com.mangala.wallet.features.chains.antelope.ram.domain.usecase.TransferRamUseCase
import com.mangala.wallet.features.chains.antelope.ram.presentation.buysell.BuySellRamScreen
import com.mangala.wallet.features.chains.antelope.ram.presentation.buysell.BuySellRamScreenModel
import com.mangala.wallet.features.chains.antelope.ram.presentation.details.RamDetailScreen
import com.mangala.wallet.features.chains.antelope.ram.presentation.details.RamDetailScreenModel
import com.mangala.wallet.features.chains.antelope.ram.presentation.details.bottomSheet.ChartRamScreen
import com.mangala.wallet.features.chains.antelope.ram.presentation.details.bottomSheet.ChartRamScreenModel
import com.mangala.wallet.features.chains.antelope.ram.presentation.transfer.RamTransferScreen
import com.mangala.wallet.features.chains.antelope.ram.presentation.transfer.RamTransferScreenModel
import com.mangala.wallet.ui.SharedScreen
import org.koin.dsl.module

val antelopeRamModule = module {
    factory { BuySellRamUseCase(get(), get(), get(), get(), get(), get(), get(), get()) }
    factory { GenerateBuyRamSignRequestUseCase(get()) }
    factory { GenerateSellRamSignRequestUseCase(get()) }
    factory { GetRamPriceUseCase(get()) }
    factory { TransferRamUseCase(get(), get(), get(), get(), get(), get(), get()) }
    factory { GenerateTransferRamSignRequestUseCase(get()) }

    factory { (accountName: String) ->
        RamDetailScreenModel(
            getSelectedNetworkUseCase = get(),
            getAccountWithBalanceInfoUseCase = get(),
            getRamPriceUseCase = get(),
            saveBalanceVisibleStatusUseCase = get(),
            getBalanceVisibleStatusUseCase = get(),
            accountName = accountName,
            getActionsUseCase = get(),
            getRamChartUseCase = get(),
            buildEnvironmentProvider = get()
        )
    }
    factory { ChartRamScreenModel(get(), get()) }
    factory { (accountName: String, isBuyRam: Boolean) ->
        BuySellRamScreenModel(
            getAccountInfoUseCase = get(),
            getRamPriceUseCase = get(),
            getSelectedNetworkUseCase = get(),
            buySellRamUseCase = get(),
            getRamChartUseCase = get(),
            accountName = accountName,
            isBuyRam = isBuyRam,
            validateAccountUseCase = get(),
            checkAccountNotExistsUseCase = get(),
        )
    }
    factory { (accountName: String) ->
        RamTransferScreenModel(
            accountName = accountName,
            transferRamUseCase = get(),
            getSelectedNetworkUseCase = get(),
            getAccountInfoUseCase = get(),
            validateAccountUseCase = get(),
            checkAccountNotExistsUseCase = get()
        )
    }
}

val antelopeRamScreenModule = screenModule {
    register<SharedScreen.RamDetailScreen> {
        RamDetailScreen(it.accountName)
    }

    register<SharedScreen.ChartRamScreen> {
        ChartRamScreen(
            it.isLoading,
            it.ramPrice,
            it.ramCurrency,
            it.pnlPercent,
            it.pnlColor
        )
    }

    register<SharedScreen.BuySellRamScreen> {
        BuySellRamScreen(
            it.accountName,
            it.isBuyRam
        )
    }

    register<SharedScreen.RamTransferScreen> {
        RamTransferScreen(it.accountName)
    }
}