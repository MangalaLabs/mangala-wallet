package com.mangala.wallet.features.send.presentation.step4.bitcoin

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.registry.ScreenRegistry
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.bottomSheet.LocalBottomSheetNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.mangala.wallet.features.send.presentation.step4.ui.ConfirmationItems
import com.mangala.wallet.features.send_base.step4.VerifyAndSendScreen
import com.mangala.wallet.features.send_base.step5.Step5SendSuccessScreen
import com.mangala.wallet.features.send_base.transactionfee.TransactionFeeScreen
import com.mangala.wallet.model.blockchain.BlockchainType
import com.mangala.wallet.common.mokoresources.CornerRadius
import com.mangala.wallet.common.mokoresources.FontType
import com.mangala.wallet.features.chains.ui.BitcoinFeeOptionUiModel
import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.ui.ButtonNormal
import com.mangala.wallet.ui.SharedScreen
import com.mangala.wallet.ui.component.MangalaBottomSheetNavigator
import com.mangala.wallet.ui.component.TotalTransactionValue
import com.mangala.wallet.features.chains.ui.TransactionSummary
import com.mangala.wallet.ui.utils.collectAsStateMultiplatform
import com.mangala.wallet.ui.utils.screenmodel.BaseScreen
import com.mangala.wallet.ui.utils.toggle
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.desc.desc
import org.koin.core.parameter.parametersOf
import kotlin.experimental.ExperimentalObjCRefinement
import kotlin.native.HiddenFromObjC
import com.mangala.wallet.features.chains.ui.FeeOptionUiModel

@OptIn(ExperimentalObjCRefinement::class)
@HiddenFromObjC
class BitcoinStep4VerifyAndSendScreen(
    private val contactId: Long?,
    private val recipientAddress: String?,
    private val blockchainUid: String?,
    private val tokenId: String,
    private val amount: String,
    private val accountId: String,
) : BaseScreen<BitcoinStep4VerifyAndSendScreenModel>() {

    override val screenName: String = "SEND_TOKEN_VERIFY_AND_SEND_BITCOIN"
    override val screenClassName: String = BitcoinStep4VerifyAndSendScreen::class.simpleName.orEmpty()

    override val isBottomBarVisible: Boolean = false

    @Composable
    override fun createScreenModel(): BitcoinStep4VerifyAndSendScreenModel = getScreenModel(parameters = {
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
    override fun ScreenContent(screenModel: BitcoinStep4VerifyAndSendScreenModel) {
        val navigator = LocalNavigator.currentOrThrow

        MangalaBottomSheetNavigator(
            sheetShape = RoundedCornerShape(
                topStart = CornerRadius.BottomSheet,
                topEnd = CornerRadius.BottomSheet
            )
        ) {
            val bottomSheetNavigator = LocalBottomSheetNavigator.current

            val uiState = screenModel.uiState.collectAsStateMultiplatform().value

            val isNetworkConfirmed = remember { mutableStateOf(false) }
            val isAddressConfirmed = remember { mutableStateOf(false) }
            val isAmountConfirmed = remember { mutableStateOf(false) }
            val isConfirmButtonEnabled = remember(
                isNetworkConfirmed,
                isAmountConfirmed,
                isAddressConfirmed,
                (uiState as? BitcoinStep4VerifyAndSendScreenUiState.Data)?.selectedTransactionFee,
            ) {
                derivedStateOf {
                    isNetworkConfirmed.value
                            && isAddressConfirmed.value
                            && isAmountConfirmed.value
                            && (uiState as? BitcoinStep4VerifyAndSendScreenUiState.Data)?.selectedTransactionFee != null
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
                            transactionFeeOptions = (uiState as? BitcoinStep4VerifyAndSendScreenUiState.Data)?.transactionFeeOptions ?: emptyList(),
                            onFeeSelected = {
                                (it as? BitcoinFeeOptionUiModel)?.let {
                                    bottomSheetNavigator.hide()
                                    screenModel.onTransactionFeeSelected(it)
                                }
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
                    (uiState as? BitcoinStep4VerifyAndSendScreenUiState.Data)?.let {
                        ConfirmationItems(
                            blockchainType = BlockchainType.fromUid(blockchainUid.orEmpty()),
                            recipientName = uiState.recipient,
                            contactAddress = recipientAddress.orEmpty(),
                            addressCompact = uiState.addressCompact,
                            assetLogoUrl = uiState.selectedToken.logoUrl,
                            symbol = uiState.selectedToken.contractSymbol,
                            fiatValue = uiState.tokenFiatValue,
                            amount = amount,
                            addressConfirmationLabel = MR.strings.label_verify_transaction_check_address.desc()
                                .localized(),
                            isNetworkConfirmed = isNetworkConfirmed.value,
                            onUpdateNetworkConfirmed = { isNetworkConfirmed.toggle() },
                            isAddressConfirmed = isAddressConfirmed.value,
                            onUpdateAddressConfirmed = { isAddressConfirmed.toggle() },
                            isAmountConfirmed = isAmountConfirmed.value,
                            onUpdateAmountConfirmed = { isAmountConfirmed.toggle() }
                        )
                    }
                },
                isLoading = false
            )
        }
    }

    @Composable
    private fun Body(
        screenModel: BitcoinStep4VerifyAndSendScreenModel,
        confirmationItems: @Composable (BitcoinStep4VerifyAndSendScreenUiState.Data) -> Unit,
        onClickBack: () -> Unit,
        onClickTransactionOption: (FeeOptionUiModel) -> Unit,
        onTransactionSuccess: () -> Unit,
        isLoading: Boolean
    ) {
        val bottomSheetNavigator = LocalBottomSheetNavigator.current

        val uiState = screenModel.uiState.collectAsStateMultiplatform().value
        val txHash = (uiState as? BitcoinStep4VerifyAndSendScreenUiState.Data)?.txHash

        // Handle navigation when transaction is successful
        if (txHash != null) {
            onTransactionSuccess()
        }

        (uiState as? BitcoinStep4VerifyAndSendScreenUiState.Data)?.let {
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
                mainButton = {
                    ButtonNormal(
                        text = MR.strings.button_verify_transaction_confirm_and_send.desc().localized(),
                        onClick = {
                            val unlockPinScreen = ScreenRegistry.get(
                                SharedScreen.UnlockPinScreen(
                                    SharedScreen.UnlockPinScreen.VERIFY_SEND_TRANSACTION,
                                    onUnlockSuccess = screenModel::onAuthenticationSuccess,
                                    antelopeAccountName = null
                                )
                            )

                            bottomSheetNavigator.show(unlockPinScreen)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        fontSize = FontType.REGULAR,
                        buttonModifier = Modifier.padding(vertical = 10.dp),
                        enabled = uiState.selectedTransactionFee != null
                    )
                },
                isLoading = isLoading
            )
        }
    }
}