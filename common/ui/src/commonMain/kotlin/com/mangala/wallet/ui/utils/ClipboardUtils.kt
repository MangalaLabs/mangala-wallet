package com.mangala.wallet.ui.utils

import androidx.compose.ui.platform.ClipEntry

expect fun clipEntryOf(string: String): ClipEntry

expect fun clipEntryText(clipEntry: ClipEntry): String?