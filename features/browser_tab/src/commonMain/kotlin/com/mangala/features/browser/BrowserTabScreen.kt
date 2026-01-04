package com.mangala.features.browser

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.ScrollableTabRow
import androidx.compose.material.Tab
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.registry.rememberScreen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.mangala.wallet.model.account.domain.AccountBlockchainModel
import com.mangala.wallet.model.category_dapp.domain.DAppModel
import com.mangala.wallet.model.category_dapp.remote.CategoryDApp
import com.mangala.wallet.common.mokoresources.Colors
import com.mangala.wallet.common.mokoresources.CornerRadius
import com.mangala.wallet.common.mokoresources.Dimensions
import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.common.mokoresources.Spacing
import com.mangala.wallet.ui.ButtonNormal
import com.mangala.wallet.ui.MangalaCommonDialog
import com.mangala.wallet.ui.TextDescription2
import com.mangala.wallet.ui.TextSubTitle
import com.mangala.wallet.ui.TextTiny
import com.mangala.wallet.ui.TextTopBar
import com.mangala.wallet.ui.imageloader.RemoteImage
import com.mangala.wallet.ui.utils.collectAsStateMultiplatform
import com.mangala.wallet.ui.utils.screenmodel.BaseScreen
import com.mangala.wallet.utils.analytics.MangalaAnalytics
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.desc.desc
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class BrowserTabScreen: BaseScreen<BrowserTabScreenModel>(), KoinComponent {

    override val screenName: String = MangalaAnalytics.Screens.BROWSER_TAB
    override val screenClassName: String = BrowserTabScreen::class.simpleName.orEmpty()

    private val openBrowser: OpenBrowser by inject()

    @Composable
    override fun createScreenModel(): BrowserTabScreenModel = getScreenModel()

    @Composable
    override fun ScreenContent(screenModel: BrowserTabScreenModel) {
        val uiModel = screenModel.uiModel.collectAsStateMultiplatform().value
        val account = uiModel.accounts.getOrNull(0)
        val categoriesDApps = screenModel.categories.collectAsStateMultiplatform().value

        openBrowser.putData(
            uiModel.chainId,
            account?.bip44Address ?: "",
            uiModel.rpcUrl,
            account?.account?.id ?: ""
        )
        BrowserTab(uiModel, account, categoriesDApps)
    }

    @Composable
    fun BrowserTab(
        uiModel: BrowserAccountsUiModel,
        account: AccountBlockchainModel?,
        categories: List<CategoryDApp>
    ) {

        Scaffold(
            modifier = Modifier.background(Colors.cloudGray).windowInsetsPadding(WindowInsets.safeDrawing),
            topBar = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(Spacing.SMALL),
                modifier = Modifier.background(Colors.cloudGray).padding(
                    horizontal = Dimensions.Padding.small, vertical = 11.dp
                ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(Spacing.TINY),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    TextSubTitle(
                        text = MR.strings.label_browser.desc().localized(),
                    )
                }
            }
        }) {
            Column(
                modifier = Modifier.background(Colors.cloudGray).padding(horizontal = Dimensions.Padding.small)
            ) {
                Spacer(Modifier.width(Spacing.SMALL))
                SearchBar {
                    openBrowser.openBrowser(
                        uiModel.chainId,
                        account?.bip44Address ?: "",
                        uiModel.rpcUrl,
                        account?.account?.id ?: ""
                    )
                }

                Spacer(modifier = Modifier.height(Spacing.BASE))

                CategoryList(categories = categories, uiModel, account)
            }
        }
    }

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    fun CategoryList(
        categories: List<CategoryDApp>,
        uiModel: BrowserAccountsUiModel,
        account: AccountBlockchainModel?
    ) {
        val pagerState = rememberPagerState {
            categories.size
        }
        val tabIndex = pagerState.currentPage
        if (categories.isNotEmpty()) {
            Column {
                ScrollableTabRow(
                    backgroundColor = Colors.cloudGray,
                    selectedTabIndex = tabIndex,
                    contentColor = Color.Black,
                    edgePadding = 0.dp,
                ) {
                    categories.forEachIndexed { index, tab ->
                        Tab(
                            selected = tabIndex == index,
                            onClick = {
                                CoroutineScope(Dispatchers.Main).launch {
                                    pagerState.scrollToPage(index)
                                }
                            },
                            text = { TextTiny(tab.category.name) },
                            modifier = Modifier.background(Colors.cloudGray)
                        )
                    }
                }

                HorizontalPager(
                    state = pagerState,
                ) {
                    Column {
                        Spacer(modifier = Modifier.height(Spacing.SMALL))
                        TextTopBar(MR.strings.title_recommend_dapp.desc().localized())
                        Spacer(modifier = Modifier.height(Spacing.XSMALL))

                        ListItem(listItem = categories[tabIndex].dapps.map { it.mapToDomainModel() as DAppModel }
                            .toList(),
                            modifierColumnIcons = Modifier
                                .fillMaxWidth()
                                .background(color = Color.White, shape = RoundedCornerShape(CornerRadius.Medium)),
                            distanceItem = Spacing.XSMALL,
                            uiModel,
                            account)
                    }
                }

                LaunchedEffect(tabIndex) {
                    pagerState.animateScrollToPage(tabIndex)
                }
            }
        }
    }

    @Composable
    fun ListItem(
        listItem: List<DAppModel>,
        modifierColumnIcons: Modifier,
        distanceItem: Dp,
        uiModel: BrowserAccountsUiModel,
        account: AccountBlockchainModel?
    ) {
        var showDialog by remember { mutableStateOf(false) }
        var selectedDAppModel: DAppModel? by remember { mutableStateOf(null) }
        if (showDialog) {
            MangalaCommonDialog(
                title = "Confirm redirect to browser",
                message = "Do you want to redirect to ${selectedDAppModel?.title}?",
                positiveButtonText = MR.strings.all_yes.desc().localized(),
                negativeButtonText = MR.strings.all_cancel.desc().localized(),
                onNegativeClick = { showDialog = false },
                onPositiveClick = {
                    showDialog = false
                    openBrowser.openBrowser(
                        uiModel.chainId,
                        account?.bip44Address ?: "",
                        selectedDAppModel?.redirectLink ?: "",
                        account?.account?.id ?: ""
                    )
                }
            )
        }

        Box(
            modifier = Modifier
                .fillMaxHeight()
                .background(Colors.cloudGray)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Colors.cloudGray)
            ) {
                items(listItem) { dAppModel ->
                    Row(
                        modifier = Modifier
                            .padding(vertical = distanceItem)
                            .fillMaxWidth()
                            .clickable {
                                selectedDAppModel = dAppModel
                                showDialog = true
                            }
                    ) {
                        Column(
                            modifier = modifierColumnIcons,
                        ) {
                            RemoteImage(
                                Modifier
                                    .fillMaxWidth()
                                    .height(140.dp),
                                dAppModel.iconUrl,
                            )
                            Column(
                                modifier = Modifier.padding(
                                    start = Dimensions.Padding.default,
                                    end = Dimensions.Padding.default,
                                    top = Dimensions.Padding.half,
                                    bottom = Dimensions.Padding.default
                                ),
                            ) {
                                TextTopBar(
                                    text = dAppModel.title
                                )
                                TextDescription2(
                                    text = dAppModel.description
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}