package com.mangala.wallet.walletconnect

import android.content.Intent
import android.net.Uri
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

@Composable
actual fun OpenWallet(uri: String) {
    if (!uri.startsWith("wc:")) return
    val context = LocalContext.current
    Button(onClick = {
        context.startActivity(
            Intent(Intent.ACTION_VIEW).apply {
                this.data = Uri.parse(uri)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
        )
    }) {
        Text("Open Wallet")
    }
}