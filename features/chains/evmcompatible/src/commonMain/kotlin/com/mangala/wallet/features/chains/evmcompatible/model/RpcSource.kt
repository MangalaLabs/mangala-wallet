package com.mangala.wallet.features.chains.evmcompatible.model

sealed class RpcSource() {
    class Http(val urls: List<String>, val auth: String?) : RpcSource()
    class WebSocket(val url: String, val auth: String?) : RpcSource()

    companion object {
        private fun infuraHttp(subdomain: String, projectId: String, projectSecret: String? = null): Http {
            return Http(listOf("https://$subdomain.infura.io/v3/$projectId"), projectSecret)
        }

        private fun infuraWebSocket(subdomain: String, projectId: String, projectSecret: String? = null): WebSocket {
            return WebSocket("https://$subdomain.infura.io/ws/v3/$projectId", projectSecret)
        }

        fun ethereumInfuraHttp(projectId: String, projectSecret: String? = null): Http {
            return infuraHttp("mainnet", projectId, projectSecret)
        }

        fun goerliInfuraHttp(projectId: String, projectSecret: String? = null): Http {
            return infuraHttp("goerli", projectId, projectSecret)
        }

        fun ethereumInfuraWebSocket(projectId: String, projectSecret: String? = null): WebSocket {
            return infuraWebSocket("mainnet", projectId, projectSecret)
        }

        fun goerliInfuraWebSocket(projectId: String, projectSecret: String? = null): WebSocket {
            return infuraWebSocket("goerli", projectId, projectSecret)
        }

        fun bscRpcHttp(): Http {
            return Http(listOf("https://bscrpc.com"), null)
        }

        fun binanceSmartChainHttp(): Http {
            return Http(
                listOf(
                    "https://bsc-dataseed.binance.org/",
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
                ),
                null
            )
        }

        fun polygonRpcHttp(): Http {
            return Http(listOf("https://polygon-rpc.com"), null)
        }

        fun optimismRpcHttp(): Http {
            return Http(listOf("https://mainnet.optimism.io"), null)
        }

        fun arbitrumOneRpcHttp(): Http {
            return Http(listOf("https://arb1.arbitrum.io/rpc"), null)
        }

        fun avaxNetworkHttp(): Http {
            return Http(listOf("https://api.avax.network/ext/bc/C/rpc"), null)
        }

        fun gnosisRpcHttp(): Http {
            return Http(listOf("https://rpc.gnosischain.com"), null)
        }

        fun fantomRpcHttp(): Http {
            return Http(listOf("https://rpc.fantom.network"), null)
        }

    }
}
