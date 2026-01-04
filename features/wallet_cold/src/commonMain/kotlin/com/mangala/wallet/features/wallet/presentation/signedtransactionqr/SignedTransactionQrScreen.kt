package com.mangala.wallet.features.wallet.presentation.signedtransactionqr

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.registry.ScreenRegistry
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.bottomSheet.BottomSheetNavigator
import cafe.adriel.voyager.navigator.bottomSheet.LocalBottomSheetNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.benasher44.uuid.uuid4
import com.ionspin.kotlin.bignum.integer.BigInteger
import com.mangala.wallet.features.chains.evmcompatible.model.Address
import com.mangala.wallet.features.chains.evmcompatible.model.GasPrice
import com.mangala.wallet.features.chains.evmcompatible.model.SignTransactionRequest
import com.mangala.wallet.features.chains.evmcompatible.model.SignTransactionType
import com.mangala.wallet.features.chains.evmcompatible.model.TransactionData
import com.mangala.wallet.model.blockchain.BlockchainType
import com.mangala.wallet.qrcode.ComposeUIWrapper
import com.mangala.wallet.common.mokoresources.Colors
import com.mangala.wallet.common.mokoresources.CornerRadius
import com.mangala.wallet.common.mokoresources.Dimensions
import com.mangala.wallet.common.mokoresources.FontType
import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.common.mokoresources.Spacing
import com.mangala.wallet.ui.AddressConfirmationItem
import com.mangala.wallet.ui.ButtonNormal
import com.mangala.wallet.ui.ConfirmationItem
import com.mangala.wallet.ui.ConfirmationLocalItem
import com.mangala.wallet.ui.SharedScreen
import com.mangala.wallet.ui.TextDescription2
import com.mangala.wallet.ui.component.MangalaBottomSheetNavigator
import com.mangala.wallet.ui.component.MangalaCircularProgressIndicatorFullScreen
import com.mangala.wallet.ui.component.MangalaWalletTopBar
import com.mangala.wallet.ui.component.MaxSizeColumn
import com.mangala.wallet.ui.component.MaxWidthColumn
import com.mangala.wallet.ui.component.MaxWidthRow
import com.mangala.wallet.ui.component.VerticalSpacer
import com.mangala.wallet.ui.utils.collectAsStateMultiplatform
import com.mangala.wallet.ui.utils.screenmodel.BaseScreen
import com.mangala.wallet.ui.utils.toggle
import com.mangala.wallet.utils.analytics.MangalaAnalytics
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.desc.desc
import org.koin.core.parameter.parametersOf
import kotlin.jvm.Transient

