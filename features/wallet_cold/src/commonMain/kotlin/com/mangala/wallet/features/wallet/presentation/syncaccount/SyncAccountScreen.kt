package com.mangala.wallet.features.wallet.presentation.syncaccount

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.registry.ScreenRegistry
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.mangala.wallet.qrcode.ComposeUIWrapper
import com.mangala.wallet.common.mokoresources.FontType
import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.common.mokoresources.Spacing
import com.mangala.wallet.ui.ButtonNormal
import com.mangala.wallet.ui.SharedScreen
import com.mangala.wallet.ui.component.MangalaCircularProgressIndicatorFullScreen
import com.mangala.wallet.ui.component.MaxWidthColumn
import com.mangala.wallet.ui.component.WalletMainScreenTopBar
import com.mangala.wallet.ui.utils.collectAsStateMultiplatform
import com.mangala.wallet.ui.utils.screenmodel.BaseScreen
import com.mangala.wallet.utils.analytics.MangalaAnalytics
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.desc.desc
import org.koin.core.parameter.parametersOf

class SyncAccountScreen(private val accountId: String): BaseScreen<SyncAccountScreenModel>() {

    override val screenName: String = MangalaAnalytics.Screens.SYNC_ACCOUNT
    override val screenClassName: String = SyncAccountScreen::class.simpleName.orEmpty()

    @Composable
    override fun createScreenModel(): SyncAccountScreenModel = getScreenModel(
        parameters = { parametersOf(accountId) }
    )

    @Composable
    override fun ScreenContent(screenModel: SyncAccountScreenModel) {
        val uiState = screenModel.uiState.collectAsStateMultiplatform()
        SyncAccountScreen(uiState.value)
    }

    @Composable
    fun SyncAccountScreen(uiState: SyncAccountScreenUiState) {
        val navigator = LocalNavigator.currentOrThrow

        WalletMainScreenTopBar(
            onClickMenuIcon = {
                val menuScreen = ScreenRegistry.get(SharedScreen.MenuScreen)
                navigator.push(menuScreen)
            },
            selectedNetwork = null,
        )

        MaxWidthColumn(
            modifier = Modifier
                .padding(horizontal = Spacing.SMALL, vertical = Spacing.SMALL)
                .background(Color.White),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
//            Spacer(modifier = Modifier.height(Spacing.BASE))
            Text(MR.strings.sync_qr_code_prompt.desc().localized())
            Spacer(modifier = Modifier.height(Spacing.XSMALL))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f) // Ensure the box maintains a square aspect ratio
            ) {
                when (uiState) {
                    is SyncAccountScreenUiState.Error -> {
                        Text("Error generating QR")
                    }

                    SyncAccountScreenUiState.Loading -> {
                        MangalaCircularProgressIndicatorFullScreen()
                    }

                    is SyncAccountScreenUiState.Success -> {
                        val composeUIWrapper = ComposeUIWrapper()
                        composeUIWrapper.QRCodeImage(
                            uiState.qrCode,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
        }

        MaxWidthColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = Spacing.SMALL, vertical = Spacing.SMALL)
                .background(Color.White),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ButtonNormal(
                text = MR.strings.reconnect_later.desc().localized(),
                modifier = Modifier
                    .fillMaxWidth(),
                buttonModifier = Modifier
                    .height(44.dp)
                    .fillMaxWidth(),
                fontSize = FontType.REGULAR,
                backgroundColor = MaterialTheme.colors.onPrimary,
            ) {
                navigator.pop()
            }
        }
    }
}