/*
 * SPDX-License-Identifier: MIT
 * Copyright (c) 2022 itsMimao
 */
package com.mangala.wallet.walletconnect.websocket

import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.websocket.*
import kotlin.time.Duration.Companion.seconds

internal fun httpClient(config: HttpClientConfig<*>.() -> Unit = {}) = HttpClient(
    engine = provideHttpClientEngine(),
) {
    install(WebSockets)
    install(HttpTimeout) {
        connectTimeoutMillis = 30.seconds.inWholeMilliseconds
        requestTimeoutMillis = 30.seconds.inWholeMilliseconds
        socketTimeoutMillis = 30.seconds.inWholeMilliseconds
    }
    config.invoke(this)
}
