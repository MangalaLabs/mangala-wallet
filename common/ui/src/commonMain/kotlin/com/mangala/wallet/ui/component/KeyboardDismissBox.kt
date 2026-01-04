package com.mangala.wallet.ui.component

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager

@Composable
fun KeyboardDismissBox(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    val focusManager = LocalFocusManager.current
    
    Box(
        modifier = modifier.pointerInput(Unit) {
            detectTapGestures(onTap = { 
                focusManager.clearFocus() 
            })
        },
        content = content
    )
}