package com.mangala.wallet.features.contract_wizard.di

import com.mangala.wallet.features.contract_wizard.data.remote.ContractWizardApi
import com.mangala.wallet.features.contract_wizard.data.remote.ContractWizardDataSource
import com.mangala.wallet.features.contract_wizard.domain.repository.ContractWizardRepository
import com.mangala.wallet.features.contract_wizard.domain.repository.ContractWizardRepositoryImpl
import com.mangala.wallet.features.contract_wizard.domain.usecases.CreateContractWizardUseCase
import com.mangala.wallet.features.contract_wizard.domain.usecases.DeployContractWizardUseCase
import com.mangala.wallet.features.contract_wizard.presentation.ContractWizardScreenModel
import com.mangala.wallet.remote.di.provideKtorfit
import org.koin.dsl.module

fun contractWizardModule() = module {
    single<ContractWizardApi> {
        provideKtorfit(
            baseUrl = "https://safe-badlands-26177-15891cd06175.herokuapp.com/",
            enableNetworkLogs = true,
            username = "",
            password = "",
            forceJsonBody = false,
            httpClientEngine = get()
        ).create()
    }
    single { ContractWizardDataSource(get()) }

    single<ContractWizardRepository> { ContractWizardRepositoryImpl(get()) }
    factory { CreateContractWizardUseCase(get()) }
    factory { DeployContractWizardUseCase(get()) }

    factory { ContractWizardScreenModel(
        createContractWizardUseCase = get(),
        deployContractWizardUseCase = get(),
        getSelectedWalletAccountsUseCase = get()
    ) }
}