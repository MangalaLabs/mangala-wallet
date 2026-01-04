package com.mangala.wallet.features.settings.currency.presentation

import androidx.compose.ui.graphics.vector.ImageVector
import com.mangala.wallet.model.currency.Currency
import dev.icerock.moko.resources.ImageResource

data class CurrencyScreenUiModel(val currency: Currency, val currencyName: String,val currencyIcon: ImageVector, val isSelected: Boolean = false)
