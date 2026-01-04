package com.mangala.wallet.features.evm_snap.di

import cafe.adriel.voyager.core.registry.screenModule
import com.mangala.wallet.features.evm_snap.domain.usecase.GetEosPrivateKeyFromEvmUseCase
import com.mangala.wallet.features.evm_snap.presentation.create.CreateEosAccountViaEVMScreen
import com.mangala.wallet.features.evm_snap.presentation.create.CreateEosAccountViaEVMScreenModel
import com.mangala.wallet.features.evm_snap.presentation.import.ChooseImportedEosAccountScreen
import com.mangala.wallet.features.evm_snap.presentation.import.ChooseImportedEosAccountScreenModel
import com.mangala.wallet.features.evm_snap.presentation.import.ImportEOSAccountViaEVMScreen
                                                    import com.mangala.wallet.features.evm_snap.presentation.import.ImportEOSAccountViaEVMScreenModel
import com.mangala.wallet.ui.SharedScreen
import org.koin.dsl.module

val featureEvmSnapModule = module {
    factory {
        ImportEOSAccountViaEVMScreenModel(
            getSelectedNetworkUseCase = get(),
            getAccountsByAuthorizersUseCase = get(),
            getAllWalletUseCase = get(),
            getEosPrivateKeyFromEvmUseCase = get()
        )
    }

    factory {
        ChooseImportedEosAccountScreenModel(
            getAccountsByAuthorizersUseCase = get(),
            getSelectedNetworkUseCase = get(),
            saveSelectedNetworkUseCase = get()
        )
    }

    factory {
        GetEosPrivateKeyFromEvmUseCase(
            getSelectedNetworkUseCase = get(),
            generateHDKeyUseCase = get()
        )
    }

    factory {
        CreateEosAccountViaEVMScreenModel(
            getEosPrivateKeyFromEvmUseCase = get(),
            getSelectedNetworkUseCase = get(),
            getAccountsByAuthorizersUseCase = get(),
            getAllWalletUseCase = get()
        )
    }
}

val featureEvmSnapScreenModule = screenModule {
    register<SharedScreen.ImportEOSAccountViaEVMScreen> {
        ImportEOSAccountViaEVMScreen()
    }

    register<SharedScreen.ChooseImportedEosAccountScreen> {
        ChooseImportedEosAccountScreen(
            eosOwnerPrivateKey = it.eosOwnerPrivateKey,
            eosActivePrivateKey = it.eosActivePrivateKey
        )
    }

    register<SharedScreen.CreateEosAccountViaEVMScreen> {
        CreateEosAccountViaEVMScreen(
            accountName = it.accountName,
            accountNameSuffix = it.accountNameSuffix,
            accountNameType = it.accountNameType
        )
    }
}