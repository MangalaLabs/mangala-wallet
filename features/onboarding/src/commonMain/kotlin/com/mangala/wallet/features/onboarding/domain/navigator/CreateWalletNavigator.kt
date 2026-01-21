package com.mangala.wallet.features.onboarding.domain.navigator

import cafe.adriel.voyager.core.registry.ScreenRegistry
import com.mangala.wallet.model.blockchain.BlockchainNetworkData
import com.mangala.wallet.model.blockchain.BlockchainType
import com.mangala.wallet.model.blockchain.PrimaryNetworkConfig
import com.mangala.wallet.ui.SharedScreen

interface CreateWalletNavigator {
    fun getCreateWalletScreen(): SharedScreen
    fun getSetupPinScreen(onSuccess: () -> Unit): SharedScreen
}

abstract class DefaultCreateWalletNavigator : CreateWalletNavigator {
    override fun getSetupPinScreen(onSuccess: () -> Unit): SharedScreen {
        val defaultNetwork = BlockchainNetworkData.getDefaultNetwork(false)
        return SharedScreen.SetupPinScreen(
            pinCase = SharedScreen.SetupPinScreen.SetupPinScreenCase.CREATE_NEW_WALLET.name,
            blockchainUid = defaultNetwork.blockChainUid,
            onPinSetupSuccess = onSuccess
        )
    }

    abstract override fun getCreateWalletScreen(): SharedScreen
}

class AntelopeCreateWalletNavigator: DefaultCreateWalletNavigator() {
    override fun getCreateWalletScreen(): SharedScreen {
        return SharedScreen.AntelopeCreateAccountV2Screen
    }
}

class EvmCreateWalletNavigator: DefaultCreateWalletNavigator() {
    override fun getCreateWalletScreen(): SharedScreen {
        return SharedScreen.CreateWalletGuideScreen
    }
}

object CreateWalletNavigatorFactory {
    fun create(blockchainType: BlockchainType = PrimaryNetworkConfig.primaryBlockchain): CreateWalletNavigator {
        return when (blockchainType) {
            BlockchainType.Ethereum -> EvmCreateWalletNavigator()
            BlockchainType.Eos -> AntelopeCreateWalletNavigator()
            else -> EvmCreateWalletNavigator() // Default fallback
        }
    }
}