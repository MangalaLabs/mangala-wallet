package com.mangala.wallet.features.menu.presentation.wallet

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.registry.rememberScreen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.mangala.wallet.common.mokoresources.font.getInterFontFamily
import com.mangala.wallet.features.menu.presentation.wallet.add_wallet.AddWalletScreen
import com.mangala.wallet.features.menu.presentation.wallet.details.WalletDetailsScreen
import com.mangala.wallet.model.wallet.domain.WalletModel
import com.mangala.wallet.common.mokoresources.Dimensions
import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.common.mokoresources.icons.MangalaWalletPack
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.Add
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.ArrowLeft
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.InfoCircle
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.SelectedWallet
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.WalletReal
import com.mangala.wallet.ui.LocalGlobalNavigator
import com.mangala.wallet.ui.SharedScreen
import com.mangala.wallet.ui.component.MangalaButtonSize
import com.mangala.wallet.ui.component.MangalaGradientButton
import com.mangala.wallet.ui.component.OnboardingGradientBackground
import com.mangala.wallet.ui.theme.MangalaTypography
import com.mangala.wallet.ui.theme.mangalaColors
import com.mangala.wallet.ui.utils.collectAsStateMultiplatform
import com.mangala.wallet.ui.utils.screenmodel.BaseScreen
import com.mangala.wallet.utils.analytics.MangalaAnalytics
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.desc.desc

internal class WalletScreen : BaseScreen<WalletScreenModel>() {

    override val screenName: String = MangalaAnalytics.Screens.WALLET
    override val screenClassName: String = WalletScreen::class.simpleName.orEmpty()

    override val isBottomBarVisible: Boolean = false

    @Composable
    override fun createScreenModel(): WalletScreenModel {
        return getScreenModel()
    }

    @Composable
    override fun ScreenContent(screenModel: WalletScreenModel) {
        val navigator = LocalNavigator.currentOrThrow
        val globalNavigator = LocalGlobalNavigator.current
        val uiModel = screenModel.uiModel.collectAsStateMultiplatform().value

        val homeScreen = rememberScreen(SharedScreen.HomeScreen())

        Wallet(
            uiModel = uiModel,
            wallets = uiModel.items,
            walletDetailsScreen = {
                val walletDetailsScreen = WalletDetailsScreen(it.id)
                navigator.push(walletDetailsScreen)
            },
            onBackClicked = { navigator.pop() },
            onclickSelectedWallet = {
                screenModel.onClickSelectWallet(it)
                globalNavigator.replaceAll(homeScreen)
            },
            onClickAddWallet = { navigator.push(AddWalletScreen()) }
        )
    }

