package com.mangala.wallet.features.chains.antelope.presentation.importaccount

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import cafe.adriel.voyager.koin.getScreenModel
import com.mangala.wallet.qrcode.ComposeUIWrapper
import com.mangala.wallet.qrcode.domain.model.QrCodeData
import com.mangala.wallet.qrcode.domain.usecase.ParseQRCodeResultUseCase
import com.mangala.wallet.scanqr.ScanQRCode
import com.mangala.wallet.scanqr.ScanQRCodeListener
import com.mangala.wallet.ui.utils.screenmodel.BaseScreen
import com.mangala.wallet.utils.analytics.MangalaAnalytics
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.jvm.Transient

class ImportAccountScreen: BaseScreen<ImportAccountScreenModel>(), KoinComponent {

    override val screenName: String = MangalaAnalytics.Screens.ANTELOPE_IMPORT_ACCOUNT_UI
    override val screenClassName: String = ImportAccountScreen::class.simpleName.orEmpty()

    override val isBottomBarVisible: Boolean = false

    @delegate:Transient
    private val scanQRCode: ScanQRCode by inject()
    @delegate:Transient
    private val parseQrCode: ParseQRCodeResultUseCase by inject()

    @Composable
    override fun createScreenModel(): ImportAccountScreenModel {
        return getScreenModel()
    }

    @Composable
    override fun ScreenContent(screenModel: ImportAccountScreenModel) {
        val uiState = screenModel.uiState.collectAsState().value

        Column {
            when (uiState) {
                ImportAccountScreenUiState.NotScanned -> {
                    Button(onClick = {
                        scanQRCode.scanQRCode(object: ScanQRCodeListener {
                            override fun onScanQRCodeResult(result: String) {
                                val qrResult = parseQrCode(result)

                                if (qrResult is QrCodeData.AntelopeImportAccount) {
                                    screenModel.onScan(qrResult.importAccountRequest)
                                }
                            }
                        })
                    }) {
                        Text("Scan QR Code")
                    }
                }
                is ImportAccountScreenUiState.Scanned -> {
                    Text("Public key: ${uiState.publicKey}")
                    TextField(
                        value = uiState.accountName,
                        onValueChange = { screenModel.onAccountNameChange(it) },
                        label = { Text("Account name") }
                    )
                    uiState.error?.let {
                        Text(it)
                    }
                    Button(onClick = { screenModel.onImportAccount() }) {
                        Text("Import account")
                    }
                }
                is ImportAccountScreenUiState.Imported -> {
                    Text("Account imported: ${uiState.accountName}")

                    val composeUIWrapper = ComposeUIWrapper()
                    composeUIWrapper.QRCodeImage(uiState.encodedImportedAccountRequest)
                }
            }
        }
    }
}