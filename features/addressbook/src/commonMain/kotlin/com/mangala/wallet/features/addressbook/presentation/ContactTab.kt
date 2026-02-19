package com.mangala.wallet.features.addressbook.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.tab.TabOptions
import com.mangala.wallet.features.addressbook.presentation.contact.list.ContactListScreen
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
object ContactTab : PopToRootTab() {

    override val key: ScreenKey
        get() = "contact_tab_key"

    override val route: String
        get() = "address_book_tab"

    override val options: TabOptions
        @Composable
        get() {
            val icon = painterResource(MR.images.profile)
            val title = MR.strings.all_contacts.desc().localized()
            return remember {
                TabOptions(
                    index = 1u,
                    title = title,
                    icon = icon
                )
            }
        }

    @Composable
    override fun Content() {
        Navigator(ContactListScreen(), onBackPressed = {
            BackHandler.handleBackPressed(it)
        }) { navigator ->
            NavigatorWithPopToRoot(navigator)
        }
    }

}
