package com.mangala.wallet.features.transactionhistory.presentation.evm.info

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
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.mangala.wallet.domain.transaction.history.TransactionStatus
import com.mangala.wallet.features.chains.evmcompatible.model.Address
import com.mangala.wallet.common.mokoresources.Colors
import com.mangala.wallet.common.mokoresources.CornerRadius
import com.mangala.wallet.common.mokoresources.Dimensions
import com.mangala.wallet.common.mokoresources.icons.MangalaWalletPack
import com.mangala.wallet.mokoresources.MR 
import com.mangala.wallet.common.mokoresources.Spacing
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.Copy
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.Share
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
import com.mangala.wallet.ui.utils.collectAsStateMultiplatform
import com.mangala.wallet.ui.utils.screenmodel.BaseScreen
import com.mangala.wallet.utils.analytics.MangalaAnalytics
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.desc.desc
import org.koin.core.parameter.parametersOf

class TransactionInfoScreen(
    private val accountId: String,
    private val txHash: String
) : BaseScreen<TransactionInfoScreenModel>() {

    override val screenName: String = MangalaAnalytics.Screens.EVM_TRANSACTION_INFO
    override val screenClassName: String = TransactionInfoScreen::class.simpleName.orEmpty()

    override val isBottomBarVisible: Boolean = false

    @Composable
    override fun createScreenModel(): TransactionInfoScreenModel {
        return getScreenModel(
            parameters = { parametersOf(accountId, txHash) }
        )
    }

    @Composable
    override fun ScreenContent(screenModel: TransactionInfoScreenModel) {
        val uriHandler = LocalUriHandler.current
        val navigator = LocalNavigator.currentOrThrow

        val uiState by screenModel.uiState.collectAsStateMultiplatform()

        TransactionInfoScreen(
            uiState,
            isRefreshing = screenModel.isRefreshing.value,
            onPullToRefresh = {
                screenModel.onPullToRefresh()
            },
            onBackClicked = {
                navigator.pop()
            },
            onClickShare = {
                screenModel.onClickShare()
            },
            onClickCopyTransactionId = {
                screenModel.onClickCopyTransactionId()
            },
            onClickCopyAddress = {
                screenModel.onClickCopyAddress()
            },
            onClickMoreDetails = {
                val blockExplorerUrl =
                    (uiState as? TransactionInfoUiState.Loaded)?.uiModel?.blockExplorerUrl.orEmpty()
                if (blockExplorerUrl.isNotBlank()) {
                    uriHandler.openUri(blockExplorerUrl)
                }
            }
        )
    }

    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    fun TransactionInfoScreen(
        uiState: TransactionInfoUiState,
        onBackClicked: () -> Unit,
        onClickShare: () -> Unit,
        onClickCopyTransactionId: () -> Unit,
        onClickCopyAddress: () -> Unit,
        onClickMoreDetails: () -> Unit,
        isRefreshing: Boolean,
        onPullToRefresh: () -> Unit
    ) {
        MaxSizeColumn(
            Modifier
                .background(MaterialTheme.colors.background)
                .windowInsetsPadding(WindowInsets.safeDrawing)
        ) {
            MangalaWalletTopBar(
                text = MR.strings.title_transaction_info.desc().localized(),
                onBackClicked = {
                    onBackClicked()
                },
                trailingButton = {
                    MangalaWalletIconButton(
                        icon = MangalaWalletPack.Share,
                        onClick = {
                            onClickShare()
                        }
                    )
                },
            )
            val pullRefreshState = PullRefreshState(
                isRefreshing = isRefreshing,
                onRefresh = {
                    onPullToRefresh()
                }
            )
            Box(
                modifier = Modifier.pullRefresh(
                    pullRefreshState,
                    enabled = !isRefreshing
                ).verticalScroll(rememberScrollState())
            ) {
                when (uiState) {
                    is TransactionInfoUiState.Loaded -> {
                        TransactionInfoScreenData(
                            uiState.uiModel,
                            onBackClicked,
                            onClickCopyTransactionId,
                            onClickCopyAddress,
                            onClickMoreDetails,
                        )
                    }
                    TransactionInfoUiState.Loading -> {
                        Text(MR.strings.all_loading_normal.desc().localized()) // temp ui, no need for localization  // TODO
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
    fun TransactionInfoScreenData(
        uiModel: TransactionInfoUi,
        onBackClicked: () -> Unit,
        onClickCopyTransactionId: () -> Unit,
        onClickCopyAddress: () -> Unit,
        onClickMoreDetails: () -> Unit,
    ) {
        MaxSizeColumn(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            VerticalSpacer(Spacing.BASE)
            TextSubTitle(
                uiModel.formattedTransactionValue,
                color = Colors.main1Text,
            )
            VerticalSpacer(Spacing.TINY)
            TextNormal(uiModel.formattedFiatValue, color = MaterialTheme.colors.onPrimary)
            VerticalSpacer(Spacing.BASE)
            Column(
                Modifier
                    .padding(horizontal = Dimensions.Padding.default)
                    .clip(RoundedCornerShape(CornerRadius.Small)),
                verticalArrangement = Arrangement.spacedBy(1.dp)
            ) {
                TransactionInfoRow(MR.strings.all_status.desc().localized()) {
                    val (statusText, color) = when (uiModel.status) {
                        TransactionStatus.PENDING -> MR.strings.all_transaction_pending.desc()
                            .localized() to Colors.third

                        TransactionStatus.SUCCESS -> MR.strings.all_transaction_completed.desc()
                            .localized() to Colors.green

                        TransactionStatus.FAILED -> MR.strings.all_transaction_failed.desc()
                            .localized() to Colors.coral
                    }
                    TextNormal(
                        statusText,
                        modifier = Modifier.fillMaxWidth().weight(1f).align(Alignment.End),
                        textAlign = TextAlign.End,
                        color = color
                    )
                }
                TransactionInfoRow(MR.strings.all_network.desc().localized()) {
                    TextNormal(
                        uiModel.network,
                        modifier = Modifier.fillMaxWidth().weight(1f).align(Alignment.End),
                        textAlign = TextAlign.End
                    )
                }
                TransactionInfoRow(
                    MR.strings.message_transaction_info_transaction_id.desc().localized()
                ) {
                    Row(
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth().weight(1f)
                    ) {
                        TextNormal(
                            uiModel.formattedTransactionId,
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
                TransactionInfoRow(MR.strings.message_transaction_info_address.desc().localized()) {
                    Row(
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth().weight(1f)
                    ) {
                        TextNormal(
                            Address(uiModel.address).eip55,
                            maxLines = 1,
                            modifier = Modifier.weight(1f),
                            textAlign = TextAlign.End
                        )
                        HorizontalSpacer(Spacing.TINY)
                        MangalaWalletIconButton(MangalaWalletPack.Copy, Modifier.size(18.dp)) {
                            onClickCopyAddress()
                        }
                    }
                }
                TransactionInfoRow(MR.strings.message_transaction_info_gas_fee.desc().localized()) {
                    TextNormal(
                        uiModel.formattedGasFeeValue,
                        modifier = Modifier.fillMaxWidth().weight(1f).align(Alignment.End),
                        textAlign = TextAlign.End
                    )
                }
                TransactionInfoRow(MR.strings.all_date.desc().localized()) {
                    TextNormal(
                        uiModel.formattedTime,
                        modifier = Modifier.fillMaxWidth().weight(1f).align(Alignment.End),
                        textAlign = TextAlign.End
                    )
                }
            }
            VerticalSpacer(Spacing.SMALL)
            if (txHash.isNotBlank()) {
                MangalaTextButton(
                    MR.strings.button_transaction_info_more_details.desc().localized(),
                    color = Colors.blue
                ) {
                    onClickMoreDetails()
                }
            }
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