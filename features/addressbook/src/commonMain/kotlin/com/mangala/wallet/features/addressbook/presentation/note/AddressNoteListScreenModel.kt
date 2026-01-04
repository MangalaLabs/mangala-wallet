package com.mangala.wallet.features.addressbook.presentation.note

import cafe.adriel.voyager.core.model.screenModelScope
import com.mangala.wallet.features.addressbook.domain.usecase.note.DeleteAddressNoteUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.note.GetAddressNotesUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.note.SearchAddressNotesUseCase
import com.mangala.wallet.ui.utils.screenmodel.BaseScreenModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AddressNoteListScreenModel(
    private val getAddressNotesUseCase: GetAddressNotesUseCase,
    private val searchAddressNotesUseCase: SearchAddressNotesUseCase,
    private val deleteAddressNoteUseCase: DeleteAddressNoteUseCase
) : BaseScreenModel() {

    private val _state = MutableStateFlow(AddressNoteListState())
    val state: StateFlow<AddressNoteListState> = _state.asStateFlow()

    fun onEvent(event: AddressNoteEvent) {
        when (event) {
            is AddressNoteEvent.LoadNotes -> loadNotes(event.walletAddressId)
            is AddressNoteEvent.SearchNotes -> searchNotes(event.query)
            is AddressNoteEvent.DeleteNote -> deleteNote(event.noteId)
            is AddressNoteEvent.ClearError -> clearError()
            else -> {} // Other events are handled by different ScreenModels
        }
    }

    private fun loadNotes(walletAddressId: String) {
        screenModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            getAddressNotesUseCase(walletAddressId).fold(
                onSuccess = { notes ->
                    _state.update {
                        it.copy(
                            notes = notes,
                            filteredNotes = notes,
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

    private fun searchNotes(query: String) {
        val trimmedQuery = query.trim()
        _state.update { it.copy(searchQuery = trimmedQuery) }

        if (trimmedQuery.isEmpty()) {
            _state.update { it.copy(filteredNotes = it.notes) }
            return
        }

        val currentWalletAddressId = _state.value.notes.firstOrNull()?.walletAddressId ?: return

        screenModelScope.launch {
            searchAddressNotesUseCase(currentWalletAddressId, trimmedQuery).fold(
                onSuccess = { notes ->
                    _state.update { it.copy(filteredNotes = notes) }
                },
                onFailure = { error ->
                    _state.update { it.copy(error = error.message) }
                }
            )
        }
    }

    private fun deleteNote(noteId: String) {
        screenModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            deleteAddressNoteUseCase(noteId).fold(
                onSuccess = {
                    // Refresh the list
                    val currentNotes = _state.value.notes
                    if (currentNotes.isNotEmpty()) {
                        val walletAddressId = currentNotes.first().walletAddressId
                        loadNotes(walletAddressId)
                    } else {
                        _state.update { it.copy(isLoading = false) }
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