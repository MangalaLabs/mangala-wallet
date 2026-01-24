package com.mangala.wallet.model.blockchain

import dev.icerock.moko.resources.ImageResource

data class BlockchainNetworkData(
    val blockchainType: BlockchainType,
    val blockChainUid: String,
    val chainId: Long,
    val localImage: ImageResource?,
    val isEIP1559Supported: Boolean,
    val isTestNet: Boolean = false
) {
    val name = blockchainType.name

    companion object {
        fun getAllBlockchainNetworkSupported(includeDebugNetworks: Boolean): List<BlockchainNetworkData> {
            val blockchainNetworks = listOf(
                BlockchainNetworkData(
                    BlockchainType.Bitcoin,
                    BlockchainType.Bitcoin.uid,
                    1,
                    BlockchainType.Bitcoin.localImage,
                    isEIP1559Supported = false,
                    isTestNet = false
                ),
                BlockchainNetworkData(
                    BlockchainType.BitcoinTestnet4,
                    BlockchainType.BitcoinTestnet4.uid,
                    1,
                    BlockchainType.Bitcoin.localImage,
                    isEIP1559Supported = false,
                    isTestNet = true
                ),
                // Uncommented additional Bitcoin-based networks
                // Note: These are commented in database but we'll leave them commented here too for consistency
                BlockchainNetworkData(BlockchainType.Ethereum,BlockchainType.Ethereum.uid,1,  BlockchainType.Ethereum.localImage,
                    isEIP1559Supported = true,
                    isTestNet = false
                ),
                BlockchainNetworkData(BlockchainType.EthereumGoerli,BlockchainType.EthereumGoerli.uid,5,  BlockchainType.EthereumGoerli.localImage,
                    isEIP1559Supported = true,
                    isTestNet = true
                ),
                BlockchainNetworkData(BlockchainType.EthereumSepolia,BlockchainType.EthereumSepolia.uid,11155111,  BlockchainType.EthereumSepolia.localImage,
                    isEIP1559Supported = true,
                    isTestNet = true
                ),
                BlockchainNetworkData(
                    BlockchainType.EthereumHolesky,
                    BlockchainType.EthereumHolesky.uid,
                    17000,
                    BlockchainType.EthereumHolesky.localImage,
                    isEIP1559Supported = true,
                    isTestNet = true
                ),
                BlockchainNetworkData(BlockchainType.BinanceSmartChain,BlockchainType.BinanceSmartChain.uid,56,  BlockchainType.BinanceSmartChain.localImage,
                    isEIP1559Supported = false,
                    isTestNet = false
                ),
                BlockchainNetworkData(BlockchainType.BinanceSmartChainTestNet,BlockchainType.BinanceSmartChainTestNet.uid,97,  BlockchainType.BinanceSmartChain.localImage,
                    isEIP1559Supported = false,
                    isTestNet = true
                ),
//            data.add(BlockchainNetworkData( BlockchainType.BinanceChain.name,BlockchainType.BinanceChain.uid,1,  BlockchainType.BinanceChain.urlImage,false))
                BlockchainNetworkData( BlockchainType.Polygon,BlockchainType.Polygon.uid,137,  BlockchainType.Polygon.localImage,
                    isEIP1559Supported = true,
                    isTestNet = false
                ),
                BlockchainNetworkData( BlockchainType.PolygonMumbai,BlockchainType.PolygonMumbai.uid,80001,  BlockchainType.PolygonMumbai.localImage,
                    isEIP1559Supported = true,
                    isTestNet = true
                ),
                // Additional EVM and other networks
                BlockchainNetworkData(BlockchainType.Avalanche, BlockchainType.Avalanche.uid, 43114, BlockchainType.Avalanche.localImage,
                    isEIP1559Supported = true,
                    isTestNet = false
                ),
                BlockchainNetworkData(BlockchainType.Optimism, BlockchainType.Optimism.uid, 10, BlockchainType.Optimism.localImage,
                    isEIP1559Supported = true,
                    isTestNet = false
                ),
                BlockchainNetworkData(BlockchainType.ArbitrumOne, BlockchainType.ArbitrumOne.uid, 42161, BlockchainType.ArbitrumOne.localImage,
                    isEIP1559Supported = false,
                    isTestNet = false
                ),
                BlockchainNetworkData(BlockchainType.Solana, BlockchainType.Solana.uid, 1, BlockchainType.Solana.localImage,
                    isEIP1559Supported = false,
                    isTestNet = false
                ),
                BlockchainNetworkData(BlockchainType.Gnosis, BlockchainType.Gnosis.uid, 100, BlockchainType.Gnosis.localImage,
                    isEIP1559Supported = true,
                    isTestNet = false
                ),
                BlockchainNetworkData(BlockchainType.Fantom, BlockchainType.Fantom.uid, 250, BlockchainType.Fantom.localImage,
                    isEIP1559Supported = true,
                    isTestNet = false
                ),
//                BlockchainNetworkData(BlockchainType.EosEvm, BlockchainType.ArbitrumOne.uid,42161,  BlockchainType.EosEvm.localImage,
//                    isEIP1559Supported = false,
//                    isTestNet = false
//                ),
                BlockchainNetworkData(BlockchainType.EosEvm, BlockchainType.EosEvm.uid,17777,  BlockchainType.EosEvm.localImage,
                    isEIP1559Supported = false,
                    isTestNet = false
                ),
                BlockchainNetworkData(BlockchainType.Eos, BlockchainType.Eos.uid, 194,  BlockchainType.Eos.localImage,
                    isEIP1559Supported = false,
                    isTestNet = false
                ),
                BlockchainNetworkData(BlockchainType.EosJungleTestnet, BlockchainType.EosJungleTestnet.uid, 194,  BlockchainType.EosJungleTestnet.localImage,
                    isEIP1559Supported = false,
                    isTestNet = true
                ),
            )

            return if (includeDebugNetworks) {
                blockchainNetworks
            } else {
                blockchainNetworks.filter { !it.isTestNet }
            }
        }

        fun getDefaultNetwork(includeDebugNetworks: Boolean): BlockchainNetworkData {
            val allNetworks = getAllBlockchainNetworkSupported(includeDebugNetworks)
            return allNetworks.find { it.blockchainType == PrimaryNetworkConfig.primaryBlockchain }
                ?: allNetworks.first()
        }

        fun getBlockchainByChainId(chainId: Long, includeDebugNetworks: Boolean): BlockchainNetworkData? {
            return getAllBlockchainNetworkSupported(includeDebugNetworks).find { it.chainId == chainId }
        }

        fun getBlockchainByUid(uid: String, includeDebugNetworks: Boolean): BlockchainNetworkData? {
            return getAllBlockchainNetworkSupported(includeDebugNetworks).find { it.blockChainUid == uid }
        }

        fun getBlockchainByType(type: BlockchainType, includeDebugNetworks: Boolean): BlockchainNetworkData? {
            return getAllBlockchainNetworkSupported(includeDebugNetworks).find { it.blockchainType == type }
        }

        fun getBlockchainByNameOrAbbreviation(nameOrAbbr: String, includeDebugNetworks: Boolean): BlockchainNetworkData? {
            val searchTerm = nameOrAbbr.trim()
            val networks = getAllBlockchainNetworkSupported(includeDebugNetworks)
            
            return networks.find { it.name.equals(searchTerm, ignoreCase = true) } 
                ?: networks.find { network ->
                    val abbreviations = getNetworkAbbreviations(network)
                    abbreviations.any { it.equals(searchTerm, ignoreCase = true) }
                }
        }

        private fun getNetworkAbbreviations(network: BlockchainNetworkData): List<String> {
            return when (network.blockchainType) {
                is BlockchainType.BinanceSmartChain -> listOf("BSC", "BNB Smart Chain", "BNB Chain")
                is BlockchainType.BinanceSmartChainTestNet -> listOf("BSC Testnet", "BSC Test")
                is BlockchainType.Bitcoin -> listOf("BTC")
                is BlockchainType.BitcoinTestnet4 -> listOf("BTC Testnet", "Bitcoin Test")
                is BlockchainType.Ethereum -> listOf("ETH", "Mainnet")
                is BlockchainType.EthereumGoerli -> listOf("Goerli", "ETH Goerli")
                is BlockchainType.EthereumSepolia -> listOf("Sepolia", "ETH Sepolia")
                is BlockchainType.EthereumHolesky -> listOf("Holesky", "ETH Holesky")
                is BlockchainType.Polygon -> listOf("MATIC", "Polygon PoS")
                is BlockchainType.PolygonMumbai -> listOf("Mumbai", "MATIC Mumbai", "Polygon Test")
                is BlockchainType.Avalanche -> listOf("AVAX", "Avalanche C-Chain")
                is BlockchainType.Optimism -> listOf("OP", "Optimistic")
                is BlockchainType.ArbitrumOne -> listOf("ARB", "Arbitrum")
                is BlockchainType.Solana -> listOf("SOL")
                is BlockchainType.Gnosis -> listOf("GNO", "xDAI", "Gnosis Chain")
                is BlockchainType.Fantom -> listOf("FTM", "Fantom Opera")
                is BlockchainType.EosEvm -> listOf("EOS EVM")
                is BlockchainType.Eos -> listOf("EOS")
                is BlockchainType.EosJungleTestnet -> listOf("Jungle", "EOS Jungle", "Jungle Testnet")
                else -> emptyList()
            }
        }
    }
}