package com.mangala.wallet.features.send_base.step4.evm

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.lifecycle.LifecycleEffect
import cafe.adriel.voyager.core.screen.Screen
import com.mangala.wallet.features.chains.ui.EvmFeeOptionUiModel
import com.mangala.wallet.features.chains.ui.FeeOptionUiModel
import com.mangala.wallet.features.send_base.step4.VerifyAndSendScreen
import com.mangala.wallet.ui.component.TotalTransactionValue
import com.mangala.wallet.features.chains.ui.TransactionSummary
import com.mangala.wallet.ui.utils.collectAsStateMultiplatform
import com.mangala.wallet.utils.analytics.MangalaAnalytics

abstract class BaseEvmStep4VerifyAndSendScreen<T: BaseEvmStep4VerifyAndSendScreenModel>(
    protected val contactId: Long?,
    protected val recipientAddress: String?,
    protected val blockchainUid: String?, // TODO: Require blockchainUid to be non-null
    protected val tokenId: Long,
    protected val amount: String,
    protected val accountId: String,
) : Screen {

    @Composable
    abstract fun createScreenModel(): T

    abstract val analyticsClassName: String

    @Composable
    fun Body(
        viewModel: T,
        confirmationItems: @Composable (BaseEvmStep4VerifyAndSendScreenUiState.Data) -> Unit,
        mainButton: @Composable () -> Unit,
        onClickBack: () -> Unit,
        onClickTransactionOption: (FeeOptionUiModel) -> Unit,
        onTransactionSuccess: () -> Unit,
        onClickConfirm: () -> Unit,
    ) {
        LifecycleEffect(
            onStarted = {
                MangalaAnalytics.trackScreenView(
                    MangalaAnalytics.Screens.SEND_TOKEN_VERIFY_AND_SEND_EVM,
                    analyticsClassName
                )
            }
        )

        val uiState = viewModel.uiState.collectAsStateMultiplatform().value
        val txHash = (uiState as? BaseEvmStep4VerifyAndSendScreenUiState.Data)?.txHash

        LaunchedEffect(txHash) {
            txHash?.let {
                onTransactionSuccess()
            }
        }

        (uiState as? BaseEvmStep4VerifyAndSendScreenUiState.Data)?.let {
            VerifyAndSendScreen(
                onClickBack = onClickBack,
                confirmationItems = {
                    confirmationItems(uiState)
                },
                transactionSummary = {
                    TransactionSummary(
                        modifier = Modifier.fillMaxWidth(),
                        uiModel = uiState.selectedTransactionFee,
                        onFeeSelected = { onClickTransactionOption(it) },
                    )
                },
                totalTransactionValue = {
                    TotalTransactionValue(
                        uiState.totalTransactionFiatValue
                    )
                },
                mainButton = mainButton
            )
        }
    }
}