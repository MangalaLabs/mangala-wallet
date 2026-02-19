package com.mangala.wallet.features.wallet.presentation

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.tab.TabOptions
import com.mangala.features.wallet.presentationv2.WalletScreenFactoryV2
import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.common.mokoresources.icons.MangalaWalletPack
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.Wallet
import com.mangala.wallet.ui.tab.PopToRootTab
import com.mangala.wallet.ui.utils.collectAsStateMultiplatform
import com.mangala.wallet.ui.utils.navigation.BackHandler
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.desc.desc
import kotlin.experimental.ExperimentalObjCRefinement
import kotlin.native.HiddenFromObjC

@OptIn(ExperimentalObjCRefinement::class)
@HiddenFromObjC
object WalletTab: PopToRootTab() {

    override val key: ScreenKey
        get() = "wallet_tab_key"

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
        val screenModel = rememberScreenModel { WalletTabScreenModel() }
        val selectedNetwork by screenModel.selectedNetwork.collectAsStateMultiplatform()

        val currentNetworkType = selectedNetwork?.blockchainType?.networkType ?: return

        val screenKey = remember(currentNetworkType) {
            "wallet_${currentNetworkType.name}"
        }

        key(screenKey) {
            Navigator(
                WalletScreenFactoryV2.createWalletScreen(currentNetworkType),
//                WalletMainScreen(),
                onBackPressed = {
                    BackHandler.handleBackPressed(it)
                }
            ) { navigator ->
                NavigatorWithPopToRoot(navigator)
            }
        }
    }

}
