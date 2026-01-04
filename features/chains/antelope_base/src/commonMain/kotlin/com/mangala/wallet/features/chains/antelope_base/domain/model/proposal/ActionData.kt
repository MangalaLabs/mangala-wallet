package com.mangala.wallet.features.chains.antelope_base.domain.model.proposal

sealed class ActionData {
    data class BuyRam(val payer: String, val receiver: String, val quant: String) : ActionData()
    data class SellRam(val account: String, val bytes: Long) : ActionData()
    data class BuyRamBytes(val payer: String, val receiver: String, val bytes: Long) : ActionData()
    data class DelegateBandwidth(
        val from: String,
        val receiver: String,
        val stakeNetQuantity: String,
        val stakeCpuQuantity: String,
    ) : ActionData()

    data class RamTransfer(val from: String, val to: String, val quantity: String) : ActionData()
    data class UndelegateBandwidth(
        val from: String,
        val receiver: String,
        val unstakeNetQuantity: String,
        val unstakeCpuQuantity: String,
    ) : ActionData()

}