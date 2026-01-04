package com.mangala.wallet.features.chains.antelope_base.domain.model.transaction

enum class SignTransactionType {
    CREATE_ACCOUNT,
    SEND_ASSET,
    POWER_UP,
    BUY_RAM,
    BUY_RAM_BYTES,
    SELL_RAM,
    TRANSFER_RAM,
    DELEGATE_BANDWIDTH,
    UN_DELEGATE_BANDWIDTH,
    RENT_VIA_REX,
    ESR_IDENTITY,
    CREATE_PROPOSE,
    APPROVE_PROPOSAL,
    UN_APPROVE_PROPOSAL,
    CANCEL_PROPOSAL,
    EXECUTE_PROPOSAL,
    GIFT_RAM
}