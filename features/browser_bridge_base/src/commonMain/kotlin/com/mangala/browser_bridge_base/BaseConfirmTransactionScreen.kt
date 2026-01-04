package com.mangala.browser_bridge_base

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.lifecycle.LifecycleEffect
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.mangala.wallet.features.send_base.step4.evm.BaseEvmStep4VerifyAndSendScreenUiState
import com.mangala.wallet.features.send_base.transactionfee.TransactionFeeScreen
import com.mangala.wallet.common.mokoresources.Colors
import com.mangala.wallet.common.mokoresources.FontType
import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.common.mokoresources.Spacing
import com.mangala.wallet.common.mokoresources.icons.MangalaWalletPack
import com.mangala.wallet.common.mokoresources.icons.mangalawalletpack.Clear
import com.mangala.wallet.features.chains.ui.FeeOptionUiModel
import com.mangala.wallet.ui.LocalGlobalNavigator
import com.mangala.wallet.ui.TextDescription2
import com.mangala.wallet.ui.TextNormal
import com.mangala.wallet.ui.TextTiny
import com.mangala.wallet.ui.TextTitle4
import com.mangala.wallet.features.chains.ui.OldFeeOptionItem
import com.mangala.wallet.ui.component.MangalaWalletIconButton
import com.mangala.wallet.ui.utils.collectAsStateMultiplatform
import com.mangala.wallet.utils.ToastFactory
import com.mangala.wallet.utils.analytics.MangalaAnalytics
import com.mangala.wallet.utils.capitalizeFirstLetter
import dev.icerock.moko.resources.compose.fontFamilyResource
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.desc.ResourceFormatted
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc
import io.ktor.http.Url
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import kotlin.jvm.Transient

abstract class BaseConfirmTransactionScreen(
    private val url: String,
    private val accountId: String,
    private val coinDecimals: Long,
    private val chainId: Long,
    private val callbackId: Long,
    private val value: String,
    private val recipient: String,
    private val payload: String,
    private val nonce: Long,
    private val isLegacyTransaction: Boolean,
    @Transient private val onSignMessageFail: () -> Unit,
    @Transient private val onSignMessageSuccessful: (callbackId: Long, signHex: String) -> Unit,
    @Transient private val onConfirm: (isOpenPin: Boolean) -> Unit,
    @Transient private val onDecline: () -> Unit
) : Screen, KoinComponent {

    abstract fun onClickConfirm(navigator: Navigator, onConfirmComplete: () -> Unit, viewModel: ConfirmTransactionViewModel)

    abstract val analyticsClassName: String

    @Composable
    override fun Content() {
        LifecycleEffect(onStarted = {
            MangalaAnalytics.trackScreenView(
                MangalaAnalytics.Screens.BROWSER_CONFIRM_TRANSACTION,
                analyticsClassName
            )
        })

//        BottomSheetNavigator {
        val toastFactory = get<ToastFactory>()
            val viewModel: ConfirmTransactionViewModel = getScreenModel()
            val navigator = LocalNavigator.currentOrThrow
            val globalNavigator = LocalGlobalNavigator.current
//            val bottomSheetNavigator = LocalBottomSheetNavigator.current

            LaunchedEffect(true) {
                viewModel.calTransactionFee(
                    accountId,
                    recipient,
                    payload,
                    value,
                    coinDecimals
                )
            }

            val toastMessage = MR.strings.message_sign_transaction_fail.desc().localized()
            val signTransactionResult = viewModel.signTransactionResult.collectAsStateMultiplatform().value
            signTransactionResult?.let {
                if(it.result.isNullOrEmpty()){
                    toastFactory.show(toastMessage)
                    onSignMessageFail()
                }else{
                    onSignMessageSuccessful(callbackId, it.result ?: "")
                }
            }
            val dapp = Url(url)

            ConfirmTransactionContent(
                viewModel = viewModel,
                url = dapp.host,
                onClickTransactionOption = {
                    val uiState: ConfirmTransactionScreenUiState = viewModel.uiState.value
                    navigator.push(
                        TransactionFeeScreen(
                            transactionFeeOptions = (uiState as? ConfirmTransactionScreenUiState.Data)?.transactionFeeOptions ?: emptyList(),
                            onFeeSelected = {
                                navigator.pop()
                                viewModel.onTransactionFeeSelected(it)
                            },
                            onBackClicked = {
                                navigator.pop()
                            }
                        )
                    )
                },
                onConfirm = {
                    onClickConfirm(navigator, onConfirmComplete = {
                        onConfirm(false)
                    }, viewModel) // TODO: Refactor so we don't have to pass viewModel here
                    onConfirm(true)
                },
                onDecline = {
                    onDecline()
                },
                onTransactionSuccess = {
//                    onSignMessageSuccessful(callbackId, it)
                }
            )

        }
//    }
}

