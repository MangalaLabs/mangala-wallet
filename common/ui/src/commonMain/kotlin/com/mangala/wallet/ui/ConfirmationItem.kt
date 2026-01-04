package com.mangala.wallet.ui

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mangala.wallet.common.mokoresources.CornerRadius
import com.mangala.wallet.common.mokoresources.Dimensions
import com.mangala.wallet.common.mokoresources.Spacing
import com.mangala.wallet.ui.component.AccountAddressImage
import com.mangala.wallet.ui.component.MangalaWalletCheckbox
import com.mangala.wallet.ui.component.MaxWidthRow
import com.mangala.wallet.ui.imageloader.LocalImage
import com.mangala.wallet.ui.imageloader.RemoteImage
import com.mangala.wallet.ui.theme.mangalaColors
import dev.icerock.moko.resources.ImageResource

@Composable
fun AddressConfirmationItem(
    address: String,
    label: String,
    value: String,
    subtitleValue: String? = null,
    isChecked: Boolean,
    onClick: () -> Unit,
) {
    ConfirmationItem(
        imageUrl = null,
        label = label,
        value = value,
        subtitleValue = subtitleValue,
        isChecked = isChecked,
        onClick = onClick,
        leadingComposable = {
            AccountAddressImage(address, size = Dimensions.ImageSendNftConfirmationSize)
        }
    )
}

@Composable
fun ConfirmationItem(
    imageUrl: String?,
    label: String,
    value: String,
    subtitleValue: String? = null,
    isChecked: Boolean,
    onClick: () -> Unit,
    leadingComposable: @Composable () -> Unit = {
        imageUrl?.let {
            RemoteImage(
                Modifier.size(Dimensions.ImageSendNftConfirmationSize).clip(CircleShape),
                url = it
            )
        }
    },
) {
    ConfirmationItem(
        imageUrl = imageUrl,
        label = label,
        value = {
            TextNormal(
                value,
                color = MaterialTheme.mangalaColors.textPrimary,
                fontWeight = FontWeight.W500
            )
        },
        subtitleValue = subtitleValue,
        isChecked = isChecked,
        onClick = onClick,
        leadingComposable = leadingComposable
    )
}

@Composable
fun ConfirmationLocalItem(
    imageUrl: ImageResource?,
    label: String,
    value: String,
    subtitleValue: String? = null,
    isChecked: Boolean,
    onClick: () -> Unit,
    leadingComposable: @Composable () -> Unit = {
        imageUrl?.let {
            LocalImage(
                Modifier.size(Dimensions.ImageSendNftConfirmationSize).clip(CircleShape),
                imageResource = it
            )
        }
    },
) {
    ConfirmationLocalItem(
        imageUrl = imageUrl,
        label = label,
        value = {
            TextNormal(
                value,
                color = MaterialTheme.mangalaColors.textPrimary,
                fontWeight = FontWeight.W500
            )
        },
        subtitleValue = subtitleValue,
        isChecked = isChecked,
        onClick = onClick,
        leadingComposable = leadingComposable
    )
}

@Composable
fun ConfirmationLocalItem(
    imageUrl: ImageResource?,
    label: String,
    value: @Composable () -> Unit,
    subtitleValue: String? = null,
    isChecked: Boolean,
    onClick: () -> Unit,
    leadingComposable: @Composable () -> Unit = {
        imageUrl?.let {
            LocalImage(
                Modifier.size(Dimensions.ImageSendNftConfirmationSize).clip(CircleShape),
                imageResource = it
            )
        } ?: run {
            Box(Modifier.size(Dimensions.ImageSendNftConfirmationSize))
        }
    }
) {
    MaxWidthRow(
        Modifier
            .clip(RoundedCornerShape(CornerRadius.Small))
            .border(
                0.5.dp,
                MaterialTheme.mangalaColors.borderHighlight,
                RoundedCornerShape(CornerRadius.Small)
            )
            .clickable { onClick() }
            .padding(Dimensions.Padding.default),
        horizontalArrangement = Arrangement.spacedBy(Spacing.SMALL),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.TINY)
        ) {
            leadingComposable()
            Column(verticalArrangement = Arrangement.spacedBy(Spacing.XTINY)) {
                TextDescription2(label, color = MaterialTheme.mangalaColors.textSecondary)
                value()
                subtitleValue?.let {
                    TextDescription2(it, color = MaterialTheme.mangalaColors.textPrimary)
                }
            }
        }
        MangalaWalletCheckbox(
            modifier = Modifier.size(Dimensions.CheckboxSendNftConfirmationSize),
            checked = isChecked,
            onCheckedChange = null, // parent component handling click
        )
    }
}

@Composable
fun ConfirmationItem(
    imageUrl: String?,
    label: String,
    value: @Composable () -> Unit,
    subtitleValue: String? = null,
    isChecked: Boolean,
    onClick: () -> Unit,
    leadingComposable: @Composable () -> Unit = {
        imageUrl?.let {
            RemoteImage(
                Modifier.size(Dimensions.ImageSendNftConfirmationSize).clip(CircleShape),
                url = it
            )
        } ?: run {
            Box(Modifier.size(Dimensions.ImageSendNftConfirmationSize))
        }
    }
) {
    MaxWidthRow(
        Modifier
            .clip(RoundedCornerShape(CornerRadius.Small))
            .border(
                1.dp,
                MaterialTheme.mangalaColors.borderHighlight,
                RoundedCornerShape(CornerRadius.Small)
            )
            .clickable { onClick() }
            .padding(Dimensions.Padding.default),
        horizontalArrangement = Arrangement.spacedBy(Spacing.SMALL),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.TINY)
        ) {
            leadingComposable()
            Column(verticalArrangement = Arrangement.spacedBy(Spacing.XTINY)) {
                TextDescription2(label, color = MaterialTheme.mangalaColors.textSecondary)
                value()
                subtitleValue?.let {
                    TextDescription2(it, color = MaterialTheme.mangalaColors.textPrimary)
                }
            }
        }
        MangalaWalletCheckbox(
            checked = isChecked,
            onCheckedChange = null,
        )
    }
}