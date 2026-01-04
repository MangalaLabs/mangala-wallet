package com.mangala.wallet.features.receive.presentation

import androidx.compose.ui.text.input.TextFieldValue
import com.mangala.wallet.ui.utils.screenmodel.BaseScreenModel
import com.mangala.wallet.utils.getCurrentLocaleDecimalSeparator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class AddAmountToReceiveQrScreenModel(
    initialAmount: String,
    private val decimal: Long?
) : BaseScreenModel() {
    private val _amount = MutableStateFlow(TextFieldValue(text = initialAmount))
    val amount = _amount.asStateFlow()

    fun onAmountChange(amount: TextFieldValue) {

        if (amount.text.isEmpty()) {
            _amount.update { TextFieldValue() }
            return
        }

        val decimalSeparator = getCurrentLocaleDecimalSeparator()
        val regex = buildDecimalRegex(decimal ?: 0L, decimalSeparator)

        if (amount.text.matches(regex)) _amount.update { amount }
    }

    private fun buildDecimalRegex(maxDecimalPlaces: Long, decimalSeparator: Char): Regex {
        val escapedSeparator = Regex.escape(decimalSeparator.toString())

        return if (maxDecimalPlaces == 0L) {
            Regex("^\\d+\$")
        } else {
            Regex("^\\d+($escapedSeparator\\d{0,$maxDecimalPlaces})?\$")
        }
    }

}