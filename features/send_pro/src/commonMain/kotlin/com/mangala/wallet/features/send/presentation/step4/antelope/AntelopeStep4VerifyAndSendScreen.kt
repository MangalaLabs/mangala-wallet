package com.mangala.wallet.features.send.presentation.step4.antelope

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.registry.ScreenRegistry
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.bottomSheet.LocalBottomSheetNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.mangala.wallet.common.mokoresources.CornerRadius
import com.mangala.wallet.features.send_base.step4.antelope.BaseAntelopeStep4VerifyAndSendScreen
import com.mangala.wallet.features.send_base.step4.antelope.BaseAntelopeStep4VerifyAndSendScreenUiState
import com.mangala.wallet.features.send_base.step4.antelope.TransactionErrorType
import com.mangala.wallet.features.send_base.step5.Step5SendSuccessScreen
import com.mangala.wallet.model.blockchain.BlockchainType
import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.ui.SharedScreen
import com.mangala.wallet.ui.component.MangalaBottomSheetNavigator
import com.mangala.wallet.ui.component.MangalaGradientButton
import com.mangala.wallet.ui.component.OnboardingGradientBackground
import com.mangala.wallet.ui.utils.collectAsStateMultiplatform
import com.mangala.wallet.ui.utils.toggle
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.desc.desc
import org.koin.core.parameter.parametersOf
import kotlin.experimental.ExperimentalObjCRefinement
import kotlin.native.HiddenFromObjC

@OptIn(ExperimentalObjCRefinement::class)
@HiddenFromObjC
class AntelopeStep4VerifyAndSendScreen(
    contactId: Long?,
    senderAccount: String,
    toAccount: String,
    blockchainUid: String?,
    tokenKey: String,
    amount: String,
    private val memo: String
) : BaseAntelopeStep4VerifyAndSendScreen<AntelopeStep4VerifyAndSendScreenModel>(
    contactId, senderAccount, toAccount, blockchainUid, tokenKey, amount
) {
    override val analyticsClassName: String =
        AntelopeStep4VerifyAndSendScreen::class.simpleName.orEmpty()

    @Composable
    override fun createScreenModel(): AntelopeStep4VerifyAndSendScreenModel {
        return getScreenModel<AntelopeStep4VerifyAndSendScreenModel>(parameters = {
            parametersOf(
                contactId,
                senderAccount,
                toAccount,
                blockchainUid.orEmpty(),
                tokenKey,
                amount,
                memo
            )
        })
    }

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
            val isMemoConfirmed = remember { mutableStateOf(false) }
            val isConfirmButtonEnabled = remember(
                isNetworkConfirmed,
                isAmountConfirmed,
                isAddressConfirmed,
                isMemoConfirmed,
                (uiState as? BaseAntelopeStep4VerifyAndSendScreenUiState.Data)?.isLoading
            ) {
                derivedStateOf {
                    isNetworkConfirmed.value
                            && isAddressConfirmed.value
                            && isAmountConfirmed.value
                            && isMemoConfirmed.value
                            && (uiState as? BaseAntelopeStep4VerifyAndSendScreenUiState.Data)?.isLoading == false
                }
            }

            val unlockPinScreen = ScreenRegistry.get(
                SharedScreen.UnlockPinScreen(
                    SharedScreen.UnlockPinScreen.VERIFY_SEND_TRANSACTION,
                    onUnlockSuccess = {
                        screenModel.onAuthenticationSuccess(senderAccount)
                        navigator.pop()
                    },
                    antelopeAccountName = null
                )
            )

            LaunchedEffect((uiState as? BaseAntelopeStep4VerifyAndSendScreenUiState.Data)?.promptConfirmTransaction) {
                if ((uiState as? BaseAntelopeStep4VerifyAndSendScreenUiState.Data)?.promptConfirmTransaction == true) {
                    navigator.push(unlockPinScreen)
                    screenModel.onPinPromptShown()
                }
            }

            OnboardingGradientBackground {
                Body(
                    screenModel,
                    confirmationItems = {
                        (uiState as? BaseAntelopeStep4VerifyAndSendScreenUiState.Data)?.let {
                            AntelopeConfirmationItems(
                                blockchainType = BlockchainType.fromUid(blockchainUid.orEmpty()),
                                recipientName = uiState.recipient,
                                contactAddress = uiState.contactAddress.orEmpty(),
                                addressCompact = uiState.recipientAccount,
                                assetLogoUrl = uiState.selectedToken.metadata.logo,
                                symbol = uiState.selectedToken.symbol,
                                fiatValue = uiState.tokenFiatValue,
                                amount = amount,
                                memo = memo,
                                isNetworkConfirmed = isNetworkConfirmed.value,
                                onUpdateNetworkConfirmed = { isNetworkConfirmed.toggle() },
                                isAddressConfirmed = isAddressConfirmed.value,
                                onUpdateAddressConfirmed = { isAddressConfirmed.toggle() },
                                isAmountConfirmed = isAmountConfirmed.value,
                                onUpdateAmountConfirmed = { isAmountConfirmed.toggle() },
                                isMemoConfirmed = isMemoConfirmed.value,
                                onUpdateMemoConfirmed = { isMemoConfirmed.toggle() },
                            )
                        }
                    },
                    mainButton = {
                        MangalaGradientButton(
                            label = MR.strings.button_verify_transaction_confirm_and_send.desc()
                                .localized(),
                            onClick = {
                                screenModel.onRequestTransaction()
                            },
                            enabled = isConfirmButtonEnabled.value,
                            modifier = Modifier.fillMaxWidth()
                        )
                    },
                    onClickBack = {
                        navigator.pop()
                    },
                    onTransactionSuccess = {
                        bottomSheetNavigator.hide()
                        screenModel.getTxHash()?.let {
                            navigator.push(
                                Step5SendSuccessScreen(
                                    txHash = it,
                                    blockchainUid = blockchainUid.orEmpty()
                                )
                            )
                            screenModel.onConsumeTxHash() // prevent double navigation
                        }
                    },
                    onConfirmResourceProviderFee = {
                        screenModel.onConfirmResourceProviderFee()
                    },
                    onDismissTransactionFeeBreakdown = {
                        screenModel.onDismissTransactionFeeBreakdown()
                    },
                    onBuyRam = {
                        val buySellRamScreen = ScreenRegistry.get(SharedScreen.BuySellRamScreen(accountName = senderAccount, isBuyRam = true))
                        navigator.push(buySellRamScreen)
                    },
                    onPowerUp = { error ->
                        val isCpu = error.type == TransactionErrorType.INSUFFICIENT_CPU
                        val powerUpScreen = ScreenRegistry.get(SharedScreen.PowerUpScreen(accountName = senderAccount, isCpu = isCpu))
                        navigator.push(powerUpScreen)
                    }
                )
            }
        }
    }
}