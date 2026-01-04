package com.mangala.wallet.features.send.presentation.sendsignedtransaction

import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import cafe.adriel.voyager.koin.getScreenModel
import com.benasher44.uuid.uuid4
import com.ionspin.kotlin.bignum.integer.BigInteger
import com.mangala.wallet.features.chains.evmcompatible.model.Address
import com.mangala.wallet.features.chains.evmcompatible.model.GasPrice
import com.mangala.wallet.features.chains.evmcompatible.model.SignTransactionRequest
import com.mangala.wallet.features.chains.evmcompatible.model.SignTransactionType
import com.mangala.wallet.features.chains.evmcompatible.model.Signature
import com.mangala.wallet.features.chains.evmcompatible.model.SignedTransactionResponse
import com.mangala.wallet.features.chains.evmcompatible.model.TransactionData
import com.mangala.wallet.model.blockchain.BlockchainType
import com.mangala.wallet.ui.utils.collectAsStateMultiplatform
import com.mangala.wallet.ui.utils.screenmodel.BaseScreen
import com.mangala.wallet.utils.analytics.MangalaAnalytics
import org.koin.core.parameter.parametersOf

class SendSignedTransactionScreen(
    private val walletId: String,
    private val accountId: String,
    private val nonce: Long,
    private val fromAddress: String,
    private val blockchainUid: String,
    private val toAddress: String,
    private val value: BigInteger,
    private val input: ByteArray,
    private val legacyGasPrice: Long?,
    private val maxFeePerGas: Long?,
    private val maxPriorityFeePerGas: Long?,
    private val baseFee: Long?,
    private val gasLimit: Long,
    private val gasFiatValue: String,
    private val contactName: String?,
    private val contactAddress: String?,
    private val transactionType: String,
    private val v: Int,
    private val r: ByteArray,
    private val s: ByteArray
): BaseScreen<SendSignedTransactionScreenModel>() {

    override val screenName: String = MangalaAnalytics.Screens.EVM_SEND_SIGNED_TRANSACTION_SCREEN
    override val screenClassName: String = SendSignedTransactionScreen::class.simpleName.orEmpty()

    override val isBottomBarVisible: Boolean
        get() = false

    override val statusBarInsetColor: Color
        @Composable
        get() = MaterialTheme.colors.background

    @Composable
    override fun createScreenModel(): SendSignedTransactionScreenModel = getScreenModel(
        parameters = {
            parametersOf(
                SignedTransactionResponse(
                    signTransactionRequest = SignTransactionRequest(
                        requestId = uuid4().toString(),
                        walletId = walletId,
                        accountId = accountId,
                        nonce = nonce,
                        fromAddress = fromAddress,
                        blockchainType = BlockchainType.fromUid(blockchainUid),
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
                        contactName = contactName,
                        contactAddress = contactAddress,
                        transactionType = SignTransactionType.SignWeb3("", "") // TODO: Map transaction type
                    ),
                    signature = Signature(v, r, s)
                )
            )
        }
    )

    @Composable
    override fun ScreenContent(screenModel: SendSignedTransactionScreenModel) {
        val uiState = screenModel.uiState.collectAsStateMultiplatform()

        SendSignedTransactionScreen(
            uiState.value,
            onClickSend = {
                screenModel.send()
            }
        )
    }

    @Composable
    fun SendSignedTransactionScreen(
        txHash: String,
        onClickSend: () -> Unit
    ) {
        Text("Received signed transaction")
        Button(onClick = onClickSend) {
            Text("Send")
        }
        Text("TxHash: $txHash")
    }
}