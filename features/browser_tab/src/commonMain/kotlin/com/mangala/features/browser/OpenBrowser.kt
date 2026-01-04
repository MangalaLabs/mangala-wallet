package com.mangala.features.browser

expect class OpenBrowser {

    fun openNewScreen()

    fun openBrowser(chainId: Long, address: String, rpcUrl: String, accountId: String)
    fun putData(chainId: Long, address: String, rpcUrl: String, accountId: String)
}