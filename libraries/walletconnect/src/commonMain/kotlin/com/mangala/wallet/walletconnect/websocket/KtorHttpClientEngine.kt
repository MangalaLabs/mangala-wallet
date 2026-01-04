/*
 * SPDX-License-Identifier: MIT
 * Copyright (c) 2022 itsMimao
 */
package com.mangala.wallet.walletconnect.websocket

import io.ktor.client.engine.HttpClientEngine

internal expect fun provideHttpClientEngine(): HttpClientEngine
