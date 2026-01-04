package com.mangala.wallet.utils

import platform.UIKit.UIPasteboard

actual class ClipboardFactory: Clipboard {
    actual override fun copyText(label: String, text: String) {
        val pasteboard = UIPasteboard.generalPasteboard
        pasteboard.string = text
    }
}