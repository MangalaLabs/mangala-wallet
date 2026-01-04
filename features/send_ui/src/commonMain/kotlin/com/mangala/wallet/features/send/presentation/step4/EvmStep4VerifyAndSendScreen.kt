package com.mangala.wallet.features.send.presentation.step4

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.registry.ScreenRegistry
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.bottomSheet.LocalBottomSheetNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.mangala.wallet.features.chains.evmcompatible.model.Address
import com.mangala.wallet.features.chains.evmcompatible.model.SignedTransactionResponse
import com.mangala.wallet.features.send_base.step4.evm.BaseEvmStep4VerifyAndSendScreen
import com.mangala.wallet.features.send_base.step4.evm.BaseEvmStep4VerifyAndSendScreenUiState
import com.mangala.wallet.features.send_base.step4.toSignTransactionRequestArgs
import com.mangala.wallet.features.send_base.step5.Step5SendSuccessScreen
import com.mangala.wallet.features.send_base.transactionfee.TransactionFeeScreen
import com.mangala.wallet.model.blockchain.BlockchainType
import com.mangala.wallet.common.mokoresources.Colors
import com.mangala.wallet.common.mokoresources.CornerRadius
import com.mangala.wallet.common.mokoresources.FontType
import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.common.mokoresources.Spacing
import com.mangala.wallet.ui.AddressConfirmationItem
import com.mangala.wallet.ui.ButtonNormal
import com.mangala.wallet.ui.ConfirmationItem
import com.mangala.wallet.ui.ConfirmationLocalItem
import com.mangala.wallet.ui.SharedScreen
import com.mangala.wallet.ui.TextNormal
import com.mangala.wallet.ui.component.MangalaBottomSheetNavigator
import com.mangala.wallet.ui.component.MaxWidthColumn
import com.mangala.wallet.ui.placeholder.mangalaWalletPlaceholder
import com.mangala.wallet.ui.utils.collectAsStateMultiplatform
import com.mangala.wallet.ui.utils.toggle
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.desc.desc
import org.koin.core.parameter.parametersOf

