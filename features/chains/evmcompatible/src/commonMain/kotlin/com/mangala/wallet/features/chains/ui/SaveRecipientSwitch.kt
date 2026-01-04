package com.mangala.wallet.features.chains.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.mangala.wallet.common.mokoresources.Spacing
import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.ui.TextSwitch
import com.mangala.wallet.ui.component.MangalaWalletSwitch
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.desc.desc

@Composable
fun SaveRecipientSwitch(
    modifier: Modifier = Modifier,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Spacer(modifier = Modifier.height(Spacing.TINY))
    Row(verticalAlignment = Alignment.CenterVertically) {
        MangalaWalletSwitch(
            checked = isChecked,
            onCheckedChange = onCheckedChange,
            modifier = modifier
        )
        TextSwitch(
            text = MR.strings.label_save_recipient_switch.desc().localized(),
            modifier = Modifier.padding(end = Spacing.SMALL)
        )
    }
}