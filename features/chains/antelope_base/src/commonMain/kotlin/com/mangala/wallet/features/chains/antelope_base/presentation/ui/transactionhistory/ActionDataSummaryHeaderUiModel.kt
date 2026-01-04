package com.mangala.wallet.features.chains.antelope_base.presentation.ui.transactionhistory

import com.ionspin.kotlin.bignum.decimal.toBigDecimal
import com.mangala.antelope.base.api.model.ActionTrace
import com.mangala.antelope.base.domain.model.Transaction
import com.mangala.wallet.antelope_balance.Balance
import com.mangala.wallet.antelope_balance.Balance.Companion.DEFAULT_PRECISION
import com.mangala.wallet.antelope_balance.BalanceFormatter
import com.mangala.wallet.features.chains.antelope_base.domain.model.actions.ActionId
import com.mangala.wallet.utils.ext.jsonObjectOrNull
import com.mangala.wallet.utils.ext.jsonPrimitiveOrNull
import com.mangala.wallet.utils.truncateDecimal
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlin.math.abs

sealed interface ActionDataSummaryHeaderUiModel {
    data class RamTransfer(
        val senderAccount: String,
        val recipientAccount: String,
        val ramBytes: Long,
        val memo: String,
        val newRamBalance: Long,
        val currentAccountName: String
    ) : ActionDataSummaryHeaderUiModel {
        val ramBytesFormatted = "${(ramBytes.toDouble() / 1024).truncateDecimal(4)} KB"
        val formattedNewRamBalance = "${(newRamBalance.toDouble() / 1024).truncateDecimal(4)} KB"
        val isOutgoingTransaction = recipientAccount != currentAccountName
    }

    data class RamBuy(
        val ramBytesBought: Long?,
        val ramFee: Balance, // RAM fee (a % of RAM cost)
        val totalCost: Balance, // RAM cost + RAM fee
        val newRamBalance: Long?,
        val recipientAccount: String, // for buy RAM for others txns
        val payerAccount: String,
        val currentAccountName: String
    ) : ActionDataSummaryHeaderUiModel {
        private val pricePerKb = if (ramBytesBought == null) null else
            (totalCost.amount / (ramBytesBought.toDouble() / 1024)).truncateDecimal(decimalPlaces = totalCost.precision)
        val pricePerKbFormatted = if (pricePerKb == null) null else "$pricePerKb ${totalCost.symbol}/ KB"
        val buyRamType by lazy {
            if (payerAccount == currentAccountName && recipientAccount == currentAccountName) {
                BuyRamType.BUY_FOR_SELF
            } else if (payerAccount == currentAccountName) {
                BuyRamType.BUY_FOR_OTHERS
            } else {
                BuyRamType.BOUGHT_BY_OTHERS
            }
        }

        val ramBytesBoughtFormatted = if (ramBytesBought == null) null else "${(ramBytesBought.toDouble() / 1024).truncateDecimal(4)} KB"
        val ramFeeFormatted = BalanceFormatter.formatEosBalance(ramFee)
        val totalCostFormatted = BalanceFormatter.formatEosBalance(totalCost)
        val newRamBalanceFormatted =
            newRamBalance?.let { "${(newRamBalance.toDouble() / 1024).truncateDecimal(4)} KB" }

        enum class BuyRamType {
            BUY_FOR_SELF,
            BUY_FOR_OTHERS,
            BOUGHT_BY_OTHERS
        }
    }

    data class RamSell(
        val ramBytesSold: Long,
        val ramFee: Balance,
        val totalReceived: Balance,
        val newRamBalance: Long?
    ) : ActionDataSummaryHeaderUiModel {
        private val pricePerKb = (totalReceived.amount / (ramBytesSold.toDouble() / 1024)).truncateDecimal(decimalPlaces = totalReceived.precision)
        val ramBytesSoldFormatted = "${(ramBytesSold.toDouble() / 1024).truncateDecimal(4)} KB"
        val pricePerKbFormatted = "$pricePerKb ${totalReceived.symbol}/ KB"
        val totalReceivedFormatted = BalanceFormatter.formatEosBalance(totalReceived)
        val ramFeeFormatted = BalanceFormatter.formatEosBalance(ramFee)
        val newRamBalanceFormatted = if (newRamBalance == null) null else "${(newRamBalance.toDouble() / 1024).truncateDecimal(4)} KB"
    }

