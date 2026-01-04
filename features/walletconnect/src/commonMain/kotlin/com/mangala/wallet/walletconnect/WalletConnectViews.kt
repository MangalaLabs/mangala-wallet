package com.mangala.wallet.walletconnect

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun WalletConnectViews(
    chainId: Long,
    addressConnect: String,
    prevTabId: String,
    importPassData: String,
    url: String,
    accountId: String,
    onConnectClick: () -> Unit,
    onBackClick: () -> Unit
) {
    val wcClient = remember {
        WCClient(
            store = FakeWCConnectionStore(),
        )
    }
    val scope = rememberCoroutineScope()
    val connections by wcClient.connections.collectAsState(emptyList())

    var signResponse by remember {
        mutableStateOf<String?>(null)
    }

    var error by remember {
        mutableStateOf<String?>(null)
    }

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text(text = "Wallet Connect") },
            navigationIcon = {
                IconButton(onClick = { onBackClick() }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = null)
                }
            }
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp).background(color = androidx.compose.ui.graphics.Color.White),
            contentAlignment = Alignment.Center
        ) {
//            ServerContent(uri = url)
            var uri by remember {
                mutableStateOf(url)
            }
            ClientContent(
                newUri = {
                    uri = it
                },
                uri = uri,
                chainId = 1L
            )
//            Column(horizontalAlignment = Alignment.CenterHorizontally) {
//                Text(text = "Connect to your Wallet", fontSize = 20.sp)
//                Spacer(modifier = Modifier.height(16.dp))
//                Text(
//                    text = "Connect with WalletConnect to interact with DApps.",
//                    textAlign = TextAlign.Center
//                )
//                Spacer(modifier = Modifier.height(32.dp))
//                Button(onClick = { onConnectClick() }) {
//                    Text(text = "Connect")
//                }
//            }
        }
    }
}