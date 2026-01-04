package com.mangala.wallet.utils

import java.awt.Desktop
import java.net.URI

actual class MailToFactory {

    actual fun mailTo(
        recipient: String,
        subject: String,
        body: String
    ) {
        val uri = URI("mailto:$recipient?subject=${subject.urlEncoded()}&body=${body.urlEncoded()}")

        if (Desktop.isDesktopSupported()) {
            val desktop = Desktop.getDesktop()
            if (desktop.isSupported(Desktop.Action.MAIL)) {
                desktop.mail(uri)
            } else {
                println("Mail action is not supported on this system.")
            }
        } else {
            println("Desktop is not supported on this system.")
        }
    }

    private fun String.urlEncoded(): String {
        return java.net.URLEncoder.encode(this, "UTF-8")
    }
}