    data class TokenTransfer(
        val senderAccount: String,
        val recipientAccount: String,
        val quantity: Balance,
        val memo: String,
        val currentAccountName: String
    ) : ActionDataSummaryHeaderUiModel {
        val isOutgoingTransaction = recipientAccount != currentAccountName
        val quantityFormatted = BalanceFormatter.formatEosBalance(quantity)
    }

    data class ResourceProviderFee(
        val amountPaid: Balance,
        val resourceProviderAccount: String,
        val memo: String
    ) : ActionDataSummaryHeaderUiModel {
        val amountPaidFormatted = BalanceFormatter.formatEosBalance(amountPaid)
    }

    data class CreateAccount(
        val newAccountName: String
    ) : ActionDataSummaryHeaderUiModel

    data class ContractCall(
        val contractName: String,
        val actionName: String,
        val from: String,
        val currentAccountName: String
    ) : ActionDataSummaryHeaderUiModel {
        val isFromSelf = from == currentAccountName
        val actionId = "$contractName:$actionName"
    }

    data class LinkAuth(
        val auth: String,
        val contractName: String,
        val action: String
    ) : ActionDataSummaryHeaderUiModel {
        val actionId = "$contractName:$action"
    }

    data class UpdateAuth(
        val permissionName: String
    ) : ActionDataSummaryHeaderUiModel

    data class RentViaRex(
        val amount: Balance,
        val from: String,
        val receiver: String,
        val currentAccountName: String,
        val resourceType: ResourceType
    ) : ActionDataSummaryHeaderUiModel {
        val amountFormatted = BalanceFormatter.formatEosBalance(amount)
        val resourceRentType by lazy {
            if (from == currentAccountName && receiver == currentAccountName) {
                ResourceRentType.RENT_FOR_SELF
            } else if (from == currentAccountName) {
                ResourceRentType.RENT_FOR_OTHERS
            } else {
                ResourceRentType.RENTED_FOR_BY_OTHERS
            }
        }

        enum class ResourceType {
            RENT_CPU,
            RENT_NET
        }
    }

    data class PowerUp(
        val amount: Balance,
        val from: String,
        val receiver: String,
        val currentAccountName: String
    ) : ActionDataSummaryHeaderUiModel {
        val amountFormatted = BalanceFormatter.formatEosBalance(amount)
        val powerUpType by lazy {
            if (from == currentAccountName && receiver == currentAccountName) {
                ResourceRentType.RENT_FOR_SELF
            } else if (from == currentAccountName) {
                ResourceRentType.RENT_FOR_OTHERS
            } else {
                ResourceRentType.RENTED_FOR_BY_OTHERS
            }
        }
    }

    data class DelegateBandwidth(
        val netDelegateAmount: Balance,
        val cpuDelegateAmount: Balance,
        val totalAmount: Balance,
        val from: String,
        val receiver: String,
        val currentAccountName: String
    ): ActionDataSummaryHeaderUiModel {
        val netDelegateAmountFormatted = if (netDelegateAmount.amount == 0.0) null else BalanceFormatter.formatEosBalance(netDelegateAmount)
        val cpuDelegateAmountFormatted = if (cpuDelegateAmount.amount == 0.0) null else BalanceFormatter.formatEosBalance(cpuDelegateAmount)
        val totalAmountFormatted = BalanceFormatter.formatEosBalance(totalAmount)
        val resources: String get() {
            val hasNet = netDelegateAmount.amount > 0
            val hasCpu = cpuDelegateAmount.amount > 0

            return when {
                hasNet && hasCpu -> "NET, CPU"
                hasNet -> "NET"
                hasCpu -> "CPU"
                else -> ""
            }
        }

        val delegateType by lazy {
            if (from == currentAccountName && receiver == currentAccountName) {
                ResourceRentType.RENT_FOR_SELF
            } else if (from == currentAccountName) {
                ResourceRentType.RENT_FOR_OTHERS
            } else {
                ResourceRentType.RENTED_FOR_BY_OTHERS
            }
        }
    }

    data class MsigPropose(
        val proposer: String,
        val proposalName: String,
        val requestedPermissions: List<Transaction.Authorization>,
        val actions: List<Transaction.Action>,
        val currentAccountName: String
    ) : ActionDataSummaryHeaderUiModel {
        val formattedRequestedPermissions =
            requestedPermissions.joinToString { "${it.actor}@${it.permission}" }
        val formattedActions = actions.joinToString { "${it.account}:${it.name}" }
        val isProposalFromCurrentAccount = proposer == currentAccountName
    }

