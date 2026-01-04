package com.mangala.wallet.features.contacts.presentation

import com.mangala.wallet.model.contact.ContactEntity

sealed interface ContactsScreenUiState {
    data object Empty: ContactsScreenUiState
    data class Data(val contacts: List<ContactEntity>): ContactsScreenUiState
}
