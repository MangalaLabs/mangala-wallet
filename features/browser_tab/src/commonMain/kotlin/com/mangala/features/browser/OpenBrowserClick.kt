package com.mangala.features.browser

interface OpenBrowserClick {
    fun openBrowserAction(isClick: Boolean)

    fun openBrowser(
        chainId: Long,
        address: String,
        rpcUrl: String,
        accountId: String
    )
}