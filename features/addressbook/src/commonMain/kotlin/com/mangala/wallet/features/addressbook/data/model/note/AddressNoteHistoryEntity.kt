package com.mangala.wallet.features.addressbook.data.model.note

import kotlinx.datetime.Instant

data class AddressNoteHistoryEntity(
    val id: String,
    val noteId: String,
    val walletAddressId: String,
    val oldContent: String,
    val changedAt: Instant,
    val changedBy: String?
)