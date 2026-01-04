package com.mangala.wallet.walletconnect

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import com.mangala.wallet.ui.utils.screenmodel.BaseScreen
import com.mangala.wallet.utils.analytics.MangalaAnalytics
import com.mangala.wallet.walletconnect.utils.WCLogger

class WalletConnectScreen(uri: String): BaseScreen<WalletConnectScreenModel>() {

    override val screenName: String = MangalaAnalytics.Screens.EVM_WALLET_CONNECT
    override val screenClassName: String = WalletConnectScreen::class.simpleName.orEmpty()

    override val isBottomBarVisible: Boolean = false

    @Composable
    override fun createScreenModel(): WalletConnectScreenModel {
        return WalletConnectScreenModel()
    }

    @Composable
    override fun ScreenContent(screenModel: WalletConnectScreenModel) {
        WCLogger.switch(true)
        var uri by remember {
            mutableStateOf("")
        }
        Column(modifier = Modifier.fillMaxSize().padding(16.dp).background(color = Color.White)) {
            Text(uri)
            Spacer(modifier = Modifier.height(8.dp))
            ClientContent(
                newUri = {
                    uri = it
                },
                uri = uri,
                chainId = 1L
            )
            Divider(modifier = Modifier.fillMaxWidth().padding(vertical = 36.dp))
            ServerContent(uri = uri)
        }
    }
}