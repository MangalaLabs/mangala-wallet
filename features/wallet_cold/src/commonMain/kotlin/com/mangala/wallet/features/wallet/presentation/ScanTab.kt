package com.mangala.wallet.features.wallet.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import cafe.adriel.voyager.core.registry.ScreenRegistry
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.navigator.tab.TabOptions
import com.mangala.wallet.common.mokoresources.icons.MangalaWalletPack
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.Scan
import com.mangala.wallet.features.chains.evmcompatible.model.GasPrice
import com.mangala.wallet.features.wallet.presentation.main.WalletMainScreenModel
import com.mangala.wallet.qrcode.domain.model.QrCodeData
import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.scanqr.ScanQRCode
import com.mangala.wallet.scanqr.ScanQRCodeListener
import com.mangala.wallet.ui.LocalGlobalNavigator
import com.mangala.wallet.ui.SharedScreen
import com.mangala.wallet.ui.tab.DestinationTab
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.desc.desc
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

object ScanTab : DestinationTab, KoinComponent {

    private val scanQRCode: ScanQRCode by inject()
    private val screenModel: WalletMainScreenModel by inject()
    override val route: String
        get() = "scan_tab"

    override val options: TabOptions
        @Composable
        get() {
            val icon = rememberVectorPainter(MangalaWalletPack.Scan)
            val title = MR.strings.label_scan.desc().localized()
            return remember {
                TabOptions(
                    index = 0u,
                    title = title,
                    icon = icon
                )
            }
        }

    @Composable
    override fun Content() {
        val globalNavigator = LocalGlobalNavigator.current

        val navigator = LocalNavigator.currentOrThrow
        scanMethod(
            navigator = navigator,
            globalNavigator = globalNavigator
        )
    }

    fun scanMethod(
        navigator: Navigator ,
        globalNavigator: Navigator
    ) {
        fun onHandleQrCodeResult(
            result: QrCodeData?,
            navigator: Navigator,
            globalNavigator: Navigator,
            screenModel: WalletMainScreenModel
        ) {
            when (result) {
                QrCodeData.Login -> TODO("Not handled scan QR code Login")
                is QrCodeData.Payment -> {
                    val step2Screen = ScreenRegistry.get(
                        SharedScreen.Step2SelectNetwork(
                            networkType = ,
                            accountId = screenModel.getCurrentAccountId(),
                            address = result.address
                        )
                    )

                    if (result.chain != null) {
                        val step3Screen = ScreenRegistry.get(
                            SharedScreen.Step3SelectAmountScreen(
                                accountId = screenModel.getCurrentAccountId(),
                                address = result.address,
                                blockchainUid = result.chain?.uid,
                                amount = result.amount,
                                contactId = null
                            )
                        )
                        globalNavigator.push(listOf(step2Screen, step3Screen))
                        return
                    }

                    globalNavigator.push(step2Screen)
                }

                is QrCodeData.NotSignedTransaction -> {
                    val signTransactionRequest = result.signTransactionRequest
                    val legacyGasPrice =
                        (signTransactionRequest.gasPrice as? GasPrice.Legacy)?.legacyGasPrice
                    val eip1559GasPrice = (signTransactionRequest.gasPrice as? GasPrice.Eip1559)

                    val signedTransactionQrScreen = ScreenRegistry.get(
                        SharedScreen.SignedTransactionQrScreen(
                            requestId = signTransactionRequest.requestId,
                            walletId = signTransactionRequest.walletId,
                            accountId = signTransactionRequest.accountId,
                            nonce = signTransactionRequest.nonce,
                            blockchainUid = signTransactionRequest.blockchainType.uid,
                            fromAddress = signTransactionRequest.fromAddress,
                            toAddress = signTransactionRequest.transactionData.to?.hex.orEmpty(),
                            value = signTransactionRequest.transactionData.value,
                            input = signTransactionRequest.transactionData.input,
                            legacyGasPrice = legacyGasPrice,
                            maxFeePerGas = eip1559GasPrice?.maxFeePerGas,
                            maxPriorityFeePerGas = eip1559GasPrice?.maxPriorityFeePerGas,
                            baseFee = eip1559GasPrice?.baseFee,
                            gasLimit = signTransactionRequest.gasLimit,
                            gasFiatValue = signTransactionRequest.gasFiatValue,
                            transactionType = signTransactionRequest.transactionType.toString(), // TODO: Check type of transaction and forward to the correct confirmation screen
                            contactName = signTransactionRequest.contactName,
                            contactAddress = signTransactionRequest.contactAddress
                        )
                    )
                    navigator.push(signedTransactionQrScreen)
                }

                is QrCodeData.SyncAccount -> TODO("Show error since Cold wallet doesn't handle a sync data request")
                is QrCodeData.SignedTransaction -> TODO("Show error since Cold wallet don't have send transaction capability")
                QrCodeData.WalletConnect -> TODO("Not implemented scan QR code WalletConnect")
                null -> TODO("Not handled scan invalid QR code")
            }
        }

        scanQRCode.scanQRCode(
            scanQRCodeListener = object : ScanQRCodeListener {
                override fun onScanQRCodeResult(result: String) {
                    onHandleQrCodeResult(
                        screenModel.onScanQrCodeResult(result),
                        navigator,
                        globalNavigator,
                        screenModel
                    )
                }
            },
            currentAccountId = screenModel.getCurrentAccountId(),,,
        )
    }
}

