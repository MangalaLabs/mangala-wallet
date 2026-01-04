package com.mangala.wallet.features.chains.antelope.presentation.createkeypair

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.graphics.Color
import cafe.adriel.voyager.koin.getScreenModel
import com.mangala.wallet.qrcode.ComposeUIWrapper
import com.mangala.wallet.qrcode.domain.model.QrCodeData
import com.mangala.wallet.qrcode.domain.usecase.ParseQRCodeResultUseCase
import com.mangala.wallet.scanqr.ScanQRCode
import com.mangala.wallet.scanqr.ScanQRCodeListener
import com.mangala.wallet.ui.component.MaxWidthColumn
import com.mangala.wallet.ui.utils.resolve
import com.mangala.wallet.ui.utils.screenmodel.BaseScreen
import com.mangala.wallet.utils.analytics.MangalaAnalytics
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.jvm.Transient

class CreateKeyPairScreen: BaseScreen<CreateKeyPairScreenModel>(), KoinComponent {

    override val screenName: String = MangalaAnalytics.Screens.ANTELOPE_CREATE_KEY_PAIR_COLD
    override val screenClassName: String = CreateKeyPairScreen::class.simpleName.orEmpty()

    override val isBottomBarVisible: Boolean = false

    @delegate:Transient
    private val scanQRCode: ScanQRCode by inject()
    @delegate:Transient
    private val parseQrCode: ParseQRCodeResultUseCase by inject()

    @Composable
    override fun createScreenModel(): CreateKeyPairScreenModel {
        return getScreenModel()
    }

    @Composable
    override fun ScreenContent(screenModel: CreateKeyPairScreenModel) {
        val uiState = screenModel.uiState.collectAsState()

        MaxWidthColumn {
            Button(onClick = { screenModel.createKeyPair() }) {
                Text("Create key pair")
            }

            when(val state = uiState.value) {
                is CreateKeyPairScreenUiState.KeyNotGenerated -> {
                    Text("Key not generated")
                }
                is CreateKeyPairScreenUiState.KeyGenerated -> {
                    Column {
                        Text("Owner public key: ${state.ownerPublicKey}")
                        Text("Owner private key: ${state.ownerPrivateKey}")
                        Text("Active public key: ${state.activePublicKey}")
                        Text("Active private key: ${state.activePrivateKey}")

                        val composeUIWrapper = ComposeUIWrapper()
                        composeUIWrapper.QRCodeImage(state.encodedQrCode)

                        Button(onClick = {
                            scanQRCode.scanQRCode(scanQRCodeListener = object: ScanQRCodeListener {
                                override fun onScanQRCodeResult(result: String) {
                                    val qrResult = parseQrCode(result)

                                    if (qrResult is QrCodeData.AntelopeSyncAccount) {
                                        screenModel.onSyncAccount(qrResult.syncAccountRequest)
                                    }
                                }
                            })
                        }) {
                            Text("Sync account name")
                        }
                        Button(onClick = {
                            scanQRCode.scanQRCode(scanQRCodeListener = object: ScanQRCodeListener {
                                override fun onScanQRCodeResult(result: String) {
                                    val qrResult = parseQrCode(result)

                                    if (qrResult is QrCodeData.AntelopeSignTransaction) {
                                        screenModel.onSignTransaction(qrResult.signTransactionRequest)
                                    }
                                }
                            })
                        }) {
                            Text("Scan sign transaction QR code")
                        }

                        state.syncAccountError?.let {
                            Text(it.resolve(), color = Color.Red)
                        }
                    }
                }
                is CreateKeyPairScreenUiState.AccountSynced -> {
                    Text("Account synced: ${state.accountName}")
                    Text("Account owner public key: ${state.ownerPublicKey}")
                    Text("Account active public key: ${state.activePublicKey}")
                }
            }
        }
    }
}