package com.mangala.wallet.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import com.mangala.wallet.common.mokoresources.Colors
import com.mangala.wallet.common.mokoresources.CornerRadius
import com.mangala.wallet.common.mokoresources.Dimensions
import com.mangala.wallet.common.mokoresources.FontType
import com.mangala.wallet.common.mokoresources.Spacing
import com.mangala.wallet.mokoresources.*
import com.mangala.wallet.common.mokoresources.icons.MangalaWalletPack
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.Ram
import com.mangala.wallet.ui.TextDescription2
import com.mangala.wallet.ui.imageloader.DisplayImageForIcon
import com.mangala.wallet.ui.imageloader.ImageHolder
import com.mangala.wallet.ui.placeholder.mangalaWalletPlaceholder
import com.mangala.wallet.ui.theme.mangalaColors
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.desc.desc

@Composable
fun RamInformation(
    isLoading: Boolean = false,
    modifier: Modifier = Modifier,
    colorIconRam: Color,
    colorTintIconRam: Color,
    totalRam: String?,
    percentageRamUsed: Float?,
    percentString: String?,
    ramPrice: String? = null,
    ram24hPnl: String? = null,
    eosBalance: String? = null,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(color = MaterialTheme.mangalaColors.bgInnerCard, shape = RoundedCornerShape(CornerRadius.Medium))
            .padding(Dimensions.Padding.default)
    ) {
        Column {
            HeaderSection(
                isLoading = isLoading,
                totalRam = totalRam,
                colorIconRam = colorIconRam,
                colorTintIconRam = colorTintIconRam,
                percentageRamUsed = percentageRamUsed,
                percentString = percentString
            )

            ramPrice?.let {
                Spacer(modifier = Modifier.height(Spacing.SMALL))

                RamPriceSection(
                    ramPrice = ramPrice,
                    ram24hPnl = ram24hPnl.orEmpty(),
                    eosBalance = eosBalance.orEmpty(),
                    isLoading = isLoading
                )
            }
        }
    }
}

@Composable
private fun HeaderSection(
    isLoading: Boolean,
    totalRam: String?,
    colorIconRam: Color,
    colorTintIconRam: Color,
    percentageRamUsed: Float?,
    percentString: String?
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = MangalaWalletPack.Ram,
                modifier = Modifier
                    .size(Dimensions.IconSizeSellAndBuyScreen)
                    .background(
                        color = colorIconRam,
                        shape = CircleShape
                    ).padding(Spacing.TINY),
                tint = colorTintIconRam,
                contentDescription = null,
            )
            Spacer(modifier = Modifier.width(Spacing.XTINY))
            TextDescription2(
                text = totalRam ?: "placeholder",
                fontSize = FontType.REGULAR,
                fontWeight = FontWeight.Medium,
                color = colorTintIconRam,
                modifier = Modifier.mangalaWalletPlaceholder(totalRam == null)
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {

            MangalaCircularPercentageIndicator(
                percentage = percentageRamUsed ?: 0f,
                percentageString = percentString.orEmpty(),
                strokeWidth = Dimensions.StrokeWidth,
                modifier = Modifier
                    .size(Dimensions.IconSizeSellAndBuyScreen)
                    .mangalaWalletPlaceholder(percentageRamUsed == null || percentString == null),
                colorBackground = colorIconRam,
                colorForeground = colorTintIconRam,
            )

            Spacer(modifier = Modifier.width(Spacing.STINY))

            TextDescription2(
                text = MR.strings.quota_used.desc().localized(),
                fontSize = FontType.SMALL,
                fontWeight = FontWeight.Normal,
                color = MaterialTheme.mangalaColors.textPrimary,
                modifier = Modifier.mangalaWalletPlaceholder(isLoading)
            )
        }
    }
}

@Composable
private fun RamPriceSection(
    ramPrice: String,
    ram24hPnl: String,
    eosBalance: String,
    isLoading: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            TextDescription2(
                text = MR.strings.current_ram_prices.desc().localized(),
                fontSize = FontType.TINY,
                fontWeight = FontWeight.Normal,
                color = MaterialTheme.mangalaColors.textSecondary,
                modifier = Modifier.mangalaWalletPlaceholder(isLoading)
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(Spacing.XTINY)
            ) {
                TextDescription2(
                    text = ramPrice,
                    fontSize = FontType.SMALL,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.mangalaColors.textPrimary,
                    modifier = Modifier.mangalaWalletPlaceholder(isLoading)
                        .alignByBaseline()
                )
                TextDescription2(
                    text = ram24hPnl,
                    fontSize = FontType.TINY,
                    fontWeight = FontWeight.Normal,
                    color = MaterialTheme.mangalaColors.textSecondary,
                    modifier = Modifier.mangalaWalletPlaceholder(isLoading)
                        .alignByBaseline()
                )
            }
        }

        BalanceSection(
            eosBalance = eosBalance,
            isLoading = isLoading
        )
    }
}

@Composable
private fun BalanceSection(
    eosBalance: String,
    isLoading: Boolean
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = { },
            modifier = Modifier.mangalaWalletPlaceholder(isLoading)
        ) {
            DisplayImageForIcon(
                imageHolder = ImageHolder.Paint(MR.images.eos_new),
                modifier = Modifier.size(Dimensions.IconButtonSize)
            )
        }
        Column {
            TextDescription2(
                text = eosBalance,
                fontSize = FontType.SMALL,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.mangalaColors.textPrimary,
                modifier = Modifier.mangalaWalletPlaceholder(isLoading)
            )
            TextDescription2(
                text = MR.strings.available_balance.desc().localized(),
                fontSize = FontType.TINY,
                fontWeight = FontWeight.Normal,
                color = MaterialTheme.mangalaColors.textSecondary,
                modifier = Modifier.mangalaWalletPlaceholder(isLoading)
            )
        }
    }
}
