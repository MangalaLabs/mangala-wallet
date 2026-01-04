package com.mangala.wallet.common.mokoresources.icons

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun MangalaWalletPackPreview() {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 128.dp)
    ) {
        items(
            items = MangalaWalletPack.AllIcons
        ) { icon ->
            Column(Modifier.padding(16.dp)) {
                Image(imageVector = icon, contentDescription = null, Modifier.size(32.dp))
                Spacer(Modifier.height(8.dp))
                Text(text = icon.name)
            }
        }
    }
}