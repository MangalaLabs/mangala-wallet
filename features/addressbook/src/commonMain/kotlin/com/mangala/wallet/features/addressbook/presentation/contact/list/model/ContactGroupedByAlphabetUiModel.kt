package com.mangala.wallet.features.addressbook.presentation.contact.list.model

import com.mangala.wallet.features.addressbook.data.model.ContactWithMultipleBlockchainsModel

sealed interface ContactGroupedByAlphabetUiModel {
    data class ContactItem(val contact: ContactWithMultipleBlockchainsModel) : ContactGroupedByAlphabetUiModel
    data class AlphabetHeader(val alphabet: String) : ContactGroupedByAlphabetUiModel
}