package com.mangala.wallet.features.chains.antelope_base.domain.model.actions

enum class ActionName(val value: String) {
    TRANSFER("transfer"),
    DELETE_AUTH("deleteauth"),
    LINK_AUTH("linkauth"),
    UNLINK_AUTH("unlinkauth"),
    BUY_RAM_BYTES("buyrambytes"),
    BUY_RAM("buyram"),
    LOG_RAM_CHANGE("logramchange"),
    LOG_BUY_RAM("logbuyram"),
    LOG_DESTROY("logdestroy"),
    LOG_MINT("logmint"),
    GENERATE("generate"),
    LOG_DROPS("logdrops"),
    LOG_GENERATE("loggenerate"),
    BID_NAME("bidname"),
    POWER_UP("powerup"),
    LOG_POWER_UP("logpowerup"),
    NOTIFY_USER("notifyuser"),
    DELEGATEBW("delegatebw");//Stake CPU NET

    //get enum by name
    companion object {
        fun fromName(name: String): ActionName? {
            return values().find { it.value == name }
        }
    }
}