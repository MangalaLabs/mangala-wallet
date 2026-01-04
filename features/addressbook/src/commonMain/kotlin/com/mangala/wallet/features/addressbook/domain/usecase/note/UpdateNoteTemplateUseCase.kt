package com.mangala.wallet.features.addressbook.domain.usecase.note

import com.mangala.wallet.features.addressbook.data.model.note.NoteTemplateEntity
import com.mangala.wallet.features.addressbook.domain.repository.note.NoteTemplateRepository

class UpdateNoteTemplateUseCase(
    private val noteTemplateRepository: NoteTemplateRepository
) {
    suspend operator fun invoke(templateId: String, name: String, content: String): Result<NoteTemplateEntity> {
        return noteTemplateRepository.updateNoteTemplate(templateId, name, content)
    }
}