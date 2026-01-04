package com.mangala.wallet.features.addressbook.data.local.note

import com.benasher44.uuid.uuid4
import com.mangala.wallet.features.addressbook.data.local.AddressBookDatabaseWrapper
import com.mangala.wallet.features.addressbook.data.model.note.AddressNoteHistoryEntity
import com.mangala.wallet.utils.localDateTimeNow
import com.mangala.wallet.utils.localDateTimeToMillis
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import kotlinx.datetime.Instant

class AddressNoteHistoryLocalDataSourceImpl(
    databaseWrapper: AddressBookDatabaseWrapper,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : AddressNoteHistoryLocalDataSource {

    private val database = databaseWrapper.database
    private val dbQuery = database.addressBookDatabaseQueries

    override suspend fun getAddressNoteHistoryByNoteId(noteId: String): Flow<List<AddressNoteHistoryEntity>> = flow {
        val history = withContext(ioDispatcher) {
            dbQuery.getAddressNoteHistoryByNoteId(noteId)
                .executeAsList()
                .map { mapToAddressNoteHistoryEntity(
                    it.id,
                    it.note_id,
                    it.wallet_address_id,
                    it.old_content,
                    it.changed_at,
                    it.changed_by
                ) }
        }
        emit(history)
    }.flowOn(ioDispatcher)

    override suspend fun insertAddressNoteHistory(
        noteId: String,
        walletAddressId: String,
        oldContent: String,
        changedBy: String?
    ): AddressNoteHistoryEntity = withContext(ioDispatcher) {
        val now = localDateTimeNow()
        val nowMillis = localDateTimeToMillis(now)
        val id = uuid4().toString()

        try {
            dbQuery.insertAddressNoteHistory(
                id = id,
                note_id = noteId,
                wallet_address_id = walletAddressId,
                old_content = oldContent,
                changed_at = nowMillis.toString(),
                changed_by = changedBy
            )

            // Create entity to return
            AddressNoteHistoryEntity(
                id = id,
                noteId = noteId,
                walletAddressId = walletAddressId,
                oldContent = oldContent,
                changedAt = Instant.parse(nowMillis.toString()),
                changedBy = changedBy
            )
        } catch (e: Exception) {
            throw Exception("Failed to insert address note history: ${e.message}", e)
        }
    }

    /**
     * Maps AddressNoteHistory from database to AddressNoteHistoryEntity
     */
    private fun mapToAddressNoteHistoryEntity(
        id: String,
        noteId: String,
        walletAddressId: String,
        oldContent: String,
        changedAt: String,
        changedBy: String?
    ): AddressNoteHistoryEntity {
        return AddressNoteHistoryEntity(
            id = id,
            noteId = noteId,
            walletAddressId = walletAddressId,
            oldContent = oldContent,
            changedAt = changedAt.let { Instant.parse(it) },
            changedBy = changedBy
        )
    }
}