    // Shared for eosio.msg:approve and eosio.msg:unapprove
    data class MsigApproveToggle(
        val proposer: String,
        val proposalName: String,
        val approvedPermission: Transaction.Authorization,
        val currentAccountName: String,
        val isApprove: Boolean
    ) : ActionDataSummaryHeaderUiModel {
        val formattedApprovedPermission =
            "${approvedPermission.actor}@${approvedPermission.permission}"
        val isMsigProposedBySelf = proposer == currentAccountName
    }

    data class MsigCancel(
        val proposer: String,
        val proposalName: String,
        val canceler: String,
        val currentAccountName: String
    ) : ActionDataSummaryHeaderUiModel {
        val isMsigProposedBySelf = proposer == currentAccountName
    }

    data class MsigExecute(
        val proposer: String,
        val proposalName: String,
        val executer: String,
        val currentAccountName: String
    ) : ActionDataSummaryHeaderUiModel {
        val isMsigProposedBySelf = proposer == currentAccountName
    }

    enum class ResourceRentType {
        RENT_FOR_SELF,
        RENT_FOR_OTHERS,
        RENTED_FOR_BY_OTHERS
    }
}

fun Map<CompositeActionGroup, List<ActionTrace>>.toActionDataSummaryHeaderUiModels(
    currentAccountName: String
): List<ActionDataSummaryHeaderUiModel> {
    val result = mutableListOf<ActionDataSummaryHeaderUiModel>()

    this.entries.forEach { entry ->
        val (group, actionTraces) = entry

        when (group) {
            CompositeActionGroup.BUY_RAM -> {
                val logBuyRamActions =
                    actionTraces.filter { it.act?.actionId == ActionId.LOG_BUY_RAM }
                val transactionMap = actionTraces.groupBy { it.act?.actionId }
                val buyRamBytesActions =
                    transactionMap[ActionId.BUY_RAM_BYTES]
                val buyRamActions =
                    transactionMap[ActionId.BUY_RAM]
                val transferActions =
                    (transactionMap[ActionId.TOKEN_TRANSFER].orEmpty() + transactionMap[ActionId.NEW_VAULTA_TOKEN_TRANSFER].orEmpty())
                val ramFeeTransfers = transferActions.filter { it.act?.to == "eosio.ramfee" }

                if (logBuyRamActions.isEmpty()) {
                    // Legacy buy RAM transactions does not have logbuyram action

                    val nativeTokenForRamTransfers = transferActions.filter { it.act?.to == "eosio.ram" }

                    val buyRamBytesHeaders = buyRamBytesActions?.mapIndexed { index, action ->
                        summarizeRamBuyHeader(
                            nativeTokenForRamTransfers,
                            index,
                            ramFeeTransfers,
                            action,
                            currentAccountName
                        )
                    } ?: emptyList()

                    val buyRamHeaders = buyRamActions?.mapIndexed { index, action ->
                        summarizeRamBuyHeader(
                            nativeTokenForRamTransfers,
                            index,
                            ramFeeTransfers,
                            action,
                            currentAccountName
                        )
                    } ?: emptyList()

                    result.addAll(buyRamHeaders)
                    result.addAll(buyRamBytesHeaders)
                    return@forEach
                }

                val headers = logBuyRamActions.mapIndexed { index, action ->
                    val recipientAccount = action.act?.receiver.orEmpty()
                    val payerAccount = action.act?.payer.orEmpty()
                    val ramCostBalance = BalanceFormatter.deserializeOrNull(
                        action.act?.quantity.orEmpty()
                    ) ?: Balance(0.0, "")
                    val ramFeeBalance = BalanceFormatter.deserializeOrNull(
                        action.act?.fee.orEmpty()
                    ) ?: BalanceFormatter.deserializeOrNull(ramFeeTransfers.getOrNull(index)?.act?.quantity.orEmpty()) // old logbuyram does not include fees
                    ?: Balance(0.0, "")
                    val newRamBalance = action.act?.ramBytes

                    ActionDataSummaryHeaderUiModel.RamBuy(
                        ramBytesBought = action.act?.bytes ?: 0,
                        totalCost = ramCostBalance,
                        ramFee = ramFeeBalance,
                        newRamBalance = newRamBalance ?: 0,
                        recipientAccount = recipientAccount,
                        payerAccount = payerAccount,
                        currentAccountName = currentAccountName
                    )
                }

                result.addAll(headers)
            }

            CompositeActionGroup.SELL_RAM -> {
                val logSellRamActions =
                    actionTraces.filter { it.act?.actionId == ActionId.LOG_SELL_RAM }
                val transactionMap = actionTraces.groupBy { it.act?.actionId }
                val transferActions =
                    transactionMap[ActionId.TOKEN_TRANSFER].orEmpty() + transactionMap[ActionId.NEW_VAULTA_TOKEN_TRANSFER].orEmpty()
                val ramFeeTransfers = transferActions.filter { it.act?.to == "eosio.ramfee" }


                if (logSellRamActions.isEmpty()) {
                    // Legacy sell RAM transactions does not have logsellram action
                    val nativeTokenForRamTransfers = transferActions.filter { it.act?.from == "eosio.ram" }
                    val sellRamActions =
                        transactionMap[ActionId.SELL_RAM]

                    val sellRamHeaders = sellRamActions?.mapIndexed { index, action ->
                        val ramCostTransferAction = nativeTokenForRamTransfers.getOrNull(index)
                        val ramFeeTransferAction = ramFeeTransfers.getOrNull(index)

                        val nativeTokenForRam = BalanceFormatter.deserializeOrNull(
                            ramCostTransferAction?.act?.quantity.orEmpty()
                        ) ?: Balance(0.0, "")
                        val ramFeeBalance = BalanceFormatter.deserializeOrNull(
                            ramFeeTransferAction?.act?.quantity.orEmpty()
                        ) ?: Balance(0.0, "")

                        ActionDataSummaryHeaderUiModel.RamSell(
                            ramBytesSold = action.act?.bytes ?: 0L,
                            ramFee = ramFeeBalance,
                            totalReceived = nativeTokenForRam,
                            newRamBalance = null
                        )
                    } ?: emptyList()

                    result.addAll(sellRamHeaders)
                    return@forEach
                } else {
                    val headers = logSellRamActions.mapIndexed { index, action ->
                        val totalReceived = BalanceFormatter.deserializeOrNull(
                            action.act?.quantity.orEmpty()
                        ) ?: Balance(0.0, "")
                        val ramFee = BalanceFormatter.deserializeOrNull(
                            action.act?.fee.orEmpty()
                        ) ?: BalanceFormatter.deserializeOrNull(ramFeeTransfers.getOrNull(index)?.act?.quantity.orEmpty()) // old logsellram does not include fees
                        ?: Balance(0.0, "")
                        val newRamBalance = action.act?.ramBytes

                        ActionDataSummaryHeaderUiModel.RamSell(
                            ramBytesSold = action.act?.bytes ?: 0,
                            ramFee = ramFee,
                            totalReceived = totalReceived,
                            newRamBalance = newRamBalance ?: 0,
                        )
                    }

                    result.addAll(headers)
                }
            }

            CompositeActionGroup.RAM_TRANSFER -> {
                val ramTransferActions =
                    actionTraces.filter { it.act?.actionId == ActionId.RAM_TRANSFER }
                        .distinctBy { it.act?.data }
                val logRamChangeActions =
                    actionTraces.filter { it.act?.actionId == ActionId.LOG_RAM_CHANGE }

                val headers = ramTransferActions.mapIndexed { index, action ->
                    val senderAccount = action.act?.from.orEmpty()
                    val recipientAccount = action.act?.to.orEmpty()
                    val memo = action.act?.memo.orEmpty()
                    val ramBytes = action.act?.bytes ?: 0

                    val newRamBalance =
                        logRamChangeActions.getOrNull(index)?.act?.ramBytes ?: 0

                    ActionDataSummaryHeaderUiModel.RamTransfer(
                        senderAccount = senderAccount,
                        recipientAccount = recipientAccount,
                        ramBytes = abs(ramBytes),
                        memo = memo,
                        newRamBalance = newRamBalance,
                        currentAccountName = currentAccountName
                    )
                }.distinct()

                result.addAll(headers)
            }

            CompositeActionGroup.TOKEN_TRANSFER -> {
                val transferActions =
                    actionTraces.filter {
                        it.act?.actionId == ActionId.TOKEN_TRANSFER ||
                                it.act?.actionId == ActionId.NEW_VAULTA_TOKEN_TRANSFER
                    }

                val headers = transferActions.map { tokenTransferAction ->
                    val senderAccount = tokenTransferAction.act?.from.orEmpty()
                    val recipientAccount = tokenTransferAction.act?.to.orEmpty()
                    val quantity = BalanceFormatter.deserializeOrNull(
                        tokenTransferAction.act?.quantity.orEmpty()
                    ) ?: Balance(0.0, "")
                    val memo = tokenTransferAction.act?.memo.orEmpty()

                    ActionDataSummaryHeaderUiModel.TokenTransfer(
                        senderAccount = senderAccount,
                        recipientAccount = recipientAccount,
                        quantity = quantity,
                        memo = memo,
                        currentAccountName = currentAccountName
                    )
                }

                result.addAll(headers)
            }

            CompositeActionGroup.RESOURCE_PROVIDER_FEE -> {
                val transferActions =
                    actionTraces.filter { (it.act?.actionId == ActionId.TOKEN_TRANSFER || it.act?.actionId == ActionId.NEW_VAULTA_TOKEN_TRANSFER ) && it.act?.to == "fuel.gm" }

                val headers = transferActions.map {
                    val amountPaid = BalanceFormatter.deserializeOrNull(
                        it.act?.quantity.orEmpty()
                    ) ?: Balance(0.0, "")

                    ActionDataSummaryHeaderUiModel.ResourceProviderFee(
                        amountPaid = amountPaid,
                        resourceProviderAccount = it.act?.to.orEmpty(),
                        memo = it.act?.memo.orEmpty()
                    )
                }

                result.addAll(headers)
            }

            CompositeActionGroup.CREATE_ACCOUNT -> {
                val headers = actionTraces.map {
                    val newAccountName = it.act?.newAct.orEmpty()
                    ActionDataSummaryHeaderUiModel.CreateAccount(newAccountName)
                }

                result.addAll(headers)
            }

            CompositeActionGroup.CONTRACT_CALL -> {
                val headers = actionTraces
                    .map {
                        val contractName = it.act?.account.orEmpty()
                        val actionName = it.act?.name.orEmpty()
                        val from = it.act?.authorization?.joinToString { "${it.actor}@${it.permission}" }.orEmpty()

                        ActionDataSummaryHeaderUiModel.ContractCall(
                            contractName = contractName,
                            actionName = actionName,
                            from = from,
                            currentAccountName = currentAccountName
                        )
                    }

                result.addAll(headers)
            }

            CompositeActionGroup.LINK_AUTH -> {
                val headers = actionTraces.map {
                    val permissionName = it.act?.requirement.orEmpty()
                    val contractName = it.act?.code.orEmpty()
                    val actionName = it.act?.type.orEmpty()
                    ActionDataSummaryHeaderUiModel.LinkAuth(
                        auth = permissionName,
                        contractName = contractName,
                        action = actionName
                    )
                }

                result.addAll(headers)
            }

            CompositeActionGroup.UPDATE_AUTH -> {
                val headers = actionTraces.map {
                    val permissionName = it.act?.permission.orEmpty()

                    ActionDataSummaryHeaderUiModel.UpdateAuth(
                        permissionName = permissionName,
                    )
                }

                result.addAll(headers)
            }

            CompositeActionGroup.RENT_CPU -> {
                val headers =
                    actionTraces.filter { it.act?.actionId == ActionId.RENT_CPU }
                        .map {
                            val amount = BalanceFormatter.deserializeOrNull(
                                it.act?.loanPayment.orEmpty()
                            ) ?: Balance(0.0, "")

                            ActionDataSummaryHeaderUiModel.RentViaRex(
                                amount = amount,
                                from = it.act?.from.orEmpty(),
                                receiver = it.act?.receiver.orEmpty(),
                                currentAccountName = currentAccountName,
                                resourceType = ActionDataSummaryHeaderUiModel.RentViaRex.ResourceType.RENT_CPU
                            )
                        }

                result.addAll(headers)
            }

            CompositeActionGroup.RENT_NET -> {
                val headers =
                    actionTraces.filter { it.act?.actionId == ActionId.RENT_NET }
                        .map {
                            val amount = BalanceFormatter.deserializeOrNull(
                                it.act?.loanPayment.orEmpty()
                            ) ?: Balance(0.0, "")

                            ActionDataSummaryHeaderUiModel.RentViaRex(
                                amount = amount,
                                from = it.act?.from.orEmpty(),
                                receiver = it.act?.receiver.orEmpty(),
                                currentAccountName = currentAccountName,
                                resourceType = ActionDataSummaryHeaderUiModel.RentViaRex.ResourceType.RENT_NET
                            )
                        }

                result.addAll(headers)

            }

            CompositeActionGroup.POWERUP -> {
                val transferActions =
                    actionTraces.filter { it.act?.actionId == ActionId.TOKEN_TRANSFER || it.act?.actionId == ActionId.NEW_VAULTA_TOKEN_TRANSFER }
                val powerUpActions =
                    actionTraces.filter { it.act?.actionId == ActionId.POWERUP }

                val headers = transferActions.mapIndexed { index, action ->
                    val amount = BalanceFormatter.deserializeOrNull(
                        action.act?.quantity.orEmpty()
                    ) ?: Balance(0.0, "")
                    val powerUpAction = powerUpActions.getOrNull(index)

                    ActionDataSummaryHeaderUiModel.PowerUp(
                        amount = amount,
                        from = powerUpAction?.act?.payer.orEmpty(),
                        receiver = powerUpAction?.act?.receiver.orEmpty(),
                        currentAccountName = currentAccountName
                    )
                }

                result.addAll(headers)
            }

            CompositeActionGroup.MSIG_PROPOSE -> {
                val headers = actionTraces.map {
                    val proposer = it.act?.proposer.orEmpty()
                    val proposalName = it.act?.proposalName.orEmpty()
                    val requestedPermissions = it.act?.requested.orEmpty()
                    val actions = it.act?.trxActions

                    ActionDataSummaryHeaderUiModel.MsigPropose(
                        proposer = proposer,
                        proposalName = proposalName,
                        requestedPermissions = requestedPermissions.mapNotNull { permissionElement ->
                            permissionElement.jsonObjectOrNull?.let { permission ->
                                Transaction.Authorization(
                                    actor = permission["actor"]?.jsonPrimitiveOrNull?.content.orEmpty(),
                                    permission = permission["permission"]?.jsonPrimitiveOrNull?.content.orEmpty()
                                )
                            }
                        },
                        actions = actions?.mapNotNull {
                            Transaction.Action(
                                account = it.jsonObjectOrNull?.get("account")?.jsonPrimitiveOrNull?.content.orEmpty(),
                                name = it.jsonObjectOrNull?.get("name")?.jsonPrimitiveOrNull?.content.orEmpty(),
                                authorization = it.jsonObjectOrNull?.get("authorization")?.jsonArray?.mapNotNull { authorizationElement ->
                                    authorizationElement.jsonObjectOrNull?.let { authorization ->
                                        Transaction.Authorization(
                                            actor = authorization["actor"]?.jsonPrimitiveOrNull?.content.orEmpty(),
                                            permission = authorization["permission"]?.jsonPrimitiveOrNull?.content.orEmpty()
                                        )
                                    }
                                }.orEmpty(),
                                data = it.jsonObject["data"]?.jsonPrimitiveOrNull?.content.orEmpty()
                            )
                        } ?: emptyList(),
                        currentAccountName = currentAccountName
                    )
                }

                result.addAll(headers)
            }

            CompositeActionGroup.MSIG_EXEC -> {
                val headers = actionTraces.map {
                    val proposer = it.act?.proposer.orEmpty()
                    val proposalName = it.act?.proposalName.orEmpty()
                    val executer = it.act?.executer.orEmpty()

                    ActionDataSummaryHeaderUiModel.MsigExecute(
                        proposer = proposer,
                        proposalName = proposalName,
                        executer = executer,
                        currentAccountName = currentAccountName
                    )
                }

                result.addAll(headers)
            }

            CompositeActionGroup.MSIG_CANCEL -> {
                val headers = actionTraces.map {
                    val proposer = it.act?.proposer.orEmpty()
                    val proposalName = it.act?.proposalName.orEmpty()
                    val canceler = it.act?.canceler.orEmpty()

                    ActionDataSummaryHeaderUiModel.MsigCancel(
                        proposer = proposer,
                        proposalName = proposalName,
                        currentAccountName = currentAccountName,
                        canceler = canceler
                    )
                }

                result.addAll(headers)
            }

            CompositeActionGroup.MSIG_APPROVE -> {
                val headers =
                    actionTraces.mapApproveToggleActionGroup(isApprove = true, currentAccountName)

                result.addAll(headers)
            }

            CompositeActionGroup.MSIG_UNAPPROVE -> {
                val headers =
                    actionTraces.mapApproveToggleActionGroup(isApprove = false, currentAccountName)

                result.addAll(headers)
            }

            CompositeActionGroup.DELEGATE_BANDWIDTH -> {
                // Used to get native coin info
                val firstTransfer = actionTraces.firstOrNull { it.act?.actionId == ActionId.TOKEN_TRANSFER || it.act?.actionId == ActionId.NEW_VAULTA_TOKEN_TRANSFER }
                val delegateBwActions = actionTraces.filter { it.act?.actionId == ActionId.DELEGATE_BANDWIDTH }

                val transferBalance = BalanceFormatter.deserializeOrNull(firstTransfer?.act?.quantity.orEmpty())
                val symbol = transferBalance?.symbol.orEmpty()
                val precision = transferBalance?.precision ?: DEFAULT_PRECISION

                val headers = delegateBwActions.map {
                    val receiver = it.act?.receiver.orEmpty()
                    val from = it.act?.from.orEmpty()
                    val netDelegateAmount = it.act?.stakeNetQuantity?.toDoubleOrNull() ?: 0.0
                    val cpuDelegateAmount = it.act?.stakeCpuQuantity?.toDoubleOrNull() ?: 0.0
                    val totalAmount = it.act?.getDataAmountAsDouble() ?: 0.0

                    ActionDataSummaryHeaderUiModel.DelegateBandwidth(
                        currentAccountName = currentAccountName,
                        netDelegateAmount = Balance(netDelegateAmount, symbol, precision),
                        cpuDelegateAmount = Balance(cpuDelegateAmount, symbol, precision),
                        totalAmount = Balance(totalAmount, symbol, precision),
                        from = from,
                        receiver = receiver
                    )
                }

                result.addAll(headers)
            }
        }
    }

    return result
}

