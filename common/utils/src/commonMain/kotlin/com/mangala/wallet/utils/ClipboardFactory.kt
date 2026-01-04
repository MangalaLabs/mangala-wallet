package com.mangala.wallet.utils

interface Clipboard {
    fun copyText(label: String, text: String)
}

expect class ClipboardFactory: Clipboard {
    override fun copyText(label: String, text: String)
}