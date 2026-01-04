package com.mangala.wallet.features.home.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.navigator.tab.CurrentTab
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabNavigator
import com.mangala.features.browser.BrowserTab
import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.ui.LocalBottomNavigationVisibility
import com.mangala.wallet.ui.TextTab
import com.mangala.wallet.features.nft_base.presentation.NftTab
import com.mangala.wallet.features.swap_base.presentation.SwapTab
import com.mangala.wallet.features.wallet.presentation.WalletTab
import com.mangala.wallet.model.blockchain.NetworkType
import com.mangala.wallet.ui.utils.collectAsStateMultiplatform
import dev.icerock.moko.resources.compose.readTextAsState
import org.koin.core.component.KoinComponent

internal class HomeScreen : Screen, KoinComponent {

    override val key: ScreenKey
        get() = uniqueScreenKey

    @Composable
    override fun Content() {
        val state = rememberScaffoldState()
        val screenModel = rememberScreenModel { HomeScreenModel() }
        val selectedNetwork by screenModel.selectedNetwork.collectAsStateMultiplatform()
//        val isNoWalletImported by screenModel.isNoAccountFound.collectAsStateMultiplatform()

        CompositionLocalProvider(LocalBottomNavigationVisibility provides mutableStateOf(true)) {
            TabNavigator(WalletTab) {
                Scaffold(
                    scaffoldState = state,
                    content = {
                        Column(Modifier.padding(it)) {
                            CurrentTab()
                        }
                    },
                    bottomBar = {
                        if (LocalBottomNavigationVisibility.current.value) {
                            BottomNavigation(backgroundColor = MaterialTheme.colors.primary) {
                                TabNavigationItem(WalletTab)
//                                TabNavigationItem(DAppTab)
                                if (selectedNetwork?.blockchainType?.networkType == NetworkType.EVM) {
//                                    if (isNoWalletImported.not()) {
//                                        TabNavigationItem(SwapTab)
//                                        TabNavigationItem(NftTab)
//                                    }
                                    TabNavigationItem(BrowserTab)
                                }
                            }
                        }
                    }
                )
            }

            val initialDatabaseState by screenModel.checkInitialDatabase.collectAsStateMultiplatform()
            screenModel.checkInitialDatabase()

            val initialBlockchainData = MR.files.InitialBlockchain.readTextAsState().value
            val initialCoinsListData = MR.files.InitialCoinsList.readTextAsState().value
            val initialTokenListData = MR.files.InitialTokenList.readTextAsState().value

            LaunchedEffect(initialDatabaseState) {
                if (initialDatabaseState == false) {
                    initialBlockchainData?.let { screenModel.createBlockchain(it) }
                    initialCoinsListData?.let { screenModel.createCoinData(it) }
                    initialTokenListData?.let { screenModel.createTokenData(it) }
                }
            }
        }
    }

    @Composable
    internal fun RowScope.TabNavigationItem(tab: Tab) {
        val tabNavigator = LocalTabNavigator.current

        BottomNavigationItem(
            selected = tabNavigator.current.key == tab.key,
            onClick = { tabNavigator.current = tab },
            icon = { Icon(painter = tab.options.icon!!, contentDescription = tab.options.title) },
            label = { TextTab(text = tab.options.title) },
            selectedContentColor = MaterialTheme.colors.secondaryVariant,
            unselectedContentColor = MaterialTheme.colors.onPrimary,
        )
    }
}