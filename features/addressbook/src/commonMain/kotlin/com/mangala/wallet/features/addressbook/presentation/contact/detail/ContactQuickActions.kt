package com.mangala.wallet.features.addressbook.presentation.contact.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.foundation.text.BasicText
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mangala.wallet.common.mokoresources.icons.MangalaWalletPack
import com.mangala.wallet.features.addressbook.icon.ContactIcon
import com.mangala.wallet.ui.theme.mangalaColors
import com.mangala.wallet.features.addressbook.icon.contacticon.HistoryButton
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.ReceiveButton
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.SendButton
import com.mangala.wallet.features.addressbook.icon.contacticon.ShareButton

@Composable
fun ContactQuickActions(
    onSendClick: () -> Unit,
    onReceiveClick: () -> Unit,
    onHistoryClick: () -> Unit,
    onShareClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        color = MaterialTheme.mangalaColors.bgInnerCard,
        shape = MaterialTheme.shapes.medium
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 16.dp,
                    vertical = 12.dp
                ), // Reduce horizontal padding, keep only 16dp from edges
            horizontalArrangement = Arrangement.SpaceBetween // Use SpaceBetween for equal spacing
        ) {
            QuickActionButton(
                icon = MangalaWalletPack.SendButton,
                label = "Send",
                onClick = onSendClick
            )

            QuickActionButton(
                icon = MangalaWalletPack.ReceiveButton,
                label = "Receive",
                onClick = onReceiveClick
            )

            QuickActionButton(
                icon = ContactIcon.HistoryButton,
                label = "History",
                onClick = onHistoryClick
            )

//            QuickActionButton(
//                icon = ContactIcon.ShareButton,
//                label = "Share",
//                onClick = onShareClick
//            )
        }
    }
}

@Composable
private fun QuickActionButton(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(54.dp).height(61.dp) // Adjusted height to match design
            .clickable(
                onClick = onClick,
                indication = null, // Remove ripple to keep visual unchanged
                interactionSource = remember { MutableInteractionSource() }
            )
            .padding(horizontal = 4.dp) // Reduced padding to give more space for text
    ) {
        // Đảm bảo kích thước giống thiết kế Figma
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(MaterialTheme.mangalaColors.bgButton),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = MaterialTheme.mangalaColors.iconPrimary,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        BasicText(
            text = label,
            style = TextStyle(
                fontSize = 12.sp,
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Center,
                color = MaterialTheme.mangalaColors.textSecondary
            ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}
