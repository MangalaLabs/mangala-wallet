package com.mangala.wallet.features.addressbook.presentation.contact.recent.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mangala.wallet.common.mokoresources.CornerRadius
import com.mangala.wallet.common.mokoresources.Dimensions
import com.mangala.wallet.common.mokoresources.Spacing
import com.mangala.wallet.features.addressbook.presentation.contact.recent.BlockchainIcons.getBackgroundColorForSymbol
import com.mangala.wallet.features.addressbook.presentation.contact.recent.BlockchainIcons.getColorForSymbol
import com.mangala.wallet.features.addressbook.presentation.contact.recent.BlockchainIcons.getIconForSymbol
import com.mangala.wallet.ui.imageloader.LocalImage
import com.mangala.wallet.ui.theme.MangalaTypography

@Composable
fun TransactionNetworkBadge(
    tokenSymbol: String,
    modifier: Modifier = Modifier
) {
    val backgroundColor = remember(tokenSymbol) {
        getBackgroundColorForSymbol(tokenSymbol)
    }
    val imageResource = remember(tokenSymbol) {
        getIconForSymbol(tokenSymbol)
    }
    val symbolColor = remember(tokenSymbol) {
        getColorForSymbol(tokenSymbol)
    }

    Row(
        modifier = modifier
            .background(
                color = backgroundColor,
                shape = RoundedCornerShape(CornerRadius.Small)
            )
            .padding(Dimensions.Padding.quarter),
        horizontalArrangement = Arrangement.spacedBy(Spacing.XTINY),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (imageResource != null) {
            LocalImage(
                modifier = Modifier.size(12.dp),
                imageResource = imageResource,
            )
        }

        Text(
            text = tokenSymbol,
            style = MangalaTypography.Size12SemiBold(),
            color = symbolColor
        )
    }
}