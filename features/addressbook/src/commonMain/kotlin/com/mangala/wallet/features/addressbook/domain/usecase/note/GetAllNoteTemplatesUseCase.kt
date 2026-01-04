package com.mangala.wallet.features.addressbook.domain.usecase.note

import com.mangala.wallet.features.addressbook.data.model.note.NoteTemplateEntity
import com.mangala.wallet.features.addressbook.domain.repository.note.NoteTemplateRepository

class GetAllNoteTemplatesUseCase(
    private val noteTemplateRepository: NoteTemplateRepository
) {
    suspend operator fun invoke(): Result<List<NoteTemplateEntity>> {
        return noteTemplateRepository.getAllNoteTemplates()
    }
}