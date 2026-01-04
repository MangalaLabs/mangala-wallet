package com.mangala.browser_bridge_base.walletconnect

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen

class WalletConnectScreen(
    val data: String,
    val chainId: Long,
    val url: String,
    val accountId: String,
    val address: String,
    val tabId: String
): Screen {

    @Composable
    override fun Content() {

    }

}