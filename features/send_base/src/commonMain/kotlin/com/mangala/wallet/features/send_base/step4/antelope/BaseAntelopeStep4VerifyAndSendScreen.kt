package com.mangala.wallet.features.send_base.step4.antelope

import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.lifecycle.LifecycleEffect
import cafe.adriel.voyager.core.screen.Screen
import com.mangala.wallet.features.chains.antelope_base.presentation.ui.AntelopeResourceProviderFeeDialog
import com.mangala.wallet.features.send_base.step4.VerifyAndSendScreen
import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.ui.MangalaCommonDialog
import com.mangala.wallet.ui.component.MangalaWalletTopBarCenteredTitle
import com.mangala.wallet.ui.component.MaxSizeColumn
import com.mangala.wallet.ui.component.TotalTransactionValue
import com.mangala.wallet.ui.theme.mangalaColors
import com.mangala.wallet.ui.utils.collectAsStateMultiplatform
import com.mangala.wallet.ui.utils.resolve
import com.mangala.wallet.utils.analytics.MangalaAnalytics
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.desc.desc

abstract class BaseAntelopeStep4VerifyAndSendScreen<T : BaseAntelopeStep4VerifyAndSendScreenModel>(
    protected val contactId: Long?,
    protected val senderAccount: String,
    protected val toAccount: String,
    protected val blockchainUid: String?,
    protected val tokenKey: String,
    protected val amount: String
) : Screen {

    abstract val analyticsClassName: String

    @Composable
    abstract fun createScreenModel(): T

    @Composable
    fun Body(
        viewModel: T,
        confirmationItems: @Composable (BaseAntelopeStep4VerifyAndSendScreenUiState.Data) -> Unit,
        mainButton: @Composable () -> Unit,
        onClickBack: () -> Unit,
        onTransactionSuccess: () -> Unit,
        onConfirmResourceProviderFee: () -> Unit,
        onDismissTransactionFeeBreakdown: () -> Unit,
        onBuyRam: (TransactionError) -> Unit,
        onPowerUp: (TransactionError) -> Unit
    ) {
        LifecycleEffect(onStarted = {
            MangalaAnalytics.trackScreenView(
                MangalaAnalytics.Screens.SEND_TOKEN_VERIFY_AND_SEND_ANTELOPE,
                analyticsClassName
            )
        })

        val uiState = viewModel.uiState.collectAsStateMultiplatform().value
        val txHash = (uiState as? BaseAntelopeStep4VerifyAndSendScreenUiState.Data)?.txHash

        LaunchedEffect(txHash) {
            txHash?.let {
                onTransactionSuccess()
            }
        }

        if ((uiState as? BaseAntelopeStep4VerifyAndSendScreenUiState.Data)?.resourceRequiredBreakdown != null) {
            val uiModel = (uiState as? BaseAntelopeStep4VerifyAndSendScreenUiState.Data)

            AntelopeResourceProviderFeeDialog(
                feeBreakdown = uiModel?.resourceRequiredBreakdown,
                resourceRequiredTotal = uiModel?.resourceRequiredTotal,
                onClick = {
                    onConfirmResourceProviderFee()
                },
                onDismiss = {
                    onDismissTransactionFeeBreakdown()
                }
            )
        }

        when (uiState) {
            is BaseAntelopeStep4VerifyAndSendScreenUiState.Data -> {
                uiState.errorDialog?.let { error ->
                    TransactionErrorDialog(
                        error = error,
                        onDismiss = { viewModel.onDismissErrorDialog() },
                        onBuyRam = {
                            viewModel.onDismissErrorDialog()
                            onBuyRam(error)
                        },
                        onPowerUp = {
                            viewModel.onDismissErrorDialog()
                            onPowerUp(error)
                        }
                    )
                }

                VerifyAndSendScreen(
                    onClickBack = onClickBack,
                    confirmationItems = {
                        confirmationItems(uiState)
                    },
                    transactionSummary = {
//                    TransactionSummary(
//                        modifier = Modifier.fillMaxWidth(),
//                        uiModel = uiState.selectedTransactionFee,
//                        onFeeSelected = { onClickTransactionOption(it) },
//                    )
                    },
                    totalTransactionValue = {
                        TotalTransactionValue(
                            uiState.totalTransactionFiatValue
                        )
                    },
                    mainButton = mainButton,
                    error = uiState.error,
                    isLoading = uiState.isLoading
                )
            }

            is BaseAntelopeStep4VerifyAndSendScreenUiState.Error -> {
                MaxSizeColumn(
                    modifier = Modifier
                        .safeDrawingPadding()
                ) {
                    MangalaWalletTopBarCenteredTitle(
                        title = MR.strings.title_verify_transaction.desc().localized(),
                        onBackClicked = onClickBack
                    )
                    Text(
                        uiState.message.resolve(),
                        color = MaterialTheme.mangalaColors.buttonDestructiveContainer
                    )
                }
            }

            BaseAntelopeStep4VerifyAndSendScreenUiState.Loading -> {

            }
        }
    }
}

@Composable
private fun TransactionErrorDialog(
    error: TransactionError,
    onDismiss: () -> Unit,
    onBuyRam: () -> Unit,
    onPowerUp: () -> Unit
) {
    when (error.type) {
        TransactionErrorType.INSUFFICIENT_RAM -> {
            MangalaCommonDialog(
                title = MR.strings.title_transaction_error.desc().localized(),
                message = error.message.resolve(),
                positiveButtonText = MR.strings.action_buy_ram.desc().localized(),
                negativeButtonText = MR.strings.all_cancel.desc().localized(),
                onPositiveClick = onBuyRam,
                onNegativeClick = onDismiss
            )
        }
        TransactionErrorType.INSUFFICIENT_CPU,
        TransactionErrorType.INSUFFICIENT_NET -> {
            MangalaCommonDialog(
                title = MR.strings.title_transaction_error.desc().localized(),
                message = error.message.resolve(),
                positiveButtonText = MR.strings.action_power_up.desc().localized(),
                negativeButtonText = MR.strings.all_cancel.desc().localized(),
                onPositiveClick = onPowerUp,
                onNegativeClick = onDismiss
            )
        }
        TransactionErrorType.GENERIC -> {
            MangalaCommonDialog(
                title = MR.strings.title_transaction_error.desc().localized(),
                message = error.message.resolve(),
                actionButtonText = MR.strings.all_ok.desc().localized(),
                onClickActionButton = onDismiss
            )
        }
    }
}