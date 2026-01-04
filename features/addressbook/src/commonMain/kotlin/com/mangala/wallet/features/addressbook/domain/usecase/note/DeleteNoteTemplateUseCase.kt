package com.mangala.wallet.features.addressbook.domain.usecase.note

import com.mangala.wallet.features.addressbook.domain.repository.note.NoteTemplateRepository

class DeleteNoteTemplateUseCase(
    private val noteTemplateRepository: NoteTemplateRepository
) {
    suspend operator fun invoke(templateId: String): Result<Boolean> {
        return noteTemplateRepository.deleteNoteTemplate(templateId)
    }
}