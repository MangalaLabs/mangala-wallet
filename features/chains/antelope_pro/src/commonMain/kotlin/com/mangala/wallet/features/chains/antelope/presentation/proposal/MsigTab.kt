package com.mangala.wallet.features.chains.antelope.presentation.proposal

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.tab.TabOptions
import com.benasher44.uuid.uuid4
import com.mangala.wallet.common.mokoresources.icons.MangalaWalletPack
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.Bitcoin
import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.ui.tab.DestinationTab
import com.mangala.wallet.ui.utils.navigation.BackHandler
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.desc.desc
import kotlin.experimental.ExperimentalObjCRefinement
import kotlin.native.HiddenFromObjC

@OptIn(ExperimentalObjCRefinement::class)
@HiddenFromObjC
object MsigTab : DestinationTab {

    override val key: ScreenKey
        get() = uuid4().toString()

    override val route: String
        get() = "msig_tab"

    override val options: TabOptions
        @Composable
        get() {
            val icon = rememberVectorPainter(MangalaWalletPack.Bitcoin)
            val title = MR.strings.label_msig.desc().localized()
            return remember {
                TabOptions(
                    index = 5u,
                    title = title,
                    icon = icon
                )
            }
        }

    @OptIn(ExperimentalAnimationApi::class)
    @Composable
    override fun Content() {

        Navigator(MsigScreen(), onBackPressed = {
            BackHandler.handleBackPressed(it)
        })
    }

}

