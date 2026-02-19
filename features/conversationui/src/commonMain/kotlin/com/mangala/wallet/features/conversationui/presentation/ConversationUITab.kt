package com.mangala.wallet.features.conversationui.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.tab.TabOptions
import com.mangala.wallet.features.conversationui.presentation.test.ConversationUiEntryPointTestScreen
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
object ConversationUITab : PopToRootTab() {

    override val key: ScreenKey
        get() = "conversation_ui_tab_key"

    override val route: String
        get() = "conversation_ui_tab"

    override val options: TabOptions
        @Composable
        get() {
            val icon = painterResource(MR.images.ai)
            val title = MR.strings.label_conversation_ui.desc().localized()
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
        Navigator(ConversationUiEntryPointTestScreen(), onBackPressed = {
            BackHandler.handleBackPressed(it)
        }) { navigator ->
            NavigatorWithPopToRoot(navigator)
        }
    }

}
