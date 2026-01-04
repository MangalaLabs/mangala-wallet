package com.mangala.wallet.model.blockchain

import com.mangala.wallet.model.BuildKonfig
import dev.icerock.moko.resources.ImageResource
import com.mangala.wallet.mokoresources.MR


sealed class BlockchainType(val name: String, val supportedAddressTypes: List<AddressType>, val networkType: NetworkType, val chainId: String, val isTestnet: Boolean) {

    object Bitcoin : BlockchainType("Bitcoin",  listOf(AddressType.Bip44, AddressType.Bip49, AddressType.Bip84), NetworkType.BITCOIN, "", isTestnet = false)
    object BitcoinTestnet4 : BlockchainType("Bitcoin Testnet4",  listOf(AddressType.Bip44, AddressType.Bip49, AddressType.Bip84), NetworkType.BITCOIN, "", isTestnet = true)

    object BitcoinCash : BlockchainType("Bitcoin Cash", listOf(AddressType.Bip44), NetworkType.BITCOIN, "", isTestnet = false)

    object ECash : BlockchainType("ECash", listOf(AddressType.Bip44), NetworkType.OTHER, "", isTestnet = false)

    object Litecoin : BlockchainType("Litecoin", listOf(AddressType.Bip44), NetworkType.BITCOIN, "", isTestnet = false)

    object Dash : BlockchainType("Dash", listOf(AddressType.Bip44), NetworkType.BITCOIN, "", isTestnet = false)

    object Zcash : BlockchainType("Zcash", listOf(AddressType.Bip44), NetworkType.BITCOIN, "", isTestnet = false)

    object Ethereum : BlockchainType("Ethereum", listOf(AddressType.Bip44), NetworkType.EVM, "", isTestnet = false)
    object EthereumGoerli : BlockchainType("Ethereum Goerli", listOf(AddressType.Bip44), NetworkType.EVM, "", isTestnet = true)
    object EthereumSepolia : BlockchainType("Ethereum Sepolia", listOf(AddressType.Bip44), NetworkType.EVM, "", isTestnet = true)
    object EthereumHolesky : BlockchainType("Ethereum Holesky", listOf(AddressType.Bip44), NetworkType.EVM, "", isTestnet = true)

    object BinanceSmartChain : BlockchainType("Binance Smart Chain", listOf(AddressType.Bip44), NetworkType.EVM, "", isTestnet = false)
    object BinanceSmartChainTestNet : BlockchainType("BSC Test Net",  listOf(AddressType.Bip44), NetworkType.EVM, "", isTestnet = true)

//    object BinanceChain : BlockchainType("Binance Chain", listOf(AddressType.Bip44))

    object Polygon : BlockchainType("Polygon", listOf(AddressType.Bip44), NetworkType.EVM, "", isTestnet = false)
    object PolygonMumbai : BlockchainType("Polygon Mumbai",  listOf(AddressType.Bip44), NetworkType.EVM, "", isTestnet = true)

    object Avalanche : BlockchainType("Avalanche", listOf(AddressType.Bip44), NetworkType.EVM, "", isTestnet = false)

    object Optimism : BlockchainType("Optimism", listOf(AddressType.Bip44), NetworkType.EVM, "", isTestnet = false)

    object ArbitrumOne : BlockchainType("Arbitrum One", listOf(AddressType.Bip44), NetworkType.EVM, "", isTestnet = false)

    object Solana : BlockchainType("Solana", listOf(AddressType.Bip44), NetworkType.OTHER, "", isTestnet = false)

    object Gnosis : BlockchainType("Gnosis", listOf(AddressType.Bip44), NetworkType.EVM, "", isTestnet = false)

    object Fantom : BlockchainType("Fantom", listOf(AddressType.Bip44), NetworkType.EVM, "", isTestnet = false)

    object EosEvm: BlockchainType("Vaulta EVM", listOf(AddressType.Bip44), NetworkType.EVM, "", isTestnet = false)

    object Eos: BlockchainType("Vaulta", listOf(), NetworkType.ANTELOPE, "aca376f206b8fc25a6ed44dbdc66547c36c6c33e3a119ffbeaef943642f0e906", isTestnet = false)
    object EosJungleTestnet: BlockchainType("Vaulta Jungle Testnet", listOf(), NetworkType.ANTELOPE, "73e4385a2708e6d7048834fbc1079f2fabb17b3c125b146af438971e90716c4d", isTestnet = true)

