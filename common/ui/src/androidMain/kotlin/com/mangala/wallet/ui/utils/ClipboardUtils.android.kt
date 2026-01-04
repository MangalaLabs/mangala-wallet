package com.mangala.wallet.ui.utils

import android.content.ClipData
import androidx.compose.ui.platform.ClipEntry

actual fun clipEntryOf(string: String): ClipEntry = ClipEntry(ClipData.newPlainText("Mangala Copy", string))

actual fun clipEntryText(clipEntry: ClipEntry): String? {
    val clipData = clipEntry.clipData
    return if (clipData.itemCount > 0) {
        clipData.getItemAt(0).text?.toString()
    } else {
        null
    }
}