package com.mangala.wallet.features.chains.antelope_base.presentation.ui.transactionhistory

import com.mangala.antelope.base.api.model.ActionTrace
import com.mangala.wallet.features.chains.antelope_base.domain.model.actions.ActionId
import com.mangala.wallet.utils.ext.putOrAppend

fun List<ActionTrace>.getGroupedActionTraces(currentAccountName: String): Map<CompositeActionGroup, List<ActionTrace>> {
    val actionGroups = mutableMapOf<CompositeActionGroup, List<ActionTrace>>()

    var context: CompositeActionGroup? = null

    sortedBy { it.creatorActionOrdinal }.forEach { actionTrace ->
        val actionId = "${actionTrace.act?.account}:${actionTrace.act?.name}"
        val correspondingGroup = CompositeActionGroup.actionToCompositeActionGroupMap[actionId]

        if (correspondingGroup == null) {
            mapToContractCall(actionGroups, actionTrace)
        } else if (correspondingGroup.size == 1) {
            // This action can only be associated with a single action group
            val actionGroup = correspondingGroup.toList().getOrNull(0)
            actionGroup?.let {
                context = actionGroup
                actionGroups.putOrAppend(actionGroup, actionTrace)
            }
        } else if (correspondingGroup.size > 1) {
            // This action can be associated with multiple action groups
            when (actionId) {
                ActionId.LOG_RAM_CHANGE -> {
                    val actionGroup =
                        mapLogRamChangeAction(actionTrace, currentAccountName, context)
                    context = actionGroup
                    actionGroups.putOrAppend(actionGroup, actionTrace)
                }

                ActionId.LOG_BUY_RAM -> {
                    val actionGroup = mapLogBuyRamAction(actionTrace)
                    context = actionGroup
                    actionGroups.putOrAppend(actionGroup, actionTrace)
                }

                ActionId.BUY_RAM_BYTES -> {
                    val actionGroup =
                        mapLogBuyRamBytesAction(actionTrace, currentAccountName, context)
                    context = actionGroup
                    actionGroups.putOrAppend(actionGroup, actionTrace)
                }

                ActionId.TOKEN_TRANSFER, ActionId.NEW_VAULTA_TOKEN_TRANSFER -> {
                    val actionGroup = mapTransferAction(actionTrace, context)
                    context = actionGroup
                    actionGroups.putOrAppend(actionGroup, actionTrace)
                }

                ActionId.DEPOSIT -> {
                    val actionGroup = mapDepositAction(context)
                    context = actionGroup
                    actionGroups.putOrAppend(actionGroup, actionTrace)
                }

                else -> mapToContractCall(actionGroups, actionTrace)
            }
        } else {
            mapToContractCall(actionGroups, actionTrace)
        }
    }

    return actionGroups
}

private fun mapLogBuyRamBytesAction(
    actionTrace: ActionTrace,
    currentAccountName: String,
    context: CompositeActionGroup?
): CompositeActionGroup {
    val actionData = actionTrace.act
    val owner = actionData?.getDataOwnerAsString()
    val payer = actionData?.payer

    return if (payer == "greymassfuel") {
        CompositeActionGroup.RESOURCE_PROVIDER_FEE
    } else {
        CompositeActionGroup.BUY_RAM
    }
}

private fun mapLogBuyRamAction(
    actionTrace: ActionTrace
): CompositeActionGroup {
    val payer = actionTrace.act?.payer

    return if (payer == "greymassfuel") {
        CompositeActionGroup.RESOURCE_PROVIDER_FEE
    } else {
        CompositeActionGroup.BUY_RAM
    }
}

private fun mapLogRamChangeAction(
    actionTrace: ActionTrace,
    currentAccountName: String,
    context: CompositeActionGroup?
): CompositeActionGroup {
    val owner = actionTrace.act?.getDataOwnerAsString()

    return if (context == CompositeActionGroup.BUY_RAM || context == CompositeActionGroup.SELL_RAM || context == CompositeActionGroup.RAM_TRANSFER || context == CompositeActionGroup.RESOURCE_PROVIDER_FEE) {
        context
    } else {
        CompositeActionGroup.RAM_TRANSFER
    }
}

private fun mapTransferAction(
    actionTrace: ActionTrace,
    context: CompositeActionGroup?
): CompositeActionGroup {
    val recipient = actionTrace.act?.to
    val memo = actionTrace.act?.memo
    val from = actionTrace.act?.from

    return when {
        recipient == "eosio.ramfee" -> {
            when (memo) {
                "sell ram fee" -> {
                    CompositeActionGroup.SELL_RAM
                }

                "ram fee" -> {
                    CompositeActionGroup.BUY_RAM
                }

                else -> {
                    CompositeActionGroup.TOKEN_TRANSFER
                }
            }
        }
        from == "eosio.ram" -> {
            when (memo) {
                "sell ram" -> {
                    CompositeActionGroup.SELL_RAM
                }

                else -> {
                    CompositeActionGroup.TOKEN_TRANSFER
                }
            }
        }
        recipient == "eosio.ram" -> {
            when (memo) {
                "buy ram" -> {
                    CompositeActionGroup.BUY_RAM
                }

                else -> {
                    CompositeActionGroup.TOKEN_TRANSFER
                }
            }
            CompositeActionGroup.BUY_RAM
        }
        recipient == "fuel.gm" -> {
            CompositeActionGroup.RESOURCE_PROVIDER_FEE
        }
        recipient == "eosio.rex" -> {
            if (memo == "deposit to REX fund") {
                context ?: CompositeActionGroup.TOKEN_TRANSFER
            } else {
                CompositeActionGroup.TOKEN_TRANSFER
            }
        }
        recipient == "eosio.fees" -> {
            if (context == CompositeActionGroup.POWERUP) {
                context
            } else {
                CompositeActionGroup.TOKEN_TRANSFER
            }
        }
        recipient == "eosio.stake" -> {
            if (context == CompositeActionGroup.DELEGATE_BANDWIDTH) {
                context
            } else {
                CompositeActionGroup.TOKEN_TRANSFER
            }
        }
        else -> {
            CompositeActionGroup.TOKEN_TRANSFER
        }
    }
}

private fun mapDepositAction(
    context: CompositeActionGroup?
): CompositeActionGroup {
    return when (context) {
        CompositeActionGroup.RENT_CPU, CompositeActionGroup.RENT_NET -> {
            return context
        }

        else -> CompositeActionGroup.CONTRACT_CALL
    }
}

private fun mapToContractCall(
    actionGroups: MutableMap<CompositeActionGroup, List<ActionTrace>>,
    actionTrace: ActionTrace
) {
    // Unhandled action, mapped to contract call by default
    actionGroups.putOrAppend(CompositeActionGroup.CONTRACT_CALL, actionTrace)
}

