package com.mangala.wallet.features.home.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import cafe.adriel.voyager.core.registry.ScreenRegistry
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.navigator.tab.CurrentTab
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabNavigator
import com.mangala.features.browser.BrowserTab
import com.mangala.wallet.features.addressbook.presentation.ContactTab
import com.mangala.wallet.features.conversationui.presentation.ConversationUITab
import com.mangala.wallet.features.menu.presentation.SettingTab
import com.mangala.wallet.features.nft_base.presentation.NftTab
import com.mangala.wallet.features.swap_base.presentation.SwapTab
import com.mangala.wallet.features.wallet.presentation.WalletTab
import com.mangala.wallet.model.blockchain.NetworkType
import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.ui.LocalBottomNavigationVisibility
import com.mangala.wallet.ui.SharedScreen
import com.mangala.wallet.ui.TextTab
import com.mangala.wallet.ui.theme.MangalaTypography
import com.mangala.wallet.ui.theme.mangalaColors
import com.mangala.wallet.ui.utils.collectAsStateMultiplatform
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.desc.desc
import org.koin.core.component.KoinComponent
import kotlin.jvm.Transient

internal class HomeScreen(initialTab: SharedScreen.HomeScreen.InitialTab) : Screen {

    @Transient
    private val initialTab = when(initialTab) {
        SharedScreen.HomeScreen.InitialTab.WALLET -> WalletTab
        SharedScreen.HomeScreen.InitialTab.CONVERSATION_UI -> ConversationUITab
    }

    override val key: ScreenKey
        get() = uniqueScreenKey

    @Composable
    override fun Content() {
        val state = rememberScaffoldState()
        val screenModel = getScreenModel<HomeScreenModel>()
        val selectedNetwork by screenModel.selectedNetwork.collectAsStateMultiplatform()
        val isNoWalletImported by screenModel.isNoEvmAccountFound.collectAsStateMultiplatform()
        val isDevelopmentEnvironmnent by screenModel.isDevelopmentEnvironment.collectAsStateMultiplatform()
        val isNoAntelopeAccountFound by screenModel.isNoAntelopeAccountFound.collectAsStateMultiplatform()
        val localNavigator = LocalNavigator.currentOrThrow

        val observer = remember {
            LifecycleEventObserver { _, event ->
                if (event == Lifecycle.Event.ON_RESUME) {
                    if (!screenModel.isDeviceSecure()) {
                        val lockScreen = ScreenRegistry.get(SharedScreen.LockScreen)
                        localNavigator.push(lockScreen)
                        return@LifecycleEventObserver
                    }
                }
            }
        }
        LocalLifecycleOwner.current.lifecycle.addObserver(observer)


        CompositionLocalProvider(LocalBottomNavigationVisibility provides mutableStateOf(true)) {
            TabNavigator(initialTab) {
                Scaffold(
                    Modifier.then(
                        if (LocalBottomNavigationVisibility.current.value) {
                            Modifier
                                .background(MaterialTheme.mangalaColors.bgInnerCard)
                                .windowInsetsPadding(WindowInsets.navigationBars)
                        } else Modifier
                    ),
                    scaffoldState = state,
                    content = {
                        Column(Modifier.padding(it)) {
                            CurrentTab()
                            EosAccountCreatedNotification(
                                screenModel = screenModel,
                                navigator = localNavigator
                            )
                        }
                    },
                    bottomBar = {
                        if (LocalBottomNavigationVisibility.current.value) {
                            BottomNavigation(
                                backgroundColor = MaterialTheme.mangalaColors.bgInnerCard,
                                modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars)
                            ) {
                                TabNavigationItem(WalletTab)
//                                TabNavigationItem(DAppTab)

                                when (selectedNetwork?.blockchainType?.networkType) {
                                    NetworkType.EVM -> {
                                        if (isNoWalletImported.not() && isDevelopmentEnvironmnent) {
                                            TabNavigationItem(SwapTab)
                                            TabNavigationItem(NftTab)
                                        }
                                        TabNavigationItem(BrowserTab)
                                    }

                                    NetworkType.ANTELOPE -> {
//                                        if (isNoAntelopeAccountFound.not()) {
//                                            TabNavigationItem(MsigTab)
//                                        }
                                        TabNavigationItem(ContactTab)
                                        TabNavigationItem(ConversationUITab)
//                                        TabNavigationItem(ResourceTab)
                                    }

                                    else -> {

                                    }
                                }
                                TabNavigationItem(SettingTab)
                            }
                        }
                    }
                )
            }
        }
    }

    @Composable
    internal fun RowScope.TabNavigationItem(tab: Tab) {
        val tabNavigator = LocalTabNavigator.current

        BottomNavigationItem(
            selected = tabNavigator.current.key == tab.key,
            onClick = {
                when (tab) {
                    WalletTab -> WalletTab.onTabSelected()
                    ContactTab -> ContactTab.onTabSelected()
                    ConversationUITab -> ConversationUITab.onTabSelected()
                    SettingTab -> SettingTab.onTabSelected()
                    BrowserTab -> BrowserTab.onTabSelected()
                    SwapTab -> SwapTab.onTabSelected()
                    NftTab -> NftTab.onTabSelected()
                }
                tabNavigator.current = tab
            },
            icon = { Icon(painter = tab.options.icon!!, contentDescription = tab.options.title) },
            label = { TextTab(text = tab.options.title) },
            selectedContentColor = MaterialTheme.mangalaColors.textPrimary,
            unselectedContentColor = MaterialTheme.mangalaColors.textSecondary,
        )
    }

    @Composable
    fun EosAccountCreatedNotification(
        screenModel: HomeScreenModel,
        navigator: Navigator
    ) {
        val eosAccountCreatedState = screenModel.eosAccountCreatedUIState.collectAsStateMultiplatform().value
        when (eosAccountCreatedState) {
            is EosAccountCreatedNotificationUIState.Success -> {
                val title = eosAccountCreatedState.uiModel.title
                val eosAccountNoticeBody = eosAccountCreatedState.uiModel.body

                val notificationScreen = ScreenRegistry.get(
                    SharedScreen.CreateAccountNotificationScreen(
                        isSuccess = true,
                        accountName = eosAccountNoticeBody.accountName,
                        chainId = eosAccountNoticeBody.chainId,
                        onDismiss = {}
                    )
                )
                navigator.push(notificationScreen)
            }

            is EosAccountCreatedNotificationUIState.Failed -> {
                val errorMessage = eosAccountCreatedState.message
                NotificationDialog(
                    title = MR.strings.title_create_eos_account_failed_notification.desc().localized(),
                    body = errorMessage,
                    onDismiss = {
                        navigator.pop()
                    }
                )
            }

            else -> {}
        }
    }

    @Composable
    fun NotificationDialog(
        title: String?,
        body: String?,
        onDismiss: () -> Unit
    ) {
        Dialog(onDismissRequest = {}) {
            Surface(
                shape = MaterialTheme.shapes.medium,
                elevation = 8.dp,
                color = MaterialTheme.mangalaColors.bgInnerCard,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                ) {
                    Text(
                        text = title ?: MR.strings.title_create_eos_account_default_notification.desc().localized(),
                        style = MangalaTypography.Size17SemiBold(),
                        color = MaterialTheme.mangalaColors.textPrimary,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Text(
                        text = body ?: "",
                        style = MangalaTypography.Size14Regular(),
                        color = MaterialTheme.mangalaColors.textSecondary,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text(MR.strings.all_ok.desc().localized(), color = MaterialTheme.mangalaColors.textPrimary)
                    }
                }
            }
        }
    }
}
