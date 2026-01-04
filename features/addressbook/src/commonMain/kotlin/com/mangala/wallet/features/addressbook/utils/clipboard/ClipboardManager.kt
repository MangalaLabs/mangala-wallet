package com.mangala.wallet.features.addressbook.utils.clipboard

/**
 * Cross-platform clipboard manager using expect/actual pattern.
 * Used for copying data to clipboard across Android, iOS, and Desktop platforms.
 */
expect class ClipboardManager {
    /**
     * Copies text to the system clipboard with a given label.
     *
     * @param label A label describing what's being copied (platform-specific usage)
     * @param text The text to copy to clipboard
     */
    fun copyToClipboard(label: String, text: String)
}
