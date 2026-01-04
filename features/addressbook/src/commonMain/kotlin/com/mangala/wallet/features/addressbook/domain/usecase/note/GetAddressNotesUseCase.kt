package com.mangala.wallet.features.addressbook.domain.usecase.note

import com.mangala.wallet.features.addressbook.data.model.note.AddressNoteEntity
import com.mangala.wallet.features.addressbook.domain.repository.note.AddressNoteRepository

class GetAddressNotesUseCase(
    private val addressNoteRepository: AddressNoteRepository
) {
    suspend operator fun invoke(walletAddressId: String): Result<List<AddressNoteEntity>> {
        return addressNoteRepository.getAddressNotesByWalletAddressId(walletAddressId)
    }
}