    class Unsupported(val _uid: String) : BlockchainType("",listOf(AddressType.Bip44), NetworkType.UNSUPPORTED, "", isTestnet = true)

    /**
     When adding new network, be sure to add it to [com.mangala.wallet.model.blockchain.BlockchainNetworkData]
     */

    val uid: String
        get() = when (this) {
            is Bitcoin -> "bitcoin"
            is BitcoinTestnet4 -> "bitcoin-testnet4"
            is BitcoinCash -> "bitcoin-cash"
            is ECash -> "ecash"
            is Litecoin -> "litecoin"
            is Dash -> "dash"
            is Zcash -> "zcash"
            is Ethereum -> "ethereum"
            is EthereumGoerli -> "ethereum-goerli"
            is EthereumSepolia -> "ethereum-sepolia"
            is EthereumHolesky -> "ethereum-holesky"
            is BinanceSmartChain -> "binance-smart-chain"
//            is BinanceChain -> "binancecoin"
            is BinanceSmartChainTestNet -> "bsc-testnet"
            is Polygon -> "polygon-pos"
            is PolygonMumbai -> "polygon-mumbai"
            is Avalanche -> "avalanche"
            is Optimism -> "optimistic-ethereum"
            is ArbitrumOne -> "arbitrum-one"
            is Solana -> "solana"
            is Gnosis -> "gnosis"
            is Fantom -> "fantom"
            is EosEvm -> "eos-evm" // TODO: Insert data for chain in .sq files
            is EosJungleTestnet -> "eos-jungle-testnet"
            is Eos -> "eos"
            is Unsupported -> this._uid
        }

    val localImage: ImageResource?
        get() = when (this) {
            is Bitcoin -> MR.images.bitcoin
            is BitcoinTestnet4 -> MR.images.bitcoin
            is BitcoinCash -> MR.images.bitcoin_cash_circle
            is ECash -> MR.images.ecash
            is Litecoin -> MR.images.litecoin
            is Dash -> MR.images.dash
            is Zcash -> MR.images.zcash
            is Ethereum -> MR.images.ethereum
            is EthereumGoerli -> MR.images.ethereum
            is EthereumSepolia -> MR.images.ethereum
            is EthereumHolesky -> MR.images.ethereum
            is BinanceSmartChain -> MR.images.binance_smart_chain
//            is BinanceChain -> "https://assets.coingecko.com/coins/images/825/large/bnb-icon2_2x.png"
            is BinanceSmartChainTestNet -> MR.images.binance_smart_chain
            is Polygon -> MR.images.polygon
            is PolygonMumbai -> MR.images.polygon
            is Avalanche -> MR.images.avalanche
            is Optimism -> MR.images.optimism
            is ArbitrumOne -> MR.images.arbitrum
            is Solana -> MR.images.solana
            is Gnosis -> MR.images.gnosis
            is Fantom -> MR.images.fantom
            is EosEvm -> MR.images.vaulta
            is Eos -> MR.images.vaulta
            is EosJungleTestnet -> MR.images.vaulta
            is Unsupported -> null //TODO: set default for it
        }

    val endPointCovalenthq: String
        get() = when (this) {
            is Bitcoin -> ""
            is BitcoinTestnet4 -> ""
            is BitcoinCash -> ""
            is ECash -> ""
            is Litecoin -> ""
            is Dash -> ""
            is Zcash -> ""
            is Ethereum -> "eth-mainnet"  //1
            is EthereumGoerli -> "eth-goerli" //5
            is EthereumSepolia -> "eth-sepolia" //11155111
            is EthereumHolesky -> "eth-holesky" //11155111
            is BinanceSmartChain -> "bsc-mainnet" //56
//            is BinanceChain -> ""
            is BinanceSmartChainTestNet -> "bsc-testnet" //97
            is Polygon -> "matic-mainnet" //137
            is PolygonMumbai -> "matic-mumbai" //80001
            is Avalanche -> ""
            is Optimism -> ""
            is ArbitrumOne -> ""
            is Solana -> ""
            is Gnosis -> ""
            is Fantom -> ""
            is EosEvm, is EosJungleTestnet, is Eos -> ""
            is Unsupported -> this._uid
        }

