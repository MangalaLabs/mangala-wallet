package com.mangala.contract.wizard.presentation

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.koin.getScreenModel
import com.mangala.browser_bridge_base.ConfirmTransactionScreenUiState
import com.mangala.browser_bridge_base.ConfirmTransactionViewModel
import com.mangala.wallet.features.chains.evmcompatible.model.GasPrice
import com.mangala.wallet.common.mokoresources.Colors
import com.mangala.wallet.mokoresources.MR
import com.mangala.wallet.common.mokoresources.Spacing
import com.mangala.wallet.common.mokoresources.icons.MangalaWalletPack
import com.mangala.wallet.common.mokoresources.icons.MangalaWalletPack.Clear
import com.mangala.wallet.ui.ButtonNormal
import com.mangala.wallet.ui.TextDescription2
import com.mangala.wallet.ui.TextNormal
import com.mangala.wallet.ui.TextTiny
import com.mangala.wallet.ui.TextTitle4
import com.mangala.wallet.ui.component.OldFeeOptionItem
import com.mangala.wallet.ui.component.FeeOptionUiModel
import com.mangala.wallet.ui.component.MangalaWalletIconButton
import com.mangala.wallet.ui.utils.collectAsStateMultiplatform
import com.mangala.wallet.ui.utils.screenmodel.BaseScreen
import dev.icerock.moko.resources.compose.localized
import dev.icerock.moko.resources.desc.desc

class ContractWizardScreen: BaseScreen<ContractWizardScreenModel>() {

    override val statusBarInsetColor: Color
        @Composable
        get() = MaterialTheme.colors.primary

    @Composable
    override fun createScreenModel(): ContractWizardScreenModel = getScreenModel()

    @Composable
    override fun ScreenContent(screenModel: ContractWizardScreenModel) {
        val confirmTransactionModel: ConfirmTransactionViewModel = getScreenModel()

        Column {

            ButtonNormal(
                text = "Create Contract",
                onClick = {
                    screenModel.createContractWizard()
                }
            )

            val uiState: ContractWizardScreenUiState = screenModel.uiState.collectAsStateMultiplatform().value

            (uiState as? ContractWizardScreenUiState.Data)?.let { data ->
                val accountId = data.accounts.getOrNull(0)?.account?.id ?: ""
                val recipient = "0x0000000000000000000000000000000000000000"
//                val recipient = ""
                val payload = data.deploy
                if(!payload.isNullOrEmpty()) {
                    confirmTransactionModel.calTransactionFee(
                        accountId,
                        recipient,
                        payload,
                        "0",
                        10,
                        true
                    )
                }

                Box(
                    modifier = Modifier.background(color = MaterialTheme.colors.primary).padding(16.dp)
                ) {
                    ContractContent(
                        viewModel = confirmTransactionModel,
                        onClickTransactionOption = {
                            val gasPrice = confirmTransactionModel.gasPrice
                            val legacyGasPrice = (gasPrice as? GasPrice.Legacy)?.legacyGasPrice
                            val maxFeePerGas = (gasPrice as? GasPrice.Eip1559)?.maxFeePerGas
                            val maxPriorityFeePerGas = (gasPrice as? GasPrice.Eip1559)?.maxPriorityFeePerGas
                            val baseFee = (gasPrice as? GasPrice.Eip1559)?.baseFee
                            val isEip1559 = gasPrice is GasPrice.Eip1559

//                            navigator.push(
//                                TransactionFeeScreen(
//                                    legacyGasPrice = legacyGasPrice ?: 0,
//                                    maxFeePerGas = maxFeePerGas ?: 0,
//                                    maxPriorityFeePerGas = maxPriorityFeePerGas ?: 0,
//                                    baseFee = baseFee ?: 0,
//                                    isEip1559 = isEip1559,
//                                    selectedTransactionFee = it,
//                                    onFeeSelected = {
//                                        navigator.pop()
//                                        viewModel.onTransactionFeeSelected(it)
//                                    },
//                                    blockchainUid = BlockchainNetworkData.getBlockchainUidByChainId(chainId),
//                                    accountId = accountId,
//                                    gasLimitInWei = viewModel.getGasLimitInWei(),
//                                )
//                            )
                        },
                        onConfirm = {
                            confirmTransactionModel.createContract(
                                accountId,
                                payload,
                                "0"
                            )
                        },
                        onDecline = {

                        },
                    )
                }
            }

        }


    }

    @Composable
    fun ContractContent(
        viewModel: ConfirmTransactionViewModel,
        onClickTransactionOption: (FeeOptionUiModel) -> Unit,
        onConfirm: () -> Unit,
        onDecline: () -> Unit,
    ) {

        val uiState: ConfirmTransactionScreenUiState = viewModel.uiState.collectAsStateMultiplatform().value

        val title = "Min NFT"
        val message = "Now or never"

        val estimateGasFail = MR.strings.message_estimate_gas_fail.desc().localized()
        val decline = MR.strings.decline.desc().localized()
        val on = MR.strings.message_receive_token_on.desc().localized()
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
                            "$estimateGasFail",
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
}