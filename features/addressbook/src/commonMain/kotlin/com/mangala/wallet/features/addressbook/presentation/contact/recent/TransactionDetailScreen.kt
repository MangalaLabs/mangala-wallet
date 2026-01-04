package com.mangala.wallet.features.addressbook.presentation.contact.recent

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.UriHandler
import androidx.compose.ui.text.style.TextAlign
import cafe.adriel.voyager.core.registry.ScreenRegistry
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.mangala.wallet.common.mokoresources.CornerRadius
import com.mangala.wallet.common.mokoresources.Dimensions
import com.mangala.wallet.common.mokoresources.Elevation
import com.mangala.wallet.common.mokoresources.Spacing
import com.mangala.wallet.common.mokoresources.icons.MangalaWalletPack
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.HelpCenter
import com.mangala.wallet.features.addressbook.data.model.TransactionDetailModel
import com.mangala.wallet.features.addressbook.data.model.enum.DisplayMode
import com.mangala.wallet.features.addressbook.data.model.enum.TransactionStatus
import com.mangala.wallet.features.addressbook.icon.ContactIcon
import com.mangala.wallet.features.addressbook.icon.contacticon.OpenExternalApp
import com.mangala.wallet.features.addressbook.icon.contacticon.ShareButton
import com.mangala.wallet.features.addressbook.presentation.components.ContactQrButton
import com.mangala.wallet.features.addressbook.presentation.components.ContactRowWithActions
import com.mangala.wallet.features.addressbook.presentation.components.DocumentCopyButton
import com.mangala.wallet.features.addressbook.presentation.contact.recent.components.TransactionAmountSection
import com.mangala.wallet.features.addressbook.presentation.contact.recent.components.TransactionNetworkBadge
import com.mangala.wallet.features.addressbook.presentation.contact.recent.components.TransactionStatusChip
import com.mangala.wallet.features.addressbook.presentation.contact.recent.model.RecentTransactionDetailUiState
import com.mangala.wallet.ui.SharedScreen
import com.mangala.wallet.ui.component.MangalaButtonSize
import com.mangala.wallet.ui.component.MangalaGradientButton
import com.mangala.wallet.ui.component.MangalaOutlinedButtonNew
import com.mangala.wallet.ui.component.MangalaWalletTopBarCenteredTitle
import com.mangala.wallet.ui.component.MaxSizeColumn
import com.mangala.wallet.ui.component.MaxWidthColumn
import com.mangala.wallet.ui.component.MaxWidthRow
import com.mangala.wallet.ui.placeholder.mangalaWalletPlaceholder
import com.mangala.wallet.ui.theme.MangalaTypography
import com.mangala.wallet.ui.theme.mangalaColors
import com.mangala.wallet.ui.utils.collectAsStateMultiplatform
import com.mangala.wallet.ui.utils.screenmodel.BaseScreen
import com.mangala.wallet.utils.FormatStyle
import com.mangala.wallet.utils.ShareFactory
import com.mangala.wallet.utils.analytics.MangalaAnalytics
import com.mangala.wallet.utils.formatDate
import com.mangala.wallet.utils.formatTime
import com.mangala.wallet.utils.formattedAddress
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.parameter.parametersOf

