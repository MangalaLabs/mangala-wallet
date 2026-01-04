package com.mangala.features.wallet.presentationv2

import cafe.adriel.voyager.core.screen.Screen
import com.mangala.wallet.model.blockchain.NetworkType
import com.mangala.features.wallet.presentationv2.antelope.AntelopeWalletScreenV2
import com.mangala.features.wallet.presentationv2.bitcoin.BitcoinWalletScreenV2
import com.mangala.features.wallet.presentationv2.evm.EVMWalletScreenV2

/**
 * Factory for creating network-specific wallet screens
 */
object WalletScreenFactoryV2 {
    
    /**
     * Creates the appropriate wallet screen based on the network type
     */
    fun createWalletScreen(networkType: NetworkType): Screen {
        return when (networkType) {
            NetworkType.ANTELOPE -> AntelopeWalletScreenV2()
            NetworkType.BITCOIN -> BitcoinWalletScreenV2()
            NetworkType.EVM -> EVMWalletScreenV2()
            NetworkType.OTHER, NetworkType.UNSUPPORTED -> AntelopeWalletScreenV2() // Default fallback
        }
    }
    
    /**
     * Creates wallet screen from blockchain ID
     */
    fun createWalletScreenFromBlockchainId(blockchainId: String): Screen {
        return when {
            isAntelopeChain(blockchainId) -> AntelopeWalletScreenV2()
            isBitcoinChain(blockchainId) -> BitcoinWalletScreenV2()
            isEVMChain(blockchainId) -> EVMWalletScreenV2()
            else -> AntelopeWalletScreenV2() // Default fallback
        }
    }
    
    private fun isAntelopeChain(blockchainId: String): Boolean {
        return blockchainId in listOf(
            "eos",
            "wax",
            "telos",
            "vaulta",
            "antelope"
        )
    }
    
    private fun isBitcoinChain(blockchainId: String): Boolean {
        return blockchainId in listOf(
            "bitcoin",
            "btc",
            "bitcoin-testnet"
        )
    }
    
    private fun isEVMChain(blockchainId: String): Boolean {
        return blockchainId in listOf(
            "ethereum",
            "eth",
            "polygon",
            "matic",
            "binance",
            "bsc",
            "avalanche",
            "avax",
            "arbitrum",
            "optimism"
        )
    }
}