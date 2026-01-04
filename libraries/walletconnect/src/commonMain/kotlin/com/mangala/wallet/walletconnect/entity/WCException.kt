/*
 * SPDX-License-Identifier: MIT
 * Copyright (c) 2022 itsMimao
 */
package com.mangala.wallet.walletconnect.entity

class WCException(val error: WCMethod.Error): Throwable(error.toString())
