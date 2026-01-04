package com.mangala.wallet.features.crypto_payment.presentation.account.paywithcrypto

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import cafe.adriel.voyager.core.lifecycle.LifecycleEffect
import cafe.adriel.voyager.core.screen.Screen
import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.ionspin.kotlin.bignum.decimal.toBigDecimal
import com.mangala.wallet.common.mokoresources.Colors
import com.mangala.wallet.common.mokoresources.Dimensions
import com.mangala.wallet.common.mokoresources.FontType
import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.common.mokoresources.Spacing
import com.mangala.wallet.ui.TextNormal
import com.mangala.wallet.ui.component.MaxWidthBox
import com.mangala.wallet.ui.component.MaxWidthColumn
import com.mangala.wallet.ui.component.VerticalSpacer
import com.mangala.wallet.utils.analytics.MangalaAnalytics
import com.mangala.wallet.utils.formatCurrencyAmount
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.desc.ResourceFormatted
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc

class PaymentDetailScreen(
    private val cpu: BigDecimal,
    private val net: BigDecimal,
    private val ram: BigDecimal,
    private val serviceFee: BigDecimal,
    private val totalEos: BigDecimal,
    private val onDismiss: () -> Unit,
    private val coinUid: String
): Screen {
    @Composable
    override fun Content() {
        LifecycleEffect(onStarted = {
            MangalaAnalytics.trackScreenView(
                MangalaAnalytics.Screens.PAY_WITH_CRYPTO_PAYMENT_DETAIL,
                PaymentDetailScreen::class.simpleName.orEmpty()
            )
        })

        MaxWidthColumn(
            modifier = Modifier.padding(horizontal = Dimensions.Padding.default)
        ) {
            MaxWidthBox {
                TextNormal(
                    text = MR.strings.payment_details_title.desc().localized(),
                    color = Colors.slateGray,
                    fontSize = FontType.REGULAR,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.align(Alignment.Center)
                )
                IconButton(
                    onClick = { onDismiss() },
                    modifier = Modifier.align(Alignment.CenterEnd)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        tint = Colors.darkDarkGray,
                        contentDescription = null,
                    )
                }
            }
            PaymentDetailElement(
                label = StringDesc.ResourceFormatted(
                    MR.strings.payment_details_account_resources,
                    "CPU"
                ).localized(),
                value = "${formatCurrencyAmount(cpu, coinUid)} EOS"
            )
            VerticalSpacer(Spacing.XSMALL)
            PaymentDetailElement(
                label = StringDesc.ResourceFormatted(
                    MR.strings.payment_details_account_resources,
                    "NET"
                ).localized(),
                value = "${formatCurrencyAmount(net, coinUid)} EOS"
            )
            VerticalSpacer(Spacing.XSMALL)
            PaymentDetailElement(
                label = StringDesc.ResourceFormatted(
                    MR.strings.payment_details_account_resources,
                    "RAM"
                ).localized(),
                value = "${formatCurrencyAmount(ram, coinUid)} EOS"
            )
            VerticalSpacer(Spacing.XSMALL)
            PaymentDetailElement(
                label = MR.strings.payment_details_our_service_fee.desc().localized(),
                value = "${formatCurrencyAmount(serviceFee, coinUid)} EOS"
            )
            VerticalSpacer(Spacing.XSMALL)
            PaymentDetailElement(
                label = MR.strings.label_pay_with_crypto_total_need_to_paid_description.desc().localized(),
                value = "${formatCurrencyAmount(totalEos, coinUid)} EOS"
            )
            VerticalSpacer(Spacing.XXBASE)
        }
    }

    @Composable
    fun PaymentDetailElement(
        label: String,
        value: String
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(vertical = Dimensions.Padding.half)
        ) {
            Text(
                text = label,
                color = Colors.darkDarkGray,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = value,
                color = Colors.darkDarkGray,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}