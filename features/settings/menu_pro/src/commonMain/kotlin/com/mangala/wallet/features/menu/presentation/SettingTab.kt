package com.mangala.wallet.features.menu.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.tab.TabOptions
import com.benasher44.uuid.uuid4
import com.mangala.wallet.features.menu.presentation.menu.MenuScreen
import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.ui.tab.PopToRootTab
import com.mangala.wallet.ui.utils.navigation.BackHandler
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.compose.painterResource
import dev.icerock.moko.resources.desc.desc
import kotlin.experimental.ExperimentalObjCRefinement
import kotlin.native.HiddenFromObjC

@OptIn(ExperimentalObjCRefinement::class)
@HiddenFromObjC
object SettingTab : PopToRootTab() {

    override val key: ScreenKey
        get() = uuid4().toString()

    override val route: String
        get() = "setting_tab"

    override val options: TabOptions
        @Composable
        get() {
            val icon = painterResource(MR.images.category_2)
            val title = MR.strings.label_setting.desc().localized()
            return remember {
                TabOptions(
                    index = 4u,
                    title = title,
                    icon = icon
                )
            }
        }

    @Composable
    override fun Content() {
        Navigator(MenuScreen(), onBackPressed = {
            BackHandler.handleBackPressed(it)
        }) { navigator ->
            NavigatorWithPopToRoot(navigator)
        }
    }

}

