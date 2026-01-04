package com.mangala.wallet.features.addressbook.data.local.note

import com.benasher44.uuid.uuid4
import com.mangala.wallet.features.addressbook.data.local.AddressBookDatabaseWrapper
import com.mangala.wallet.features.addressbook.data.model.note.AddressNoteEntity
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

class AddressNoteLocalDataSourceImpl(
    databaseWrapper: AddressBookDatabaseWrapper,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : AddressNoteLocalDataSource {

    private val database = databaseWrapper.database
    private val dbQuery = database.addressBookDatabaseQueries

    override suspend fun getAddressNotesByWalletAddressId(walletAddressId: String): List<AddressNoteEntity> = withContext(ioDispatcher) {
        dbQuery.getAddressNotesByWalletAddressId(walletAddressId)
            .executeAsList()
            .map { mapToAddressNoteEntity(
                it.id,
                it.wallet_address_id,
                it.content,
                it.format_options,
                it.created_at,
                it.updated_at,
                it.is_deleted,
                it.ai_suggestion_source,
                it.reminder_date
            ) }
    }

    override suspend fun getAddressNoteById(noteId: String): AddressNoteEntity? = withContext(ioDispatcher) {
        try {
            dbQuery.getAddressNoteById(noteId)
                .executeAsOneOrNull()
                ?.let { mapToAddressNoteEntity(
                    it.id,
                    it.wallet_address_id,
                    it.content,
                    it.format_options,
                    it.created_at,
                    it.updated_at,
                    it.is_deleted,
                    it.ai_suggestion_source,
                    it.reminder_date
                ) }
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun insertAddressNote(
        walletAddressId: String,
        content: String,
        formatOptions: String?,
        aiSuggestionSource: String?,
        reminderDate: Instant?
    ): AddressNoteEntity = withContext(ioDispatcher) {
        val now = localDateTimeNow()
        val nowMillis = localDateTimeToMillis(now)
        val id = uuid4().toString()

        try {
            dbQuery.insertAddressNote(
                id = id,
                wallet_address_id = walletAddressId,
                content = content,
                format_options = formatOptions,
                created_at = nowMillis.toString(),
                updated_at = null,
                is_deleted = 0L,
                ai_suggestion_source = aiSuggestionSource,
                reminder_date = reminderDate?.toString()
            )

            // Get the inserted note to return
            val addressNote = dbQuery.getAddressNoteById(id).executeAsOne()
            mapToAddressNoteEntity(
                addressNote.id,
                addressNote.wallet_address_id,
                addressNote.content,
                addressNote.format_options,
                addressNote.created_at,
                addressNote.updated_at,
                addressNote.is_deleted,
                addressNote.ai_suggestion_source,
                addressNote.reminder_date
            )
        } catch (e: Exception) {
            throw Exception("Failed to insert address note: ${e.message}", e)
        }
    }

    override suspend fun updateAddressNote(
        noteId: String,
        content: String,
        formatOptions: String?,
        aiSuggestionSource: String?,
        reminderDate: Instant?
    ): AddressNoteEntity? = withContext(ioDispatcher) {
        val now = localDateTimeNow()
        val nowMillis = localDateTimeToMillis(now)

        try {
            // Get the current note first
            val currentNote = dbQuery.getAddressNoteById(noteId)
                .executeAsOneOrNull()
                ?: return@withContext null

            dbQuery.updateAddressNote(
                content = content,
                format_options = formatOptions,
                updated_at = nowMillis.toString(),
                ai_suggestion_source = aiSuggestionSource,
                reminder_date = reminderDate?.toString(),
                id = noteId
            )

            // Get the updated note to return
            val updatedNote = dbQuery.getAddressNoteById(noteId).executeAsOne()
            mapToAddressNoteEntity(
                updatedNote.id,
                updatedNote.wallet_address_id,
                updatedNote.content,
                updatedNote.format_options,
                updatedNote.created_at,
                updatedNote.updated_at,
                updatedNote.is_deleted,
                updatedNote.ai_suggestion_source,
                updatedNote.reminder_date
            )
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun softDeleteAddressNote(noteId: String): Boolean = withContext(ioDispatcher) {
        try {
            val now = localDateTimeNow()
            val nowMillis = localDateTimeToMillis(now)

            dbQuery.softDeleteAddressNote(
                updated_at = nowMillis.toString(),
                id = noteId
            )
            true
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun searchAddressNotes(
        walletAddressId: String,
        searchQuery: String
    ): Flow<List<AddressNoteEntity>> = flow {
        val notes = withContext(ioDispatcher) {
            dbQuery.searchAddressNotes(walletAddressId, searchQuery)
                .executeAsList()
                .map { mapToAddressNoteEntity(
                    it.id,
                    it.wallet_address_id,
                    it.content,
                    it.format_options,
                    it.created_at,
                    it.updated_at,
                    it.is_deleted,
                    it.ai_suggestion_source,
                    it.reminder_date
                ) }
        }
        emit(notes)
    }.flowOn(ioDispatcher)

    /**
     * Maps AddressNote from database to AddressNoteEntity
     */
    private fun mapToAddressNoteEntity(
        id: String,
        wallet_address_id: String,
        content: String,
        format_options: String?,
        created_at: String,
        updated_at: String?,
        is_deleted: Long,
        ai_suggestion_source: String?,
        reminder_date: String?
    ): AddressNoteEntity {
        return AddressNoteEntity(
            id = id,
            walletAddressId = wallet_address_id,
            content = content,
            formatOptions = format_options,
            createdAt = created_at.let { Instant.parse(it) },
            updatedAt = updated_at?.let { Instant.parse(it) },
            isDeleted = is_deleted != 0L,
            aiSuggestionSource = ai_suggestion_source,
            reminderDate = reminder_date?.let { Instant.parse(it) }
        )
    }
}