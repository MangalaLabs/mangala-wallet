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
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.registry.rememberScreen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.mangala.wallet.common.mokoresources.Colors
import com.mangala.wallet.common.mokoresources.FontType
import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.common.mokoresources.Spacing
import com.mangala.wallet.ui.ButtonNormal
import com.mangala.wallet.ui.MangalaTextButton
import com.mangala.wallet.ui.SharedScreen
import com.mangala.wallet.ui.TextNormal
import com.mangala.wallet.ui.component.MangalaWalletTopBar
import com.mangala.wallet.ui.utils.screenmodel.BaseScreen
import com.mangala.wallet.utils.analytics.MangalaAnalytics
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.compose.painterResource
import dev.icerock.moko.resources.desc.desc
import org.koin.core.component.KoinComponent

internal class AddWalletScreen : BaseScreen<AddWalletScreenModel>() {

    override val screenName: String = MangalaAnalytics.Screens.MENU_ADD_WALLET
    override val screenClassName: String = AddWalletScreen::class.simpleName.orEmpty()

    override val isBottomBarVisible = false

    @Composable
    override fun createScreenModel(): AddWalletScreenModel = getScreenModel()

    @Composable
    override fun ScreenContent(screenModel: AddWalletScreenModel) {
        val navigator = LocalNavigator.currentOrThrow

        val step4CreatingAccountScreen = rememberScreen(SharedScreen.Step4CreatingAccountScreen(
            accountName = "default",
            accountSuffix = "",
            accountType = com.mangala.wallet.model.account.domain.eos.AccountNameType.PREMIUM
        ))
        val setUpPinScreen = rememberScreen(SharedScreen.SetupPinScreen(SharedScreen.SetupPinScreen.CREATE_NEW_WALLET))
        val restoreRecoveryPhraseScreen = rememberScreen(SharedScreen.RestoreRecoveryPhraseScreen())
        AddWalletScreen(
            onBackClicked = { navigator.pop() },
            onCreateWalletClicked = {
                if(screenModel.isPinExist()){
                    navigator.push(step4CreatingAccountScreen)
                } else {
                    navigator.push(setUpPinScreen)
                }
            },
            onImportWalletClicked = { navigator.push(restoreRecoveryPhraseScreen) }
        )
    }

    @Composable
    fun AddWalletScreen(
        onBackClicked: () -> Unit,
        onCreateWalletClicked: () -> Unit,
        onImportWalletClicked: () -> Unit
    ) {
        Scaffold(
            topBar = {
                MangalaWalletTopBar(modifier = Modifier.background(Colors.cloudGray),
                    text = MR.strings.message_wallet_add_wallet.desc().localized(),
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
                        painterResource(MR.images.logo),
                        null,
                    )

                    Spacer(Modifier.height(Spacing.SMALL))
                    TextNormal(
                        text = MR.strings.description_create_wallet_guide.desc().localized(),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(262.dp))
                    Column {
                        ButtonNormal(
                            MR.strings.button_wallet_main_create_wallet.desc().localized(),
                            modifier = Modifier.fillMaxWidth(),
                            buttonModifier = Modifier.height(44.dp).fillMaxWidth(),
                            fontSize = FontType.REGULAR,
                            onClick = { onCreateWalletClicked() }
                        )
                        Spacer(Modifier.height(Spacing.BASE))
                        MangalaTextButton(
                            MR.strings.button_wallet_main_import_wallet.desc().localized(),
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