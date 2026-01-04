package com.mangala.wallet.features.chains.antelope

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.lifecycle.LifecycleEffect
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.mangala.wallet.features.chains.antelope.presentation.createkeypair.CreateKeyPairScreen
import com.mangala.wallet.features.chains.antelope.presentation.importaccount.ImportAccountScreen
import com.mangala.wallet.features.chains.antelope.presentation.signtransaction.SignTransactionScreen
import com.mangala.wallet.qrcode.domain.model.QrCodeData
import com.mangala.wallet.qrcode.domain.usecase.ParseQRCodeResultUseCase
import com.mangala.wallet.scanqr.ScanQRCode
import com.mangala.wallet.scanqr.ScanQRCodeListener
import com.mangala.wallet.utils.analytics.MangalaAnalytics
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class AntelopeColdScreen: Screen, KoinComponent {

    @delegate:Transient
    private val scanQRCode: ScanQRCode by inject()
    @delegate:Transient
    private val parseQrCode: ParseQRCodeResultUseCase by inject()

    @Composable
    override fun Content() {
        LifecycleEffect(onStarted = {
            MangalaAnalytics.trackScreenView(
                MangalaAnalytics.Screens.ANTELOPE_COLD,
                AntelopeColdScreen::class.simpleName.orEmpty()
            )
        })

        val navigator = LocalNavigator.currentOrThrow

        val generateKeyScreen = CreateKeyPairScreen()
        val importAccountScreen = ImportAccountScreen()

        Column {
            Button(onClick = {
                navigator.push(generateKeyScreen)
            }) {
                Text("Generate key")
            }
            Button(onClick = {
                navigator.push(importAccountScreen)
            }) {
                Text("Import account")
            }
            Button(onClick = {
                scanQRCode.scanQRCode(scanQRCodeListener = object: ScanQRCodeListener {
                    override fun onScanQRCodeResult(result: String) {
                        val qrResult = parseQrCode(result)

                        if (qrResult is QrCodeData.Esr) {
                            navigator.push(SignTransactionScreen(
                                signTransactionRequest = qrResult
                            ))
                        }
                    }
                })
            }) {
                Text("Scan QR code")
            }
        }
    }
}