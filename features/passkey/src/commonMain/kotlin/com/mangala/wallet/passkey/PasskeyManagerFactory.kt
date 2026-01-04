package com.mangala.wallet.passkey

expect object PasskeyManagerFactory {
    fun create(): PasskeyManager
}