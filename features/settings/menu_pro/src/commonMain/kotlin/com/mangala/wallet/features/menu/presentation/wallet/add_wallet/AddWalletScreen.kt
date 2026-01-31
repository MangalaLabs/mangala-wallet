package com.mangala.wallet.features.menu.presentation.wallet.add_wallet

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.registry.ScreenRegistry
import cafe.adriel.voyager.core.registry.rememberScreen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.mangala.wallet.model.blockchain.BlockchainType
import com.mangala.wallet.model.blockchain.NetworkType
import com.mangala.wallet.common.mokoresources.Spacing
import com.mangala.wallet.common.mokoresources.font.getInterFontFamily
import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.ui.SharedScreen
import com.mangala.wallet.ui.component.MangalaWalletTopBarCenteredTitle
import com.mangala.wallet.ui.component.OnboardingButton
import com.mangala.wallet.ui.component.OnboardingGradientBackground
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
            AddWalletScreenContent(
                onBackClicked = { navigator.pop() },
                onCreateWalletClicked = {
                    val antelopeCreateAccountScreen =
                        ScreenRegistry.get(SharedScreen.AntelopeCreateAccountStep1Screen)

                    navigator.push(antelopeCreateAccountScreen)
                },
                onImportWalletClicked = { navigator.push(antelopeImportAccountScreen) },
                textTopBar = MR.strings.message_wallet_add_account.desc().localized(),
                textButtonCreate = MR.strings.all_add_new_account.desc().localized(),
                textButtonImport = MR.strings.button_wallet_main_import_account.desc().localized(),
                textDescription = MR.strings.description_create_wallet_guide.desc().localized()
            )
        } else {
            AddWalletScreenContent(
                onBackClicked = { navigator.pop() },
                onCreateWalletClicked = {
                    if (screenModel.isPinExist()) {
                        // PIN exists - verify user identity first (V2 callback approach)
                        val unlockPinScreen = ScreenRegistry.get(
                            SharedScreen.UnlockPinScreen(
                                onUnlockSuccess = {
                                    val createWalletScreen = ScreenRegistry.get(
                                        SharedScreen.CreateWalletScreen(
                                            blockchainUid = blockchainType?.uid,
                                            antelopeAccountName = null,
                                            createWalletCase = SharedScreen.CreateWalletScreen.CreateWalletScreenCase.CREATE_NEW_WALLET
                                        )
                                    )
                                    navigator.replace(createWalletScreen)
                                }
                            )
                        )
                        navigator.push(unlockPinScreen)
                    } else {
                        navigator.push(setUpPinScreen)
                    }
                },
                onImportWalletClicked = {
                    val screen = ScreenRegistry.get(SharedScreen.ImportWalletGuideScreen())

                    navigator.push(screen)
                },
                textTopBar = MR.strings.message_wallet_add_wallet.desc().localized(),
                textButtonCreate = MR.strings.button_wallet_main_create_wallet.desc().localized(),
                textButtonImport = MR.strings.button_wallet_main_import_wallet.desc().localized(),
                textDescription = MR.strings.description_create_wallet_guide.desc().localized()
            )
        }
    }

    @Composable
    private fun AddWalletScreenContent(
        onBackClicked: () -> Unit,
        onCreateWalletClicked: () -> Unit,
        onImportWalletClicked: () -> Unit,
        textTopBar: String,
        textButtonCreate: String,
        textButtonImport: String,
        textDescription: String
    ) {
        OnboardingGradientBackground(
            afterBackgroundModifier = Modifier.safeDrawingPadding()
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Top Bar
                MangalaWalletTopBarCenteredTitle(
                    title = textTopBar,
                    textColor = Color.White.copy(alpha = 0.8f),
                    onBackClicked = onBackClicked
                )

                // Main Content - Centered
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // Logo Icon Container
                    Box(
                        modifier = Modifier
                            .size(Spacing.HUGE)
                            .clip(RoundedCornerShape(Spacing.XBASE))
                            .background(
                                brush = Brush.verticalGradient(
                                    colors = listOf(
                                        Color.White.copy(alpha = 0.1f),
                                        Color.White.copy(alpha = 0.05f)
                                    )
                                )
                            )
                            .border(
                                width = 1.dp,
                                color = Color.White.copy(alpha = 0.1f),
                                shape = RoundedCornerShape(Spacing.XBASE)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "M",
                            fontSize = 64.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontFamily = getInterFontFamily(),
                            letterSpacing = (-2).sp
                        )
                    }

                    Spacer(modifier = Modifier.height(Spacing.XBASE))

                    // Description Text
                    Text(
                        text = textDescription,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color(0xFFA5B4CB),
                        textAlign = TextAlign.Center,
                        lineHeight = 24.sp,
                        fontFamily = getInterFontFamily(),
                        modifier = Modifier.padding(horizontal = Spacing.BASE)
                    )
                }

                // Bottom Actions
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Spacing.SMALL)
                        .padding(bottom = Spacing.BASE),
                    verticalArrangement = Arrangement.spacedBy(Spacing.SMALL)
                ) {
                    // Primary Button - Create Wallet
                    OnboardingButton(
                        text = textButtonCreate,
                        onClick = onCreateWalletClicked,
                        isPrimary = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Secondary Button - Import Wallet
                    OnboardingButton(
                        text = textButtonImport,
                        onClick = onImportWalletClicked,
                        isPrimary = false,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}
