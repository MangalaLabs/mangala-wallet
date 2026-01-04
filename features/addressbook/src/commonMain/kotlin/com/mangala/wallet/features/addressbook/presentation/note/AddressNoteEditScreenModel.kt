package com.mangala.wallet.features.addressbook.presentation.note

import cafe.adriel.voyager.core.model.screenModelScope
import com.mangala.wallet.features.addressbook.domain.usecase.note.AddAddressNoteUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.note.GetAddressNoteByIdUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.note.UpdateAddressNoteUseCase
import com.mangala.wallet.ui.utils.screenmodel.BaseScreenModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant

class AddressNoteEditScreenModel(
    private val getAddressNoteByIdUseCase: GetAddressNoteByIdUseCase,
    private val addAddressNoteUseCase: AddAddressNoteUseCase,
    private val updateAddressNoteUseCase: UpdateAddressNoteUseCase
) : BaseScreenModel() {

    private val _state = MutableStateFlow(AddressNoteEditState())
    val state: StateFlow<AddressNoteEditState> = _state.asStateFlow()

    fun onEvent(event: AddressNoteEvent) {
        // Phần đầu của hàm onEvent đã được triển khai ở file trước

        when (event) {
            is AddressNoteEvent.LoadNote -> loadNote(event.noteId)
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
            is AddressNoteEvent.ClearError -> clearError()
            else -> {} // Other events are handled by different ScreenModels
        }
    }

    private fun loadNote(noteId: String) {
        screenModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            getAddressNoteByIdUseCase(noteId).fold(
                onSuccess = { note ->
                    _state.update {
                        it.copy(
                            noteId = note.id,
                            walletAddressId = note.walletAddressId,
                            content = note.content,
                            formatOptions = note.formatOptions,
                            aiSuggestionSource = note.aiSuggestionSource,
                            reminderDate = note.reminderDate,
                            isLoading = false
                        )
                    }
                },
                onFailure = { error ->
                    _state.update { it.copy(error = error.message, isLoading = false) }
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
            _state.update {
                it.copy(
                    isLoading = true,
                    error = null,
                    isSuccess = false
                )
            }

            addAddressNoteUseCase(
                walletAddressId, content, formatOptions, aiSuggestionSource, reminderDate
            ).fold(
                onSuccess = { note ->
                    _state.update {
                        it.copy(
                            noteId = note.id,
                            isLoading = false,
                            isSuccess = true
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
            _state.update {
                it.copy(
                    isLoading = true,
                    error = null,
                    isSuccess = false
                )
            }

            updateAddressNoteUseCase(
                noteId, content, formatOptions, aiSuggestionSource, reminderDate
            ).fold(
                onSuccess = { note ->
                    _state.update {
                        it.copy(
                            noteId = note.id,
                            content = note.content,
                            formatOptions = note.formatOptions,
                            aiSuggestionSource = note.aiSuggestionSource,
                            reminderDate = note.reminderDate,
                            isLoading = false,
                            isSuccess = true
                        )
                    }
                },
                onFailure = { error ->
                    _state.update { it.copy(error = error.message, isLoading = false) }
                }
            )
        }
    }

    private fun clearError() {
        _state.update { it.copy(error = null) }
    }
}