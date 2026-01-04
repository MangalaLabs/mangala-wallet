package com.mangala.wallet.features.addressbook.presentation.contact.model

import com.mangala.wallet.features.addressbook.presentation.contact.validation.ValidationLoadingState
import kotlinx.datetime.Instant

// UI State classes for ContactScreen and ContactScreenModel
// These data classes represent the UI state for various contact information fields

data class WalletAddressUiState(
    val id: String,
    val address: String,
    val label: String,
    val blockchain: String,
    val blockchainUid: String,
    val isPrimary: Boolean = false,
    val isValid: Boolean = true,
    val isTempInvalid: Boolean = false,
    val error: String? = null,
    val warning: String? = null,
    val validationState: ValidationLoadingState = ValidationLoadingState.IDLE,
    val placeholder: String = "",
    val mask: String? = null,
    val isHighSecurity: Boolean = false,
    val alias: String? = null,
    val isSensitive: Boolean = false,
    val suggestion: String? = null,
    val resolvedAddress: String? = null,
    val isResolvingDomain: Boolean = false
)

data class EmailAddressUiState(
    val id: String,
    val email: String,
    val label: String,
    val isPrimary: Boolean = false,
    val isValid: Boolean = true,
    val error: String? = null
)

data class PhoneNumberUiState(
    val id: String,
    val number: String,
    val label: String,
    val isPrimary: Boolean = false,
    val isValid: Boolean = true,
    val error: String? = null
)

data class PhysicalAddressUiState(
    val id: String,
    val street: String,
    val street2: String,
    val city: String,
    val state: String,
    val zip: String,
    val country: String,
    val label: String,
    val isPrimary: Boolean = false,
    val isValid: Boolean = true,
    val error: String? = null,
    val streetAddress: String = "",
    val ward: String = "",
    val district: String = "",
    val stateProvince: String = "",
    val postalCode: String = "",
    val addressType: String = ""
)

data class SocialProfileUiState(
    val id: String,
    val platform: String,
    val handle: String,
    val isPrimary: Boolean = false,
    val isValid: Boolean = true,
    val error: String? = null,
    val username: String = "",
    val url: String? = null
)

data class RelatedNameUiState(
    val id: String,
    val name: String,
    val relationship: String,
    val isPrimary: Boolean = false,
    val isValid: Boolean = true,
    val error: String? = null
)

data class ImportantDateUiState(
    val id: String,
    val date: Instant,
    val label: String,
    val calendarType: String,
    val isPrimary: Boolean = false,
    val isValid: Boolean = true,
    val error: String? = null,
    val description: String? = null
)