class EvmStep4VerifyAndSendScreen(
    contactId: Long?,
    recipientAddress: String?,
    blockchainUid: String?,
    tokenId: String,
    amount: String,
    accountId: String,
) : BaseEvmStep4VerifyAndSendScreen<EvmStep4VerifyAndSendScreenModel>(contactId, recipientAddress, blockchainUid, tokenId, amount, accountId) {

    override val analyticsClassName: String = EvmStep4VerifyAndSendScreen::class.simpleName.orEmpty()

    @Composable
    override fun createScreenModel(): EvmStep4VerifyAndSendScreenModel = getScreenModel(parameters = {
        parametersOf(
            contactId,
            blockchainUid.orEmpty(),
            tokenId,
            recipientAddress.orEmpty(),
            amount,
            accountId
        )
    })

    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow

        MangalaBottomSheetNavigator(
            sheetShape = RoundedCornerShape(
                topStart = CornerRadius.BottomSheet,
                topEnd = CornerRadius.BottomSheet
            )
        ) {
            val bottomSheetNavigator = LocalBottomSheetNavigator.current

            val screenModel = createScreenModel()

            val uiState = screenModel.uiState.collectAsStateMultiplatform().value

            val isNetworkConfirmed = remember { mutableStateOf(false) }
            val isAddressConfirmed = remember { mutableStateOf(false) }
            val isAmountConfirmed = remember { mutableStateOf(false) }
            val scannedSignedTransaction = remember { mutableStateOf(false) }
            val isConfirmButtonEnabled = remember(
                isNetworkConfirmed,
                isAmountConfirmed,
                isAddressConfirmed,
                (uiState as? BaseEvmStep4VerifyAndSendScreenUiState.Data)?.selectedTransactionFee,
                scannedSignedTransaction
            ) {
                derivedStateOf {
                    isNetworkConfirmed.value
                        && isAddressConfirmed.value
                        && isAmountConfirmed.value
                        && scannedSignedTransaction.value
                        && (uiState as? BaseEvmStep4VerifyAndSendScreenUiState.Data)?.selectedTransactionFee != null
                }
            }

            LaunchedEffect((uiState as? BaseEvmStep4VerifyAndSendScreenUiState.Data)?.selectedTransactionFee) {
                // If there's a change in gas price then resets scannedSignedTransaction
                if (uiState is BaseEvmStep4VerifyAndSendScreenUiState.Data) {
                    scannedSignedTransaction.value = false
                }
            }

            Body(
                screenModel,
                onClickBack = {
                    navigator.pop()
                },
                onClickTransactionOption = {
                    bottomSheetNavigator.show(
                        TransactionFeeScreen(
                            transactionFeeOptions = (uiState as? BaseEvmStep4VerifyAndSendScreenUiState.Data)?.transactionFeeOptions ?: emptyList(),
                            onFeeSelected = {
                                bottomSheetNavigator.hide()
                                screenModel.onTransactionFeeSelected(it)
                            },
                            onBackClicked = { bottomSheetNavigator.hide() }
                        )
                    )
                },
                onTransactionSuccess = {
                    bottomSheetNavigator.hide()
                    screenModel.getTxHash()?.let {
                        navigator.push(Step5SendSuccessScreen(txHash = it, blockchainUid = blockchainUid.orEmpty()))
                        screenModel.onConsumeTxHash() // prevent double navigation
                    }
                },
                confirmationItems = {
                    ConfirmationItems(
                        uiState,
                        isNetworkConfirmed.value,
                        { isNetworkConfirmed.toggle() },
                        isAddressConfirmed.value,
                        { isAddressConfirmed.toggle() },
                        isAmountConfirmed.value,
                        { isAmountConfirmed.toggle() },
                        isSignedTransaction = scannedSignedTransaction.value,
                        signTransactionEnabled = (uiState as? BaseEvmStep4VerifyAndSendScreenUiState.Data)?.selectedTransactionFee != null,
                        onClickSignTransaction = {
                            screenModel.stopGasRefreshJob() // Cancels refresh job so users won't get a different gas price than what they signed
                            val signTransactionRequest = screenModel.getSignTransactionRequest()

                            val screen = ScreenRegistry.get(
                                SharedScreen.TransactionQrScreen(
                                    signTransactionRequest.toSignTransactionRequestArgs(),
                                    onScannedSignedTransaction = { v, r, s ->
                                        bottomSheetNavigator.hide()
                                        scannedSignedTransaction.value = true
                                        screenModel.onScannedSignedTransaction(
                                            SignedTransactionResponse(
                                                signTransactionRequest,
                                                v, r, s
                                            )
                                        )
                                    },
                                    onDispose = {
                                        if (!scannedSignedTransaction.value) {
                                            screenModel.restartGasRefreshJob()
                                        }
                                    }
                                )
                            )
                            bottomSheetNavigator.show(screen)
                        }
                    )
                },
                mainButton = {
                    ButtonNormal(
                        text = MR.strings.button_verify_transaction_confirm_and_send.desc().localized(),
                        onClick = {
                            screenModel.sendSignedTransaction()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        fontSize = FontType.REGULAR,
                        buttonModifier = Modifier.padding(vertical = 10.dp),
                        enabled = isConfirmButtonEnabled.value
                    )
                },
                onClickConfirm = {}
            )
        }
    }


    @Composable
    fun ConfirmationItems(
        uiState: BaseEvmStep4VerifyAndSendScreenUiState,
        isNetworkConfirmed: Boolean,
        onUpdateNetworkConfirmed: () -> Unit,
        isAddressConfirmed: Boolean,
        onUpdateAddressConfirmed: () -> Unit,
        isAmountConfirmed: Boolean,
        onUpdateAmountConfirmed: () -> Unit,
        signTransactionEnabled: Boolean,
        isSignedTransaction: Boolean,
        onClickSignTransaction: () -> Unit
    ) {
        (uiState as? BaseEvmStep4VerifyAndSendScreenUiState.Data)?.let {
            MaxWidthColumn(verticalArrangement = Arrangement.spacedBy(Spacing.XSMALL)) {
                ConfirmationLocalItem(
                    imageUrl = BlockchainType.fromUid(blockchainUid.orEmpty()).localImage,
                    label = MR.strings.label_verify_transaction_check_network.desc()
                        .localized(),
                    value =  BlockchainType.fromUid(blockchainUid.orEmpty()).name,
                    isChecked = isNetworkConfirmed,
                    onClick = { onUpdateNetworkConfirmed() }
                )
                AddressConfirmationItem(
                    address = Address(recipientAddress.orEmpty()).eip55,
                    label = MR.strings.label_verify_transaction_check_address.desc()
                        .localized(),
                    value = uiState.recipient,
                    subtitleValue = uiState.addressCompact,
                    isChecked = isAddressConfirmed,
                    onClick = { onUpdateAddressConfirmed() },
                )
                ConfirmationItem(
                    imageUrl = uiState.selectedToken.logoUrl,
                    label = MR.strings.label_verify_transaction_check_amount.desc()
                        .localized(),
                    value = "$amount ${uiState.selectedToken.contractSymbol}",
                    subtitleValue = uiState.tokenFiatValue,
                    isChecked = isAmountConfirmed,
                    onClick = { onUpdateAmountConfirmed() }
                )
                ConfirmationItem(
                    imageUrl = null,
                    label = "Sign transaction",
                    value = {
                        if (signTransactionEnabled) {
                            TextNormal("Received signature from the cold wallet app", color = Colors.main1Text, fontWeight = FontWeight.W500)
                        } else {
                            Box(Modifier.width(50.dp).mangalaWalletPlaceholder(visible = true))
                        }
                    },
                    isChecked = isSignedTransaction,
                    onClick = { if (signTransactionEnabled) onClickSignTransaction() }
                )
            }
        }
    }
}