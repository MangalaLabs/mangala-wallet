/*
 * SPDX-License-Identifier: MIT
 * Copyright (c) 2022 itsMimao
 */
package com.mangala.wallet.walletconnect.utils

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

internal val JSON = Json {
    ignoreUnknownKeys = true
    isLenient = true
    coerceInputValues = true
}

internal inline fun <reified T> T.encodeJson(): String = JSON.encodeToString(this)

internal inline fun <reified T> String.decodeJson(): T = JSON.decodeFromString(this)
