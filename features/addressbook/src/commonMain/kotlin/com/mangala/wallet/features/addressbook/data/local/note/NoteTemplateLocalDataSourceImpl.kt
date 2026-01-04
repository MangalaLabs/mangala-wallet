package com.mangala.wallet.features.addressbook.data.local.note

import com.benasher44.uuid.uuid4
import com.mangala.wallet.features.addressbook.data.local.AddressBookDatabaseWrapper
import com.mangala.wallet.features.addressbook.data.model.note.NoteTemplateEntity
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

class NoteTemplateLocalDataSourceImpl(
    databaseWrapper: AddressBookDatabaseWrapper,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : NoteTemplateLocalDataSource {

    private val database = databaseWrapper.database
    private val dbQuery = database.addressBookDatabaseQueries

    override suspend fun getAllNoteTemplates(): Flow<List<NoteTemplateEntity>> = flow {
        val templates = withContext(ioDispatcher) {
            dbQuery.getAllNoteTemplates()
                .executeAsList()
                .map { mapToNoteTemplateEntity(
                    it.id,
                    it.name,
                    it.content,
                    it.created_at,
                    it.updated_at
                ) }
        }
        emit(templates)
    }.flowOn(ioDispatcher)

    override suspend fun getNoteTemplateById(templateId: String): NoteTemplateEntity? = withContext(ioDispatcher) {
        try {
            dbQuery.getNoteTemplateById(templateId)
                .executeAsOneOrNull()
                ?.let { mapToNoteTemplateEntity(
                    it.id,
                    it.name,
                    it.content,
                    it.created_at,
                    it.updated_at
                ) }
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun insertNoteTemplate(
        name: String,
        content: String
    ): NoteTemplateEntity = withContext(ioDispatcher) {
        val now = localDateTimeNow()
        val nowMillis = localDateTimeToMillis(now)
        val id = uuid4().toString()

        try {
            dbQuery.insertNoteTemplate(
                id = id,
                name = name,
                content = content,
                created_at = nowMillis.toString(),
                updated_at = null
            )

            // Create entity to return
            NoteTemplateEntity(
                id = id,
                name = name,
                content = content,
                createdAt = Instant.parse(nowMillis.toString()),
                updatedAt = null
            )
        } catch (e: Exception) {
            throw Exception("Failed to insert note template: ${e.message}", e)
        }
    }

    override suspend fun updateNoteTemplate(
        templateId: String,
        name: String,
        content: String
    ): NoteTemplateEntity? = withContext(ioDispatcher) {
        val now = localDateTimeNow()
        val nowMillis = localDateTimeToMillis(now)

        try {
            dbQuery.updateNoteTemplate(
                name = name,
                content = content,
                updated_at = nowMillis.toString(),
                id = templateId
            )

            // Get the updated template to return
            dbQuery.getNoteTemplateById(templateId)
                .executeAsOneOrNull()
                ?.let { mapToNoteTemplateEntity(
                    it.id,
                    it.name,
                    it.content,
                    it.created_at,
                    it.updated_at
                ) }
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun deleteNoteTemplate(templateId: String): Boolean = withContext(ioDispatcher) {
        try {
            dbQuery.deleteNoteTemplate(templateId)
            true
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Maps NoteTemplate from database to NoteTemplateEntity
     */
    private fun mapToNoteTemplateEntity(
        id: String,
        name: String,
        content: String,
        createdAt: String,
        updatedAt: String?
    ): NoteTemplateEntity {
        return NoteTemplateEntity(
            id = id,
            name = name,
            content = content,
            createdAt = createdAt.let { Instant.parse(it) },
            updatedAt = updatedAt?.let { Instant.parse(it) }
        )
    }
}