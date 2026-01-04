package com.mangala.wallet.features.addressbook.presentation.group

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import com.mangala.wallet.common.mokoresources.CornerRadius
import com.mangala.wallet.common.mokoresources.Dimensions
import com.mangala.wallet.common.mokoresources.Spacing
import com.mangala.wallet.features.addressbook.data.model.group.GroupModel
import com.mangala.wallet.features.addressbook.presentation.contact.recent.components.TransactionNetworkBadge
import com.mangala.wallet.ui.component.MaxWidthColumn
import com.mangala.wallet.ui.component.MaxWidthRow
import com.mangala.wallet.ui.placeholder.mangalaWalletPlaceholder
import com.mangala.wallet.ui.theme.MangalaTypography
import com.mangala.wallet.ui.theme.mangalaColors

@Composable
fun GroupItem(
    group: GroupModel,
    onGroupClick: (GroupModel) -> Unit,
    isLoading: Boolean = false,
) {
    val onGroupClickRemembered = remember(group) {
        {
            onGroupClick(group)
        }
    }

    MaxWidthColumn(
        modifier = Modifier
            .mangalaWalletPlaceholder(
                visible = isLoading,
                color = MaterialTheme.mangalaColors.skeletonBase,
                highlightColor = MaterialTheme.mangalaColors.skeletonShimmer,
                shape = RoundedCornerShape(CornerRadius.Small),
            )
            .clip(RoundedCornerShape(CornerRadius.Small))
            .clickable(onClick = onGroupClickRemembered)
            .background(MaterialTheme.mangalaColors.bgInnerCard)
            .padding(
                vertical = Dimensions.Padding.small,
                horizontal = Dimensions.Padding.default
            ),
        verticalArrangement = Arrangement.spacedBy(Spacing.XTINY)
    ) {
        MaxWidthRow(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.TINY)
        ) {
            val groupColor = remember(group.color) { getColorFromGroupColor(group.color) }
            GroupIcon(
                name = group.name,
                icon = group.icon,
                backgroundColor = groupColor,
                modifier = Modifier.size(Dimensions.AddressBookContactAvatarSize)
            )

            MaxWidthColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = group.name,
                    style = MangalaTypography.Size14Medium(),
                    color = MaterialTheme.mangalaColors.textPrimary,
                )

                Text(
                    text = "${group.walletAddressCount} ${if (group.walletAddressCount != 1) "addresses" else "address"}",
                    style = MangalaTypography.Size14Regular(),
                    color = MaterialTheme.mangalaColors.textSecondary,
                )
            }

            group.mainBlockchainSymbol?.let {
                TransactionNetworkBadge(
                    tokenSymbol = group.mainBlockchainSymbol,
                    modifier = Modifier
                )
            }
        }
    }
}