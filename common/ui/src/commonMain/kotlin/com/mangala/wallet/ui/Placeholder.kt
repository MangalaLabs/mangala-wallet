package com.mangala.wallet.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.mangala.wallet.ui.placeholder.mangalaWalletPlaceholder

@Composable
fun Placeholder(modifier: Modifier = Modifier) {
    Box(Modifier.then(modifier.mangalaWalletPlaceholder(true, modifier = modifier)))
}