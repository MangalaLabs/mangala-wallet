package com.mangala.wallet.model.blockchain

enum class Chain(
    val id: Int,
    val coinType: Int,
    val blockIntervalMillis: Long,
    val syncIntervalMillis: Long, // sync interval accounting for Covalent data delay
    val isEIP1559Supported: Boolean
) {
    Ethereum(1, 60, 15_000, 30_000, true),
    BinanceSmartChain(56, 60, 3_000, 30_000, false),
    BinanceSmartChainTestNet(97, 60, BinanceSmartChain.blockIntervalMillis,30_000, false),
    Polygon(137, 60, 3_000, 30_000, true),
    Optimism(10, 60, 3_000, 30_000, false),
    ArbitrumOne(42161, 60, 1_000,30_000, false),
    Avalanche(43114, 60, 3_000,30_000, true),
    Gnosis(100, 60, 5_000, 30_000, true),
    Fantom(250, 60, 3_000,30_000, false),
    EthereumGoerli(5, 1, Ethereum.blockIntervalMillis, 30_000, true),
    EthereumSepolia(11155111, 1, Ethereum.blockIntervalMillis, 30_000, true),
    EthereumHolesky(17000, 1, Ethereum.blockIntervalMillis, 30_000, true),
    ArbitrumGoerli(421613,1, ArbitrumOne.blockIntervalMillis, 30_000,false),
    Mumbai(80001,60, Polygon.blockIntervalMillis, 30_000,true),
    EosEvm(17777, 60, 1_000, 5_000, false),
    Eos(17777, 60, 1_000, 5_000, false),
    EosJungleTestnet(17777, 60, 1_000, 5_000, false);

    val isMainNet = coinType != 1

    fun toBlockchainType(): BlockchainType = when(this) {
        ArbitrumOne -> BlockchainType.ArbitrumOne
        Avalanche -> BlockchainType.Avalanche
        BinanceSmartChain -> BlockchainType.BinanceSmartChain
        BinanceSmartChainTestNet -> BlockchainType.BinanceSmartChainTestNet
        Ethereum -> BlockchainType.Ethereum
        EthereumGoerli -> BlockchainType.EthereumGoerli
        EthereumSepolia -> BlockchainType.EthereumSepolia
        EthereumHolesky -> BlockchainType.EthereumHolesky
        Fantom -> BlockchainType.Fantom
        Gnosis -> BlockchainType.Gnosis
        Optimism -> BlockchainType.Optimism
        Polygon -> BlockchainType.Polygon
        Mumbai -> BlockchainType.PolygonMumbai
        EosEvm -> BlockchainType.EosEvm
        else -> TODO()
    }

    companion object {
        fun fromBlockchainType(blockchainType: BlockchainType): Chain = when(blockchainType) {
            BlockchainType.ArbitrumOne -> ArbitrumOne
            BlockchainType.Avalanche -> Avalanche
            BlockchainType.BinanceSmartChain -> BinanceSmartChain
            BlockchainType.BinanceSmartChainTestNet -> BinanceSmartChainTestNet
            BlockchainType.Ethereum -> Ethereum
            BlockchainType.EthereumGoerli -> EthereumGoerli
            BlockchainType.EthereumSepolia -> EthereumSepolia
            BlockchainType.EthereumHolesky -> EthereumHolesky
            BlockchainType.Fantom -> Fantom
            BlockchainType.Gnosis -> Gnosis
            BlockchainType.Optimism -> Optimism
            BlockchainType.Polygon -> Polygon
            BlockchainType.PolygonMumbai -> Mumbai
            BlockchainType.EosEvm -> EosEvm
            BlockchainType.Eos -> Eos
            BlockchainType.EosJungleTestnet -> EosJungleTestnet
            else -> TODO()
        }
    }
}
