package com.mangala.wallet.features.transactionqr.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.lifecycle.LifecycleEffect
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.bottomSheet.BottomSheetNavigator
import cafe.adriel.voyager.navigator.bottomSheet.LocalBottomSheetNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.mangala.wallet.features.chains.evmcompatible.model.SignTransactionRequest
import com.mangala.wallet.qrcode.ComposeUIWrapper
import com.mangala.wallet.common.mokoresources.Colors
import com.mangala.wallet.common.mokoresources.Dimensions
import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.common.mokoresources.Spacing
import com.mangala.wallet.common.mokoresources.icons.MangalaWalletPack
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.Scan
import com.mangala.wallet.scanqr.ScanQRCode
import com.mangala.wallet.scanqr.ScanQRCodeListener
import com.mangala.wallet.ui.TextDescription2
import com.mangala.wallet.ui.component.MangalaCircularProgressIndicatorFullScreen
import com.mangala.wallet.ui.component.MangalaWalletIconButton
import com.mangala.wallet.ui.component.MangalaWalletTopBar
import com.mangala.wallet.ui.utils.collectAsStateMultiplatform
import com.mangala.wallet.ui.utils.screenmodel.BaseScreen
import com.mangala.wallet.utils.analytics.MangalaAnalytics
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.desc.desc
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf
import kotlin.jvm.Transient

internal class TransactionQrScreen(
    @Transient private val signTransactionRequest: SignTransactionRequest,
    @Transient private val onScannedSignedTransaction: (v: Int, r: ByteArray, s: ByteArray) -> Unit,
    @Transient private val onDispose: () -> Unit
): BaseScreen<TransactionQrScreenModel>(), KoinComponent {

    override val screenName: String = MangalaAnalytics.Screens.TRANSACTION_QR
    override val screenClassName: String = TransactionQrScreen::class.simpleName.orEmpty()

    private val scanQRCode: ScanQRCode by inject()

    @Composable
    override fun createScreenModel(): TransactionQrScreenModel = getScreenModel(
        parameters = { parametersOf(signTransactionRequest) }
    )

    override val isBottomBarVisible: Boolean = false

    override val statusBarInsetColor: Color
        @Composable
        get() = MaterialTheme.colors.background

    @Composable
    override fun ScreenContent(screenModel: TransactionQrScreenModel) {
        val uiState = screenModel.uiState.collectAsStateMultiplatform()
        val bottomSheetNavigator = LocalBottomSheetNavigator.current

        LifecycleEffect(onDisposed = {
            onDispose()
        })

        TransactionQrScreen(
            uiState.value,
            onClickScan = {
                scanQRCode.scanQRCode(object : ScanQRCodeListener {
                    override fun onScanQRCodeResult(result: String) {
                        screenModel.onScanQrCodeResult(result)
                    }
                })
            },
            bottomSheetNavigator
        )
    }

    @Composable
    fun TransactionQrScreen(
        uiState: TransactionQrScreenUiState,
        onClickScan: () -> Unit,
        bottomSheetNavigator: BottomSheetNavigator
    ) {
        val navigator = LocalNavigator.currentOrThrow

        LaunchedEffect((uiState as? TransactionQrScreenUiState.Success)?.qrCodeData) {
            if (uiState is TransactionQrScreenUiState.Success) {
                val qrCodeData = uiState.qrCodeData
                if (qrCodeData != null) {
                    onScannedSignedTransaction(qrCodeData.v, qrCodeData.r, qrCodeData.s)
                }
            }
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            MangalaWalletTopBar(
                text = MR.strings.sync_transaction.desc().localized(),
                color = Colors.main1Text,
                fontWeight = FontWeight.W500,
                onBackClicked = { bottomSheetNavigator.hide() }
            )
            when (uiState) {
                is TransactionQrScreenUiState.Success -> {
                    Column(
                        modifier = Modifier.padding(horizontal = Spacing.SMALL),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Spacer(modifier = Modifier.height(Spacing.BASE))

                        TextDescription2(
                            MR.strings.alert_for_user_confirm.desc().localized(),
                            color = Color(0xFF767676)
                        )
                        val qrCodeSize =
                            with(LocalDensity.current) { Dimensions.qrConfirmTransactionDefault } // Default size

                        val composeUIWrapper = ComposeUIWrapper()
                        composeUIWrapper.QRCodeImage(
                            uiState.qrCode, modifier = Modifier
                                .width(qrCodeSize)
                                .height(qrCodeSize)
                                .padding(Spacing.BASE)
                        )

                        Row(
                            modifier = Modifier
                                .width(201.dp)
                                .height(40.dp)
                                .background(
                                    color = Color(0xFFD7D7D7),
                                    shape = RoundedCornerShape(size = 16.dp)
                                )
                                .padding(start = 16.dp, top = 8.dp, end = 16.dp, bottom = 8.dp)
                                .clickable {
                                    onClickScan()
                                },
                            horizontalArrangement = Arrangement.spacedBy(
                                0.dp,
                                Alignment.CenterHorizontally
                            ),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            MangalaWalletIconButton(
                                icon = MangalaWalletPack.Scan,
                                onClick = { onClickScan() },
                            )
                            Spacer(modifier = Modifier.width(Spacing.XTINY))
                            TextDescription2(
                                text = MR.strings.scan_qr_code.desc().localized(),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF484848),
                            )
                        }
                        Spacer(modifier = Modifier.height(Spacing.XBASE))
                    }
                }

                is TransactionQrScreenUiState.Loading -> {
                    MangalaCircularProgressIndicatorFullScreen()
                }
                is TransactionQrScreenUiState.Error -> {
                    Text(uiState.message)
                }
            }
        }
    }
}