package com.mangala.wallet.wallet.presentation.create

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.registry.ScreenRegistry
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.mangala.wallet.model.blockchain.BlockchainType
import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.common.mokoresources.Spacing
import com.mangala.wallet.common.mokoresources.icons.MangalaWalletPack
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.ArrowLeft
import com.mangala.wallet.ui.ButtonNormal
import com.mangala.wallet.ui.SharedScreen
import com.mangala.wallet.ui.TextDescription1
import com.mangala.wallet.ui.TextTitle3
import com.mangala.wallet.ui.utils.screenmodel.BaseScreen
import com.mangala.wallet.utils.analytics.MangalaAnalytics
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.desc.desc

class CreateWalletGuideScreen : BaseScreen<CreateWalletGuideScreenModel>() {

    override val screenName: String = MangalaAnalytics.Screens.EVM_CREATE_WALLET_GUIDE
    override val screenClassName: String = CreateWalletGuideScreen::class.simpleName.orEmpty()

    override val isBottomBarVisible: Boolean = false

    @Composable
    override fun createScreenModel(): CreateWalletGuideScreenModel = getScreenModel()

    @Composable
    override fun ScreenContent(screenModel: CreateWalletGuideScreenModel) {
        val navigator = LocalNavigator.currentOrThrow

        val title = MR.strings.title_create_wallet_guide.desc().localized()
        val description1 = MR.strings.description_create_wallet_guide.desc().localized()
        val textButton = MR.strings.create_a_wallet.desc().localized()

        CreateWalletGuide(
            title,
            description1,
            textButton,
            onBackClicked = { navigator.pop() }
        ) {
            val nextScreen = if (screenModel.isPinSetup) {
                SharedScreen.CreateWalletScreen(
                    blockchainUid = BlockchainType.Ethereum.uid,
                    createWalletCase = SharedScreen.CreateWalletScreen.CreateWalletScreenCase.CREATE_NEW_WALLET
                )
            } else {
                SharedScreen.SetupPinScreen(
                    blockchainUid = BlockchainType.Ethereum.uid,
                    pinCase = SharedScreen.SetupPinScreen.SetupPinScreenCase.CREATE_NEW_WALLET.name
                )
            }
            navigator.push(ScreenRegistry.get(nextScreen))
        }
    }

    @Composable
    private fun CreateWalletGuide(
        title: String,
        description1: String,
        textButton: String,
        onBackClicked: (Boolean) -> Unit,
        onCreateWallet: (Boolean) -> Unit,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(color = MaterialTheme.colors.background)
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(Spacing.TINY))

            IconButton(onClick = {
                onBackClicked(true)
            }) {
                Icon(
                    imageVector = MangalaWalletPack.ArrowLeft,
                    contentDescription = "Back"
                )
            }

            TextTitle3(
                text = title,
                modifier = Modifier.padding(start = Spacing.SMALL, end = Spacing.SMALL)
            )

            TextDescription1(
                text = description1,
                modifier = Modifier.padding(16.dp)
            )

            Spacer(modifier = Modifier.height(Spacing.TINY))

            Column(
                modifier = Modifier.padding(
                    start = Spacing.SMALL,
                    end = Spacing.SMALL,
                    bottom = Spacing.SMALL,
                    top = Spacing.SMALL
                )
            ) {
                ButtonNormal(textButton, enabled = true, modifier = Modifier.fillMaxWidth()) {
                    onCreateWallet(true)
                }
            }
        }
    }
}