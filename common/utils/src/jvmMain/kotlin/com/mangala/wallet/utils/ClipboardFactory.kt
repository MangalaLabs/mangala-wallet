package com.mangala.wallet.utils

import java.awt.Toolkit
import java.awt.datatransfer.StringSelection

actual class ClipboardFactory: Clipboard {
    actual override fun copyText(label: String, text: String) {
        val clipboard = Toolkit.getDefaultToolkit().systemClipboard
        val selection = StringSelection(text)
        clipboard.setContents(selection, null)
    }
}