    fun getRpcUrl(): List<String>  = when (this){
        is Bitcoin -> listOf("")
        is BitcoinTestnet4 -> listOf("")
        is BitcoinCash -> listOf("")
        is ECash -> listOf("")
        is Litecoin -> listOf("")
        is Dash -> listOf("")
        is Zcash -> listOf("")
        is Ethereum -> infuraHttp("mainnet")
        is EthereumGoerli -> infuraHttp("goerli")
        is EthereumSepolia -> infuraHttp("sepolia")
        is EthereumHolesky -> listOf(
            "https://ethereum-holesky-rpc.publicnode.com",
            "https://rpc.holesky.ethpandaops.io",
            "https://holesky-rpc.nocturnode.tech",
            "https://endpoints.omniatech.io/v1/eth/holesky/public",
            "https://rpc-holesky.rockx.com",
            "https://1rpc.io/holesky",
            "https://ethereum-holesky.blockpi.network/v1/rpc/public"
        )
        is BinanceSmartChain -> listOf(
            "https://bsc-dataseed.binance.org",
            "https://bsc-dataseed1.defibit.io/",
            "https://bsc-dataseed1.ninicoin.io/",
            "https://bsc-dataseed2.defibit.io/",
            "https://bsc-dataseed3.defibit.io/",
            "https://bsc-dataseed4.defibit.io/",
            "https://bsc-dataseed2.ninicoin.io/",
            "https://bsc-dataseed3.ninicoin.io/",
            "https://bsc-dataseed4.ninicoin.io/",
            "https://bsc-dataseed1.binance.org/",
            "https://bsc-dataseed2.binance.org/",
            "https://bsc-dataseed3.binance.org/",
            "https://bsc-dataseed4.binance.org/"
        )
//        is BinanceChain -> listOf("")
        is BinanceSmartChainTestNet -> listOf("https://data-seed-prebsc-1-s1.binance.org:8545")
        is Polygon -> infuraHttp("polygon-mainnet")
        is PolygonMumbai -> infuraHttp("polygon-mumbai")
        is Avalanche -> listOf("https://api.avax.network/ext/bc/C/rpc")
        is Optimism -> listOf("https://mainnet.optimism.io")
        is ArbitrumOne -> listOf("https://arb1.arbitrum.io/rpc")
        is Solana -> listOf("")
        is Gnosis -> listOf("https://rpc.gnosischain.com")
        is Fantom -> listOf("https://rpc.fantom.network")
        is EosEvm -> listOf("https://api.evm.eosnetwork.com")
        is Eos -> listOf("https://eos.greymass.com/")
        is EosJungleTestnet -> listOf("https://jungle4.greymass.com/")
        is Unsupported -> listOf("")
    }

    private fun infuraHttp(subdomain: String): List<String> {
        return listOf("https://$subdomain.infura.io/v3/${BuildKonfig.INFURA_API_KEY}")
    }

    override fun equals(other: Any?): Boolean {
        return other is BlockchainType && other.uid == uid
    }

    override fun hashCode(): Int {
        return uid.hashCode()
    }

    override fun toString() = when (this) {
        Bitcoin -> "bitcoin"
        BitcoinTestnet4 -> "bitcoin-testnet4"
        BitcoinCash -> "bitcoinCash"
        ECash -> "ecash"
        Litecoin -> "litecoin"
        Dash -> "dash"
        Zcash -> "zcash"
        Ethereum -> "ethereum"
        EthereumGoerli -> "ethereum-goerli"
        EthereumSepolia -> "ethereum-sepolia"
        EthereumHolesky -> "ethereum-holesky"
        BinanceSmartChain -> "binanceSmartChain"
        BinanceSmartChainTestNet -> "bsc-testnet"
        Polygon -> "polygon"
        PolygonMumbai -> "polygon-mumbai"
        Avalanche -> "avalanche"
        ArbitrumOne -> "arbitrumOne"
//        BinanceChain -> "binanceChain"
        Optimism -> "optimism"
        Solana -> "solana"
        Gnosis -> "gnosis"
        Fantom -> "fantom"
        EosEvm -> "eos-evm" // TODO: Add support
        Eos -> "eos"
        EosJungleTestnet -> "eos-jungle-testnet"
        is Unsupported -> "unsupported|$uid"
    }