private fun summarizeRamBuyHeader(
    nativeTokenForRamTransfers: List<ActionTrace>?,
    index: Int,
    ramFeeTransfers: List<ActionTrace>?,
    action: ActionTrace,
    currentAccountName: String
): ActionDataSummaryHeaderUiModel.RamBuy {
    val ramCostTransferAction = nativeTokenForRamTransfers?.getOrNull(index)
    val ramFeeTransferAction = ramFeeTransfers?.getOrNull(index)

    val nativeTokenForRam = BalanceFormatter.deserializeOrNull(
        ramCostTransferAction?.act?.quantity.orEmpty()
    ) ?: Balance(0.0, "")
    val ramFeeBalance = BalanceFormatter.deserializeOrNull(
        ramFeeTransferAction?.act?.quantity.orEmpty()
    ) ?: Balance(0.0, "")
    val ramCost = (nativeTokenForRam.amount.toBigDecimal() + ramFeeBalance.amount.toBigDecimal()).doubleValue(exactRequired = false)
    val ramCostBalance = Balance(ramCost, nativeTokenForRam.symbol, nativeTokenForRam.precision)
    val newRamBalance = action.act?.ramBytes

    return ActionDataSummaryHeaderUiModel.RamBuy(
        ramBytesBought = action.act?.bytes,
        totalCost = ramCostBalance,
        ramFee = ramFeeBalance,
        newRamBalance = newRamBalance,
        recipientAccount = action.act?.receiver.orEmpty(),
        payerAccount = action.act?.payer.orEmpty(),
        currentAccountName = currentAccountName
    )
}

private fun List<ActionTrace>.mapApproveToggleActionGroup(
    isApprove: Boolean,
    currentAccountName: String
): List<ActionDataSummaryHeaderUiModel.MsigApproveToggle> {
    return this.map {
        val proposer = it.act?.proposer.orEmpty()
        val proposalName = it.act?.proposalName.orEmpty()
        val approvedPermission = it.act?.level?.let { permission ->
            Transaction.Authorization(
                actor = permission["actor"]?.jsonPrimitiveOrNull?.content.orEmpty(),
                permission = permission["permission"]?.jsonPrimitiveOrNull?.content.orEmpty()
            )
        } ?: Transaction.Authorization("", "")

        ActionDataSummaryHeaderUiModel.MsigApproveToggle(
            proposer = proposer,
            proposalName = proposalName,
            approvedPermission = approvedPermission,
            currentAccountName = currentAccountName,
            isApprove = isApprove
        )
    }
}