package com.mangala.wallet.features.addressbook.utils.clipboard

import platform.UIKit.UIPasteboard

/**
 * iOS implementation of ClipboardManager.
 * Uses iOS's UIPasteboard for clipboard operations.
 */
actual class ClipboardManager {

    /**
     * Copies text to the system clipboard.
     * On iOS, we use the general pasteboard and ignore the label parameter
     * since iOS doesn't use labels for clipboard data.
     *
     * @param label Not used in iOS implementation
     * @param text The text to copy to clipboard
     */
    actual fun copyToClipboard(label: String, text: String) {
        UIPasteboard.generalPasteboard.string = text
    }
}
