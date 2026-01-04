package com.mangala.wallet.walletconnect

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ListItem
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mangala.wallet.walletconnect.entity.WCMethod
import com.mangala.wallet.walletconnect.entity.WCPeerMeta
import com.mangala.wallet.walletconnect.entity.WCSessionConfig
import kotlinx.coroutines.launch
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.jsonArray


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ClientContent(
    newUri: (String) -> Unit,
    uri: String,
    chainId: Long,
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

    Column {
        Text("WalletConnect Client:")
        Row {
            Button(onClick = {

                val config = WCSessionConfig.fromUri(uri)
                scope.launch {
                    wcClient.connect(
//                        config = WCSessionConfig(
//                            bridge = "https://o.bridge.walletconnect.org",
//                        ).also {
//                            newUri(it.uri)
//                        }
                        config = config!!,
                        clientMeta = WCPeerMeta(
                            name = "Mangala Wallet",
                            description = "a walletconnect client for kotlin multiplatform",
                            icons = listOf("https://alphawallet.com/wp-content/themes/alphawallet/img/alphawallet-logo.svg"),
                            url = "https://www.alphawallet.com"
                        ),
                        chainId = chainId

                    ).onFailure {
                        it.printStackTrace()
                        newUri(it.toString())
                    }.onSuccess {
                        newUri("")
                    }
                }
            }) {
                Text("Client new connection")
            }
            Spacer(modifier = Modifier.width(8.dp))
            OpenWallet(uri = uri)
        }

        signResponse?.let {
            Text("Response:$it")
        }

        error?.let {
            Text("Response failed:$it")
        }

        LazyColumn {
            items(connections) {
                ListItem(
                    trailing = {
                        Row {
                            Button(onClick = {
                                scope.launch {
                                    error = null
                                    signResponse = null
                                    wcClient.request(
                                        connectionId = it.id,
                                        method = "personal_sign",
                                        params = kotlinx.serialization.json.Json.encodeToJsonElement(
                                            listOf(
                                                "0x48656c6c6f2c207765623321",
                                                it.accounts.first(),
                                            )
                                        ).jsonArray
                                    ).onSuccess {
                                        if (it is WCMethod.Response) {
                                            signResponse = it.result.toString()
                                        } else if (it is WCMethod.Error) {
                                            error = it.toString()
                                        }
                                    }.onFailure {
                                        error = it.toString()
                                    }
                                }
                            }) {
                                Text("Sign")
                            }
                            Button(onClick = {
                                scope.launch {
                                    wcClient.disconnect(it.id)
                                }
                            }) {
                                Text("Disconnect")
                            }
                        }

                    },
                    secondaryText = {
                        Text(text ="Self id:${it.clientId}")
                    }
                ) {
                    Text("${it.peerMeta?.name}:${it.peerId}")
                }
            }
        }

    }
}

@Composable
expect fun OpenWallet(uri: String)