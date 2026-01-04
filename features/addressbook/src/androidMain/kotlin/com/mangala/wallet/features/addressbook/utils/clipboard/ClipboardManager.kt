package com.mangala.wallet.features.addressbook.utils.clipboard

import android.content.ClipData
import android.content.Context

/**
 * Android implementation of ClipboardManager.
 * Uses Android's native clipboard service.
 *
 * @param context The Android application context needed to access system services
 */
actual class ClipboardManager(private val context: Context) {

    /**
     * Copies text to the system clipboard with a given label.
     * On Android, the label is used as the clip data label.
     *
     * @param label A user-friendly label for the content being copied
     * @param text The text to copy to clipboard
     */
    actual fun copyToClipboard(label: String, text: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
        val clip = ClipData.newPlainText(label, text)
        clipboard.setPrimaryClip(clip)
    }
}
