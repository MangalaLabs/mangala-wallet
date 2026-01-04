package com.mangala.wallet.features.transactionhistory.presentation.bitcoin.info

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.mangala.wallet.common.mokoresources.Colors
import com.mangala.wallet.common.mokoresources.CornerRadius
import com.mangala.wallet.common.mokoresources.Dimensions
import com.mangala.wallet.common.mokoresources.Spacing
import com.mangala.wallet.common.mokoresources.icons.MangalaWalletPack
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.Copy
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.Share
import com.mangala.wallet.features.chains.bitcoin.domain.utils.formatBitcoin
import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.ui.MangalaTextButton
import com.mangala.wallet.ui.PullRefreshState
import com.mangala.wallet.ui.TextNormal
import com.mangala.wallet.ui.TextSubTitle
import com.mangala.wallet.ui.component.HorizontalSpacer
import com.mangala.wallet.ui.component.MangalaWalletIconButton
import com.mangala.wallet.ui.component.MangalaWalletTopBar
import com.mangala.wallet.ui.component.MaxSizeColumn
import com.mangala.wallet.ui.component.MaxWidthRow
import com.mangala.wallet.ui.component.VerticalSpacer
import com.mangala.wallet.ui.utils.screenmodel.BaseScreen
import com.mangala.wallet.utils.analytics.MangalaAnalytics
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.desc.desc
import org.koin.core.parameter.parametersOf

/**
 * Screen to display detailed information about a Bitcoin transaction
 */
