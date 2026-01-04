package com.mangala.wallet.utils

import org.koin.core.component.KoinComponent
import java.awt.Desktop
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection
import java.net.URI

actual class ShareFactory : IShare, KoinComponent {

    actual override fun shareText(title: String, text: String) {
        val clipboard = Toolkit.getDefaultToolkit().systemClipboard
        val selection = StringSelection(text)
        clipboard.setContents(selection, null)
    }

    actual override fun shareImage(image: Any) {
        // TODO: Implement image sharing
    }

}