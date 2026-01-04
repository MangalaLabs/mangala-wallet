package com.mangala.wallet.utils

import org.koin.core.component.KoinComponent
import java.awt.Desktop
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection
import java.net.URI

actual class ToastFactory : IToast, KoinComponent {

    actual override fun show(text: String) {
        TODO()
    }

}