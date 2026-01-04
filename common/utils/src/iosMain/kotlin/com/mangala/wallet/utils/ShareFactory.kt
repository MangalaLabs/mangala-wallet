package com.mangala.wallet.utils

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import platform.Foundation.NSURL
import platform.UIKit.*

actual class ShareFactory : IShare, KoinComponent {

    actual override fun shareImage(image: Any) {
        MainScope().launch(Dispatchers.Main) {
            val activity = UIApplication.sharedApplication.keyWindow?.rootViewController
            val items = listOf(NSURL.fileURLWithPath(image as String))
            val shareSheet = UIActivityViewController(items, null)
            activity?.presentViewController(shareSheet, true, null)
        }
    }

    actual override fun shareText(title: String, text: String) {
        MainScope().launch(Dispatchers.Main) {
            val activity = UIApplication.sharedApplication.keyWindow?.rootViewController
            val items = listOf(text)
            val shareSheet = UIActivityViewController(items, null)
            activity?.presentViewController(shareSheet, true, null)
        }
    }

}
