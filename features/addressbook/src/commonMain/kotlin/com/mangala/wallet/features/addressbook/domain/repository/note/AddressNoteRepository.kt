package com.mangala.wallet.features.addressbook.domain.repository.note

import com.mangala.wallet.features.addressbook.data.model.note.AddressNoteEntity
import com.mangala.wallet.features.addressbook.data.model.note.AddressNoteHistoryEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.Instant

interface AddressNoteRepository {
    suspend fun getAddressNotesByWalletAddressId(walletAddressId: String): Result<List<AddressNoteEntity>>

    suspend fun getAddressNoteById(noteId: String): Result<AddressNoteEntity>

    suspend fun addAddressNote(
        walletAddressId: String,
        content: String,
        formatOptions: String? = null,
        aiSuggestionSource: String? = null,
        reminderDate: Instant? = null
    ): Result<AddressNoteEntity>

    suspend fun updateAddressNote(
        noteId: String,
        content: String,
        formatOptions: String? = null,
        aiSuggestionSource: String? = null,
        reminderDate: Instant? = null
    ): Result<AddressNoteEntity>

    suspend fun deleteAddressNote(noteId: String): Result<Boolean>

    suspend fun searchAddressNotes(walletAddressId: String, searchQuery: String): Result<List<AddressNoteEntity>>

    suspend fun getAddressNoteHistory(noteId: String): Result<List<AddressNoteHistoryEntity>>
}