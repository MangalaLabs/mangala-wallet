package com.mangala.wallet.features.addressbook.domain.usecase.note

import com.mangala.wallet.features.addressbook.data.model.note.NoteTemplateEntity
import com.mangala.wallet.features.addressbook.domain.repository.note.NoteTemplateRepository

class GetNoteTemplateByIdUseCase(
    private val noteTemplateRepository: NoteTemplateRepository
) {
    suspend operator fun invoke(templateId: String): Result<NoteTemplateEntity> {
        return noteTemplateRepository.getNoteTemplateById(templateId)
    }
}