@Composable
fun ConfirmTransactionContent(
    viewModel: ConfirmTransactionViewModel,
    url: String,
    onClickTransactionOption: (FeeOptionUiModel) -> Unit,
    onConfirm: () -> Unit,
    onDecline: () -> Unit,
    onTransactionSuccess: (txHash: String) -> Unit
) {
//    viewModel.estimateGas(recipient, payload, value)

    val uiState: ConfirmTransactionScreenUiState = viewModel.uiState.collectAsStateMultiplatform().value

    val txHash = (uiState as? BaseEvmStep4VerifyAndSendScreenUiState.Data)?.txHash

//    LaunchedEffect(txHash) {
//        txHash?.let {
//            onTransactionSuccess(it)
//        }
//    }

    val title = MR.strings.title_confirm_transaction_dapp.desc().localized()

    val message = StringDesc.ResourceFormatted(MR.strings.message_refer_detail, url).localized()

    val estimateGasFail = MR.strings.message_estimate_gas_fail.desc().localized()
    val decline = MR.strings.decline.desc().localized()
    val on = MR.strings.message_receive_token_on.desc().localized().capitalizeFirstLetter()
    val confirm = MR.strings.confirm.desc().localized()
    val from = MR.strings.message_from.desc().localized()
    val fee = MR.strings.fee.desc().localized()
    (uiState as? ConfirmTransactionScreenUiState.Data)?.let {
        Box(modifier = Modifier.background(color = MaterialTheme.colors.primary).padding(16.dp)) {
            Column {
                Spacer(modifier = Modifier.height(Spacing.TINY))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TextTitle4(
                        text = title,
                    )
                    MangalaWalletIconButton(
                        icon = MangalaWalletPack.Clear,
                        modifier = Modifier.size(36.dp),
                        onClick = onDecline
                    )
                }

                Text(
                    text = url,
                    color = MaterialTheme.colors.onSecondary,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = FontType.TITLE_3,
                    fontFamily = fontFamilyResource(MR.fonts.sfpro),
                )

                TextNormal(text = "$on ${viewModel.blockchainType.name}")

                Spacer(modifier = Modifier.height(Spacing.BASE))

                TextNormal(
                    text = "$from ${uiState.account.name}",
                )
                TextDescription2(
                    text = uiState?.account?.bip44Address ?: "",
                )

                Spacer(modifier = Modifier.height(Spacing.BASE))

                TextNormal(message)

                Spacer(modifier = Modifier.height(16.dp))

                TextTiny(
                    "$fee:"
                )

                if (uiState.estimateGasErrorVisible) {
                    Text(
                        estimateGasFail,
                        color = Colors.coral
                    )
                } else {
                    uiState.selectedTransactionFee?.let {
                        OldFeeOptionItem(it, onClickTransactionOption, Modifier.fillMaxWidth())
                    }
                }

                Spacer(modifier = Modifier.height(Spacing.LARGE))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier.weight(1f).clickable {
                            onDecline()
                        },
                        contentAlignment = Alignment.Center
                    ) {
                        TextNormal(text = decline)
                    }
                    Button(
                        onClick = { onConfirm() },
                        modifier = Modifier
                            .weight(2f)
                            .padding(start = 2.dp),
                        colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.onSecondary)
                    ) {
                        TextNormal(
                            text = confirm,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}