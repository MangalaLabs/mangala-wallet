package com.mangala.wallet.features.addressbook.presentation.tag

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
import androidx.compose.ui.graphics.Color
import com.mangala.wallet.common.mokoresources.ColorsNew
import com.mangala.wallet.common.mokoresources.CornerRadius
import com.mangala.wallet.common.mokoresources.Dimensions
import com.mangala.wallet.common.mokoresources.Spacing
import com.mangala.wallet.features.addressbook.data.model.tag.TagEntity
import com.mangala.wallet.features.addressbook.utils.stringToColor
import com.mangala.wallet.ui.component.MaxWidthColumn
import com.mangala.wallet.ui.component.MaxWidthRow
import com.mangala.wallet.ui.placeholder.mangalaWalletPlaceholder
import com.mangala.wallet.ui.theme.MangalaTypography
import com.mangala.wallet.ui.theme.mangalaColors

@Composable
fun TagItem(
    tag: TagEntity,
    onTagClick: (TagEntity) -> Unit,
    isLoading: Boolean = false,
) {
    val onTagClickRemembered = remember(tag) {
        { onTagClick(tag) }
    }

    // Remember derived states to avoid recalculation on recomposition
    val backgroundColor = remember(tag.color) {
        val colorRaw = tag.color
        if (colorRaw.toIntOrNull() != null) {
            // If it's a valid index, use indexToColor directly
            ColorsNew.indexToColor(colorRaw.toInt())
        } else {
            // Otherwise fall back to stringToColor for hex values
            stringToColor(colorRaw, ColorsNew.tagTeal)
        }
    }
    
    val textColor = remember(tag.textColor, tag.color) {
        val textColorRaw = tag.textColor
        if (textColorRaw?.toIntOrNull() != null) {
            // If it's a valid index, use indexToColor directly
            ColorsNew.indexToColor(textColorRaw.toInt())
        } else {
            // Otherwise fall back to stringToColor for hex values
            if (textColorRaw != null) {
                stringToColor(textColorRaw, Color.White)
            } else {
                // If textColor is null, calculate the appropriate contrast color
                stringToColor(tag.calculateTextColor(), Color.White)
            }
        }
    }

    // Remember contact count text to avoid string concatenation on recomposition
    val contactCountText = remember(tag.contactCount) {
        val count = tag.contactCount ?: 0
        "$count ${if (count == 1) "contact" else "contacts"}"
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
            .clickable(onClick = onTagClickRemembered)
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
            TagIcon(
                name = tag.name,
                icon = tag.icon,
                backgroundColor = backgroundColor,
                contentColor = textColor,
                modifier = Modifier.size(Dimensions.AddressBookContactAvatarSize),
                useFullOpacityBackground = true
            )

            MaxWidthColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = tag.name,
                    style = MangalaTypography.Size14Medium(),
                    color = MaterialTheme.mangalaColors.textPrimary,
                )

                Text(
                    text = contactCountText,
                    style = MangalaTypography.Size14Regular(),
                    color = MaterialTheme.mangalaColors.textSecondary,
                )
            }
        }
    }
}