class TransactionInfoBitcoinScreen(
    private val bitcoinAddress: String, 
    private val txHash: String
) : BaseScreen<TransactionInfoBitcoinScreenModel>() {

    override val screenName: String = MangalaAnalytics.Screens.BITCOIN_TRANSACTION_INFO
    override val screenClassName: String = TransactionInfoBitcoinScreen::class.simpleName.orEmpty()

    override val isBottomBarVisible: Boolean = false

    @Composable
    override fun createScreenModel(): TransactionInfoBitcoinScreenModel = getScreenModel(
        parameters = {
            parametersOf(
                bitcoinAddress,
                txHash
            )
        }
    )

    @Composable
    override fun ScreenContent(screenModel: TransactionInfoBitcoinScreenModel) {
        val navigator = LocalNavigator.currentOrThrow
        val uriHandler = LocalUriHandler.current
        val transactionDetails by screenModel.transactionDetails.collectAsState()
        val isLoading by screenModel.isLoading.collectAsState()
        val error by screenModel.error.collectAsState()
        val explorerUrl by screenModel.explorerUrl.collectAsState()

        LaunchedEffect(Unit) {
            screenModel.loadTransactionDetails()
            screenModel.loadExplorerUrl()
        }

        TransactionInfoScreen(
            transactionDetails = transactionDetails,
            isLoading = isLoading,
            isRefreshing = isLoading,
            error = error,
            onBackClicked = { navigator.pop() },
            onPullToRefresh = { 
                // Force refresh data from network
                screenModel.refreshTransaction()
                screenModel.loadExplorerUrl()
            },
            onClickCopyTransactionId = { transactionDetails?.txid?.let { screenModel.copyToClipboard(it) } },
            onClickMoreDetails = { 
                if (explorerUrl.isNotBlank()) {
                    uriHandler.openUri(explorerUrl)
                }
            },
            onClickCopyBlockHash = {
                transactionDetails?.blockHash?.let { screenModel.copyToClipboard(it) }
            },
            onClickShare = {
                transactionDetails?.let { transaction ->
                    screenModel.shareTransaction()
                }
            },
            onClickRefresh = {
                // Force refresh data from network
                screenModel.refreshTransaction()
            }
        )
    }

    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    fun TransactionInfoScreen(
        transactionDetails: BitcoinTransactionDetailsUi?,
        isLoading: Boolean,
        isRefreshing: Boolean,
        error: String,
        onBackClicked: () -> Unit,
        onPullToRefresh: () -> Unit,
        onClickCopyTransactionId: () -> Unit,
        onClickCopyBlockHash: () -> Unit,
        onClickMoreDetails: () -> Unit,
        onClickShare: () -> Unit,
        onClickRefresh: () -> Unit
    ) {
        MaxSizeColumn(
            Modifier
                .background(MaterialTheme.colors.background)
                .windowInsetsPadding(WindowInsets.safeDrawing)
        ) {
            MangalaWalletTopBar(
                text = MR.strings.title_transaction_details.desc().localized(),
                onBackClicked = onBackClicked,
                trailingButton = {
                    Row {
                        MangalaWalletIconButton(
                            icon = MangalaWalletPack.Share,
                            onClick = onClickShare
                        )
                    }
                }
            )
            
            val pullRefreshState = PullRefreshState(
                isRefreshing = isRefreshing,
                onRefresh = onPullToRefresh
            )
            
            Box(
                modifier = Modifier
                    .pullRefresh(
                        pullRefreshState,
                        enabled = !isRefreshing
                    )
            ) {
                if (isLoading && transactionDetails == null) {
                    Text(
                        text = MR.strings.all_loading_normal.desc().localized(),
                        modifier = Modifier.padding(Dimensions.Padding.default)
                    )
                } else if (error.isNotEmpty() && transactionDetails == null) {
                    Text(
                        text = error,
                        modifier = Modifier.padding(Dimensions.Padding.default)
                    )
                } else {
                    // Show transaction details
                    transactionDetails?.let { transaction ->
                        TransactionDetailsContent(
                            transaction = transaction,
                            onClickCopyTransactionId = onClickCopyTransactionId,
                            onClickCopyBlockHash = onClickCopyBlockHash,
                            onClickMoreDetails = onClickMoreDetails
                        )
                    }
                }
                
                PullRefreshIndicator(
                    refreshing = isRefreshing,
                    state = pullRefreshState,
                    modifier = Modifier.align(Alignment.TopCenter)
                )
            }
        }
    }

    @Composable
    fun TransactionDetailsContent(
        transaction: BitcoinTransactionDetailsUi,
        onClickCopyTransactionId: () -> Unit,
        onClickCopyBlockHash: () -> Unit,
        onClickMoreDetails: () -> Unit
    ) {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            VerticalSpacer(Spacing.BASE)
            
            // Transaction amount
            TextSubTitle(
                text = transaction.amount,
                color = Colors.main1Text
            )
            
            VerticalSpacer(Spacing.MEDIUM)
            
            // Transaction details card
            Column(
                Modifier
                    .padding(horizontal = Dimensions.Padding.default)
                    .clip(RoundedCornerShape(CornerRadius.Small)),
                verticalArrangement = Arrangement.spacedBy(1.dp)
            ) {
                // Status row
                TransactionInfoRow(MR.strings.all_status.desc().localized()) {
                    val (statusText, color) = if (transaction.confirmed) {
                        MR.strings.all_confirmed.desc().localized() to Colors.green
                    } else {
                        MR.strings.all_pending.desc().localized() to Colors.coral
                    }
                    
                    TextNormal(
                        text = statusText,
                        modifier = Modifier.fillMaxWidth().weight(1f).align(Alignment.End),
                        textAlign = TextAlign.End,
                        color = color
                    )
                }
                
                // Transaction ID row
                TransactionInfoRow(MR.strings.title_transaction_hash.desc().localized()) {
                    Row(
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth().weight(1f)
                    ) {
                        val formattedTxId = if (transaction.txid.length > 14) {
                            "${transaction.txid.take(8)}...${transaction.txid.takeLast(6)}"
                        } else {
                            transaction.txid
                        }
                        
                        TextNormal(
                            text = formattedTxId,
                            maxLines = 1,
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.End
                        )
                        HorizontalSpacer(Spacing.TINY)
                        MangalaWalletIconButton(MangalaWalletPack.Copy, Modifier.size(18.dp)) {
                            onClickCopyTransactionId()
                        }
                    }
                }

                transaction.formattedDate?.let {
                    // Don't show date for pending transactions
                    TransactionInfoRow(MR.strings.title_transaction_date.desc().localized()) {
                        TextNormal(
                            text = transaction.formattedDate,
                            modifier = Modifier.fillMaxWidth().weight(1f).align(Alignment.End),
                            textAlign = TextAlign.End
                        )
                    }
                }
                
                TransactionInfoRow(MR.strings.title_transaction_fee.desc().localized()) {
                    TextNormal(
                        text = transaction.fee.formatBitcoin(),
                        modifier = Modifier.fillMaxWidth().weight(1f).align(Alignment.End),
                        textAlign = TextAlign.End
                    )
                }
                
                if (transaction.confirmed) {
                    TransactionInfoRow(MR.strings.title_block_height.desc().localized()) {
                        TextNormal(
                            text = transaction.blockHeight.toString(),
                            modifier = Modifier.fillMaxWidth().weight(1f).align(Alignment.End),
                            textAlign = TextAlign.End
                        )
                    }
                    
                    transaction.blockHash?.let { blockHash ->
                        TransactionInfoRow(MR.strings.title_block_hash.desc().localized()) {
                            Row(
                                horizontalArrangement = Arrangement.End,
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth().weight(1f)
                            ) {
                                val formattedHash = if (blockHash.length > 14) {
                                    "${blockHash.take(8)}...${blockHash.takeLast(6)}"
                                } else {
                                    blockHash
                                }
                                TextNormal(
                                    text = formattedHash,
                                    maxLines = 1,
                                    modifier = Modifier.weight(1f),
                                    textAlign = TextAlign.End
                                )
                                HorizontalSpacer(Spacing.TINY)
                                MangalaWalletIconButton(MangalaWalletPack.Copy, Modifier.size(18.dp)) {
                                    onClickCopyBlockHash()
                                }
                            }
                        }
                    }
                }
            }
            
            VerticalSpacer(Spacing.SMALL)
            
            // View in explorer button
            if (txHash.isNotBlank()) {
                MangalaTextButton(
                    text = MR.strings.action_open_in_explorer.desc().localized(),
                    color = Colors.blue
                ) {
                    onClickMoreDetails()
                }
            }
            
            VerticalSpacer(Spacing.MEDIUM)
        }
    }

    @Composable
    fun TransactionInfoRow(label: String, value: @Composable () -> Unit) {
        MaxWidthRow(
            Modifier
                .background(Color(0xFFFAFAFA))
                .padding(vertical = 14.dp, horizontal = Dimensions.Padding.default),
            horizontalArrangement = Arrangement.spacedBy(Spacing.TINY),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextNormal(label, color = Colors.gray)
            value()
        }
    }
}