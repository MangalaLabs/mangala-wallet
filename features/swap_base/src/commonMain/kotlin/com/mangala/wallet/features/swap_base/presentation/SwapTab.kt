package com.mangala.wallet.features.swap_base.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.tab.TabOptions
import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.common.mokoresources.icons.MangalaWalletPack
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.Swap
import com.mangala.wallet.ui.tab.PopToRootTab
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.desc.desc
import kotlin.experimental.ExperimentalObjCRefinement
import kotlin.native.HiddenFromObjC

@OptIn(ExperimentalObjCRefinement::class)
@HiddenFromObjC
object SwapTab: PopToRootTab() {

    override val route: String
        get() = "swap_tab"

    override val options: TabOptions
        @Composable
        get() {
            val icon = rememberVectorPainter(MangalaWalletPack.Swap)
            val title = MR.strings.label_swap.desc().localized()
            return remember {
                TabOptions(
                    index = 2u,
                    title = title,
                    icon = icon
                )
            }
        }

    @Composable
    override fun Content() {
        Navigator(SwapTokenScreen()) { navigator ->
            NavigatorWithPopToRoot(navigator)
        }
    }
}