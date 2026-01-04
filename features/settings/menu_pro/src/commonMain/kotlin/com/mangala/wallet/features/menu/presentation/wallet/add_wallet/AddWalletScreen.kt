package com.mangala.wallet.features.menu.presentation.wallet.add_wallet

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.registry.ScreenRegistry
import cafe.adriel.voyager.core.registry.rememberScreen
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.mangala.wallet.model.blockchain.BlockchainType
import com.mangala.wallet.model.blockchain.NetworkType
import com.mangala.wallet.common.mokoresources.Colors
import com.mangala.wallet.common.mokoresources.Dimensions
import com.mangala.wallet.common.mokoresources.FontType
import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.common.mokoresources.Spacing
import com.mangala.wallet.common.mokoresources.icons.MangalaWalletPack
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.Logo
import com.mangala.wallet.ui.ButtonNormal
import com.mangala.wallet.ui.MangalaTextButton
import com.mangala.wallet.ui.SharedScreen
import com.mangala.wallet.ui.TextNormal
import com.mangala.wallet.ui.component.MangalaWalletTopBar
import com.mangala.wallet.ui.utils.collectAsStateMultiplatform
import com.mangala.wallet.ui.utils.screenmodel.BaseScreen
import com.mangala.wallet.utils.analytics.MangalaAnalytics
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.desc.desc

internal class AddWalletScreen : BaseScreen<AddWalletScreenModel>() {

    override val screenName: String = MangalaAnalytics.Screens.MENU_ADD_WALLET
    override val screenClassName: String = AddWalletScreen::class.simpleName.orEmpty()

    override val isBottomBarVisible = false

    @Composable
    override fun createScreenModel(): AddWalletScreenModel = getScreenModel()

    @Composable
    override fun ScreenContent(screenModel: AddWalletScreenModel) {
        val navigator = LocalNavigator.currentOrThrow
        val blockchainType = screenModel.uiModel.collectAsStateMultiplatform().value.network?.blockchainType

        val setUpPinScreen = rememberScreen(
            SharedScreen.SetupPinScreen(
                blockchainUid = blockchainType?.uid ?: BlockchainType.Ethereum.uid,
                antelopeAccountName = null,
                pinCase = SharedScreen.SetupPinScreen.SetupPinScreenCase.CREATE_NEW_WALLET.name
            )
        )

        val antelopeImportAccountScreen = rememberScreen(SharedScreen.AntelopeImportAccountScreen(null))

        if (blockchainType?.networkType == NetworkType.ANTELOPE) {
            AddWalletScreen(
                onBackClicked = { navigator.pop() },
                onCreateWalletClicked = {
                    val antelopeCreateAccountScreen =
                        ScreenRegistry.get(SharedScreen.AntelopeCreateAccountStep1Screen)

                    navigator.push(antelopeCreateAccountScreen)
                },
                onImportWalletClicked = { navigator.push(antelopeImportAccountScreen) },
                textTopBar = MR.strings.message_wallet_add_account.desc().localized(),
                textButtonCreate = MR.strings.all_add_new_account.desc().localized(),
                textButtonImport = MR.strings.button_wallet_main_import_account.desc().localized()
            )
        } else {
            AddWalletScreen(
                onBackClicked = { navigator.pop() },
                onCreateWalletClicked = {
                    if (screenModel.isPinExist()) {
                        val step4CreatingAccountScreen = ScreenRegistry.get(
                            SharedScreen.CreateWalletScreen(
                                blockchainUid = blockchainType?.uid,
                                antelopeAccountName = null,
                                createWalletCase = SharedScreen.CreateWalletScreen.CreateWalletScreenCase.CREATE_NEW_WALLET
                            )
                        )
                        navigator.push(step4CreatingAccountScreen)
                    } else {
                        navigator.push(setUpPinScreen)
                    }
                },
                onImportWalletClicked = {
                    val screen = ScreenRegistry.get(SharedScreen.ImportWalletGuideScreen())

                    navigator.push(screen)
                },
                textTopBar =  MR.strings.message_wallet_add_wallet.desc().localized(),
                textButtonCreate = MR.strings.button_wallet_main_create_wallet.desc().localized(),
                textButtonImport = MR.strings.button_wallet_main_import_wallet.desc().localized()
            )
        }
    }

    @Composable
    fun AddWalletScreen(
        onBackClicked: () -> Unit,
        onCreateWalletClicked: () -> Unit,
        onImportWalletClicked: () -> Unit,
        textTopBar : String = "",
        textButtonCreate: String = "",
        textButtonImport: String = ""
    ) {
        Scaffold(
            topBar = {
                MangalaWalletTopBar(modifier = Modifier.background(Colors.cloudGray),
                    text = textTopBar,
                    onBackClicked = { onBackClicked() })
            },
            modifier = Modifier.background(Colors.cloudGray).windowInsetsPadding(WindowInsets.safeDrawing)
        ) {
            Spacer(Modifier.height(Spacing.LARGE))
            Box(
                modifier = Modifier.fillMaxSize().background(Colors.cloudGray),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(
                        start = Spacing.SMALL,
                        end = Spacing.SMALL,
                        bottom = Spacing.SMALL,
                        top = Spacing.SMALL
                    )
                ) {
                    Image(
                        painter = rememberVectorPainter(MangalaWalletPack.Logo),
                        contentDescription = "Logo",
                        modifier = Modifier
                            .size(Dimensions.IconLogoSize)
                            .clip(RoundedCornerShape(Dimensions.IconLogoSize / 3.75f))
                    )

                    Spacer(Modifier.height(Spacing.SMALL))
                    TextNormal(
                        text = MR.strings.description_create_wallet_guide.desc().localized(),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(262.dp))
                    Column {
                        ButtonNormal(
                            text = textButtonCreate,
                            modifier = Modifier.fillMaxWidth(),
                            buttonModifier = Modifier.height(44.dp).fillMaxWidth(),
                            fontSize = FontType.REGULAR,
                            onClick = { onCreateWalletClicked() }
                        )
                        Spacer(Modifier.height(Spacing.BASE))
                        MangalaTextButton(
                            text = textButtonImport,
                            fontSize = FontType.REGULAR,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.SemiBold,
                            onClick = { onImportWalletClicked() }
                        )
                    }
                }
            }
        }
    }
}