package com.mangala.wallet.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mangala.wallet.common.mokoresources.icons.MangalaWalletPack
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.ArrowLeft

@Composable
fun MangalaTopBar(
    textButton: String,
    buttonEnabled: MutableState<Boolean>,
    onBackClicked: (Boolean) -> Unit,
    onClickNext: (Boolean) -> Unit = {},
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth(),
    ) {
        IconButton(onClick = {
            onBackClicked(true)
        }) {
            Icon(
                imageVector = MangalaWalletPack.ArrowLeft,
                contentDescription = "Back"
            )
        }

        Box(
            modifier = Modifier.clickable(onClick = {
                if (buttonEnabled.value) {
                    onClickNext(true)
                }
            })
        ) {
            TextCanClick(
                text = textButton,
                enabled = buttonEnabled.value,
                modifier = Modifier.padding(
                    start = 16.dp,
                    end = 16.dp,
                    top = 8.dp,
                    bottom = 8.dp
                )
            )
        }
    }
}
