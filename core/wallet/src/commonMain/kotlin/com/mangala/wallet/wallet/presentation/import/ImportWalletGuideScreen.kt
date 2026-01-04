package com.mangala.wallet.wallet.presentation.import

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.registry.ScreenRegistry
import cafe.adriel.voyager.core.registry.rememberScreen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
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

class ImportWalletGuideScreen(
    val nextScreen: SharedScreen.ScreenType = SharedScreen.ScreenType.HOME_SCREEN
) : BaseScreen<ImportWalletGuideScreenModel>() {

    override val screenName: String = MangalaAnalytics.Screens.EVM_IMPORT_WALLET_GUIDE
    override val screenClassName: String = ImportWalletGuideScreen::class.simpleName.orEmpty()

    @Composable
    override fun createScreenModel(): ImportWalletGuideScreenModel {
        return ImportWalletGuideScreenModel()
    }

    override val isBottomBarVisible: Boolean
        get() = false

    @Composable
    override fun ScreenContent(screenModel: ImportWalletGuideScreenModel) {
        val navigator = LocalNavigator.currentOrThrow

        val title = MR.strings.title_import_wallet.desc().localized()
        val description1 = MR.strings.message_import_wallet.desc().localized()
        val textButton = MR.strings.button_continue.desc().localized()

        ImportWalletGuide(
            title = title,
            description1 = description1,
            textButton = textButton,
            onBackClicked = {
                navigator.pop()
            },
            onCreateWallet = {
                val restoreRecoveryPhraseScreen = ScreenRegistry.get(SharedScreen.RestoreRecoveryPhraseScreen(nextScreen = nextScreen))
                navigator.push(restoreRecoveryPhraseScreen)
            }
        )
    }

    @Composable
    private fun ImportWalletGuide(
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