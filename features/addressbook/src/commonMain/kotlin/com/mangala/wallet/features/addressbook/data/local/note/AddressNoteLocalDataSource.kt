package com.mangala.wallet.features.addressbook.data.local.note

import com.mangala.wallet.features.addressbook.data.model.note.AddressNoteEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Instant

interface AddressNoteLocalDataSource {
    // Address Notes
    suspend fun getAddressNotesByWalletAddressId(walletAddressId: String): List<AddressNoteEntity>
    suspend fun getAddressNoteById(noteId: String): AddressNoteEntity?
    suspend fun insertAddressNote(
        walletAddressId: String,
        content: String,
        formatOptions: String? = null,
        aiSuggestionSource: String? = null,
        reminderDate: Instant? = null
    ): AddressNoteEntity
    suspend fun updateAddressNote(
        noteId: String,
        content: String,
        formatOptions: String? = null,
        aiSuggestionSource: String? = null,
        reminderDate: Instant? = null
    ): AddressNoteEntity?
    suspend fun softDeleteAddressNote(noteId: String): Boolean
    suspend fun searchAddressNotes(walletAddressId: String, searchQuery: String): Flow<List<AddressNoteEntity>>
}