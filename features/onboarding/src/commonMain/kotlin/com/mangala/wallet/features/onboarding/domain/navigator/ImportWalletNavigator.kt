package com.mangala.wallet.features.onboarding.domain.navigator

import com.mangala.wallet.model.blockchain.BlockchainType
import com.mangala.wallet.model.blockchain.NetworkType
import com.mangala.wallet.model.blockchain.PrimaryNetworkConfig
import com.mangala.wallet.ui.SharedScreen

interface ImportWalletNavigator {
    /**
     * Returns the ImportWalletScreen for this wallet type
     */
    fun getImportWalletScreen(): SharedScreen
}

class AntelopeImportWalletNavigator : ImportWalletNavigator {
    override fun getImportWalletScreen(): SharedScreen {
        return SharedScreen.ImportPrivateKeyScreen
    }
}

class EvmImportWalletNavigator : ImportWalletNavigator {
    override fun getImportWalletScreen(): SharedScreen {
        return SharedScreen.RestoreRecoveryPhraseScreen()
    }
}

class BitcoinImportWalletNavigator : ImportWalletNavigator {
    override fun getImportWalletScreen(): SharedScreen {
        // Bitcoin uses recovery phrase same as EVM
        return SharedScreen.RestoreRecoveryPhraseScreen()
    }
}

object ImportWalletNavigatorFactory {
    fun create(
        blockchainType: BlockchainType = PrimaryNetworkConfig.primaryBlockchain
    ): ImportWalletNavigator {
        return when (blockchainType.networkType) {
            NetworkType.EVM -> EvmImportWalletNavigator()
            NetworkType.ANTELOPE -> AntelopeImportWalletNavigator()
            NetworkType.BITCOIN -> BitcoinImportWalletNavigator()
            NetworkType.OTHER, NetworkType.UNSUPPORTED -> EvmImportWalletNavigator()
        }
    }
}
