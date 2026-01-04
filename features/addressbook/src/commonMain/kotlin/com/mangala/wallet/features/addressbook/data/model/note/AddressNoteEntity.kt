package com.mangala.wallet.features.addressbook.data.model.note

import kotlinx.datetime.Instant

data class AddressNoteEntity(
    val id: String,
    val walletAddressId: String,
    val content: String,
    val formatOptions: String?,
    val createdAt: Instant,
    val updatedAt: Instant?,
    val isDeleted: Boolean,
    val aiSuggestionSource: String?,
    val reminderDate: Instant?
)