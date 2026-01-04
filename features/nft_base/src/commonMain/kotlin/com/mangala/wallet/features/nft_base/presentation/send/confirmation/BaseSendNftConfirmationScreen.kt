package com.mangala.wallet.features.nft_base.presentation.send.confirmation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import cafe.adriel.voyager.core.lifecycle.LifecycleEffect
import cafe.adriel.voyager.core.screen.Screen
import com.mangala.wallet.features.nft_base.presentation.ui.NftImage
import com.mangala.wallet.features.nft_base.presentation.ui.NftImageType
import com.mangala.wallet.common.mokoresources.Colors
import com.mangala.wallet.common.mokoresources.Dimensions
import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.common.mokoresources.Spacing
import com.mangala.wallet.features.chains.ui.EvmFeeOptionUiModel
import com.mangala.wallet.features.chains.ui.FeeOptionUiModel
import com.mangala.wallet.ui.TextDescription2
import com.mangala.wallet.ui.component.MaxWidthRow
import com.mangala.wallet.features.chains.ui.TransactionSummary
import com.mangala.wallet.ui.component.MangalaWalletTopBar
import com.mangala.wallet.ui.component.MaxSizeColumn
import com.mangala.wallet.ui.component.MaxWidthColumn
import com.mangala.wallet.ui.component.VerticalSpacer
import com.mangala.wallet.ui.utils.collectAsStateMultiplatform
import com.mangala.wallet.utils.analytics.MangalaAnalytics
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.desc.desc

abstract class BaseSendNftConfirmationScreen <T: BaseSendNftConfirmationScreenModel>(
    protected val blockchainUid: String,
    protected val contactId: Long?,
    protected val accountId: String,
    protected val recipientAddress: String,
    protected val collectionContractAddress: String,
    protected val tokenId: String
) : Screen {

    @Composable
    abstract fun createScreenModel(): T

    abstract val analyticsClassName: String

    @Composable
    protected fun SendNftConfirmationScreen(
        screenModel: T,
        onClickTransactionOption: (FeeOptionUiModel) -> Unit,
        onTransactionSuccess: () -> Unit,
        confirmationItems: @Composable (SendNftConfirmationScreenUiState.Data) -> Unit,
        onBackClicked: () -> Unit,
        mainButton: @Composable () -> Unit
    ) {
        LifecycleEffect(onStarted = {
            MangalaAnalytics.trackScreenView(
                MangalaAnalytics.Screens.EVM_SEND_NFT_CONFIRMATION,
                analyticsClassName
            )
        })

        MaxSizeColumn(
            Modifier
                .background(MaterialTheme.colors.background)
                .windowInsetsPadding(WindowInsets.safeDrawing)
        ) {
            MangalaWalletTopBar(
                text = MR.strings.title_verify_transaction.desc().localized(),
                color = Colors.main1Text,
                fontWeight = FontWeight.W500,
                onBackClicked = onBackClicked
            )

            val uiState = (screenModel.uiState.collectAsStateMultiplatform().value) as? SendNftConfirmationScreenUiState.Data

            MaxWidthColumn(
                Modifier
                    .weight(1f)
                    .padding(horizontal = Dimensions.Padding.default)
                    .verticalScroll(rememberScrollState())
            ) {
                uiState?.let {
                    LaunchedEffect(it.txHash) {
                        it.txHash?.let {
                            onTransactionSuccess()
                        }
                    }
                    VerticalSpacer(Spacing.BASE)
                    MaxWidthRow(horizontalArrangement = Arrangement.Center) {
                        NftImage(
                            nftCollectionName = it.nftName,
                            nft = it.nft.nft.first(),
                            nftImageType = NftImageType.MEDIUM
                        )
                    }
                    VerticalSpacer(Spacing.BASE)
                    TextDescription2(
                        MR.strings.message_verify_transaction.desc().localized(),
                        color = Colors.caption
                    )
                    VerticalSpacer(Spacing.SMALL)
                    confirmationItems(
                        uiState
                    )
                    VerticalSpacer(Spacing.XSMALL)
                    TransactionSummary(
                        uiModel = uiState.selectedTransactionFee,
                        onFeeSelected = { onClickTransactionOption(it) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
            MaxWidthRow(Modifier.padding(Dimensions.Padding.default)) {
                mainButton()
            }
        }
    }
}