class TransactionDetailScreen(
    val transactionId: String,
) : BaseScreen<TransactionDetailScreenModel>(), KoinComponent {

    @Composable
    override fun createScreenModel(): TransactionDetailScreenModel = getScreenModel(
        parameters = {
            parametersOf(transactionId)
        }
    )

    override val screenName: String = MangalaAnalytics.Screens.RECENT_TRANSACTION_DETAILS

    override val screenClassName: String = TransactionDetailScreen::class.simpleName.orEmpty()
    override val isBottomBarVisible: Boolean
        get() = false

    @Composable
    override fun ScreenContent(screenModel: TransactionDetailScreenModel) {
        val navigator = LocalNavigator.currentOrThrow
        val uriHandler = LocalUriHandler.current
        val shareFactory = get<ShareFactory>()

        val uiState by screenModel.uiState.collectAsStateMultiplatform()
        val scrollState = rememberScrollState()

        MaxSizeColumn(
            modifier = Modifier
                .background(MaterialTheme.mangalaColors.bg)
                .safeDrawingPadding()
        ) {
            // Header with back button and title using existing component
            MangalaWalletTopBarCenteredTitle(
                title = "Recent Transactions",
                textColor = MaterialTheme.mangalaColors.textPrimary,
                backIconTint = MaterialTheme.mangalaColors.iconPrimary,
                onBackClicked = navigator::pop,
                trailingButton = if (uiState is RecentTransactionDetailUiState.Success) {
                    {
                        IconButton(
                            onClick = {
                                shareFactory.shareText(
                                    "Mangala share via",
                                    (uiState as RecentTransactionDetailUiState.Success).txBlockExplorerLink
                                )
                            }
                        ) {
                            Icon(
                                imageVector = ContactIcon.ShareButton,
                                contentDescription = "Share",
                                tint = MaterialTheme.mangalaColors.iconPrimary,
                            )
                        }
                    }
                }
                else null
            )

            MaxSizeColumn(
                modifier = Modifier
                    .padding(
                        bottom = Dimensions.Padding.default,
                        start = Dimensions.Padding.default,
                        end = Dimensions.Padding.default
                    )
            ) {
                when (val currentState = uiState) {
                    is RecentTransactionDetailUiState.Loading -> TransactionDetailLoadingContent()

                    is RecentTransactionDetailUiState.Success -> {
                        TransactionDetailLoadedContent(
                            modifier = Modifier.weight(1f),
                            transactionDetail = currentState.transactionDetail,
                            transactionExplorerUrl = currentState.txBlockExplorerLink,
                            uriHandler = uriHandler,
                            scrollState = scrollState
                        )

                        Spacer(modifier = Modifier.height(Spacing.TINY))

                        TransactionDetailActions(
                            transactionDetail = currentState.transactionDetail,
                            transactionExplorerUrl = currentState.txBlockExplorerLink,
                            onClickRepeatTx = {
                                val step3Screen = ScreenRegistry.get(
                                    SharedScreen.Step3SelectAmountScreen(
                                        accountId = "",
                                        contactId = null,
                                        address = currentState.transactionDetail.transaction.let {
                                            if (it.isFromImportedWallet) it.toAddress else it.fromAddress
                                        },
                                        blockchainUid = currentState.transactionDetail.blockchainType.id,
                                        amount = currentState.transactionDetail.transaction.amount
                                    )
                                )
                                navigator.push(step3Screen)
                            },
                            uriHandler = uriHandler
                        )
                    }

                    is RecentTransactionDetailUiState.Error -> {
                        MaxWidthColumn(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "Error loading transaction details",
                                style = MangalaTypography.Size14Regular(),
                                color = MaterialTheme.mangalaColors.textPrimary
                            )

                            Spacer(modifier = Modifier.height(Spacing.SMALL))

                            Button(
                                onClick = navigator::pop,
                                shape = RoundedCornerShape(CornerRadius.Small),
                                contentPadding = PaddingValues(Dimensions.Padding.xsmall),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.mangalaColors.buttonNeutralContainer,
                                    contentColor = MaterialTheme.mangalaColors.buttonNeutralContent
                                )
                            ) {
                                Text(
                                    text = "Go Back",
                                    style = MangalaTypography.Size13SemiBold(),
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun TransactionHeader(
        transactionDetail: TransactionDetailModel,
        isLoading: Boolean = false,
    ) {
        Card(
            shape = RoundedCornerShape(CornerRadius.Small),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.mangalaColors.bgInnerCard,
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = Elevation.Medium,
            ),
            modifier = Modifier.mangalaWalletPlaceholder(
                visible = isLoading,
                shape = RoundedCornerShape(CornerRadius.Small),
                color = MaterialTheme.mangalaColors.skeletonBase,
                highlightColor = MaterialTheme.mangalaColors.skeletonShimmer,
            )
        ) {
            MaxWidthColumn(
                modifier = Modifier
                    .padding(Dimensions.Padding.small),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                // Status chip
                TransactionStatusChip(status = transactionDetail.transaction.status)

                Spacer(modifier = Modifier.height(Spacing.XSMALL))

                // Amount section
                TransactionAmountSection(
                    formattedAmount = transactionDetail.transaction.formattedAmount,
                    usdEquivalent = null // TODO: This would be calculated in a real app
                )
            }
        }
    }

    @Composable
    private fun TransactionDetailsCard(
        transactionDetail: TransactionDetailModel,
        isLoading: Boolean = false,
        onClickOpenTxHash: () -> Unit,
        onClickFeeInfo: () -> Unit,
    ) {
        val formattedTimestamp = remember(transactionDetail.transaction.timestamp) {
            transactionDetail.transaction.timestamp.toLocalDateTime(TimeZone.currentSystemDefault())
                .let { localDateTime ->
                    "${localDateTime.formatDate(style = FormatStyle.MEDIUM)} • ${localDateTime.formatTime()}"
                }
        }

        Card(
            shape = RoundedCornerShape(CornerRadius.Small),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.mangalaColors.bgInnerCard,
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = Elevation.Medium,
            ),
            modifier = Modifier.mangalaWalletPlaceholder(
                visible = isLoading,
                shape = RoundedCornerShape(CornerRadius.Small),
                color = MaterialTheme.mangalaColors.skeletonBase,
                highlightColor = MaterialTheme.mangalaColors.skeletonShimmer,
            )
        ) {
            MaxWidthColumn(
                modifier = Modifier
                    .padding(
                        horizontal = Dimensions.Padding.default,
                        vertical = Dimensions.Padding.small
                    ),
                verticalArrangement = Arrangement.spacedBy(Spacing.SMALL)
            ) {
                // Network
                MaxWidthRow(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Network",
                        style = MangalaTypography.Size12Regular(),
                        color = MaterialTheme.mangalaColors.textSecondary
                    )

                    TransactionNetworkBadge(
                        tokenSymbol = transactionDetail.blockchainType.symbol
                    )
                }

                // Send amount
                DetailRow(
                    label = "Send",
                    value = transactionDetail.transaction.formattedAmount
                )

                // Fee
                transactionDetail.transaction.formattedFee?.let { formattedFee ->
                    DetailRow(
                        label = "Fee",
                        value = formattedFee,
//                    onClickInfo = onClickFeeInfo
                    )
                }

                // Total Debited
                DetailRow(
                    label = "Total Debited",
                    value = transactionDetail.transaction.totalDebited
                )

                // Timestamp
                DetailRow(
                    label = "Timestamp",
                    value = formattedTimestamp
                )

                if (transactionDetail.transaction.status == TransactionStatus.DRAFT)
                    DetailRow(
                        label = "Note",
                        value = "This transaction will be submitted when the device comes online.",
                    )
                else {
                    DetailRow(
                        label = "Transaction Hash",
                        value = transactionDetail.transaction.transactionHash.formattedAddress(),
                        textToCopy = transactionDetail.transaction.transactionHash,
                        onClickOpen = onClickOpenTxHash
                    )

                    transactionDetail.transaction.note?.takeIf { it.isNotBlank() }?.let {
                        DetailRow(
                            label = "Memo",
                            value = it,
                        )
                    }
                }
            }
        }
    }

    @Composable
    private fun FromToDetailsCard(
        transactionDetail: TransactionDetailModel,
        isLoading: Boolean = false,
        onClickQrCodeFromAccount: () -> Unit,
        onClickQrCodeToAccount: () -> Unit,
    ) {
        Card(
            shape = RoundedCornerShape(CornerRadius.Small),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.mangalaColors.bgInnerCard,
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = Elevation.Medium,
            ),
            modifier = Modifier.mangalaWalletPlaceholder(
                visible = isLoading,
                shape = RoundedCornerShape(CornerRadius.Small),
                color = MaterialTheme.mangalaColors.skeletonBase,
                highlightColor = MaterialTheme.mangalaColors.skeletonShimmer,
            )
        ) {
            MaxWidthColumn(
                modifier = Modifier
                    .padding(
                        horizontal = Dimensions.Padding.default,
                        vertical = Dimensions.Padding.small
                    ),
            ) {
                Text(
                    text = "From",
                    style = MangalaTypography.Size12Regular(),
                    color = MaterialTheme.mangalaColors.textSecondary
                )

                Spacer(modifier = Modifier.height(Spacing.TINY))

                MaxWidthRow {
                    ContactRowWithActions(
                        contactId = transactionDetail.fromContact?.id
                            ?: transactionDetail.transaction.fromAddress,
                        contactName = transactionDetail.fromContact?.name ?: "Unknown Contact",
                        avatar = transactionDetail.fromContact?.avatar,
                        textToCopy = transactionDetail.transaction.fromAddress,
                        onClickQrCode = onClickQrCodeFromAccount,
                        privacyDisplayMode = transactionDetail.fromContact?.privacyDisplayMode ?: DisplayMode.FULL,
                        address = transactionDetail.transaction.fromAddress
                    )
                }

                Spacer(modifier = Modifier.height(Spacing.SMALL))

                // To section
                Text(
                    text = "To",
                    style = MangalaTypography.Size12Regular(),
                    color = MaterialTheme.mangalaColors.textSecondary
                )

                Spacer(modifier = Modifier.height(Spacing.TINY))

                MaxWidthRow {
                    ContactRowWithActions(
                        contactId = transactionDetail.toContact?.id
                            ?: transactionDetail.transaction.toAddress,
                        contactName = transactionDetail.toContact?.name ?: "Unknown Contact",
                        avatar = transactionDetail.toContact?.avatar,
                        textToCopy = transactionDetail.transaction.toAddress,
                        onClickQrCode = onClickQrCodeToAccount,
                        privacyDisplayMode = transactionDetail.toContact?.privacyDisplayMode ?: DisplayMode.FULL,
                        address = transactionDetail.transaction.toAddress
                    )
                }
            }
        }
    }


    @Composable
    private fun DetailRow(
        label: String,
        value: String,
        textToCopy: String? = null,
        onClickInfo: (() -> Unit)? = null,
        onClickOpen: (() -> Unit)? = null,
    ) {
        MaxWidthRow(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = MangalaTypography.Size12Regular(),
                color = MaterialTheme.mangalaColors.textSecondary
            )

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = value,
                    style = MangalaTypography.Size14Medium(),
                    color = MaterialTheme.mangalaColors.textPrimary,
                    textAlign = TextAlign.Right
                )

                Spacer(modifier = Modifier.width(Spacing.XTINY))

                textToCopy?.let {
                    DocumentCopyButton(
                        textToCopy = textToCopy,
                        label = label,
                        iconTint = MaterialTheme.mangalaColors.iconSecondary,
                    )
                }

                onClickInfo?.let { onClickInfo ->
                    IconButton(
                        onClick = onClickInfo,
                        modifier = Modifier.size(Dimensions.IconButtonSize)
                    ) {
                        Icon(
                            imageVector = MangalaWalletPack.HelpCenter,
                            contentDescription = "Export",
                            tint = MaterialTheme.mangalaColors.iconSecondary,
                            modifier = Modifier.size(Dimensions.IconSize)
                        )
                    }
                }

                onClickOpen?.let { onClickOpen ->
                    IconButton(
                        onClick = onClickOpen,
                        modifier = Modifier.size(Dimensions.IconButtonSize)
                    ) {
                        Icon(
                            imageVector = ContactIcon.OpenExternalApp,
                            contentDescription = "Export",
                            tint = MaterialTheme.mangalaColors.iconSecondary,
                            modifier = Modifier.size(Dimensions.IconSize)
                        )
                    }
                }
            }
        }
    }

    @Composable
    private fun TransactionDetailLoadingContent() {
        val fooModel = remember { TransactionDetailModel() }
        MaxWidthColumn(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Spacer(modifier = Modifier.height(Spacing.SMALL))

            // Placeholder for transaction header
            TransactionHeader(
                transactionDetail = fooModel,
                isLoading = true
            )

            Spacer(modifier = Modifier.height(Spacing.SMALL))

            // Placeholder for From/To section
            FromToDetailsCard(
                transactionDetail = fooModel,
                isLoading = true,
                onClickQrCodeFromAccount = {},
                onClickQrCodeToAccount = {},
            )

            Spacer(modifier = Modifier.height(Spacing.TINY))

            // Placeholder for transaction details card
            TransactionDetailsCard(
                transactionDetail = fooModel,
                isLoading = true,
                onClickOpenTxHash = {},
                onClickFeeInfo = {}
            )

            Spacer(modifier = Modifier.height(Spacing.BASE))
        }
    }

    @Composable
    private fun TransactionDetailLoadedContent(
        transactionDetail: TransactionDetailModel,
        transactionExplorerUrl: String,
        uriHandler: UriHandler,
        scrollState: ScrollState,
        modifier: Modifier = Modifier
    ) {
        MaxWidthColumn(
            modifier = modifier.verticalScroll(scrollState)
        ) {
            Spacer(modifier = Modifier.height(Spacing.SMALL))

            // Status card with transaction amount
            TransactionHeader(transactionDetail)

            Spacer(modifier = Modifier.height(Spacing.SMALL))

            // From/To section
            FromToDetailsCard(
                transactionDetail = transactionDetail,
                onClickQrCodeFromAccount = {
                    // TODO: Implement QR code functionality
                },
                onClickQrCodeToAccount = {
                    // TODO: Implement QR code functionality
                },
            )

            Spacer(modifier = Modifier.height(Spacing.TINY))

            // Transaction details sections
            TransactionDetailsCard(
                transactionDetail = transactionDetail,
                onClickOpenTxHash = {
                    uriHandler.openUri(transactionExplorerUrl)
                },
                onClickFeeInfo = {
                    // TODO: Implement fee info functionality
                },
            )

            Spacer(modifier = Modifier.height(Spacing.SMALL))
        }
    }

    @Composable
    private fun TransactionDetailActions(
        transactionDetail: TransactionDetailModel,
        transactionExplorerUrl: String,
        onClickRepeatTx: () -> Unit,
        uriHandler: UriHandler,
    ) {
        if (transactionDetail.transaction.status != TransactionStatus.DRAFT) {
            MaxWidthRow(
                horizontalArrangement = Arrangement.spacedBy(Spacing.SMALL)
            ) {
                // View on Explorer button
                MangalaOutlinedButtonNew(
                    onClick = {
                        uriHandler.openUri(transactionExplorerUrl)
                    },
                    label = "View on Explorer",
                    size = MangalaButtonSize.Small,
                    modifier = Modifier.weight(1f),
                )

                if (transactionDetail.transaction.status != TransactionStatus.FAILED) {
                    // Repeat Transaction button - positive action using gradient
                    MangalaGradientButton(
                        label = "Repeat Transaction",
                        onClick = onClickRepeatTx,
                        size = MangalaButtonSize.Small,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}