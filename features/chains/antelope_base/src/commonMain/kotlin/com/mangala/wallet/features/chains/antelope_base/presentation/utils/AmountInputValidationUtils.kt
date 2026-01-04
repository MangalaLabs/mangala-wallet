package com.mangala.wallet.features.chains.antelope_base.presentation.utils

object AmountInputValidationUtils {
    fun isValidInput(value: String, precision: Int): Boolean {
        if (isValidInput(value).not()) return false

        if (value.contains('.')) {
            val parts = value.split('.')
            if (parts.size > 1 && parts[1].length > precision) return false
        }

        return true
    }

    fun isValidInput(value: String): Boolean {
        return !(value.isNotEmpty() && value.matches(Regex("^[0-9]*\\.?[0-9]*$")).not())
    }

    const val RESOURCES_PRECISION = 4
}