package com.mangala.wallet.utils

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import org.koin.core.component.KoinComponent
import org.koin.android.ext.koin.androidContext
import org.koin.core.component.inject

actual class ClipboardFactory(val applicationContext: Context) : Clipboard, KoinComponent {

    private val clipboardManager: ClipboardManager =
        applicationContext.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

    actual override fun copyText(label: String, text: String) {
        val clip = ClipData.newPlainText(label, text)
        clipboardManager.setPrimaryClip(clip)
    }

}