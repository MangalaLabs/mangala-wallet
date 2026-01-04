package com.mangala.wallet.features.addressbook.presentation.note

import cafe.adriel.voyager.core.model.screenModelScope
import com.mangala.wallet.features.addressbook.domain.usecase.note.AddAddressNoteUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.note.GetAddressNoteByIdUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.note.GetAddressNoteHistoryUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.note.UpdateAddressNoteUseCase
import com.mangala.wallet.ui.utils.screenmodel.BaseScreenModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant

class AddressNoteDetailScreenModel(
    private val getAddressNoteByIdUseCase: GetAddressNoteByIdUseCase,
    private val getAddressNoteHistoryUseCase: GetAddressNoteHistoryUseCase,
    private val addAddressNoteUseCase: AddAddressNoteUseCase,
    private val updateAddressNoteUseCase: UpdateAddressNoteUseCase
) : BaseScreenModel() {
    private val _state = MutableStateFlow(AddressNoteDetailState())
    val state: StateFlow<AddressNoteDetailState> = _state.asStateFlow()

    fun onEvent(event: AddressNoteEvent) {
        when (event) {
            is AddressNoteEvent.LoadNote -> loadNote(event.noteId)
            is AddressNoteEvent.LoadNoteHistory -> loadNoteHistory(event.noteId)
            is AddressNoteEvent.AddNote -> addNote(
                event.walletAddressId,
                event.content,
                event.formatOptions,
                event.aiSuggestionSource,
                event.reminderDate
            )

            is AddressNoteEvent.UpdateNote -> updateNote(
                event.noteId,
                event.content,
                event.formatOptions,
                event.aiSuggestionSource,
                event.reminderDate
            )

            is AddressNoteEvent.StartEditing -> _state.update { it.copy(isEditing = true) }
            is AddressNoteEvent.StopEditing -> _state.update { it.copy(isEditing = false) }
            is AddressNoteEvent.ClearError -> clearError()
            else -> {} // Other events are handled by different ScreenModels
        }
    }

    private fun loadNote(noteId: String) {
        screenModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            getAddressNoteByIdUseCase(noteId).fold(
                onSuccess = { note ->
                    _state.update { it.copy(note = note, isLoading = false) }
                    // Also load history
                    loadNoteHistory(noteId)
                },
                onFailure = { error ->
                    _state.update { it.copy(error = error.message, isLoading = false) }
                }
            )
        }
    }

    private fun loadNoteHistory(noteId: String) {
        screenModelScope.launch {
            _state.update { it.copy(isHistoryLoading = true, historyError = null) }

            getAddressNoteHistoryUseCase(noteId).fold(
                onSuccess = { history ->
                    _state.update { it.copy(history = history, isHistoryLoading = false) }
                },
                onFailure = { error ->
                    _state.update {
                        it.copy(
                            historyError = error.message,
                            isHistoryLoading = false
                        )
                    }
                }
            )

        }
    }

    private fun addNote(
        walletAddressId: String,
        content: String,
        formatOptions: String?,
        aiSuggestionSource: String?,
        reminderDate: Instant?
    ) {
        screenModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            addAddressNoteUseCase(
                walletAddressId, content, formatOptions, aiSuggestionSource, reminderDate
            ).fold(
                onSuccess = { note ->
                    _state.update {
                        it.copy(
                            note = note,
                            isLoading = false,
                            isEditing = false
                        )
                    }
                },
                onFailure = { error ->
                    _state.update { it.copy(error = error.message, isLoading = false) }
                }
            )
        }
    }

    private fun updateNote(
        noteId: String,
        content: String,
        formatOptions: String?,
        aiSuggestionSource: String?,
        reminderDate: Instant?
    ) {
        screenModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            updateAddressNoteUseCase(
                noteId, content, formatOptions, aiSuggestionSource, reminderDate
            ).fold(
                onSuccess = { note ->
                    _state.update {
                        it.copy(
                            note = note,
                            isLoading = false,
                            isEditing = false
                        )
                    }
                    // Reload history after update
                    loadNoteHistory(noteId)
                },
                onFailure = { error ->
                    _state.update { it.copy(error = error.message, isLoading = false) }
                }
            )
        }
    }

    private fun clearError() {
        _state.update { it.copy(error = null, historyError = null) }
    }
}