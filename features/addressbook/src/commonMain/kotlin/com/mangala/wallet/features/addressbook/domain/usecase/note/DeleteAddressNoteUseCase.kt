package com.mangala.wallet.features.addressbook.domain.usecase.note

import com.mangala.wallet.features.addressbook.domain.repository.note.AddressNoteRepository

class DeleteAddressNoteUseCase(
    private val addressNoteRepository: AddressNoteRepository
) {
    suspend operator fun invoke(noteId: String): Result<Boolean> {
        return addressNoteRepository.deleteAddressNote(noteId)
    }
}