package com.mangala.wallet.features.addressbook.domain.repository.note

import com.mangala.wallet.features.addressbook.data.model.note.NoteTemplateEntity
import kotlinx.coroutines.flow.Flow

interface NoteTemplateRepository {
    suspend fun getAllNoteTemplates(): Result<List<NoteTemplateEntity>>

    suspend fun getNoteTemplateById(templateId: String): Result<NoteTemplateEntity>

    suspend fun createNoteTemplate(name: String, content: String): Result<NoteTemplateEntity>

    suspend fun updateNoteTemplate(templateId: String, name: String, content: String): Result<NoteTemplateEntity>

    suspend fun deleteNoteTemplate(templateId: String): Result<Boolean>
}