package com.mangala.wallet.menu_base.presentation.dev

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import cafe.adriel.voyager.core.registry.ScreenRegistry
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.mangala.wallet.common.mokoresources.Colors
import com.mangala.wallet.ui.SharedScreen
import com.mangala.wallet.ui.TextTitle2
import com.mangala.wallet.ui.component.MaxSizeColumn
import com.mangala.wallet.ui.utils.screenmodel.BaseScreen
import com.mangala.wallet.utils.ClipboardFactory
import com.mangala.wallet.utils.analytics.MangalaAnalytics
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.jvm.Transient

class DevMenuScreen : BaseScreen<DevMenuScreenModel>(), KoinComponent {

    override val screenName: String = MangalaAnalytics.Screens.DEV_MENU
    override val screenClassName: String = DevMenuScreen::class.simpleName.orEmpty()

    @delegate:Transient
    private val clipboardFactory: ClipboardFactory by inject()

    @Composable
    override fun createScreenModel(): DevMenuScreenModel {
        return getScreenModel()
    }

    @OptIn(ExperimentalLayoutApi::class)
    @Composable
    override fun ScreenContent(screenModel: DevMenuScreenModel) {
        val navigator = LocalNavigator.currentOrThrow

        MaxSizeColumn(
            Modifier
                .background(Colors.cloudGray)
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .verticalScroll(rememberScrollState())
        ) {
            TextTitle2("FCM Token")
            screenModel.fcmToken.value?.let {
                FlowRow {
                    Text(it)
                    IconButton(
                        onClick = {
                            clipboardFactory.copyText("FCM token", it)
                        }
                    ) {
                        Icon(Icons.Default.ContentCopy, contentDescription = "Copy FCM token")
                    }
                }
            }
            TextTitle2("IAP tokens")
            screenModel.iapData.value.let {
                it.forEach {
                    FlowRow {
                        Text(it.toString())
                        IconButton(
                            onClick = {
                                screenModel.copyPurchaseToken(it)
                            }
                        ) {
                            Icon(Icons.Default.ContentCopy, contentDescription = "Copy IAP data")
                        }
                    }
                }
            }
            Button(
                onClick = {
                    screenModel.clearPin()
                }
            ) {
                Text("Clear PIN")
            }
            Button(
                onClick = {
                    sendTestCrash()
                }
            ) {
                Text("Trigger crash")
            }
            Button(
                onClick = {
                    val screen = ScreenRegistry.get(SharedScreen.BitcoinTestScreen)
                    navigator.push(screen)
                }
            ) {
                Text("Bitcoin Test Screen")
            }
            Button(
                onClick = {
                    screenModel.logout()
                }
            ) {
                Text("Logout (Keep Passkeys)", color = Color.Black)
            }
            Button(
                onClick = {
                    screenModel.clearAllData()
                }
            ) {
                Text("🗑️ Clear All Data", color = Color.Red)
            }
            Button(
                onClick = {
                    screenModel.testPasskeyStorage()
                }
            ) {
                Text("🧪 Test Passkey Storage", color = Color.Black)
            }
            Button(
                onClick = {
                    val screen = ScreenRegistry.get(SharedScreen.AuthDemoScreen)
                    navigator.push(screen)
                }
            ) {
                Text("Auth demo screen")
            }
        }
    }
}