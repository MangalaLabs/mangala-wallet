package com.mangala.wallet.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import platform.Foundation.NSNotificationCenter
import platform.UIKit.UIScreen
import platform.UIKit.UIScreenCapturedDidChangeNotification

@Composable
actual fun SecureScreen(content: @Composable () -> Unit) {
    var isCaptured by remember { mutableStateOf(UIScreen.mainScreen.isCaptured()) }

    DisposableEffect(Unit) {
        val observer = NSNotificationCenter.defaultCenter.addObserverForName(
            name = UIScreenCapturedDidChangeNotification,
            `object` = UIScreen.mainScreen,
            queue = null,
            usingBlock = { _ ->
                isCaptured = UIScreen.mainScreen.isCaptured()
            }
        )
        onDispose {
            NSNotificationCenter.defaultCenter.removeObserver(observer)
        }
    }

    Box {
        content()
        if (isCaptured) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Screen recording is\nnot allowed for security",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
