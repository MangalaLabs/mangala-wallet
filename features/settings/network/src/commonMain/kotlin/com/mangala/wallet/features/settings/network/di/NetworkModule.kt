package com.mangala.wallet.features.settings.network.di

import cafe.adriel.voyager.core.registry.screenModule
import com.mangala.wallet.features.settings.network.NetworkBottomSheetScreen
import com.mangala.wallet.features.settings.network.NetworkBottomSheetScreenModel
import com.mangala.wallet.features.settings.network.NetworkScreen
import com.mangala.wallet.features.settings.network.NetworkScreenModel
import com.mangala.wallet.model.blockchain.BlockchainNetworkData
import com.mangala.wallet.ui.SharedScreen
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val settingsNetworkModule = module {
    factoryOf(::NetworkScreenModel)
    factory { (selectedNetwork: BlockchainNetworkData?) ->
        NetworkBottomSheetScreenModel(
            selectedNetwork,
            get()
        )
    }
}

val settingsNetworkScreenModule = screenModule {
    register<SharedScreen.NetworkScreen> { NetworkScreen() }
    register<SharedScreen.NetworkBottomSheetScreen> {
        NetworkBottomSheetScreen(
            it.selectedNetwork,
            it.onItemSelected
        )
    }
}
