package com.mangala.wallet.features.chains.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.mangala.wallet.common.mokoresources.Colors
import com.mangala.wallet.common.mokoresources.CornerRadius
import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.common.mokoresources.Spacing
import com.mangala.wallet.common.mokoresources.icons.MangalaWalletPack
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.ContactsSquare
import com.mangala.wallet.ui.TextDescription2
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.desc.desc

@Composable
fun SendToContactButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(Spacing.XTINY, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .border(width = 1.dp, color = Colors.second, shape = RoundedCornerShape(CornerRadius.Medium))
            .clip(RoundedCornerShape(CornerRadius.Medium))
            .clickable { onClick() }
            .padding(vertical = Spacing.XTINY, horizontal = Spacing.TINY)
    ) {
        Image(
            imageVector = MangalaWalletPack.ContactsSquare,
            contentDescription = null,
            modifier = Modifier.size(18.dp)
        )
        TextDescription2(
            text = MR.strings.button_send_to_contact.desc().localized(),
            modifier = Modifier.padding(end = Spacing.XTINY),
            color = Colors.second
        )
    }
}