package com.mangala.wallet.features.addressbook.data.repository.note

import com.mangala.wallet.features.addressbook.data.local.note.NoteTemplateLocalDataSource
import com.mangala.wallet.features.addressbook.data.model.note.NoteTemplateEntity
import com.mangala.wallet.features.addressbook.domain.repository.note.NoteTemplateRepository
import kotlinx.coroutines.flow.first

class NoteTemplateRepositoryImpl(
    private val noteTemplateLocalDataSource: NoteTemplateLocalDataSource
) : NoteTemplateRepository {

    override suspend fun getAllNoteTemplates(): Result<List<NoteTemplateEntity>> {
        return try {
            val templates = noteTemplateLocalDataSource.getAllNoteTemplates().first()
            Result.success(templates)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getNoteTemplateById(templateId: String): Result<NoteTemplateEntity> {
        return try {
            val templateEntity = noteTemplateLocalDataSource.getNoteTemplateById(templateId)
            if (templateEntity != null) {
                Result.success(templateEntity)
            } else {
                Result.failure(NoSuchElementException("Template not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun createNoteTemplate(name: String, content: String): Result<NoteTemplateEntity> {
        return try {
            val newTemplate = noteTemplateLocalDataSource.insertNoteTemplate(
                name = name,
                content = content
            )
            Result.success(newTemplate)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateNoteTemplate(templateId: String, name: String, content: String): Result<NoteTemplateEntity> {
        return try {
            val updatedTemplate = noteTemplateLocalDataSource.updateNoteTemplate(
                templateId = templateId,
                name = name,
                content = content
            )

            if (updatedTemplate != null) {
                Result.success(updatedTemplate)
            } else {
                Result.failure(Exception("Failed to update template"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteNoteTemplate(templateId: String): Result<Boolean> {
        return try {
            val result = noteTemplateLocalDataSource.deleteNoteTemplate(templateId)
            if (result) {
                Result.success(true)
            } else {
                Result.failure(Exception("Failed to delete template"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}