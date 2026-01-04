package com.mangala.features.wallet.presentation

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.PagerDefaults
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.HomeSend
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.HomeReceive
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.Portfolio
import com.mangala.wallet.ui.HIDDEN_BALANCE_STRING
import com.mangala.wallet.ui.TextDescription2
import com.mangala.wallet.ui.TextTitle2_36
import com.mangala.wallet.ui.placeholder.mangalaWalletPlaceholder
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.desc.desc
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import com.mangala.wallet.common.mokoresources.Colors
import com.mangala.wallet.common.mokoresources.CornerRadius
import com.mangala.wallet.common.mokoresources.Dimensions
import com.mangala.wallet.features.wallet.presentation.main.WalletMainScreenUiState
import com.mangala.wallet.common.mokoresources.Spacing
import com.mangala.wallet.common.mokoresources.icons.MangalaWalletPack
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.Copy
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.Group
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.Plus
import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.ui.MangalaTextButton
import com.mangala.wallet.ui.TextDescription1
import com.mangala.wallet.ui.TextDescription2
import com.mangala.wallet.ui.component.MangalaWalletIconButton
import com.mangala.wallet.ui.component.PagerIndicator
import com.mangala.wallet.ui.component.VisibilityToggleIconButton
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.desc.desc
import kotlinx.coroutines.launch

/**
 * Composable for displaying Bitcoin account cards in a pager layout
 */
@Composable
fun BitcoinAccountsPagerDataState(
    uiState: WalletMainScreenUiState.BitcoinData,
    modifier: Modifier = Modifier,
    onAccountChange: (Int) -> Unit,
    onClickCopy: () -> Unit,
    onClickShowQr: (BitcoinAccountItemUiModel) -> Unit,
    onClickManageAccounts: () -> Unit,
    onToggleBalanceVisible: (Boolean) -> Unit,
    onClickPortfolio: (accountId: String, address: String, accountName: String) -> Unit,
    onClickSend: () -> Unit,
    onClickReceive: (BitcoinAccountItemUiModel) -> Unit,
    addAccountCard: @Composable (Modifier) -> Unit,
    matchCardsHeight: Boolean = false
) {
    println("BitcoinAccountsPagerDataState: Composing with ${uiState.accounts.size} accounts")
    val pageCount =
        if (uiState.accounts.isEmpty()) 1 else uiState.accounts.size + 1
//                val pagerState = rememberPagerState()
    val pagerState = rememberPagerState(
        initialPage = uiState.selectedAccountIndex, // Can load from state for initial page
        initialPageOffsetFraction = 0f
    ) {
        // provide pageCount
        pageCount
    }
    val localDensity = LocalDensity.current

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.settledPage }.collect { page ->
            onAccountChange(page)
        }
    }

    var cardMaxHeightDp by remember {
        mutableStateOf(0.dp)
    }

    // Logic to ensure AccountInfoCard and AddAccountCard will have same height regardless of font size change of AccountInfoCard
    HorizontalPager(
        modifier = modifier,
        state = pagerState,
        pageSpacing = Spacing.TINY,
        userScrollEnabled = true,
        reverseLayout = false,
        contentPadding = PaddingValues(
            start = Dimensions.Padding.default,
            end = Dimensions.Padding.default
        ),
        beyondViewportPageCount = 1,
        pageSize = PageSize.Fill,
        flingBehavior = PagerDefaults.flingBehavior(state = pagerState),
        key = {
            if (it == pageCount - 1) {
                "AddAccountCard"
            } else {
                val id = if (it >= uiState.accounts.size) {
                    it
                } else {
                    uiState.accounts[it].account.accountId
                }
                "AccountInfoCard$id"
            }
        },
        pageNestedScrollConnection = PagerDefaults.pageNestedScrollConnection(
            pagerState,
            Orientation.Horizontal
        ),
        pageContent = { pageIndex ->
//                        val pageIndex = it
            if (pageIndex < pageCount - 1) {
                uiState.accounts.getOrNull(pageIndex)?.let {
                    BitcoinAccountCard(
                        uiModel = it,
                        onClickCopy = onClickCopy,
                        onClickShowQr = onClickShowQr,
                        modifier = Modifier.background(
                            colorAccount(
                                uiState,
                                it
                            )
                        ).onGloballyPositioned {
                            val temp = with(localDensity) { it.size.height.toDp() }
                            cardMaxHeightDp = max(
                                cardMaxHeightDp,
                                temp
                            ) // Logic to ensure AccountInfoCard and AddAccountCard will have same height regardless of font size change of AccountInfoCard
                        },
                        onToggleBalanceVisible = onToggleBalanceVisible,
                        onClickPortfolio = onClickPortfolio,
                        onClickSend = onClickSend,
                        onClickReceive = onClickReceive
                    )
                }
            } else {
                addAccountCard(if (!matchCardsHeight) Modifier.height(cardMaxHeightDp) else Modifier)
            }
        }
    )
    Spacer(modifier = Modifier.height(Spacing.SMALL))
    Row(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = Dimensions.Padding.default),
        horizontalArrangement = Arrangement.spacedBy(Spacing.SMALL),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val coroutineScope = rememberCoroutineScope()
        PagerIndicator(
            pageCount = pageCount,
            modifier = Modifier.weight(1f).padding(start = Spacing.SMALL),
            pagerState = pagerState,
            indicatorCount = 5,
            indicatorSize = 8.dp,
            activeColor = Colors.darkGray,
            inActiveColor = Color(0xFFC8C8C8),
            space = 6.dp,
            lastItemComposable = { isSelected, modifier ->
                Icon(
                    MangalaWalletPack.Plus,
                    contentDescription = null,
                    tint = if (isSelected) Colors.darkGray else Color(0xFFC8C8C8),
                    modifier = modifier
                )
            },
            onClick = {
                coroutineScope.launch {
                    pagerState.scrollToPage(it)
                }
            },
        )
        MangalaTextButton(
            MR.strings.all_manage_accounts.desc().localized(),
            isEnabled = uiState.manageAccountButtonEnabled
        ) {
            onClickManageAccounts()
        }
    }
}

