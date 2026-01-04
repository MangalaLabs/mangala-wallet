package com.mangala.wallet.features.addressbook.data.local.note

import com.mangala.wallet.features.addressbook.data.model.note.NoteTemplateEntity
import kotlinx.coroutines.flow.Flow

interface NoteTemplateLocalDataSource {
    // Note Templates
    suspend fun getAllNoteTemplates(): Flow<List<NoteTemplateEntity>>
    suspend fun getNoteTemplateById(templateId: String): NoteTemplateEntity?
    suspend fun insertNoteTemplate(name: String, content: String): NoteTemplateEntity
    suspend fun updateNoteTemplate(templateId: String, name: String, content: String): NoteTemplateEntity?
    suspend fun deleteNoteTemplate(templateId: String): Boolean
}