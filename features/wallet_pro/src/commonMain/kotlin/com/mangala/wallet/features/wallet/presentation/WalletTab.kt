package com.mangala.wallet.features.wallet.presentation

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.tab.TabOptions
import com.benasher44.uuid.uuid4
import com.mangala.features.wallet.presentationv2.WalletScreenFactoryV2

import com.mangala.features.wallet.presentationv2.antelope.AntelopeWalletScreenV2

import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.common.mokoresources.icons.MangalaWalletPack
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.Wallet
import com.mangala.wallet.domain.datastore.usecases.GetSelectedNetworkUseCase
import com.mangala.wallet.features.wallet.presentation.WalletTab.key
import com.mangala.wallet.model.blockchain.NetworkType

import com.mangala.wallet.ui.tab.PopToRootTab
import com.mangala.wallet.ui.utils.navigation.BackHandler
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.desc.desc
import org.koin.compose.koinInject
import kotlin.experimental.ExperimentalObjCRefinement
import kotlin.native.HiddenFromObjC

@OptIn(ExperimentalObjCRefinement::class)
@HiddenFromObjC
object WalletTab: PopToRootTab() {

    override val key: ScreenKey
        get() = uuid4().toString()

    override val route: String
        get() = "wallet_tab"

    override val options: TabOptions
        @Composable
        get() {
            val icon = rememberVectorPainter(MangalaWalletPack.Wallet)
            val title = MR.strings.label_wallet.desc().localized()
            return remember {
                TabOptions(
                    index = 0u,
                    title = title,
                    icon = icon
                )
            }
        }

    @OptIn(ExperimentalAnimationApi::class)
    @Composable
    override fun Content() {

        val getSelectedNetworkUseCase = koinInject<GetSelectedNetworkUseCase>()
        val selectedNetwork by getSelectedNetworkUseCase.invokeFlow()
            .collectAsState(initial = null)

        val currentNetworkType = selectedNetwork?.blockchainType?.networkType
            ?: NetworkType.ANTELOPE

        val screenKey = remember(currentNetworkType) {
            "wallet_${currentNetworkType.name}"
        }

        key(screenKey) {
            Navigator(
                WalletScreenFactoryV2.createWalletScreen(currentNetworkType),
                onBackPressed = {
                    BackHandler.handleBackPressed(it)
                }
            ) { navigator ->
                NavigatorWithPopToRoot(navigator)
            }
        }
    }

}

