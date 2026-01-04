package com.mangala.wallet.features.swap_base.presentation.selecttoken

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import app.cash.paging.LoadStateLoading
import app.cash.paging.LoadStateNotLoading
import app.cash.paging.compose.LazyPagingItems
import app.cash.paging.compose.collectAsLazyPagingItems
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.mangala.wallet.features.swap_base.presentation.SwapTokenUiModel
import com.mangala.wallet.common.mokoresources.Colors
import com.mangala.wallet.common.mokoresources.CornerRadius
import com.mangala.wallet.common.mokoresources.Dimensions
import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.common.mokoresources.Spacing
import com.mangala.wallet.common.mokoresources.icons.MangalaWalletPack
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.Check
import com.mangala.wallet.ui.TextDescription2
import com.mangala.wallet.ui.component.MangalaCircularProgressIndicator
import com.mangala.wallet.ui.component.MangalaCircularProgressIndicatorFullScreen
import com.mangala.wallet.ui.component.MangalaWalletSearchBar
import com.mangala.wallet.ui.component.MangalaWalletTopBar
import com.mangala.wallet.ui.component.MaxSizeColumn
import com.mangala.wallet.ui.imageloader.RemoteImage
import com.mangala.wallet.ui.modifier.roundedCornersItemShape
import com.mangala.wallet.ui.utils.collectAsStateMultiplatform
import com.mangala.wallet.ui.utils.screenmodel.BaseScreen
import com.mangala.wallet.utils.analytics.MangalaAnalytics
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.desc.desc
import org.koin.core.parameter.parametersOf
import kotlin.jvm.Transient

internal class SelectTokenScreen(
    private val accountAddress: String,
    private val accountId: String,
    private val blockChainUid: String,
    private val isFromToken: Boolean,
    private val selectedTokenAddress: String,
    @Transient private val onSelectToken: (SwapTokenUiModel.TokenUiModel) -> Unit,
) : BaseScreen<SelectTokenScreenModel>() {

    override val screenName: String = MangalaAnalytics.Screens.EVM_SWAP_SELECT_TOKEN
    override val screenClassName: String = SelectTokenScreen::class.simpleName.orEmpty()

    override val isBottomBarVisible = false

    @Composable
    override fun createScreenModel(): SelectTokenScreenModel = getScreenModel(
        parameters = {
            parametersOf(
                accountAddress,
                accountId,
                blockChainUid,
            )
        }
    )

    @Composable
    override fun ScreenContent(screenModel: SelectTokenScreenModel) {
        val navigator = LocalNavigator.currentOrThrow

        SelectTokenScreen(
            onBackClicked = {
                navigator.pop()
            },
            screenModel = screenModel,
            onSelectToken = {
                onSelectToken(it)
                navigator.pop()
            }
        )

    }

    @Composable
    private fun SelectTokenScreen(
        onBackClicked: () -> Unit,
        screenModel: SelectTokenScreenModel,
        onSelectToken: (SwapTokenUiModel.TokenUiModel) -> Unit
    ) {
        val isInitSupportedToken =
            screenModel.isInitListSupportedTokenFlow.collectAsStateMultiplatform()
        if (!isInitSupportedToken.value) {
            MangalaCircularProgressIndicatorFullScreen()
            return
        }


        val searchText = screenModel.searchText.collectAsStateMultiplatform()
        val supportedTokens: LazyPagingItems<SwapTokenUiModel.TokenUiModel> =
            screenModel.listSupportedToken.collectAsLazyPagingItems()

        MaxSizeColumn(
            modifier = Modifier
                .background(color = Colors.cloudGray)
                .windowInsetsPadding(WindowInsets.safeDrawing)
        ) {
            MangalaWalletTopBar(
                text = if (isFromToken) MR.strings.title_swap_from_token.desc().localized()
                else MR.strings.title_swap_to_token.desc().localized(),
                onBackClicked = onBackClicked
            )
            Spacer(modifier = Modifier.height(Spacing.SMALL))
            MaxSizeColumn(Modifier.padding(horizontal = Dimensions.Padding.default)) {
                MangalaWalletSearchBar(
                    searchText = searchText,
                    placeholder = MR.strings.message_language_search_language.desc().localized(),
                    onValueChange = screenModel::onSearchTextChanged
                )

                Spacer(modifier = Modifier.height(Spacing.SMALL))

                if (supportedTokens.loadState.refresh is LoadStateNotLoading && supportedTokens.itemCount == 0) {
                    Box(Modifier.fillMaxSize()) {
                        TextDescription2(
                            text = "Not found",
                            fontStyle = FontStyle.Italic,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                } else {
                    LazyColumn(modifier = Modifier.clip(RoundedCornerShape(CornerRadius.Small))) {
                        items(supportedTokens.itemCount) { index ->
                            supportedTokens[index]?.let {
                                if (index != 0) Spacer(modifier = Modifier.height(1.dp))
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.roundedCornersItemShape(
                                        supportedTokens.itemSnapshotList, index
                                    ).background(color = Color.White).fillMaxWidth().clickable {
                                        onSelectToken(it)
                                    }.padding(Dimensions.Padding.default)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        RemoteImage(
                                            modifier = Modifier.size(Dimensions.IconButtonSize),
                                            url = it.logoUrl
                                        )

                                        Spacer(modifier = Modifier.width(Spacing.TINY))

                                        TextDescription2(
                                            text = it.tokenCode,
                                            color = Colors.main1Text
                                        )
                                    }

                                    if (it.address == selectedTokenAddress) {
                                        Icon(
                                            imageVector = MangalaWalletPack.Check,
                                            tint = Colors.second,
                                            contentDescription = "",
                                            modifier = Modifier.size(
                                                Dimensions.IconChosenLanguageSizeLanguageScreen
                                            )
                                        )
                                    }
                                }
                            }
                        }

                        if (supportedTokens.loadState.append == LoadStateLoading) {
                            item {
                                MangalaCircularProgressIndicator()
                            }
                        }
                    }
                }
            }
        }
    }
}
