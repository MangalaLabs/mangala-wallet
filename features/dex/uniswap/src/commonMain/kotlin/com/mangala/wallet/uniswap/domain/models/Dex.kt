package com.mangala.wallet.uniswap.domain.models

import com.mangala.wallet.model.blockchain.Chain

sealed class Dex(val name: String, val addresses: List<kotlin.Pair<Chain, DexAddress>>, val urlImage: String) {
    data object Biswap : Dex(
        name = "Biswap",
        addresses = listOf(
            Chain.BinanceSmartChain to DexAddress(
                routerAddress = "0x3a6d8cA21D1CF76F653A67577FA0D27453350dD8",
                factoryAddress = "0x858E3312ed3A876947EA49d572A7C42DE08af7EE",
                initCodeHash = "0xfea293c909d87cd4153593f077b76bb7e94340200f4ee84211ae8e4f9bd7ffdf"
            ),
            Chain.BinanceSmartChainTestNet to DexAddress(
                routerAddress = "0xD1F83598095740c6eA4F7E58329422Bff645e473",
                factoryAddress = "0xAcC07ca87F28110dE0E0629747710e5688704601",
                initCodeHash = "0xbef5f75196fa9b3315f482d8afacf5ee250880ddd333541800afc36fc5fa56f0"
            )
        ),
        urlImage = "https://cryptologos.cc/logos/biswap-bsw-logo.svg?v=029"
    )

    data object PancakeSwap : Dex(
        name = "PancakeSwap",
        addresses = listOf(
            Chain.BinanceSmartChain to DexAddress(
                routerAddress = "0x10ED43C718714eb63d5aA57B78B54704E256024E",
                factoryAddress = "0xcA143Ce32Fe78f1f7019d7d551a6402fC5350c73",
                initCodeHash = "0x00fb7f630766e6a796048ea87d01acd3068e8ff67d078148a3fa3f4a84f69bd5"
            ),
            Chain.BinanceSmartChainTestNet to DexAddress(
                routerAddress = "0x9Ac64Cc6e4415144C455BD8E4837Fea55603e5c3",
                factoryAddress = "0xB7926C0430Afb07AA7DEfDE6DA862aE0Bde767bc",
                initCodeHash = "0xecba335299a6693cb2ebc4782e74669b84290b6378ea3a3873c7231a8d7d1074"
            )
        ),
        urlImage = "https://cryptologos.cc/logos/pancakeswap-cake-logo.svg?v=029"
    )

    data object UniSwap : Dex(
        name = "UniSwap",
        addresses = listOf(
            Chain.BinanceSmartChain to DexAddress(
                routerAddress = "",
                factoryAddress = "",
                initCodeHash = ""
            ),
            Chain.Mumbai to DexAddress(
                routerAddress = "0x7a250d5630B4cF539739dF2C5dAcb4c659F2488D",
                factoryAddress = "0x5C69bEe701ef814a2B6a3EDD4B1652CB9cc5aA6f",
                initCodeHash = "0x96e8ac4277198ff8b6f785478aa9a39f403cb768dd02cbee326c3e7da348845f"
            ),
            Chain.EthereumSepolia to DexAddress(
                routerAddress = "0x86dcd3293C53Cf8EFd7303B57beb2a3F671dDE98",
                factoryAddress = "0xc9f18c25Cfca2975d6eD18Fc63962EBd1083e978",
                initCodeHash = "0x0efd7612822d579e24a8851501d8c2ad854264a1050e3dfcee8afcca08f80a86"
            ),
            Chain.EthereumHolesky to DexAddress( //TODO: LEONARD - Update this address(Request LinhNV, DangNH support)
                routerAddress = "",
                factoryAddress = "",
                initCodeHash = ""
            ),
        ),
        urlImage = "https://cryptologos.cc/logos/uniswap-uni-logo.svg?v=029"
    )

    companion object {
        fun fromName(name: String): Dex {
            return when (name) {
                "Biswap" -> Biswap
                "PancakeSwap" -> PancakeSwap
                "UniSwap" -> UniSwap
                else -> throw IllegalArgumentException("Invalid dex: $name")
            }
        }
    }
}
