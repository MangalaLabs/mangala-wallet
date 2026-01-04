package com.mangala.wallet.utils

import android.content.Context
import android.content.Intent
import android.content.Intent.*
import android.net.Uri
import androidx.core.content.FileProvider
import androidx.core.content.contentValuesOf
import org.koin.core.component.KoinComponent
import java.io.File

actual class ShareFactory(val applicationContext: Context) : IShare, KoinComponent {
    actual override fun shareText(title: String, text: String) {
        val intent = Intent().apply {
            action = ACTION_SEND
            type = "text/plain"
            putExtra(EXTRA_TEXT, text)
        }
        val chooser = createChooser(intent, title)
        chooser.flags = FLAG_ACTIVITY_NEW_TASK
        applicationContext.startActivity(chooser)
    }

    actual override fun shareImage(image: Any) {
        val fileUri = FileProvider.getUriForFile(applicationContext, "${applicationContext.packageName}.provider", image as File)
        val shareIntent = Intent().apply {
            action = ACTION_SEND
            putExtra(Intent.EXTRA_STREAM, fileUri)
            type = "image/png"
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        val intent = createChooser(shareIntent, null)
        val chooser = createChooser(intent, null)
        chooser.flags = FLAG_ACTIVITY_NEW_TASK
        applicationContext.startActivity(chooser)
    }
}