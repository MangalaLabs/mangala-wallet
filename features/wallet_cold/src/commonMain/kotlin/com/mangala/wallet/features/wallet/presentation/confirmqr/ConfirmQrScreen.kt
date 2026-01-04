package com.mangala.wallet.features.wallet.presentation.confirmqr

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.bottomSheet.LocalBottomSheetNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.mangala.wallet.qrcode.ComposeUIWrapper
import com.mangala.wallet.common.mokoresources.Colors
import com.mangala.wallet.common.mokoresources.Dimensions
import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.common.mokoresources.Spacing
import com.mangala.wallet.ui.TextDescription2
import com.mangala.wallet.ui.component.MangalaCircularProgressIndicatorFullScreen
import com.mangala.wallet.ui.component.MangalaWalletTopBar
import com.mangala.wallet.ui.utils.collectAsStateMultiplatform
import com.mangala.wallet.ui.utils.screenmodel.BaseScreen
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.desc.desc
import org.koin.core.parameter.parametersOf

class ConfirmQrScreen(private val qrCode: String) : BaseScreen<ConfirmQrScreenModel>() {

    override val statusBarInsetColor: Color
        @Composable
        get() = MaterialTheme.colors.background

    @Composable
    override fun createScreenModel(): ConfirmQrScreenModel = getScreenModel(
        parameters = { parametersOf(qrCode) })

    @Composable
    override fun ScreenContent(screenModel: ConfirmQrScreenModel) {
        val navigator = LocalNavigator.currentOrThrow
        val uiState = screenModel.uiState.collectAsStateMultiplatform()
        val bottomSheetNavigator = LocalBottomSheetNavigator.current

        ConfirmQrScreen(
            onBackClicked = { bottomSheetNavigator.hide() },
            uiState.value
        )
    }

    @Composable
    fun ConfirmQrScreen(
        onBackClicked: () -> Unit,
        uiState: ConfirmQrScreenUiState
    ) {
        MangalaWalletTopBar(
            text = MR.strings.sync_transaction.desc().localized(),
            color = Colors.main1Text,
            fontWeight = FontWeight.W500,
            onBackClicked = onBackClicked
        )
        Spacer(modifier = Modifier.height(Spacing.BASE))

        when (uiState) {
            is ConfirmQrScreenUiState.Loading -> {
                MangalaCircularProgressIndicatorFullScreen()
            }

            is ConfirmQrScreenUiState.Success -> {
                Column(
                    modifier = Modifier.padding(horizontal = Spacing.SMALL),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    TextDescription2(
                        MR.strings.alert_for_user_confirm.desc().localized(),
                        color = Color(0xFF767676)
                    )
                    Spacer(modifier = Modifier.height(Spacing.TINY))

                    val qrCodeSize = with(LocalDensity.current) { Dimensions.qrConfirmTransactionDefault } // Default size

                    val composeUIWrapper = ComposeUIWrapper()
                    composeUIWrapper.QRCodeImage(
                        uiState.qrCode, modifier = Modifier
                            .width(qrCodeSize)
                            .height(qrCodeSize)
                            .padding(Spacing.BASE)
                    )
                    Spacer(modifier = Modifier.height(Spacing.XXXLARGE))
                }
            }
        }
    }
}
