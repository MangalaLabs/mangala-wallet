package com.mangala.wallet.features.addressbook.presentation.note

import com.mangala.wallet.features.addressbook.data.model.note.AddressNoteEntity
import com.mangala.wallet.features.addressbook.data.model.note.AddressNoteHistoryEntity
import com.mangala.wallet.features.addressbook.data.model.note.NoteTemplateEntity
import kotlinx.datetime.Instant

/**
 * State for Address Note List screen
 */
data class AddressNoteListState(
    val notes: List<AddressNoteEntity> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val searchQuery: String = "",
    val filteredNotes: List<AddressNoteEntity> = emptyList()
)

/**
 * State for Address Note Detail screen
 */
data class AddressNoteDetailState(
    val note: AddressNoteEntity? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val history: List<AddressNoteHistoryEntity> = emptyList(),
    val isHistoryLoading: Boolean = false,
    val historyError: String? = null,
    val isEditing: Boolean = false
)

/**
 * State for Address Note Edit screen
 */
data class AddressNoteEditState(
    val noteId: String? = null,
    val walletAddressId: String = "",
    val content: String = "",
    val formatOptions: String? = null,
    val aiSuggestionSource: String? = null,
    val reminderDate: Instant? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSuccess: Boolean = false,
    val isPremium: Boolean = false
)

/**
 * State for Note Template List screen
 */
data class NoteTemplateListState(
    val templates: List<NoteTemplateEntity> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

/**
 * State for Note Template Edit screen
 */
data class NoteTemplateEditState(
    val templateId: String? = null,
    val name: String = "",
    val content: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSuccess: Boolean = false
)

/**
 * Events for Address Note screens
 */
sealed class AddressNoteEvent {
    data class LoadNotes(val walletAddressId: String) : AddressNoteEvent()
    data class LoadNote(val noteId: String) : AddressNoteEvent()
    data class SearchNotes(val query: String) : AddressNoteEvent()
    data class AddNote(
        val walletAddressId: String,
        val content: String,
        val formatOptions: String? = null,
        val aiSuggestionSource: String? = null,
        val reminderDate: Instant? = null
    ) : AddressNoteEvent()
    data class UpdateNote(
        val noteId: String,
        val content: String,
        val formatOptions: String? = null,
        val aiSuggestionSource: String? = null,
        val reminderDate: Instant? = null
    ) : AddressNoteEvent()
    data class DeleteNote(val noteId: String) : AddressNoteEvent()
    data class LoadNoteHistory(val noteId: String) : AddressNoteEvent()
    object StartEditing : AddressNoteEvent()
    object StopEditing : AddressNoteEvent()
    object ClearError : AddressNoteEvent()
}

/**
 * Events for Note Template screens
 */
sealed class NoteTemplateEvent {
    object LoadTemplates : NoteTemplateEvent()
    data class LoadTemplate(val templateId: String) : NoteTemplateEvent()
    data class CreateTemplate(val name: String, val content: String) : NoteTemplateEvent()
    data class UpdateTemplate(val templateId: String, val name: String, val content: String) : NoteTemplateEvent()
    data class DeleteTemplate(val templateId: String) : NoteTemplateEvent()
    data class UseTemplate(val templateId: String) : NoteTemplateEvent()
    object ClearError : NoteTemplateEvent()
}