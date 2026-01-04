package com.mangala.wallet.utils

expect class MailToFactory {
    fun mailTo(recipient: String, subject: String, body: String)
}

const val MANGALA_SUPPORT_EMAIL = "mangalacryptowallet@gmail.com"