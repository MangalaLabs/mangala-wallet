package com.mangala.wallet.features.chains.antelope_base.presentation.ui.transactionhistory

import com.mangala.wallet.features.chains.antelope_base.domain.model.actions.ActionId


enum class CompositeActionGroup {
    BUY_RAM,
    SELL_RAM,
    RAM_TRANSFER,
    TOKEN_TRANSFER,
    RESOURCE_PROVIDER_FEE, // e.g Greymass Fuel
    CREATE_ACCOUNT,
    LINK_AUTH,
    UPDATE_AUTH,
    RENT_CPU,
    RENT_NET,
    POWERUP,
    DELEGATE_BANDWIDTH,
    MSIG_PROPOSE,
    MSIG_EXEC,
    MSIG_CANCEL,
    MSIG_APPROVE,
    MSIG_UNAPPROVE,
    CONTRACT_CALL; // For unknown transactions

    companion object {
        val actionToCompositeActionGroupMap: Map<String, Set<CompositeActionGroup>> = mapOf(
            ActionId.LOG_SELL_RAM to hashSetOf(SELL_RAM),
            ActionId.SELL_RAM to hashSetOf(SELL_RAM),
            ActionId.LOG_BUY_RAM to hashSetOf(BUY_RAM, RESOURCE_PROVIDER_FEE),
            ActionId.BUY_RAM to hashSetOf(BUY_RAM),
            ActionId.BUY_RAM_BYTES to hashSetOf(BUY_RAM, RESOURCE_PROVIDER_FEE),
            ActionId.LOG_RAM_CHANGE to hashSetOf(BUY_RAM, SELL_RAM, RAM_TRANSFER),
            ActionId.RAM_TRANSFER to hashSetOf(RAM_TRANSFER),
            ActionId.TOKEN_TRANSFER to hashSetOf(
                BUY_RAM,
                SELL_RAM,
                TOKEN_TRANSFER,
                RENT_CPU,
                RENT_NET
            ),
            ActionId.NEW_VAULTA_TOKEN_TRANSFER to hashSetOf(
                BUY_RAM,
                SELL_RAM,
                TOKEN_TRANSFER,
                RENT_CPU,
                RENT_NET
            ),
            ActionId.NEW_ACCOUNT to hashSetOf(CREATE_ACCOUNT),
            ActionId.LINK_AUTH to hashSetOf(LINK_AUTH),
            ActionId.UPDATE_AUTH to hashSetOf(UPDATE_AUTH),
            ActionId.RENT_CPU to hashSetOf(RENT_CPU),
            ActionId.RENT_NET to hashSetOf(RENT_NET),
            ActionId.DEPOSIT to hashSetOf(RENT_CPU, RENT_NET),
            ActionId.POWERUP to hashSetOf(POWERUP),
            ActionId.DELEGATE_BANDWIDTH to hashSetOf(DELEGATE_BANDWIDTH),
            ActionId.MSIG_PROPOSE to hashSetOf(MSIG_PROPOSE),
            ActionId.MSIG_EXECUTE to hashSetOf(MSIG_EXEC),
            ActionId.MSIG_CANCEL to hashSetOf(MSIG_CANCEL),
            ActionId.MSIG_APPROVE to hashSetOf(MSIG_APPROVE),
            ActionId.MSIG_UNAPPROVE to hashSetOf(MSIG_UNAPPROVE)
        )
    }
}