/*
 * SPDX-License-Identifier: MIT
 * Copyright (c) 2022 itsMimao
 */
package com.mangala.wallet.walletconnect.utils


class WCLogger private constructor(){
    private var turnOn: Boolean = false

    internal fun log(msg: String) {
        if (turnOn) {

        }
    }

    companion object {
        private val logger by lazy {
            WCLogger()
        }

        internal fun log(msg: String){
            logger.log(msg)
        }

        fun switch(on: Boolean) {
            logger.turnOn = on
        }
    }
}

