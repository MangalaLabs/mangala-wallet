package com.mangala.wallet.features.nft_base.presentation

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.Text
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.PullRefreshState
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.registry.ScreenRegistry
import cafe.adriel.voyager.core.registry.rememberScreen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.mangala.eticket.presentation.event.EvenDetailScreen
import com.mangala.eticket.presentation.event.EventDetailScreenModel
import com.mangala.eticket.presentation.onboard.ETicketOnboardScreen
import com.mangala.eticket.presentation.onboard.ETicketOnboardScreenModel
import com.mangala.wallet.features.nft_base.presentation.details.NftDetailsScreen
import com.mangala.wallet.features.nft_base.presentation.mynft.MyNftScreen
import com.mangala.wallet.common.mokoresources.Colors
import com.mangala.wallet.common.mokoresources.Dimensions
import com.mangala.wallet.common.mokoresources.FontType
import com.mangala.wallet.common.mokoresources.Spacing
import com.mangala.wallet.common.mokoresources.icons.MangalaWalletPack
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.TransactionHistory
import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.ui.PullRefreshState
import com.mangala.wallet.ui.SharedScreen
import com.mangala.wallet.ui.TextNormal
import com.mangala.wallet.ui.TextTopBar
import com.mangala.wallet.ui.component.MaxSizeColumn
import com.mangala.wallet.ui.component.VerticalSpacer
import com.mangala.wallet.ui.utils.collectAsStateMultiplatform
import com.mangala.wallet.ui.utils.screenmodel.BaseScreen
import com.mangala.wallet.utils.analytics.MangalaAnalytics
import com.mangala.wallet.utils.isNotNullOrBlank
import dev.icerock.moko.resources.StringResource
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.desc.desc
import kotlinx.coroutines.launch
import org.koin.core.parameter.parametersOf

class NftMainScreen : BaseScreen<NftScreenModel>() {

    override val screenName: String = MangalaAnalytics.Screens.EVM_NFT_MAIN
    override val screenClassName: String = NftMainScreen::class.simpleName.orEmpty()

    @Composable
    override fun createScreenModel(): NftScreenModel = getScreenModel()

    @Composable
    override fun ScreenContent(screenModel: NftScreenModel) {
        val navigator = LocalNavigator.currentOrThrow

        val uiState = screenModel.uiState.collectAsStateMultiplatform().value

        LaunchedEffect(key1 = uiState as? NftScreenUiState.Success) {
            screenModel.loadData()
        }

        NftScreen(
            uiState,
            onClickNft = { collectionContractAddress, tokenId ->
                navigator.push(
                    NftDetailsScreen(
                        accountId = uiState.selectedAccount?.account?.account?.id.orEmpty(),
                        collectionContractAddress = collectionContractAddress,
                        tokenId = tokenId
                    )
                )
            },
            onSelectAccount = {
                screenModel.onSelectAccount(it)
            },
            onToggleExpandCollection = {
                screenModel.onExpandCollection(it)
            },
            onClickTransactionHistory = {
                val screen = ScreenRegistry.get(
                    SharedScreen.TransactionHistoryScreen(
                        accountId = it
                    )
                )
                navigator.push(screen)
            },
            isRefreshing = screenModel.isRefreshing.value,
            onPullToRefresh = { screenModel.pullToRefresh() }
        )
    }

    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    private fun NftScreen(
        uiState: NftScreenUiState,
        onClickNft: (collectionContractAddress: String, tokenId: String) -> Unit,
        onToggleExpandCollection: (collectionContractAddress: String) -> Unit,
        onSelectAccount: (index: Int) -> Unit,
        onClickTransactionHistory: (walletId: String) -> Unit,
        isRefreshing: Boolean,
        onPullToRefresh: () -> Unit
    ) {

        val pullRefreshState = PullRefreshState(
            isRefreshing = isRefreshing,
            onRefresh = onPullToRefresh
        )

        MaxSizeColumn(Modifier.windowInsetsPadding(WindowInsets.safeDrawing)) {
            val walletId = uiState.selectedAccount?.account?.account?.id.orEmpty()
            NftTopAppBar(walletId) {
                onClickTransactionHistory(walletId)
            }
            Box() {
                MaxSizeColumn(
                    Modifier.padding(horizontal = Dimensions.Padding.default)
                ) {
                    VerticalSpacer(Spacing.SMALL)
                    NftPager(
                        uiState,
                        onSelectAccount,
                        onToggleExpandCollection,
                        onClickNft,
                        isRefreshing,
                        pullRefreshState
                    )
                }
                PullRefreshIndicator(
                    isRefreshing,
                    pullRefreshState,
                    modifier = Modifier.align(Alignment.TopCenter)
                )
            }
        }
    }

