package com.mangala.wallet.passkey

actual object PasskeyManagerFactory {
    actual fun create(): PasskeyManager = PasskeyManagerImpl()
}