    fun getNativeTokenSymbol(): String = when(this) {
        Bitcoin, BitcoinTestnet4 -> "BTC"
        BinanceSmartChain -> "BNB"
        BinanceSmartChainTestNet -> "BNBT"
        ArbitrumOne -> TODO()
        Avalanche -> TODO()
        Bitcoin -> TODO()
        BitcoinCash -> TODO()
        Dash -> TODO()
        ECash -> TODO()
        Eos -> "A"
        EosEvm -> TODO()
        EosJungleTestnet -> "A"
        Ethereum, EthereumGoerli, EthereumHolesky, EthereumSepolia -> "ETH"
        Fantom -> TODO()
        Gnosis -> TODO()
        Litecoin -> TODO()
        Optimism -> TODO()
        Polygon -> TODO()
        PolygonMumbai -> TODO()
        Solana -> TODO()
        is Unsupported -> TODO()
        Zcash -> TODO()
    }

    /**
     * Checks if this blockchain is an EOS network.
     * Returns true for EOS mainnet and testnet.
     */
    fun isEosNetwork(): Boolean = when(this) {
        Eos, EosJungleTestnet -> true
        else -> false
    }

    fun getNativeTokenName(): String = when(this) {
        BinanceSmartChainTestNet, BinanceSmartChain -> "Binance Coin"
        ArbitrumOne -> TODO()
        Avalanche -> TODO()
        Bitcoin, BitcoinTestnet4 -> "Bitcoin"
        BitcoinCash -> TODO()
        Dash -> TODO()
        ECash -> TODO()
        Eos -> TODO()
        EosEvm -> TODO()
        EosJungleTestnet -> TODO()
        Ethereum, EthereumGoerli, EthereumHolesky, EthereumSepolia -> "Ether"
        Fantom -> TODO()
        Gnosis -> TODO()
        Litecoin -> TODO()
        Optimism -> TODO()
        Polygon -> TODO()
        PolygonMumbai -> TODO()
        Solana -> TODO()
        is Unsupported -> TODO()
        Zcash -> TODO()
    }

    companion object {
        fun fromUid(uid: String): BlockchainType =
            when (uid) {
                "bitcoin" -> Bitcoin
                "bitcoin-testnet4" -> BitcoinTestnet4
                "bitcoin-cash" -> BitcoinCash
                "ecash" -> ECash
                "litecoin" -> Litecoin
                "dash" -> Dash
                "zcash" -> Zcash
                "ethereum" -> Ethereum
                "ethereum-goerli" -> EthereumGoerli
                "ethereum-sepolia" -> EthereumSepolia
                "ethereum-holesky" -> EthereumHolesky
                "binance-smart-chain" -> BinanceSmartChain
//                "binancecoin" -> BinanceChain
                "bsc-testnet" -> BinanceSmartChainTestNet
                "polygon-pos" -> Polygon
                "polygon-mumbai" -> PolygonMumbai
                "avalanche" -> Avalanche
                "optimistic-ethereum" -> Optimism
                "arbitrum-one" -> ArbitrumOne
                "solana" -> Solana
                "gnosis" -> Gnosis
                "fantom" -> Fantom
                "eos-evm" -> EosEvm
                "eos" -> Eos
                "eos-jungle-testnet" -> EosJungleTestnet
                else -> Unsupported(uid)
            }

        fun fromChainId(chainId: Any): BlockchainType =
            when (chainId) {
//                "bitcoin" -> Bitcoin
//                "bitcoin-cash" -> BitcoinCash
//                "ecash" -> ECash
//                "litecoin" -> Litecoin
//                "dash" -> Dash
//                "zcash" -> Zcash
                1L-> Ethereum
                5L -> EthereumGoerli
                11155111L -> EthereumSepolia
                56L -> BinanceSmartChain
//                "binancecoin" -> BinanceChain
                97L -> BinanceSmartChainTestNet
                137L -> Polygon
                80001L -> PolygonMumbai
//                "avalanche" -> Avalanche
//                "optimistic-ethereum" -> Optimism
//                "arbitrum-one" -> ArbitrumOne
//                "solana" -> Solana
//                "gnosis" -> Gnosis
//                "fantom" -> Fantom
//                "eos-evm" -> EosEvm
                EosJungleTestnet.chainId -> EosJungleTestnet
                Eos.chainId -> Eos
                else -> Unsupported("")
            }
    }

}