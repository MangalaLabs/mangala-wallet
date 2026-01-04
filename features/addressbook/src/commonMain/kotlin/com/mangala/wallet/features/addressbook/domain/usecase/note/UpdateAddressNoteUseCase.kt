package com.mangala.wallet.features.addressbook.domain.usecase.note

import com.mangala.wallet.features.addressbook.data.model.note.AddressNoteEntity
import com.mangala.wallet.features.addressbook.domain.repository.note.AddressNoteRepository
import kotlinx.datetime.Instant

class UpdateAddressNoteUseCase(
    private val addressNoteRepository: AddressNoteRepository
) {
    suspend operator fun invoke(
        noteId: String,
        content: String,
        formatOptions: String? = null,
        aiSuggestionSource: String? = null,
        reminderDate: Instant? = null
    ): Result<AddressNoteEntity> {
        return addressNoteRepository.updateAddressNote(
            noteId = noteId,
            content = content,
            formatOptions = formatOptions,
            aiSuggestionSource = aiSuggestionSource,
            reminderDate = reminderDate
        )
    }
}