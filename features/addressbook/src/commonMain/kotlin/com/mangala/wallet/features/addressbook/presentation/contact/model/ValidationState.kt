package com.mangala.wallet.features.addressbook.presentation.contact.model

import com.mangala.wallet.features.addressbook.presentation.contact.validation.ValidationLoadingState

data class ValidationState(
    val state: ValidationLoadingState = ValidationLoadingState.IDLE,
    val error: String? = null
)