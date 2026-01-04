package com.mangala.wallet.ui.component

import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.mangala.wallet.common.mokoresources.icons.MangalaWalletPack
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.Hide
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.Show
import com.mangala.wallet.ui.theme.mangalaColors

@Composable
fun VisibilityToggleIconButton(
    isVisible: Boolean,
    onClickBalanceVisible: (Boolean) -> Unit,
    iconButtonModifier: Modifier,
    modifier: Modifier,
    enabled: Boolean = true
) {
    IconButton(
        modifier = iconButtonModifier,
        onClick = { onClickBalanceVisible(isVisible.not()) },
        enabled = enabled
    ) {
        Icon(
            modifier = modifier,
            imageVector = if (isVisible) MangalaWalletPack.Show else MangalaWalletPack.Hide,
            contentDescription = null,
            tint = MaterialTheme.mangalaColors.iconPrimary
        )
    }
}
