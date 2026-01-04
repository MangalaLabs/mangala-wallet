package com.mangala.wallet.ui.component

import androidx.compose.material.Switch
import androidx.compose.material.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.mangala.wallet.common.mokoresources.Colors

// https://stackoverflow.com/a/70567213/10325347
@Composable
fun MangalaWalletSwitch(
    checked: Boolean,
    modifier: Modifier = Modifier,
    onCheckedChange: (Boolean) -> Unit
) {
    Switch(
        checked = checked,
        onCheckedChange = onCheckedChange,
        modifier = modifier,
        colors = SwitchDefaults.colors(
            checkedTrackColor = Colors.teal,
            checkedThumbColor = Color.White,
            uncheckedTrackColor = Color.LightGray,
            uncheckedThumbColor = Color.White
        )
    )
}