package com.mangala.wallet.ui.utils

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.ClipEntry

@OptIn(ExperimentalComposeUiApi::class)
actual fun clipEntryOf(string: String): ClipEntry = ClipEntry.withPlainText(string)

@OptIn(ExperimentalComposeUiApi::class)
actual fun clipEntryText(clipEntry: ClipEntry): String? {
    return clipEntry.getPlainText()
}