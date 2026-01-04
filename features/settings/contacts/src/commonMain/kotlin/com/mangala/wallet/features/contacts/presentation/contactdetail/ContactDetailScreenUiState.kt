package com.mangala.wallet.features.contacts.presentation.contactdetail

import com.mangala.wallet.features.chains.evmcompatible.model.Address
import com.mangala.wallet.model.contact.ContactEntity

sealed class ContactDetailScreenUiState() {
    data object Loading : ContactDetailScreenUiState()
    data class Success(val contact: ContactEntity) :
        ContactDetailScreenUiState()
}

fun ContactEntity.formattedBip44Address() = Address(address).eip55.take(12) + "..." + Address(address).eip55.takeLast(12)