class SignedTransactionQrScreen(
    requestId: String,
    walletId: String,
    accountId: String,
    nonce: Long,
    blockchainUid: String,
    fromAddress: String,
    toAddress: String,
    value: BigInteger,
    input: ByteArray,
    legacyGasPrice: Long?,
    maxFeePerGas: Long?,
    maxPriorityFeePerGas: Long?,
    baseFee: Long?,
    gasLimit: Long,
    gasFiatValue: String,
    transactionType: String,
    contactName: String?,
    contactAddress: String?
): BaseScreen<SignedTransactionQrScreenModel>() {

    override val screenName: String = MangalaAnalytics.Screens.SIGNED_TRANSACTION_QR
    override val screenClassName: String = SignedTransactionQrScreen::class.simpleName.orEmpty()
    override val isBottomBarVisible: Boolean
        get() = false

    @Transient
    private val signTransactionRequest = SignTransactionRequest(
        requestId = requestId,
        walletId = walletId,
        accountId = accountId,
        nonce = nonce,
        blockchainType = BlockchainType.fromUid(blockchainUid),
        fromAddress = fromAddress,
        transactionData = TransactionData(
            to = Address(toAddress),
            value = value,
            input = input
        ),
        gasPrice = if (legacyGasPrice != null) {
            GasPrice.Legacy(legacyGasPrice)
        } else {
            GasPrice.Eip1559(
                maxFeePerGas = maxFeePerGas!!,
                maxPriorityFeePerGas = maxPriorityFeePerGas!!,
                baseFee = baseFee!!
            )
        },
        gasLimit = gasLimit,
        gasFiatValue = gasFiatValue,
        transactionType = SignTransactionType.SendCoinOrErc20Token("0.45", "TEST", "$0.0"),
        contactName = contactName,
        contactAddress = contactAddress
    )

    @Composable
    override fun createScreenModel(): SignedTransactionQrScreenModel = getScreenModel(
        parameters = { parametersOf(signTransactionRequest) }
    )

    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    override fun ScreenContent(screenModel: SignedTransactionQrScreenModel) {
        val navigator = LocalNavigator.currentOrThrow
        val uiState = screenModel.uiState.collectAsStateMultiplatform()

        MangalaBottomSheetNavigator(
            sheetShape = RoundedCornerShape(
                topStart = CornerRadius.BottomSheet,
                topEnd = CornerRadius.BottomSheet,
            ),
            sheetBackgroundColor = MaterialTheme.colors.background
        ) {
            val bottomNavigator = LocalBottomSheetNavigator.current

            SignedTransactionQrScreen(
                uiState.value,
                onBackClicked = { navigator.pop() },
                onClickConfirm = {
                    val pinScreen = ScreenRegistry.get(
                        SharedScreen.UnlockPinScreen(
                            SharedScreen.UnlockPinScreen.CONFIRM_DAPP,
                            antelopeAccountName = null,
                            unlockPinCallback = { isCorrectPin ->
                                if (isCorrectPin) {
                                    screenModel.generateSignedTransactionQr()
                                }
                            }
                        )
                    )
                    navigator.push(pinScreen)
                },
                bottomNavigator
            )
        }
    }

    @Composable
    fun SignedTransactionQrScreen(
        uiState: SignedTransactionQrScreenUiState,
        onBackClicked: () -> Unit,
        onClickConfirm: () -> Unit,
        bottomNavigator: BottomSheetNavigator
    ) {
        MaxSizeColumn(
            Modifier
                .background(MaterialTheme.colors.background).verticalScroll(rememberScrollState())

        ) {
            MangalaWalletTopBar(
                text = MR.strings.title_verify_transaction.desc().localized(),
                color = Colors.main1Text,
                fontWeight = FontWeight.W500,
                onBackClicked = onBackClicked
            )
            when (uiState) {
                SignedTransactionQrScreenUiState.Loading -> {
                    MangalaCircularProgressIndicatorFullScreen()
                }

                is SignedTransactionQrScreenUiState.Success -> {


                }

                is SignedTransactionQrScreenUiState.PendingApprove -> {
                    NeedApproveState(uiState, onClickConfirm, bottomNavigator)
                }
            }
        }
    }

    @Composable
    fun ColumnScope.NeedApproveState(
        uiState: SignedTransactionQrScreenUiState.PendingApprove,
        onClickConfirm: () -> Unit,
        bottomNavigator: BottomSheetNavigator
    ) {
        LaunchedEffect(
            key1 = uiState.pendingSignTransactionUiModel.qrCode
        ) {
            if (uiState.pendingSignTransactionUiModel.qrCode.isNotBlank()){
                val screen = ScreenRegistry.get(
                    SharedScreen.ConfirmQrScreen(
                        qrCode = uiState.pendingSignTransactionUiModel.qrCode
                    )
                )
                bottomNavigator.show(screen)
            }
        }

        val isNetworkConfirmed = remember { mutableStateOf(false) }
        val isAddressConfirmed = remember { mutableStateOf(false) }
        val isAssetConfirmed = remember { mutableStateOf(false) }
        val isConfirmButtonEnabled = remember(
            isNetworkConfirmed,
            isAddressConfirmed,
            isAssetConfirmed
        ) {
            derivedStateOf { isNetworkConfirmed.value && isAddressConfirmed.value && isAssetConfirmed.value }
        }

        MaxWidthColumn(
            Modifier
                .weight(1f)
                .padding(horizontal = Dimensions.Padding.default)
                .verticalScroll(rememberScrollState())
        ) {
            VerticalSpacer(Spacing.BASE)
            TextDescription2(
                MR.strings.message_verify_transaction.desc().localized(),
                color = Colors.caption
            )
            VerticalSpacer(Spacing.SMALL)
            ConfirmationLocalItem(
                imageUrl = uiState.pendingSignTransactionUiModel.blockchainType.localImage,
                label = MR.strings.label_verify_transaction_check_network.desc()
                    .localized(),
                value = uiState.pendingSignTransactionUiModel.blockchainType.name,
                isChecked = isNetworkConfirmed.value,
                onClick = { isNetworkConfirmed.toggle() }
            )
            VerticalSpacer(Spacing.XSMALL)
            AddressConfirmationItem(
                address = uiState.pendingSignTransactionUiModel.recipientAddress,
                label = MR.strings.label_verify_transaction_check_address.desc()
                    .localized(),
                value = uiState.pendingSignTransactionUiModel.recipient,
                subtitleValue = uiState.pendingSignTransactionUiModel.addressCompact,
                isChecked = isAddressConfirmed.value,
                onClick = { isAddressConfirmed.toggle() },
            )
            VerticalSpacer(Spacing.XSMALL)
            when (uiState.pendingSignTransactionUiModel.transactionType) {
                is SignTransactionType.SendCoinOrErc20Token -> {
                    ConfirmationItem(
                        imageUrl = null, // Null for now, we'll decide if we want the image later
                        label = MR.strings.label_verify_transaction_check_amount.desc()
                            .localized(),
                        value = uiState.pendingSignTransactionUiModel.transactionType.amount,
                        subtitleValue = uiState.pendingSignTransactionUiModel.transactionType.fiatValue,
                        isChecked = isAssetConfirmed.value,
                        onClick = { isAssetConfirmed.toggle() }
                    )
                }
                is SignTransactionType.SendErc721Or1155Token -> {
                    MaxWidthRow(horizontalArrangement = Arrangement.Center) {
                        // Null for now, we'll decide if we want the image later
//                    NftImage(
//                        nftCollectionName = it.nftName,
//                        nft = it.nft.nft.first(),
//                        nftImageType = NftImageType.MEDIUM
//                    )
                    }
                }
                is SignTransactionType.Erc20Approve -> TODO()
                is SignTransactionType.SignWeb3 -> TODO()
                is SignTransactionType.Swap -> TODO()
            }
            VerticalSpacer(Spacing.XSMALL)
            TextDescription2(
                "Information such as asset and gas fiat estimate is passed in from UI app and may not reflect the latest price of the asset",
                color = Colors.caption
            )
            VerticalSpacer(Spacing.SMALL)
        }
        MaxWidthRow(Modifier.padding(Dimensions.Padding.default)) {
            ButtonNormal(
                text = "Sign Transaction", // No need to localize for now, awaiting final UI design
                onClick = { onClickConfirm() },
                modifier = Modifier.fillMaxWidth(),
                fontSize = FontType.REGULAR,
                buttonModifier = Modifier.padding(vertical = 10.dp),
                enabled = isConfirmButtonEnabled.value
            )
        }
    }
}