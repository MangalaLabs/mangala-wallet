package com.mangala.wallet.features.addressbook.presentation.group

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.mangala.wallet.common.mokoresources.Spacing
import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.ui.component.MangalaGradientButton
import com.mangala.wallet.ui.component.MangalaButtonSize
import com.mangala.wallet.ui.component.MaxWidthColumn
import com.mangala.wallet.ui.imageloader.LocalImage
import com.mangala.wallet.ui.theme.MangalaTypography
import com.mangala.wallet.ui.theme.mangalaColors

/**
 * Empty state screen displayed when there are no groups
 */
@Composable
fun EmptyGroupsState(
    onAddNewGroup: () -> Unit,
) {
    MaxWidthColumn(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(Modifier.height(Spacing.BASE))

        LocalImage(
            imageResource = MR.images.NoGroup,
            modifier = Modifier.size(120.dp)
        )

        Spacer(modifier = Modifier.height(Spacing.SMALL))

        Text(
            text = "Send to Many, With a Single Transaction",
            style = MangalaTypography.Size17Medium(),
            color = MaterialTheme.mangalaColors.textPrimary
        )

        Spacer(modifier = Modifier.height(Spacing.TINY))

        Text(
            text = "Create groups for family, teams, or staking addresses to send tokens to everyone at once. Save time and reduce errors.",
            style = MangalaTypography.Size14Regular(),
            color = MaterialTheme.mangalaColors.textSecondary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(Spacing.BASE))

        MangalaGradientButton(
            label = "Create New Group",
            onClick = onAddNewGroup,
            size = MangalaButtonSize.Small,
            modifier = Modifier
                .defaultMinSize(minWidth = 180.dp),
        )

        Spacer(modifier = Modifier.height(Spacing.XBASE))
    }
}

/**
 * State displayed when a search query returns no results
 */
@Composable
fun NoGroupSearchResultsState() {
    MaxWidthColumn(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(Modifier.height(Spacing.BASE))

        LocalImage(
            imageResource = MR.images.NoGroup,
            modifier = Modifier.size(120.dp)
        )

        Spacer(modifier = Modifier.height(Spacing.SMALL))

        Text(
            text = "No Groups Found",
            style = MangalaTypography.Size17Medium(),
            color = MaterialTheme.mangalaColors.textPrimary
        )

        Spacer(modifier = Modifier.height(Spacing.TINY))

        Text(
            text = "Please check the group name you entered. If this group doesn't exist yet, you can create a new one now.",
            style = MangalaTypography.Size14Regular(),
            color = MaterialTheme.mangalaColors.textSecondary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(Spacing.XBASE))
    }
}