    @Composable
    fun Wallet(
        uiModel: WalletScreenModelUiModel,
        wallets: List<WalletScreenModelItemUiModel>,
        onBackClicked: () -> Unit,
        walletDetailsScreen: (WalletModel) -> Unit,
        onclickSelectedWallet: (WalletScreenModelItemUiModel) -> Unit,
        onClickAddWallet: () -> Unit
    ) {
        val colors = MaterialTheme.mangalaColors

        OnboardingGradientBackground(circleBackgroundEnabled = true) {
            Column(modifier = Modifier.fillMaxSize()) {
                WalletHeader(onBackClicked = onBackClicked)

                when {
                    uiModel.isLoading -> {
                        LoadingState()
                    }

                    wallets.isEmpty() -> {
                        EmptyState(
                            modifier = Modifier.weight(1f),
                            onClickAddWallet = onClickAddWallet
                        )
                    }

                    else -> {
                        Text(
                            text = "TOTAL ${wallets.size} WALLETS",
                            style = MangalaTypography.Size12SemiBold(),
                            color = colors.textPrimary.copy(alpha = 0.72f),
                            letterSpacing = 1.4.sp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = Dimensions.Padding.default, vertical = 8.dp)
                        )

                        LazyColumn(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                            contentPadding = PaddingValues(
                                start = Dimensions.Padding.default,
                                end = Dimensions.Padding.default,
                                bottom = 20.dp
                            )
                        ) {
                            items(uiModel.items, key = { it.wallet.id }) { wallet ->
                                WalletItem(
                                    item = wallet,
                                    onClickViewWalletDetails = walletDetailsScreen,
                                    onClickSelectedWallet = onclickSelectedWallet
                                )
                            }
                        }

                        FooterAddButton(onClickAddWallet = onClickAddWallet)
                    }
                }
            }
        }
    }

    @Composable
    private fun WalletHeader(onBackClicked: () -> Unit) {
        val colors = MaterialTheme.mangalaColors

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(start = 16.dp, end = 16.dp, top = 10.dp, bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .clickable(onClick = onBackClicked),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = MangalaWalletPack.ArrowLeft,
                    contentDescription = "Back",
                    tint = colors.iconPrimary,
                    modifier = Modifier.size(22.dp)
                )
            }

            Text(
                text = MR.strings.all_wallets.desc().localized(),
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = getInterFontFamily(),
                color = colors.textPrimary,
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp)
            )

            Spacer(modifier = Modifier.width(40.dp))
        }
    }

    @Composable
    private fun LoadingState() {
        val colors = MaterialTheme.mangalaColors

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 64.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            CircularProgressIndicator(
                color = colors.textLink,
                modifier = Modifier.size(28.dp),
                strokeWidth = 2.5.dp
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = MR.strings.all_loading.desc().localized(),
                style = MangalaTypography.Size13Regular(),
                color = colors.textSecondary
            )
        }
    }

    @Composable
    private fun EmptyState(
        modifier: Modifier = Modifier,
        onClickAddWallet: () -> Unit
    ) {
        val colors = MaterialTheme.mangalaColors

        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(88.dp)
                        .clip(RoundedCornerShape(28.dp))
                        .background(Color.White.copy(alpha = 0.04f))
                        .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(28.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = MangalaWalletPack.WalletReal,
                    contentDescription = null,
                    tint = colors.iconPrimary,
                    modifier = Modifier.size(38.dp)
                )
            }

            Spacer(Modifier.height(16.dp))

            Text(
                text = MR.strings.message_wallet_no_wallets_found.desc().localized(),
                style = MangalaTypography.Size14Medium(),
                color = colors.textSecondary
            )

            Spacer(Modifier.height(20.dp))

            MangalaGradientButton(
                onClick = onClickAddWallet,
                size = MangalaButtonSize.XMedium,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = MR.strings.message_wallet_add_wallet.desc().localized(),
                        style = MangalaTypography.Size14SemiBold(),
                        color = Color.White
                    )
                }
            }
        }
    }

    @Composable
    private fun FooterAddButton(onClickAddWallet: () -> Unit) {
        val colors = MaterialTheme.mangalaColors

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .background(colors.bg.copy(alpha = 0.92f))
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            MangalaGradientButton(
                onClick = onClickAddWallet,
                size = MangalaButtonSize.XMedium,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = MR.strings.message_wallet_add_wallet.desc().localized(),
                        style = MangalaTypography.Size14SemiBold(),
                        color = Color.White
                    )
                }
            }
        }
    }

    @Composable
    fun WalletItem(
        item: WalletScreenModelItemUiModel,
        onClickViewWalletDetails: (WalletModel) -> Unit,
        onClickSelectedWallet: (WalletScreenModelItemUiModel) -> Unit
    ) {
        val colors = MaterialTheme.mangalaColors
        val isSelected = item.wallet.isSelected
        val shape = RoundedCornerShape(14.dp)

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clip(shape)
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        listOf(
                            Color.White.copy(alpha = 0.07f),
                            Color.White.copy(alpha = 0.03f)
                        )
                    )
                )
                .border(
                    width = 1.dp,
                    color = if (isSelected) colors.textLink.copy(alpha = 0.55f) else Color.White.copy(alpha = 0.12f),
                    shape = shape
                )
                .clickable(onClick = { onClickSelectedWallet(item) })
                .padding(horizontal = 14.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            if (isSelected) {
                                Brush.linearGradient(
                                    listOf(
                                        colors.textLink,
                                        colors.bgBadge
                                    )
                                )
                            } else {
                                Brush.linearGradient(
                                    listOf(
                                        Color.White.copy(alpha = 0.08f),
                                        Color.White.copy(alpha = 0.04f)
                                    )
                                )
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = MangalaWalletPack.WalletReal,
                        contentDescription = item.wallet.name,
                        tint = if (isSelected) Color.White else colors.iconPrimary,
                        modifier = Modifier.size(22.dp)
                    )

                    if (isSelected) {
                        Icon(
                            imageVector = MangalaWalletPack.SelectedWallet,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier
                                .size(10.dp)
                                .align(Alignment.TopEnd)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = item.wallet.name,
                        style = MangalaTypography.Size14SemiBold(),
                        color = colors.textPrimary,
                        lineHeight = 20.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Text(
                        text = if (isSelected) {
                            MR.strings.content_description_selected.desc().localized()
                        } else {
                            item.wallet.id.toDisplayWalletAddress()
                        },
                        style = MangalaTypography.Size12Regular(),
                        color = colors.textSecondary.copy(alpha = 0.9f),
                        lineHeight = 16.sp,
                        letterSpacing = 0.2.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        textAlign = TextAlign.Start
                    )
                }
            }

            IconButton(onClick = { onClickViewWalletDetails(item.wallet) }) {
                Icon(
                    imageVector = MangalaWalletPack.InfoCircle,
                    contentDescription = "Wallet detail",
                    tint = colors.iconSecondary,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }

    private fun String.toDisplayWalletAddress(): String {
        if (length <= 12) return this
        return "${take(4)}...${takeLast(6)}"
    }
}
