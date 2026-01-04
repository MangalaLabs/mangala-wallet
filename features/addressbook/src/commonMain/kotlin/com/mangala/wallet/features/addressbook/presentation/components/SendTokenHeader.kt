package com.mangala.wallet.features.addressbook.presentation.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mangala.wallet.common.mokoresources.icons.MangalaWalletPack
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.Add
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.IcBack
import com.mangala.wallet.ui.component.MangalaWalletTopBarCenteredTitle
import com.mangala.wallet.ui.theme.mangalaColors


/**
 * Enhanced SendTokenHeader with integrated privacy mode toggle
 * This replaces the existing SendTokenHeader to add privacy mode support
 */
@Composable
fun SendTokenHeaderWithPrivacy(
    onBackClick: (() -> Unit)?,
    addClick: (() -> Unit)?,
    privacyModeEnabled: Boolean,
    onPrivacyToggle: () -> Unit,
) {
    MangalaWalletTopBarCenteredTitle(
        title = "Contacts",
        textColor = MaterialTheme.mangalaColors.textPrimary,
        navigationIcon = onBackClick?.let {
            {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = MangalaWalletPack.IcBack,
                        contentDescription = "Back",
                        tint = MaterialTheme.mangalaColors.iconPrimary
                    )
                }
            }
        },
        trailingButton = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                PrivacyToggleButton(
                    isEnabled = privacyModeEnabled,
                    onToggle = onPrivacyToggle,
                )

                addClick?.let {
                    IconButton(onClick = addClick) {
                        Icon(
                            imageVector = MangalaWalletPack.Add,
                            contentDescription = "Menu",
                            tint = MaterialTheme.mangalaColors.iconPrimary
                        )
                    }
                }
            }
        }
    )
}
