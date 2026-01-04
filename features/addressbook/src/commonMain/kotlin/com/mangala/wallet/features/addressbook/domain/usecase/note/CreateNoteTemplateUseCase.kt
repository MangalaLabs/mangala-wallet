package com.mangala.wallet.features.addressbook.domain.usecase.note

import com.mangala.wallet.features.addressbook.data.model.note.NoteTemplateEntity
import com.mangala.wallet.features.addressbook.domain.repository.note.NoteTemplateRepository

class CreateNoteTemplateUseCase(
    private val noteTemplateRepository: NoteTemplateRepository
) {
    suspend operator fun invoke(name: String, content: String): Result<NoteTemplateEntity> {
        return noteTemplateRepository.createNoteTemplate(name, content)
    }
}