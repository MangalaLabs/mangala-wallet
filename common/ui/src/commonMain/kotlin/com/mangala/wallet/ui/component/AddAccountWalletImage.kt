package com.mangala.wallet.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mangala.wallet.common.mokoresources.Dimensions
import com.mangala.wallet.common.mokoresources.icons.MangalaWalletPack
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.WalletReal
import com.mangala.wallet.ui.TextNormal

@Composable
fun AddAccountWalletImage(onClickButtonAdd: () -> Unit, textButton: String, textMessage: String) {
    Column(
        modifier = Modifier
            .padding(horizontal = Dimensions.Padding.default).fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            imageVector = MangalaWalletPack.WalletReal,
            contentDescription = "Image",
            modifier = Modifier.size(200.dp, 200.dp)
        )
        TextNormal(textMessage)
        MangalaGradientButton(
            label = textButton,
            onClick = onClickButtonAdd
        )
    }
}