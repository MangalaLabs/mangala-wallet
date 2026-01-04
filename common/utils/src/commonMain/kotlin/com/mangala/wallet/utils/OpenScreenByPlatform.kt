package com.mangala.wallet.utils

expect class OpenScreenByPlatform {
    fun openNewScreen()

    fun openBrowser(chainId: Long, address: String, rpcUrl: String, accountId: String)

}