    @Composable
    private fun NftTopAppBar(walletId: String?, onClickTransactionHistory: () -> Unit) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(Spacing.SMALL),
            modifier = Modifier.fillMaxWidth().padding(horizontal = Spacing.SMALL),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(Spacing.TINY),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                TextTopBar(
                    text = MR.strings.all_nft.desc().localized(),
                    fontSize = FontType.LARGE_24,
                    modifier = Modifier.padding(vertical = Dimensions.Padding.small)
                )
            }
            IconButton(
                modifier = Modifier.size(Dimensions.TopBarTrailingIconSize),
                onClick = onClickTransactionHistory,
                enabled = walletId.isNotNullOrBlank()
            ) {
                Icon(
                    imageVector = MangalaWalletPack.TransactionHistory,
                    contentDescription = null,
                    tint = Colors.darkGray
                )
            }
        }
    }

    @Composable
    private fun NftTabRow(
        selectedTabIndex: Int,
        onChangeTab: (Int) -> Unit
    ) {
        TabRow(selectedTabIndex = selectedTabIndex) {
            NftTab(
                MR.strings.all_my_nft,
                selectedTabIndex == 0,
                onChangeTab = {
                    onChangeTab(0)
                }
            )
            NftTab(
                MR.strings.label_eticket_tab,
                selectedTabIndex == 1,
                onChangeTab = {
                    onChangeTab(1)
                }
            )
        }
    }

    @Composable
    private fun NftTab(
        labelStringResource: StringResource,
        isSelected: Boolean,
        onChangeTab: () -> Unit
    ) {
        Tab(
            selected = isSelected,
            onClick = {
                onChangeTab()
            }
        ) {
            TextNormal(
                labelStringResource.desc().localized(),
                color = if (isSelected) Colors.darkGray else Colors.gray
            )
        }
    }

    @OptIn(ExperimentalFoundationApi::class, ExperimentalMaterialApi::class)
    @Composable
    private fun NftPager(
        uiState: NftScreenUiState,
        onSelectAccount: (index: Int) -> Unit,
        onToggleExpandCollection: (collectionContractAddress: String) -> Unit,
        onClickNft: (collectionContractAddress: String, tokenId: String) -> Unit,
        isRefreshing: Boolean,
        pullRefreshState: PullRefreshState
    ) {

        val coroutineScope = rememberCoroutineScope()
        val pagerState = rememberPagerState { 2 }

        NftTabRow(pagerState.currentPage, onChangeTab = {
            coroutineScope.launch {
                pagerState.scrollToPage(it)
            }
        })
        HorizontalPager(modifier = Modifier.fillMaxSize(),
            state = pagerState,
            pageSize = PageSize.Fill,
            key = { it },
            userScrollEnabled = false,
            pageContent = { pageIndex ->
                MaxSizeColumn {
                    if (pageIndex == 0) {
                        MyNftScreen(
                            uiState,
                            onSelectAccount,
                            onToggleExpandCollection,
                            onClickNft,
                            isRefreshing,
                            pullRefreshState
                        )
                    } else {
                        val eTicketOnboardScreenModel: ETicketOnboardScreenModel =
                            getScreenModel()
                        ETicketOnboardScreen(eTicketOnboardScreenModel)
//                        Navigator(EvenDetailScreen())
                    }
                }
            })
    }
}

@Composable
fun ETicketScreen() {
    MaxSizeColumn {
        Text("ETicket Coming Soon!")
    }
}
