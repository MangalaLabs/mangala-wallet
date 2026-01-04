package com.mangala.wallet.walletconnect

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.mangala.wallet.walletconnect.entity.WCMessage
import com.mangala.wallet.walletconnect.entity.WCMethod
import com.mangala.wallet.walletconnect.entity.WCPeerMeta
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ServerContent(uri: String) {
    val scope = rememberCoroutineScope()
    val wcServer = remember {
        WCServer(
            store = FakeWCConnectionStore(),
        )
    }
    var pairRequest by remember {
        mutableStateOf<WCServer.PairRequest?>(null)
    }
    var error by remember {
        mutableStateOf<String?>(null)
    }

    val connections by wcServer.connections.collectAsState(emptyList())

    var loading by remember {
        mutableStateOf(false)
    }

    var clientRequest by remember {
        mutableStateOf<WCMessage?>(null)
    }

    LaunchedEffect(wcServer) {
        wcServer.request.collect{
            clientRequest = it
        }
    }

    var inputUri by remember(uri) {
        mutableStateOf(uri)
    }

    Column {
        Text("WalletConnect Server:")
        Row {
            Button(onClick = {
                if (loading) return@Button
                error = null
                loading = true
                scope.launch {
                    wcServer.pair(
                        uri = inputUri,
                        clientMeta = WCPeerMeta(
                            name = "Kmp Server",
                        )
                    ).onSuccess {
                        loading = false
                        pairRequest = it
                    }.onFailure {
                        loading = false
                        error = it.toString()
                    }
                }
            }) {
                Text("Pair current client")
            }
            TextField(
                value = inputUri,
                onValueChange = {
                    inputUri = it
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
        pairRequest?.let {
            ListItem(
                modifier = Modifier.fillMaxWidth(),
                trailing = {
                    Row {
                        Button(
                            onClick = {
                                scope.launch {
                                    it.approve(
                                        account = listOf("0xD97bBF1e644E9C33A4d7C0e2E54A5B00738e3C36"),
                                        chainId = 1
                                    ).onSuccess {
                                        pairRequest = null
                                    }.onFailure {
                                        error = it.toString()
                                        pairRequest = null
                                    }
                                }
                            }
                        ) {
                            Text("Approve")
                        }
                        Button(
                            onClick = {
                                scope.launch {
                                    it.reject()
                                    pairRequest = null
                                }
                            }
                        ) {
                            Text("Reject")
                        }
                    }
                },
                secondaryText = {
                    Text(text = it.peerId)
                }
            ) {
                Text(
                    text = "${it.peerMeta?.name}",
                )
            }
        }

        clientRequest?.let {
            ListItem(
                modifier = Modifier.fillMaxWidth(),
                trailing = {
                    Row {
                        if (it.method is WCMethod.CustomRequest && (it.method as WCMethod.CustomRequest).method == "personal_sign") {
                            Button(
                                onClick = {
                                    scope.launch {
                                        wcServer.response(
                                            connectionId = it.connectionId,
                                            requestId = it.method.requestId,
                                            response = Json.encodeToJsonElement(
                                                "0xd648380273705c43f4bb68a1ea6670c84de4228c74dad1265d7b98d8e01d5b3e011704962bf17f00dde363068737d0c9266633dddcdf59ae1cf9020d2c0b7b431b"
                                            )
                                        )
                                        clientRequest = null
                                    }
                                }
                            ) {
                                Text("Approve Sign")
                            }
                        }

                        Button(
                            onClick = {
                                scope.launch {
                                    wcServer.errorResponse(
                                        connectionId = it.connectionId,
                                        requestId = it.method.requestId,
                                        errorCode = -32000,
                                        errorMessage = "reject"
                                    )
                                    clientRequest = null
                                }
                            }
                        ) {
                            Text("Reject")
                        }
                    }
                },
                secondaryText = {
                    Text(text = it.method.toString())
                }
            ) {
                Text(
                    text = it.connectionId,
                )
            }
        }

        error?.let {
            Text(it)
        }
        LazyColumn {
            items(connections) {
                ListItem(
                    trailing = {
                        Row {
                            Button(onClick = {
                                scope.launch {
                                    wcServer.disconnect(it.id)
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