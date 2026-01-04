package com.mangala.wallet.passkey.effect

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.fragment.app.FragmentActivity
import com.mangala.wallet.passkey.PasskeyManager
import com.mangala.wallet.passkey.PasskeyManagerImpl

@Composable
actual fun BindPasskeyManagerEffect(passkeyManager: PasskeyManager) {
    val context: Context = LocalContext.current
    
    LaunchedEffect(passkeyManager, context) {
        if (context is FragmentActivity && passkeyManager is PasskeyManagerImpl) {
            passkeyManager.setActivityContext(context)
        }
    }
}