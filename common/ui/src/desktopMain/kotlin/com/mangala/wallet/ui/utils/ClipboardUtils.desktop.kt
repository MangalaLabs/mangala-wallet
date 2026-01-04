package com.mangala.wallet.ui.utils

import androidx.compose.ui.platform.ClipEntry
import java.awt.datatransfer.StringSelection
import java.awt.datatransfer.DataFlavor
import java.awt.datatransfer.Transferable

actual fun clipEntryOf(string: String): ClipEntry = ClipEntry(StringSelection(string))

actual fun clipEntryText(clipEntry: ClipEntry): String? {
    val transferable = clipEntry.nativeClipEntry
    return if (transferable is Transferable && transferable.isDataFlavorSupported(DataFlavor.stringFlavor)) {
        try {
            transferable.getTransferData(DataFlavor.stringFlavor) as? String
        } catch (e: Exception) {
            null
        }
    } else {
        null
    }
}