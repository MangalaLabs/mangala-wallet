package com.mangala.browser.presentation.qr

import androidx.compose.foundation.layout.Column
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import cafe.adriel.voyager.koin.getScreenModel
import com.mangala.wallet.features.chains.evmcompatible.model.SignTransactionRequest
import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.qrcode.ComposeUIWrapper
import com.mangala.wallet.ui.component.MangalaCircularProgressIndicatorFullScreen
import com.mangala.wallet.ui.utils.collectAsStateMultiplatform
import com.mangala.wallet.ui.utils.screenmodel.BaseScreen
import com.mangala.wallet.utils.analytics.MangalaAnalytics
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.desc.desc
import org.koin.core.parameter.parametersOf

// TODO: Duplicate of send's TransactionQrScreen. Consider extracting common class
internal class TransactionQrScreen(
    private val signTransactionRequest: SignTransactionRequest
): BaseScreen<TransactionQrScreenModel>() {

    override val screenName: String = MangalaAnalytics.Screens.TRANSACTION_QR
    override val screenClassName: String = TransactionQrScreen::class.simpleName.orEmpty()

    @Composable
    override fun createScreenModel(): TransactionQrScreenModel = getScreenModel(
        parameters = { parametersOf(signTransactionRequest) }
    )

    override val isBottomBarVisible: Boolean = false

    @Composable
    override fun ScreenContent(screenModel: TransactionQrScreenModel) {
        val uiState = screenModel.uiState.collectAsStateMultiplatform()

        TransactionQrScreen(uiState.value)
    }

    @Composable
    fun TransactionQrScreen(
        uiState: TransactionQrScreenUiState
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(MR.strings.title_transaction_qr_screen.desc().localized())
            when (uiState) {
                is TransactionQrScreenUiState.Success -> {
                    val composeUIWrapper = ComposeUIWrapper()
                    composeUIWrapper.QRCodeImage(uiState.qrCode)
                }
                is TransactionQrScreenUiState.Loading -> {
                    MangalaCircularProgressIndicatorFullScreen()
                }
                else -> {}
            }
        }
    }
}