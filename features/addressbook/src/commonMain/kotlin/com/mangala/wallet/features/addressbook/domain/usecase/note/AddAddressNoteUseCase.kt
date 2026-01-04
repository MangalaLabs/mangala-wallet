package com.mangala.wallet.features.addressbook.domain.usecase.note

import com.mangala.wallet.features.addressbook.data.model.note.AddressNoteEntity
import com.mangala.wallet.features.addressbook.domain.repository.note.AddressNoteRepository
import kotlinx.datetime.Instant

class AddAddressNoteUseCase(
    private val addressNoteRepository: AddressNoteRepository
) {
    suspend operator fun invoke(
        walletAddressId: String,
        content: String,
        formatOptions: String? = null,
        aiSuggestionSource: String? = null,
        reminderDate: Instant? = null
    ): Result<AddressNoteEntity> {
        return addressNoteRepository.addAddressNote(
            walletAddressId = walletAddressId,
            content = content,
            formatOptions = formatOptions,
            aiSuggestionSource = aiSuggestionSource,
            reminderDate = reminderDate
        )
    }
}