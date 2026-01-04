package com.mangala.wallet.utils

import kotlinx.cinterop.BetaInteropApi
import platform.Foundation.NSCharacterSet
import platform.Foundation.NSString
import platform.Foundation.NSURL
import platform.Foundation.URLQueryAllowedCharacterSet
import platform.Foundation.create
import platform.Foundation.stringByAddingPercentEncodingWithAllowedCharacters
import platform.UIKit.UIApplication

actual class MailToFactory {
    actual fun mailTo(
        recipient: String,
        subject: String,
        body: String
    ) {
        val url =
            NSURL.URLWithString("mailto:$recipient?subject=${subject.urlEncoded()}&body=${body.urlEncoded()}")
        if (url != null && UIApplication.sharedApplication.canOpenURL(url)) {
            UIApplication.sharedApplication.openURL(url)
        }
    }

    @OptIn(BetaInteropApi::class)
    private fun String.urlEncoded(): String {
        return NSString.create(string = this)
            .stringByAddingPercentEncodingWithAllowedCharacters(NSCharacterSet.URLQueryAllowedCharacterSet())
            ?: this
    }
}