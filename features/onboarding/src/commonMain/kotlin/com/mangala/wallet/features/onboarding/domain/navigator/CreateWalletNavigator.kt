package com.mangala.wallet.features.onboarding.domain.navigator

import com.mangala.wallet.model.blockchain.BlockchainType
import com.mangala.wallet.model.blockchain.NetworkType
import com.mangala.wallet.model.blockchain.PrimaryNetworkConfig
import com.mangala.wallet.ui.SharedScreen

interface CreateWalletNavigator {
    fun getCreateWalletScreen(): SharedScreen
}

class AntelopeCreateWalletNavigator : CreateWalletNavigator {
    override fun getCreateWalletScreen(): SharedScreen {
        return SharedScreen.AntelopeCreateAccountV2Screen
    }
}

class EvmCreateWalletNavigator : CreateWalletNavigator {
    override fun getCreateWalletScreen(): SharedScreen {
        return SharedScreen.CreateWalletGuideScreen
    }
}

class BitcoinCreateWalletNavigator : CreateWalletNavigator {
    override fun getCreateWalletScreen(): SharedScreen {
        return SharedScreen.BitcoinCreateAccountScreen()
    }
}

object CreateWalletNavigatorFactory {
    fun create(blockchainType: BlockchainType = PrimaryNetworkConfig.primaryBlockchain): CreateWalletNavigator {
        return when (blockchainType.networkType) {
            NetworkType.EVM -> EvmCreateWalletNavigator()
            NetworkType.ANTELOPE -> AntelopeCreateWalletNavigator()
            NetworkType.BITCOIN -> BitcoinCreateWalletNavigator()
            NetworkType.OTHER, NetworkType.UNSUPPORTED -> EvmCreateWalletNavigator()
        }
    }
}