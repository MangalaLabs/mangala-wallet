package com.mangala.wallet.features.send_base.step4

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.mangala.wallet.common.mokoresources.Dimensions
import com.mangala.wallet.common.mokoresources.Spacing
import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.ui.TextDescription2
import com.mangala.wallet.ui.component.MangalaCircularProgressIndicatorFullScreen
import com.mangala.wallet.ui.component.MangalaWalletTopBarCenteredTitle
import com.mangala.wallet.ui.component.MaxSizeColumn
import com.mangala.wallet.ui.component.MaxWidthRow
import com.mangala.wallet.ui.component.VerticalSpacer
import com.mangala.wallet.ui.theme.mangalaColors
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.desc.desc

@Composable
fun VerifyAndSendScreen(
    onClickBack: () -> Unit,
    confirmationItems: @Composable () -> Unit,
    transactionSummary: @Composable () -> Unit,
    totalTransactionValue: @Composable () -> Unit,
    mainButton: @Composable () -> Unit,
    error: String? = null,
    isLoading: Boolean = false
) {
    MaxSizeColumn(
        modifier = Modifier
            .safeDrawingPadding()
    ) {
        MangalaWalletTopBarCenteredTitle(
            title = MR.strings.title_verify_transaction.desc().localized(),
            onBackClicked = onClickBack
        )
        Box(Modifier.weight(1f)) {
            Column(
                modifier = Modifier
                    .padding(horizontal = Dimensions.Padding.default)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                Column(
                    modifier = Modifier.padding(vertical = Spacing.SMALL)
                ) {
                    Spacer(modifier = Modifier.height(Spacing.TINY))
                    TextDescription2(
                        MR.strings.message_verify_transaction.desc().localized(),
                        color = MaterialTheme.mangalaColors.textSecondary
                    )
                    VerticalSpacer(Spacing.SMALL)
                    confirmationItems()
                    VerticalSpacer(Spacing.XSMALL)
                    transactionSummary()
                    VerticalSpacer(Spacing.BASE)
                    totalTransactionValue()
                }
                error?.let {
                    Text(
                        text = it,
                        color = MaterialTheme.mangalaColors.buttonDestructiveContainer,
                    )
                }
            }
            if (isLoading) {
                MangalaCircularProgressIndicatorFullScreen(
                    color = MaterialTheme.mangalaColors.iconPrimary,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
        MaxWidthRow(Modifier.padding(Dimensions.Padding.default)) {
            mainButton()
        }
    }
}