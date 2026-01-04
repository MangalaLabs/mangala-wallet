package com.mangala.wallet.ui.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.mangala.wallet.common.mokoresources.Spacing
import com.mangala.wallet.ui.TextDescription2

@Composable
fun DataInput(
    label: String,
    inputField: @Composable () -> Unit
) {
    Column {
        TextDescription2(text = label)
        Spacer(Modifier.height(Spacing.TINY))
        inputField()
    }
}