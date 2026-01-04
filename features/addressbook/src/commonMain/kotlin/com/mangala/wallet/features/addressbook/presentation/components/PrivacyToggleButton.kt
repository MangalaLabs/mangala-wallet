package com.mangala.wallet.features.addressbook.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mangala.wallet.common.mokoresources.icons.MangalaWalletPack
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.Hide
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.Show
import com.mangala.wallet.ui.theme.mangalaColors

/**
 * Privacy toggle button component for the header/navigation area
 *
 * @param isEnabled Current privacy mode state
 * @param onToggle Callback when toggle is clicked
 * @param modifier Optional modifier
 * @param showLabel Whether to show text label alongside icon
 */
@Composable
fun PrivacyToggleButton(
    isEnabled: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier,
    showLabel: Boolean = false,
) {
    if (showLabel) {
        // Compact pill-style button with icon and label
        Box(
            modifier = modifier
                .clip(RoundedCornerShape(20.dp))
                .background(
                    if (isEnabled) MaterialTheme.mangalaColors.bgAlpha
                    else Color.Transparent
                )
                .clickable { onToggle() }
                .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = if (isEnabled) MangalaWalletPack.Hide else MangalaWalletPack.Show,
                    contentDescription = if (isEnabled) "Disable Privacy Mode" else "Enable Privacy Mode",
                    tint = if (isEnabled) MaterialTheme.mangalaColors.iconPrimary else MaterialTheme.mangalaColors.iconSecondary,
                    modifier = Modifier.size(18.dp)
                )

                Spacer(modifier = Modifier.width(4.dp))

                Text(
                    text = if (isEnabled) "Privacy On" else "Privacy Off",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (isEnabled) MaterialTheme.mangalaColors.textPrimary else MaterialTheme.mangalaColors.textSecondary
                )
            }
        }
    } else {
        // Icon-only button for compact spaces
        IconButton(
            onClick = onToggle,
            modifier = modifier,
            colors = IconButtonDefaults.iconButtonColors(
                contentColor = MaterialTheme.mangalaColors.iconPrimary,
                disabledContentColor = MaterialTheme.mangalaColors.iconSecondary
            ),
        ) {
            Icon(
                imageVector = if (isEnabled) MangalaWalletPack.Hide else MangalaWalletPack.Show,
                contentDescription = if (isEnabled) "Disable Privacy Mode" else "Enable Privacy Mode",
            )
        }
    }
}

/**
 * Privacy mode indicator badge to show current state
 * Can be placed in various locations to indicate privacy mode is active
 */
@Composable
fun PrivacyModeIndicator(
    isEnabled: Boolean,
    modifier: Modifier = Modifier,
) {
    if (isEnabled) {
        Box(
            modifier = modifier
                .background(
                    color = MaterialTheme.mangalaColors.bgBadge,
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = MangalaWalletPack.Hide,
                    contentDescription = "Privacy Mode Active",
                    tint = Color.White,
                    modifier = Modifier.size(12.dp)
                )

                Spacer(modifier = Modifier.width(4.dp))

                Text(
                    text = "Private",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White
                )
            }
        }
    }
}