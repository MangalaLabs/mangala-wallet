package com.mangala.wallet.utils

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.Intent.*
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import org.koin.core.component.KoinComponent
import org.koin.android.ext.koin.androidContext
import org.koin.core.component.inject

actual class ToastFactory(val applicationContext: Context) : IToast, KoinComponent {

    actual override fun show(text: String) {
        Toast.makeText(applicationContext, text, Toast.LENGTH_SHORT).show()
    }

}