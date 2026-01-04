package com.mangala.wallet.features.settings.network

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.bottomSheet.LocalBottomSheetNavigator
import com.mangala.wallet.model.blockchain.BlockchainNetworkData
import com.mangala.wallet.common.mokoresources.Colors
import com.mangala.wallet.common.mokoresources.Dimensions
import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.common.mokoresources.icons.MangalaWalletPack
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.Clear
import com.mangala.wallet.ui.TextTopBar
import com.mangala.wallet.ui.component.MangalaWalletIconButton
import com.mangala.wallet.ui.utils.collectAsStateMultiplatform
import com.mangala.wallet.ui.utils.screenmodel.BaseScreen
import com.mangala.wallet.utils.analytics.MangalaAnalytics
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.desc.desc
import org.koin.core.parameter.parametersOf

class NetworkBottomSheetScreen(
    private val selectedNetwork: BlockchainNetworkData?,
    private val onItemSelected: (BlockchainNetworkData) -> Unit
): BaseScreen<NetworkBottomSheetScreenModel>() {

    override val screenName: String = MangalaAnalytics.Screens.NETWORK_BOTTOM_SHEET
    override val screenClassName: String = NetworkBottomSheetScreen::class.simpleName.orEmpty()

    override val isBottomBarVisible: Boolean = false

    @Composable
    override fun createScreenModel(): NetworkBottomSheetScreenModel {
        return getScreenModel<NetworkBottomSheetScreenModel>(parameters = {
            parametersOf(selectedNetwork)
        })
    }

    @Composable
    override fun ScreenContent(screenModel: NetworkBottomSheetScreenModel) {
        val bottomSheetNavigator = LocalBottomSheetNavigator.current

        val uiModel = screenModel.uiModel.collectAsStateMultiplatform().value

        NetworkBottomSheetScreen(
            uiModel = uiModel,
            onClose = { bottomSheetNavigator.hide() },
            onItemSelected = {
                onItemSelected(it.network)
                bottomSheetNavigator.hide()
            },
            onChangeQuery = { screenModel.onChangeQuery(it) }
        )
    }

    @Composable
    private fun NetworkBottomSheetScreen(
        uiModel: NetworkScreenModelUiModel,
        onClose: () -> Unit,
        onChangeQuery: (String) -> Unit,
        onItemSelected: (NetworkScreenModelItemUiModel) -> Unit
    ) {
        Column(Modifier.background(color = Colors.cloudGray).statusBarsPadding()) {
            Box(Modifier.fillMaxWidth()) {
                TextTopBar(
                    text = MR.strings.all_select_network.desc().localized(),
                    modifier = Modifier.align(Alignment.Center)
                )
                MangalaWalletIconButton(
                    icon = MangalaWalletPack.Clear,
                    modifier = Modifier.align(Alignment.CenterEnd),
                    onClick = onClose
                )
            }

            NetworkList(
                uiModel = uiModel,
                onChangeQuery = onChangeQuery,
                onItemSelected = onItemSelected,
                modifier = Modifier.padding(horizontal = Dimensions.Padding.default)
            )
        }
    }
}