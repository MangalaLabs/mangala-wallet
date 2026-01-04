package com.mangala.features.browser

actual class OpenBrowser {

    private var openBrowserClick: OpenBrowserClick? = null

    fun setOpenBrowserClick(listener: OpenBrowserClick) {
        this.openBrowserClick = listener
    }

    actual fun openNewScreen(){

    }

    actual fun openBrowser(
        chainId: Long,
        address: String,
        rpcUrl: String,
        accountId: String
    ) {
        openBrowserClick?.openBrowserAction(true)
    }

    actual fun putData(
        chainId: Long,
        address: String,
        rpcUrl: String,
        accountId: String
    ) {
        openBrowserClick?.openBrowser(chainId, address, rpcUrl, accountId)
    }
}