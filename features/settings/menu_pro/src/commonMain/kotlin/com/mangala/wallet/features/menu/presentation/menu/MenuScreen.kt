package com.mangala.wallet.features.menu.presentation.menu

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.registry.ScreenRegistry
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.mangala.wallet.common.mokoresources.icons.MangalaWalletPack
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.Contacts
import com.mangala.wallet.menu_base.presentation.menu.BaseMenuScreen
import com.mangala.wallet.menu_base.presentation.menu.BaseMenuScreenContent
import com.mangala.wallet.menu_base.presentation.menu.BaseMenuScreenModel
import com.mangala.wallet.menu_base.presentation.menu.MenuRow
import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.ui.SharedScreen
import com.mangala.wallet.utils.analytics.MangalaAnalytics
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.desc.desc
import kotlin.experimental.ExperimentalObjCRefinement
import kotlin.native.HiddenFromObjC

@OptIn(ExperimentalObjCRefinement::class)
@HiddenFromObjC
class MenuScreen : BaseMenuScreen() {

    override val screenName: String = MangalaAnalytics.Screens.MENU
    override val screenClassName: String = MenuScreen::class.simpleName.orEmpty()

    @Composable
    override fun ScreenContent(screenModel: BaseMenuScreenModel) {
        val navigator = LocalNavigator.currentOrThrow

        BaseMenuScreenContent(
            screenModel = screenModel,
            onBackPressed = { navigator.pop() },
            secondGroupAdditionalContent = {
//                    MenuRow(
//                        title = MR.strings.all_contacts.desc().localized(),
//                        onClickNavigate = {
//                            val contactsScreen = ScreenRegistry.get(SharedScreen.ContactListScreen)
//                            navigator.push(contactsScreen) },
//                        iconRepresent = MangalaWalletPack.Contacts
//                    )
                }
            )
    }
}