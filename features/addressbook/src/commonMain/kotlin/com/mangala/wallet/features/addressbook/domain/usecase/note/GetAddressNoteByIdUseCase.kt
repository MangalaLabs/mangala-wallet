package com.mangala.wallet.features.addressbook.domain.usecase.note

import com.mangala.wallet.features.addressbook.data.model.note.AddressNoteEntity
import com.mangala.wallet.features.addressbook.domain.repository.note.AddressNoteRepository

class GetAddressNoteByIdUseCase(
    private val addressNoteRepository: AddressNoteRepository
) {
    suspend operator fun invoke(noteId: String): Result<AddressNoteEntity> {
        return addressNoteRepository.getAddressNoteById(noteId)
    }
}