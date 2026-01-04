/*
 * SPDX-License-Identifier: MIT
 * Copyright (c) 2022 itsMimao
 */
package com.mangala.wallet.walletconnect

import com.mangala.wallet.walletconnect.entity.WCConnection

interface WCConnectionPersistStore {
    suspend fun store(storeId: String,connection: WCConnection)
    suspend fun load(storeId: String): WCConnection?
    suspend fun all():List<WCConnection>
    suspend fun remove(storeId: String)
}
