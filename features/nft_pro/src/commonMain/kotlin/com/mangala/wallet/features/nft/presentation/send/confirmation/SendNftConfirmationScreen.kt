package com.mangala.wallet.features.nft.presentation.send.confirmation

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
import com.mangala.wallet.features.chains.evmcompatible.model.Address
import com.mangala.wallet.features.nft_base.presentation.send.confirmation.BaseSendNftConfirmationScreen
import com.mangala.wallet.features.nft_base.presentation.send.confirmation.SendNftConfirmationScreenUiState
import com.mangala.wallet.common.mokoresources.CornerRadius
import com.mangala.wallet.common.mokoresources.FontType
import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.common.mokoresources.Spacing
import com.mangala.wallet.features.chains.ui.EvmFeeOptionUiModel
import com.mangala.wallet.features.send_base.transactionfee.TransactionFeeScreen
import com.mangala.wallet.ui.AddressConfirmationItem
import com.mangala.wallet.ui.ButtonNormal
import com.mangala.wallet.ui.ConfirmationLocalItem
import com.mangala.wallet.ui.SharedScreen
import com.mangala.wallet.ui.component.MangalaBottomSheetNavigator
import com.mangala.wallet.ui.component.VerticalSpacer
import com.mangala.wallet.ui.utils.collectAsStateMultiplatform
import com.mangala.wallet.ui.utils.toggle
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.desc.desc
import org.koin.core.parameter.parametersOf

internal class SendNftConfirmationScreen(
    blockchainUid: String,
    contactId: Long?,
    accountId: String,
    recipientAddress: String,
    collectionContractAddress: String,
    tokenId: String
): BaseSendNftConfirmationScreen<SendNftConfirmationScreenModel>(
    blockchainUid, contactId, accountId, recipientAddress, collectionContractAddress, tokenId
) {

    override val analyticsClassName: String = SendNftConfirmationScreen::class.simpleName.orEmpty()

    @Composable
    override fun createScreenModel(): SendNftConfirmationScreenModel = getScreenModel(
        parameters = {
            parametersOf(
                blockchainUid,
                accountId,
                recipientAddress,
                collectionContractAddress,
                tokenId,
                contactId
            )
        }
    )

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
            val isConfirmButtonEnabled = remember(
                isNetworkConfirmed,
                isAddressConfirmed,
                (uiState as? SendNftConfirmationScreenUiState.Data)?.selectedTransactionFee,
            ) {
                derivedStateOf {
                    isNetworkConfirmed.value
                            && isAddressConfirmed.value
                            && (uiState as? SendNftConfirmationScreenUiState.Data)?.selectedTransactionFee != null
                }
            }

            SendNftConfirmationScreen(
                screenModel,
                onClickTransactionOption = {
                    val transactionFeeOptions =
                        (uiState as? SendNftConfirmationScreenUiState.Data)?.transactionFeeOptions
                            ?: emptyList()

                    val screen =
                        TransactionFeeScreen(
                            transactionFeeOptions = transactionFeeOptions,
                            onFeeSelected = {
                                (it as? EvmFeeOptionUiModel)?.let {
                                    bottomSheetNavigator.hide()
                                    screenModel.onTransactionFeeSelected(it)
                                }
                            },
                            onBackClicked = {
                                bottomSheetNavigator.hide()
                            }
                        )

                    bottomSheetNavigator.show(screen)
                },
                onTransactionSuccess = {
                    bottomSheetNavigator.hide()
                    screenModel.getTxHash()?.let {
                        val blockchainUid =
                            (uiState as? SendNftConfirmationScreenUiState.Data)?.blockchainType?.uid.orEmpty()
                        // Need to use GlobalNavigator because we have bottom navigation
                        val screen = ScreenRegistry.get(
                            SharedScreen.Step5SendSuccessScreen(
                                txHash = it,
                                blockchainUid = blockchainUid
                            )
                        )
                        navigator.push(screen)
                        screenModel.onConsumeTxHash() // prevent double navigation
                    }
                },
                onBackClicked = {
                    navigator.pop()
                },
                confirmationItems = {
                    ConfirmationItems(
                        it,
                        isNetworkConfirmed = isNetworkConfirmed.value,
                        onUpdateNetworkConfirmed = { isNetworkConfirmed.toggle() },
                        isAddressConfirmed = isAddressConfirmed.value,
                        onUpdateAddressConfirmed = { isAddressConfirmed.toggle() }
                    )
                },
                mainButton = {
                    ButtonNormal(
                        text = MR.strings.button_verify_transaction_confirm_and_send.desc().localized(),
                        onClick = {
                            val unlockPinScreen = ScreenRegistry.get(
                                SharedScreen.UnlockPinScreen(
                                    SharedScreen.UnlockPinScreen.VERIFY_SEND_TRANSACTION,
                                    onUnlockSuccess = {
                                        screenModel.onAuthenticationSuccess()
                                    },
                                    antelopeAccountName = null
                                )
                            )
                            bottomSheetNavigator.show(unlockPinScreen)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        fontSize = FontType.REGULAR,
                        buttonModifier = Modifier.padding(vertical = 10.dp),
                        enabled = isConfirmButtonEnabled.value
                    )
                }
            )
        }
    }

    @Composable
    fun ConfirmationItems(
        uiState: SendNftConfirmationScreenUiState.Data,
        isNetworkConfirmed: Boolean,
        onUpdateNetworkConfirmed: () -> Unit,
        isAddressConfirmed: Boolean,
        onUpdateAddressConfirmed: () -> Unit
    ) {
        ConfirmationLocalItem(
            imageUrl = uiState.blockchainType.localImage,
            label = MR.strings.label_verify_transaction_check_network.desc()
                .localized(),
            value = uiState.blockchainType.name,
            isChecked = isNetworkConfirmed,
            onClick = onUpdateNetworkConfirmed
        )
        VerticalSpacer(Spacing.XSMALL)
        AddressConfirmationItem(
            address = Address(uiState.recipientAddress).eip55,
            label = MR.strings.label_verify_transaction_check_address.desc()
                .localized(),
            value = uiState.recipient,
            subtitleValue = uiState.addressCompact,
            isChecked = isAddressConfirmed,
            onClick = onUpdateAddressConfirmed,
        )
    }
}