@Composable
fun BitcoinAccountCard(
    uiModel: BitcoinAccountItemUiModel,
    onClickCopy: () -> Unit,
    onClickShowQr: (BitcoinAccountItemUiModel) -> Unit,
    onToggleBalanceVisible: (Boolean) -> Unit,
    onClickPortfolio: (accountId: String, address: String, accountName: String) -> Unit,
    onClickSend: () -> Unit,
    onClickReceive: (BitcoinAccountItemUiModel) -> Unit,
    modifier: Modifier = Modifier
) {
    AccountInfoCard(
        accountBasicInfo = {
            BitcoinBasicInfo(
                accountName = uiModel.account.name.orEmpty(),
                address = uiModel.account.bip84Address,
                cornerButton = {
                    VisibilityToggleIconButton(
                        uiModel.isBalanceVisible,
                        onClickBalanceVisible = onToggleBalanceVisible,
                        iconButtonModifier = Modifier.size(24.dp),
                        modifier = Modifier.size(24.dp)
                    )
                },
                onClickCopy = onClickCopy,
                onClickShowQr = {
                    onClickShowQr(uiModel)
                }
            )
        },
        sideButton = {},
        cardInfo = {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextTitle2_36(
                    uiModel.formattedBalanceFiat,
                    Modifier
                        .mangalaWalletPlaceholder(uiModel.totalValuePlaceholderEnabled)
                        .weight(1f)
                        .basicMarquee(iterations = Int.MAX_VALUE)
                )
                Spacer(Modifier.width(Spacing.SMALL))
                TextDescription2(
                    uiModel.formattedPnl.orEmpty(),
                    fontWeight = FontWeight.Medium,
                    color = if (uiModel.isBalanceVisible.not()) Colors.darkGray else Colors.third,
                    modifier = Modifier.background(
                        Color.White, shape = RoundedCornerShape(CornerRadius.Medium)
                    ).padding(
                        horizontal = Dimensions.Padding.half,
                        vertical = Dimensions.Padding.quarter
                    ).mangalaWalletPlaceholder(uiModel.totalValuePlaceholderEnabled)
                )
            }
        },
        modifier = modifier,
        bottomButtons = {
            AccountCardButton(
                MR.strings.button_wallet_main_account_card_portfolio.desc().localized(),
                MangalaWalletPack.Portfolio,
                isLoading = uiModel.totalValuePlaceholderEnabled
            ) {
                onClickPortfolio(
                    uiModel.account.accountId,
                    uiModel.account.bip84Address,
                    uiModel.account.name.orEmpty()
                )
            }
            AccountCardButton(
                MR.strings.all_send.desc().localized(),
                MangalaWalletPack.HomeSend,
                isLoading = uiModel.totalValuePlaceholderEnabled
            ) {
                onClickSend()
            }
            AccountCardButton(
                MR.strings.all_receive.desc().localized(),
                MangalaWalletPack.HomeReceive,
                isLoading = uiModel.totalValuePlaceholderEnabled
            ) {
                onClickReceive(uiModel)
            }
        }
    )
}

@Composable
private fun BitcoinBasicInfo(
    accountName: String,
    address: String,
    cornerButton: @Composable() (RowScope.() -> Unit),
    onClickCopy: () -> Unit,
    onClickShowQr: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        TextDescription1(
            accountName,
            color = MaterialTheme.colors.onPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        cornerButton()
    }
    Spacer(Modifier.height(Spacing.XTINY))
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextDescription2(
            address.take(8) + "..." + address.takeLast(8),
            color = MaterialTheme.colors.onPrimary
        )
        Spacer(Modifier.width(Spacing.TINY))
        MangalaWalletIconButton(
            MangalaWalletPack.Copy,
            onClick = {
                onClickCopy()
            },
            modifier = Modifier.size(16.dp)
        )
        Spacer(Modifier.width(Spacing.TINY))
        MangalaWalletIconButton(
            MangalaWalletPack.Group,
            onClick = {
                onClickShowQr()
            },
            modifier = Modifier.size(16.dp)
        )
    }
}