package com.mangala.wallet.features.menu.presentation.wallet

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.registry.rememberScreen
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.mangala.wallet.features.menu.presentation.wallet.add_wallet.AddWalletScreen
import com.mangala.wallet.features.menu.presentation.wallet.details.WalletDetailsScreen
import com.mangala.wallet.model.wallet.domain.WalletModel
import com.mangala.wallet.common.mokoresources.Colors
import com.mangala.wallet.common.mokoresources.Dimensions
import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.common.mokoresources.Spacing
import com.mangala.wallet.common.mokoresources.icons.MangalaWalletPack
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.Add
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.InfoCircle
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.SelectedWallet
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.WalletReal
import com.mangala.wallet.ui.ButtonNormal
import com.mangala.wallet.ui.LocalGlobalNavigator
import com.mangala.wallet.ui.MangalaCommonDialog
import com.mangala.wallet.ui.MangalaCommonDialogDelete
import com.mangala.wallet.ui.SharedScreen
import com.mangala.wallet.ui.TextDescription2
import com.mangala.wallet.ui.TextNormal
import com.mangala.wallet.ui.component.AddAccountWalletImage
import com.mangala.wallet.ui.component.MangalaWalletSwipeToReveal
import com.mangala.wallet.ui.component.MangalaWalletTopBar
import com.mangala.wallet.ui.modifier.roundedCornerItemShape
import com.mangala.wallet.ui.utils.collectAsStateMultiplatform
import com.mangala.wallet.ui.utils.screenmodel.BaseScreen
import com.mangala.wallet.utils.analytics.MangalaAnalytics
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.desc.desc
import org.koin.core.component.KoinComponent

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
        val isOpenConfirmDeleteDialog = remember { mutableStateOf(false) }

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
            onClickDeletedWallet = { screenModel.onClickDeletedWallet(it) },
            onClickAddWallet = {navigator.push(AddWalletScreen())}
        )
    }

    @Composable
    fun Wallet(
        uiModel: WalletScreenModelUiModel,
        wallets: List<WalletScreenModelItemUiModel>,
        onBackClicked: () -> Unit,
        walletDetailsScreen: (WalletModel) -> Unit,
        onclickSelectedWallet: (WalletScreenModelItemUiModel) -> Unit,
        onClickDeletedWallet: (WalletScreenModelItemUiModel) -> Unit,
        onClickAddWallet:() -> Unit
    ) {
        Scaffold(
            topBar = {
                MangalaWalletTopBar(
                    modifier = Modifier.background(Colors.cloudGray),
                    text = MR.strings.all_wallets.desc().localized(),
                    onBackClicked = onBackClicked,
                    trailingButton = {
                        IconButton(onClick = onClickAddWallet) {
                            Icon(
                                imageVector = MangalaWalletPack.Add,
                                contentDescription = "Add Wallet",
                                tint = Colors.darkGray
                            )
                        }
                    }
                )
            },
            modifier = Modifier.background(Colors.cloudGray).windowInsetsPadding(WindowInsets.statusBars)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Colors.cloudGray)
                    .padding(horizontal = Dimensions.Padding.default)
            ) {
                Spacer(Modifier.height(Spacing.SMALL))
                if (wallets.isNotEmpty()) {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(1.dp),
                        contentPadding = PaddingValues(bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding())
                    ) {
                        itemsIndexed(
                            uiModel.items
                        ) { index, wallet ->
                            WalletItem(
                                item = wallet,
                                shape = roundedCornerItemShape(wallets, index),
                                onClickViewWalletDetails = walletDetailsScreen,
                                onClickSelectedWallet = onclickSelectedWallet,
                                onClickDeletedWallet = onClickDeletedWallet
                            )
                        }
                    }
                } else {
                    AddAccountWalletImage(
                        onClickButtonAdd = onClickAddWallet,
                        textButton = MR.strings.message_wallet_add_wallet.desc().localized(),
                        textMessage = MR.strings.message_wallet_no_wallets_found.desc().localized()
                    )
                }
            }
        }
    }

    @Composable
    fun WalletItem(
        item: WalletScreenModelItemUiModel,
        shape: Shape,
        onClickViewWalletDetails: (WalletModel) -> Unit,
        onClickSelectedWallet: (WalletScreenModelItemUiModel) -> Unit,
        onClickDeletedWallet: (WalletScreenModelItemUiModel) -> Unit
    ) {
        val currentItem by rememberUpdatedState(item)
        Column(
            modifier = Modifier.fillMaxSize().clip(shape)
        ) {
            val isOpenConfirmDeleteDialog = remember { mutableStateOf(false) }
            MangalaWalletSwipeToReveal(
                shape = shape,
                revealedBackgroundColor = Colors.coral,
                text = MR.strings.all_delete.desc().localized(),
                onClickRevealed = {
                    isOpenConfirmDeleteDialog.value = true

//                    onClickDeletedWallet(currentItem)

                }
            ){
                if (isOpenConfirmDeleteDialog.value){
                    MangalaCommonDialogDelete(
                        onNegativeClick = { isOpenConfirmDeleteDialog.value = false },
                        onPositiveClick = {
                            isOpenConfirmDeleteDialog.value = false
                            onClickDeletedWallet(currentItem)
                        }
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(shape)
                        .fillMaxWidth()
                        .background(Color.White)
                        .clickable(onClick = { onClickSelectedWallet(item) })
                        .padding(Dimensions.Padding.default),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Box(Modifier.size(36.dp).background(Colors.cloudGray, CircleShape)) {
                        if (item.wallet.isSelected) {
                            Icon(
                                imageVector = MangalaWalletPack.SelectedWallet,
                                contentDescription = item.wallet.name,
                                modifier = Modifier.size(10.dp)
                                    .background(Colors.green, CircleShape)
                                    .padding(2.dp).align(Alignment.TopEnd), tint = Color.White
                            )
                            Icon(
                                imageVector = MangalaWalletPack.WalletReal,
                                contentDescription = item.wallet.name,
                                modifier = Modifier.size(Dimensions.IconButtonSize).align(Alignment.Center)
                            )
                        } else {
                            Icon(
                                imageVector = MangalaWalletPack.WalletReal,
                                contentDescription = item.wallet.name,
                                modifier = Modifier.size(Dimensions.IconButtonSize).align(Alignment.Center)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(Spacing.TINY))
                    TextDescription2(
                        text = item.wallet.name,
                        color = MaterialTheme.colors.onPrimary,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(Spacing.SMALL))
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(start = 20.dp)
                    ) {
                        IconButton(onClick = { onClickViewWalletDetails(item.wallet) }) {
                            Icon(
                                imageVector = MangalaWalletPack.InfoCircle,
                                contentDescription = "Navigate",
                                tint = MaterialTheme.colors.onBackground
                            )
                        }

                    }
                }
            }
        }
    }
}