package com.mangala.wallet.features.addressbook.utils.clipboard

import java.awt.Toolkit
import java.awt.datatransfer.StringSelection

/**
 * Desktop implementation of ClipboardManager.
 * Uses Java AWT's clipboard functionality for JVM desktop platforms.
 */
actual class ClipboardManager {

    /**
     * Copies text to the system clipboard.
     * On desktop platforms, we use AWT Toolkit's system clipboard
     * and StringSelection to transfer text data.
     *
     * @param label Not used in desktop implementation
     * @param text The text to copy to clipboard
     */
    actual fun copyToClipboard(label: String, text: String) {
        val selection = StringSelection(text)
        val clipboard = Toolkit.getDefaultToolkit().systemClipboard
        clipboard.setContents(selection, null)
    }
}
