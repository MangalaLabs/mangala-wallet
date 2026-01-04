package com.mangala.wallet.features.wallet.presentation

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.tab.TabOptions
import com.mangala.wallet.features.wallet.presentation.main.WalletMainScreen
import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.common.mokoresources.icons.MangalaWalletPack
import com.mangala.wallet.common.mokoresources.icons.MangalaWalletPack.Wallet
import com.mangala.wallet.ui.tab.DestinationTab
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.desc.desc
import org.koin.core.component.KoinComponent

object WalletTab: DestinationTab, KoinComponent {

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

//            Navigator(SendTokenScreen())
            Navigator(WalletMainScreen())

//            val bottomSheetNavigator = LocalBottomSheetNavigator.current
//            val navigator = LocalNavigator.currentOrThrow
//            WalletTabScreen {
//                if (it) {
////                    navigator.push(PINScreen())
////                    bottomSheetNavigator.show(PINScreen())
//                }
//            }
//        }
    }

}

