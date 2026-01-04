package com.mangala.wallet.features.addressbook.presentation.contact.detail

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.mangala.wallet.common.mokoresources.CornerRadius
import com.mangala.wallet.common.mokoresources.Dimensions
import com.mangala.wallet.common.mokoresources.icons.MangalaWalletPack
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.MoreVertical
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.Trash
import com.mangala.wallet.features.addressbook.icon.ContactIcon
import com.mangala.wallet.features.addressbook.icon.contacticon.EditButton
import com.mangala.wallet.features.addressbook.icon.contacticon.OutlineStar
import com.mangala.wallet.features.addressbook.icon.contacticon.Star
import com.mangala.wallet.ui.component.MangalaWalletTopBarCenteredTitle
import com.mangala.wallet.ui.theme.MangalaTypography
import com.mangala.wallet.ui.theme.mangalaColors

@Composable
fun ContactDetailTopBar(
    onBackClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    isFavorite: Boolean,
    onFavoriteClick: () -> Unit,
    isEditEnabled: Boolean = true // Control edit button visibility
) {
    MangalaWalletTopBarCenteredTitle(
        title = "Contact Details",
        modifier = Modifier.statusBarsPadding(),
        onBackClicked = onBackClick,
        trailingButton = {
            Row {
                IconButton(
                    onClick = onFavoriteClick,
                ) {
                    Icon(
                        imageVector = if (isFavorite) ContactIcon.Star else ContactIcon.OutlineStar,
                        contentDescription = "Favorite",
                        tint = if (isFavorite) MaterialTheme.mangalaColors.iconFavoriteStar else MaterialTheme.mangalaColors.iconPrimary,
                        modifier = Modifier.size(Dimensions.IconSize)
                    )
                }

                // Three-dots menu button
                var showDropdown by remember { mutableStateOf(false) }

                IconButton(
                    onClick = { showDropdown = true },
                ) {
                    Icon(
                        imageVector = MangalaWalletPack.MoreVertical,
                        contentDescription = "More options",
                        tint = MaterialTheme.mangalaColors.iconPrimary,
                    )
                }

                // Dropdown menu
                DropdownMenu(
                    expanded = showDropdown,
                    onDismissRequest = { showDropdown = false },
                    containerColor = MaterialTheme.mangalaColors.bgInnerCard,
                    shape = RoundedCornerShape(CornerRadius.Medium),
                    offset = DpOffset((-8).dp, 0.dp)
                ) {
                    // Only show edit option if authenticated or not high security
                    if (isEditEnabled) {
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = "Edit contact",
                                    style = MangalaTypography.Size14Regular(),
                                    color = MaterialTheme.mangalaColors.textPrimary
                                )
                            },
                            onClick = {
                                showDropdown = false
                                onEditClick()
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = ContactIcon.EditButton,
                                    contentDescription = "Edit",
                                    tint = MaterialTheme.mangalaColors.iconPrimary
                                )
                            }
                        )
                    }
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = "Delete",
                                style = MangalaTypography.Size14Regular(),
                                color = MaterialTheme.mangalaColors.buttonDestructiveContainer,
                            )
                        },
                        onClick = {
                            showDropdown = false
                            onDeleteClick()
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = MangalaWalletPack.Trash,
                                contentDescription = "Delete",
                                tint = MaterialTheme.mangalaColors.buttonDestructiveContainer
                            )
                        }
                    )
                }
            }
        }
    )
}