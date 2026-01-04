package com.mangala.wallet.features.nft_base.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.tab.TabOptions
import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.common.mokoresources.icons.MangalaWalletPack
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.Nft
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.desc.desc
import com.mangala.wallet.ui.tab.PopToRootTab
import com.mangala.wallet.ui.tab.TabContent
import kotlin.experimental.ExperimentalObjCRefinement
import kotlin.native.HiddenFromObjC

@OptIn(ExperimentalObjCRefinement::class)
@HiddenFromObjC
object NftTab: PopToRootTab() {

    override val route: String
        get() = "nft_tab"

    override val options: TabOptions
        @Composable
        get() {
            val icon = rememberVectorPainter(MangalaWalletPack.Nft)
            val title = MR.strings.all_nft.desc().localized()
            return remember {
                TabOptions(
                    index = 3u,
                    title = title,
                    icon = icon
                )
            }
        }

    @Composable
    override fun Content() {
        TabContent()
        Navigator(NftMainScreen()) { navigator ->
            NavigatorWithPopToRoot(navigator)
        }
    }
}