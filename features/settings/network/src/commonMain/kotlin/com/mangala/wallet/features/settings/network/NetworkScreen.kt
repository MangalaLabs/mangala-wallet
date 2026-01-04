package com.mangala.wallet.features.settings.network

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import cafe.adriel.voyager.core.registry.rememberScreen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.mangala.wallet.common.mokoresources.Dimensions
import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.common.mokoresources.Spacing
import com.mangala.wallet.ui.LocalGlobalNavigator
import com.mangala.wallet.ui.SharedScreen
import com.mangala.wallet.ui.component.MangalaWalletTopBarCenteredTitle
import com.mangala.wallet.ui.component.OnboardingGradientBackground
import com.mangala.wallet.ui.theme.mangalaColors
import com.mangala.wallet.ui.utils.collectAsStateMultiplatform
import com.mangala.wallet.ui.utils.screenmodel.BaseScreen
import com.mangala.wallet.utils.analytics.MangalaAnalytics
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.desc.desc

class NetworkScreen : BaseScreen<NetworkScreenModel>() {

    override val screenName: String = MangalaAnalytics.Screens.NETWORK
    override val screenClassName: String = NetworkScreen::class.simpleName.orEmpty()

    override val isBottomBarVisible: Boolean = false

    private var chainIdCallback: ((Long) -> Unit)? = null

    private var isFromBrowser: Boolean = false
    fun setChainIdCallback(chainIdCallback: (Long) -> Unit) {
        this.chainIdCallback = chainIdCallback
        this.isFromBrowser = true
    }

    @Composable
    override fun createScreenModel(): NetworkScreenModel = getScreenModel()

    @Composable
    override fun ScreenContent(screenModel: NetworkScreenModel) {
        val navigator = LocalNavigator.currentOrThrow
        val globalNavigator = LocalGlobalNavigator.current

        val homeScreen = rememberScreen(SharedScreen.HomeScreen())
        val uiModel = screenModel.uiModel.collectAsStateMultiplatform().value

        NetworkScreen(
            uiModel = uiModel,
            onBackPressed = {
                navigator.pop()
                chainIdCallback?.invoke(uiModel.chainIdSelected)
            },
            onItemSelected = {
//                if (!isFromBrowser) {
                    screenModel.onClickSelectNetwork(it)
//                }

                if(!isFromBrowser){
                    globalNavigator.replaceAll(homeScreen)
                }
            },
            onChangeQuery = { screenModel.onChangeQuery(it) }
        )
    }

    @Composable
    fun NetworkScreen(
        uiModel: NetworkScreenModelUiModel,
        onBackPressed: () -> Unit,
        onChangeQuery: (String) -> Unit,
        onItemSelected: (NetworkScreenModelItemUiModel) -> Unit
    ) {
        val keyboardController = LocalSoftwareKeyboardController.current
        val focusManager = LocalFocusManager.current

        OnboardingGradientBackground {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectTapGestures {
                            keyboardController?.hide()
                            focusManager.clearFocus()
                        }
                    }
                    .windowInsetsPadding(WindowInsets.safeDrawing)
            ) {
                MangalaWalletTopBarCenteredTitle(
                    title = MR.strings.all_network.desc().localized(),
                    onBackClicked = { onBackPressed() }
                )
                
                Spacer(Modifier.height(Spacing.SMALL))
                
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = Dimensions.Padding.default)
                ) {
                    NetworkList(uiModel, onChangeQuery, onItemSelected)
                }
            }
        }
    }
}