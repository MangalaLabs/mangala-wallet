package com.mangala.wallet.features.addressbook.presentation.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import com.mangala.wallet.common.mokoresources.ColorsNew
import com.mangala.wallet.common.mokoresources.Dimensions
import com.mangala.wallet.features.addressbook.icon.ContactIcon
import com.mangala.wallet.features.addressbook.icon.contacticon.Qrcode

@Composable
fun ContactQrButton(
    onShowQrCodeClick: () -> Unit,
    iconSize: Dp = Dimensions.IconSize,
    buttonSize: Dp = Dimensions.IconButtonSize
){
    IconButton(
        onClick = { onShowQrCodeClick() },
        modifier = Modifier.size(buttonSize)
    ) {
        Icon(
            imageVector = ContactIcon.Qrcode,
            contentDescription = "Show QR Code",
            tint = ColorsNew.primary_300,
            modifier = Modifier.size(iconSize)
        )
    }
}