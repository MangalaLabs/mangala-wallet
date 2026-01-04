package com.mangala.wallet.features.addressbook.data.local.note

import com.mangala.wallet.features.addressbook.data.model.note.AddressNoteHistoryEntity
import kotlinx.coroutines.flow.Flow

interface AddressNoteHistoryLocalDataSource {
    // Address Note History
    suspend fun getAddressNoteHistoryByNoteId(noteId: String): Flow<List<AddressNoteHistoryEntity>>
    suspend fun insertAddressNoteHistory(
        noteId: String,
        walletAddressId: String,
        oldContent: String,
        changedBy: String? = null
    ): AddressNoteHistoryEntity
}