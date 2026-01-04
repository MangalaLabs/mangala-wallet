package com.mangala.wallet.features.chains.ui

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.ionspin.kotlin.bignum.decimal.DecimalMode
import com.ionspin.kotlin.bignum.decimal.RoundingMode
import com.mangala.wallet.domain.transaction.fee.TransactionFeeOption
import com.mangala.wallet.domain.transaction.fee.TransactionFeeType
import com.mangala.wallet.common.mokoresources.Colors
import com.mangala.wallet.common.mokoresources.CornerRadius
import com.mangala.wallet.common.mokoresources.Dimensions
import com.mangala.wallet.common.mokoresources.Spacing
import com.mangala.wallet.utils.ext.formatFiat
import com.mangala.wallet.utils.ext.removeTrailingZeroes
import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.ui.TextNormal
import com.mangala.wallet.ui.TextTiny
import com.mangala.wallet.ui.component.MaxWidthColumn
import com.mangala.wallet.ui.component.MaxWidthRow
import com.mangala.wallet.ui.placeholder.mangalaWalletPlaceholder
import com.mangala.wallet.utils.toBigDecimalOrZero
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.desc.desc

@Composable
@Deprecated(
    "Use NewFeeOptionItem instead",
    replaceWith = ReplaceWith("NewFeeOptionItem(uiModel, onFeeSelected, modifier)")
)
fun OldFeeOptionItem(
    uiModel: FeeOptionUiModel,
    onFeeSelected: (FeeOptionUiModel) -> Unit,
    modifier: Modifier = Modifier
) {
    val borderModifier = if (uiModel.isSelected) {
        Modifier.border(1.dp, color = MaterialTheme.colors.primary)
    } else {
        Modifier
    }

    Row(modifier
        .then(borderModifier)
        .then(
            Modifier
                .clickable { onFeeSelected(uiModel) }
                .padding(Dimensions.Padding.default)
        ),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            val feeTypeText = when (uiModel.transactionFee.transactionFeeType) {
                TransactionFeeType.ECONOMY -> "Economy" // TODO: Extract string resource
                TransactionFeeType.REGULAR -> "Regular"
                TransactionFeeType.FAST -> "Fast"
            }
            Text(text = feeTypeText)
            Text(text = uiModel.transactionFee.transactionFeeType.estimatedProcessingTimeMinutes.toString() + " min") // TODO: Extract string resource
        }
        Row(horizontalArrangement = Arrangement.spacedBy(Spacing.TINY)) {
            Text(text = uiModel.transactionFeeValueString)
            Text(text = uiModel.transactionFeeFiatValueString)
        }
    }
}

@Composable
fun TransactionSummary(
    uiModel: FeeOptionUiModel?,
    onFeeSelected: (FeeOptionUiModel) -> Unit,
    modifier: Modifier = Modifier
) {
    val placeholderVisible = uiModel == null

    MaxWidthColumn(horizontalAlignment = Alignment.Start, verticalArrangement = Arrangement.spacedBy(
        Spacing.BASE), modifier = modifier) {
        FeeSummary(uiModel, placeholderVisible, onFeeSelected)
    }
}

@Composable
private fun FeeSummary(
    uiModel: FeeOptionUiModel?,
    placeholderVisible: Boolean,
    onFeeSelected: (FeeOptionUiModel) -> Unit
) {
    MaxWidthColumn(
        Modifier
            .clip(RoundedCornerShape(CornerRadius.Small))
            .border(
                width = 0.5.dp,
                color = Colors.stroke,
                shape = RoundedCornerShape(CornerRadius.Small)
            )
            .clickable {
                uiModel?.let(onFeeSelected)
            }
            .padding(
                vertical = Dimensions.Padding.half,
                horizontal = Dimensions.Padding.default
            ),
        verticalArrangement = Arrangement.spacedBy(Spacing.TINY)
    ) {
        MaxWidthRow(
            horizontalArrangement = Arrangement.spacedBy(Spacing.SMALL),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextNormal(
                MR.strings.fee.desc().localized(),
                color = Colors.caption,
                modifier = Modifier.weight(1f)
            )
            TextNormal(
                modifier = Modifier.mangalaWalletPlaceholder(
                    placeholderVisible,
                    modifier = Modifier.size(width = 32.dp, height = 24.dp)
                ), // TODO: Confirm loading design
                text = uiModel?.transactionFeeFiatValueString.orEmpty(),
                color = Colors.main1Text,
                fontWeight = FontWeight.W500
            )
        }
        TextTiny(
            MR.strings.button_fee_option_change.desc().localized(),
            color = if (uiModel != null) Colors.second else Colors.caption, // TODO: Confirm loading design
            textAlign = TextAlign.End,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

sealed interface FeeOptionUiModel {
    val transactionFeeFiatValueString: String
    val transactionFee: TransactionFeeOption
    val isSelected: Boolean
    val transactionFeeValueString: String
}

data class EvmFeeOptionUiModel(
    override val transactionFee: TransactionFeeOption,
    override val isSelected: Boolean,
    val transactionFeeValue: BigDecimal,
    val transactionFeeFiatValue: BigDecimal,
    val decimals: Int,
    val symbol: String,
    val fiatCurrencySymbol: String
): FeeOptionUiModel {
    override val transactionFeeValueString = BigDecimal.fromBigDecimal(
        transactionFeeValue,
        decimalMode = DecimalMode(
            scale = 5L,
            roundingMode = RoundingMode.ROUND_HALF_TOWARDS_ZERO,
            decimalPrecision = 20
        )
    ).removeTrailingZeroes().toPlainString() + " " + symbol
    override val transactionFeeFiatValueString = transactionFeeFiatValue.formatFiat(fiatCurrencySymbol)
    
    enum class GasOption {
        LOW, MEDIUM, HIGH
    }
}

data class BitcoinFeeOptionUiModel(
    val id: Int,
    val label: String,
    val description: String,
    val gasOption: EvmFeeOptionUiModel.GasOption,
    val feeSatPerVByte: Long,
    val networkCurrencySymbol: String,
    val feeAmount: String,
    val feeAmountInFiat: String,
    val estimatedTimeInSeconds: Int,
    val fiatCurrencySymbol: String,
    override val isSelected: Boolean = false
): FeeOptionUiModel {
    override val transactionFee = TransactionFeeOption(
        transactionFeeType = when(gasOption) {
            EvmFeeOptionUiModel.GasOption.LOW -> TransactionFeeType.ECONOMY
            EvmFeeOptionUiModel.GasOption.MEDIUM -> TransactionFeeType.REGULAR
            EvmFeeOptionUiModel.GasOption.HIGH -> TransactionFeeType.FAST
        },
        gasPrice = BigDecimal.parseString(feeSatPerVByte.toString()),
        priorityFee = BigDecimal.ZERO,
        baseFee = BigDecimal.ZERO,
//        maxGas = BigDecimal.parseString(feeSatPerVByte.toString())
    )

    override val transactionFeeValueString = BigDecimal.fromBigDecimal(
        feeAmount.toBigDecimalOrZero(),
        decimalMode = DecimalMode(
            scale = 8L,
            roundingMode = RoundingMode.ROUND_HALF_TOWARDS_ZERO,
            decimalPrecision = 20
        )
    ).removeTrailingZeroes().toPlainString() + " " + networkCurrencySymbol
    val transactionFeeFiatValue = BigDecimal.parseString(feeAmountInFiat.replace("$", ""))

    override val transactionFeeFiatValueString = transactionFeeFiatValue.formatFiat(fiatCurrencySymbol)
}