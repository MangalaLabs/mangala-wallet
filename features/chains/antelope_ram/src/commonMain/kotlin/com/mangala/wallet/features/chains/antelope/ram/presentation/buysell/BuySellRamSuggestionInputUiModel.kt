package com.mangala.wallet.features.chains.antelope.ram.presentation.buysell

data class BuySellRamSuggestionInputUiModel(
    val amount: Int,
    val quantity: Quantity,
    val isSelected: Boolean = false
){
    enum class Quantity {
        Percent, Eos, Kb
    }
}
