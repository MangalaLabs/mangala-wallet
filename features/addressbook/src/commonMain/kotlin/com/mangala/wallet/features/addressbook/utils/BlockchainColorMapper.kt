package com.mangala.wallet.features.addressbook.utils

import com.mangala.wallet.model.blockchain.BlockchainType

/**
 * Maps blockchain types to their brand colors
 */
object BlockchainColorMapper {
    
    fun getColorForBlockchain(blockchainType: BlockchainType): String {
        return when (blockchainType) {
            // Bitcoin - Orange
            BlockchainType.Bitcoin,
            BlockchainType.BitcoinTestnet4 -> "#F7931A"
            
            // Bitcoin Cash - Green
            BlockchainType.BitcoinCash -> "#0AC18E"
            
            // Other Bitcoin-like
            BlockchainType.Litecoin -> "#BFBBBB"
            BlockchainType.Dash -> "#008DE4"
            BlockchainType.Zcash -> "#F4B728"
            BlockchainType.ECash -> "#0074C2"
            
            // Ethereum - Purple
            BlockchainType.Ethereum,
            BlockchainType.EthereumGoerli,
            BlockchainType.EthereumSepolia,
            BlockchainType.EthereumHolesky -> "#627EEA"
            
            // Binance - Yellow
            BlockchainType.BinanceSmartChain,
            BlockchainType.BinanceSmartChainTestNet -> "#F0B90B"
            
            // Polygon - Purple
            BlockchainType.Polygon,
            BlockchainType.PolygonMumbai -> "#8247E5"
            
            // EOS/Vaulta - Black/Dark
            BlockchainType.Eos,
            BlockchainType.EosJungleTestnet,
            BlockchainType.EosEvm -> "#000000"
            
            // Avalanche - Red
            BlockchainType.Avalanche -> "#E84142"
            
            // Optimism - Red
            BlockchainType.Optimism -> "#FF0420"
            
            // Arbitrum - Blue
            BlockchainType.ArbitrumOne -> "#2D374B"
            
            // Solana - Gradient (using primary color)
            BlockchainType.Solana -> "#14F195"
            
            // Gnosis - Green
            BlockchainType.Gnosis -> "#04795B"
            
            // Fantom - Blue
            BlockchainType.Fantom -> "#1969FF"
            
            // Default fallback
            else -> "#6B7280" // Gray
        }
    }
}