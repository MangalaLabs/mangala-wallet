package com.mangala.wallet.features.home.home_cold

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
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
import cafe.adriel.voyager.core.registry.ScreenRegistry
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.navigator.tab.CurrentTab
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabNavigator
import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.ui.LocalBottomNavigationVisibility
import com.mangala.wallet.ui.TextTab
import com.mangala.wallet.ui.utils.collectAsStateMultiplatform
import dev.icerock.moko.resources.compose.readTextAsState
import org.koin.core.component.KoinComponent
import com.mangala.wallet.features.wallet.presentation.ScanTab
import com.mangala.wallet.features.wallet.presentation.WalletTab
import com.mangala.wallet.ui.LocalGlobalNavigator

internal class HomeScreen : Screen, KoinComponent {

    override val key: ScreenKey
        get() = uniqueScreenKey

    @Composable
    override fun Content() {
        val state = rememberScaffoldState()
        val globalNavigator = LocalGlobalNavigator.current
        val navigator = LocalNavigator.currentOrThrow

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
                                TabNavigationItem(
                                    WalletTab
                                )
                                TabNavigationItemCold(
                                    ScanTab,
                                    onClick = {
                                        ScanTab.scanMethod(navigator, globalNavigator)
                                    }
                                )
                            }
                        }
                    }
                )
            }

            val screenModel = rememberScreenModel { HomeScreenModel() }
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
            onClick = {
                tabNavigator.current = tab
            },
            icon = { Icon(painter = tab.options.icon!!, contentDescription = tab.options.title) },
            label = { TextTab(text = tab.options.title) },
            selectedContentColor = MaterialTheme.colors.secondaryVariant,
            unselectedContentColor = MaterialTheme.colors.onPrimary,
        )
    }

    @Composable
    internal fun RowScope.TabNavigationItemCold(tab: Tab, onClick: () -> Unit) {
        val tabNavigator = LocalTabNavigator.current

        BottomNavigationItem(
            selected = tabNavigator.current.key == tab.key,
            onClick = {
                onClick()
            },
            icon = { Icon(painter = tab.options.icon!!, contentDescription = tab.options.title) },
            label = { TextTab(text = tab.options.title) },
            selectedContentColor = MaterialTheme.colors.secondaryVariant,
            unselectedContentColor = MaterialTheme.colors.onPrimary,
        )
    }
}