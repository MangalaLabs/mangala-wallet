package com.mangala.wallet.features.chains.antelope.presentation.importaccount

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import cafe.adriel.voyager.koin.getScreenModel
import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.qrcode.ComposeUIWrapper
import com.mangala.wallet.qrcode.domain.model.QrCodeData
import com.mangala.wallet.qrcode.domain.usecase.ParseQRCodeResultUseCase
import com.mangala.wallet.scanqr.ScanQRCode
import com.mangala.wallet.scanqr.ScanQRCodeListener
import com.mangala.wallet.ui.utils.resolve
import com.mangala.wallet.ui.utils.screenmodel.BaseScreen
import com.mangala.wallet.utils.analytics.MangalaAnalytics
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.desc.desc
import dev.icerock.moko.resources.format
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.jvm.Transient

class ImportAccountScreen: BaseScreen<ImportAccountScreenModel>(), KoinComponent {

    override val screenName: String = MangalaAnalytics.Screens.ANTELOPE_IMPORT_ACCOUNT_COLD
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
            TextField(
                value = uiState.privateKey,
                onValueChange = { screenModel.onPrivateKeyChange(it) },
                label = { Text(MR.strings.all_private_key.desc().localized()) }
            )
            uiState.publicKey?.let {
                Text(MR.strings.label_import_account_screen_public_key.format(it).localized())
            }
            when (uiState) {
                is ImportAccountScreenUiState.NotImported -> {
                    uiState.error?.let {
                        Text(it)
                    }
                    Button(
                        onClick = { screenModel.onImportAccount() },
                        enabled = uiState.error == null
                    ) {
                        Text(MR.strings.button_import_account_screen_import_account.desc().localized())
                    }
                }
                is ImportAccountScreenUiState.GeneratedKeyPair -> {
                    val composeUIWrapper = ComposeUIWrapper()
                    composeUIWrapper.QRCodeImage(uiState.encodedRequest)

                    uiState.error?.let {
                        Text(it.resolve())
                    }

                    Button(onClick = {
                        scanQRCode.scanQRCode(scanQRCodeListener = object: ScanQRCodeListener {
                            override fun onScanQRCodeResult(result: String) {
                                val qrResult = parseQrCode(result)

                                if (qrResult is QrCodeData.AntelopeImportAccount) {
                                    screenModel.onScanImportAccount(qrResult.importAccountRequest)
                                }
                            }
                        })
                    }) {
                        Text(MR.strings.button_import_account_screen_sync_account_name.desc().localized())
                    }
                }
                is ImportAccountScreenUiState.Imported -> {
                    Text(MR.strings.all_account_name_custom.format(uiState.accountName).localized())
                }
            }
        }
    }
}