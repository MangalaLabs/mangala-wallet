package com.mangala.wallet.features.addressbook.presentation.contact

import cafe.adriel.voyager.core.model.screenModelScope
import com.mangala.wallet.features.addressbook.data.model.tag.TagEntity
import com.mangala.wallet.features.addressbook.domain.usecase.tag.BatchAssignTagsToContactUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.tag.GetActiveTagsUseCase
import com.mangala.wallet.ui.utils.screenmodel.BaseScreenModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ScreenModel for batch tag operations
 * Used for applying tags to multiple contacts at once
 */
class BatchTagManagementScreenModel(
    private val selectedContactId: String,
    private val getActiveTagsUseCase: GetActiveTagsUseCase,
    private val batchAssignTagsToContactUseCase: BatchAssignTagsToContactUseCase
): BaseScreenModel() {
    private val _state = MutableStateFlow(BatchTagManagementState())
    val state: StateFlow<BatchTagManagementState> = _state.asStateFlow()

    private val _events = MutableSharedFlow<BatchTagEvent>()
    val events: SharedFlow<BatchTagEvent> = _events.asSharedFlow()

    // Selected tags for batch operation
    private val selectedTagIds = mutableSetOf<String>()

    init {
        _state.update { it.copy(contactCount = 1) }
        loadTags()
    }

    private fun loadTags() {
        screenModelScope.launch {
            try {
                _state.update { it.copy(isLoading = true) }

                getActiveTagsUseCase().fold(
                    onSuccess = { tags ->
                        _state.update {
                            it.copy(
                                availableTags = tags,
                                isLoading = false
                            )
                        }
                    },
                    onFailure = { exception ->
                        val errorMessage = exception.message ?: "Failed to load tags"
                        _state.update {
                            it.copy(
                                isLoading = false,
                                error = errorMessage
                            )
                        }
                        _events.emit(BatchTagEvent.Error(errorMessage))
                    }
                )
            } catch (e: Exception) {
                val errorMessage = e.message ?: "Failed to load tags"
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = errorMessage
                    )
                }
                _events.emit(BatchTagEvent.Error(errorMessage))
            }
        }
    }

    fun toggleTagSelection(tagId: String) {
        if (selectedTagIds.contains(tagId)) {
            selectedTagIds.remove(tagId)
        } else {
            selectedTagIds.add(tagId)
        }

        _state.update { it.copy(selectedTagIds = selectedTagIds.toList()) }
    }

    fun applyTagsToContacts() {
        if (selectedTagIds.isEmpty()) {
            screenModelScope.launch {
                _events.emit(BatchTagEvent.ValidationError("Please select at least one tag to apply"))
            }
            return
        }

        screenModelScope.launch {
            try {
                _state.update { it.copy(isProcessing = true) }

                val result = batchAssignTagsToContactUseCase(
                    contactId = selectedContactId,
                    tagIds = selectedTagIds.toList()
                )

                if (result.isSuccess) {
                    _events.emit(BatchTagEvent.TagsApplied(
                        "Tags applied to ${1} contacts"
                    ))
                } else {
                    val error = result.exceptionOrNull()?.message ?: "Failed to apply tags"
                    _state.update { it.copy(error = error) }
                    _events.emit(BatchTagEvent.Error(error))
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        error = e.message ?: "Failed to apply tags"
                    )
                }
                _events.emit(BatchTagEvent.Error(e.message ?: "Failed to apply tags"))
            } finally {
                _state.update { it.copy(isProcessing = false) }
            }
        }
    }

    fun clearError() {
        _state.update { it.copy(error = null) }
    }
}

data class BatchTagManagementState(
    val contactCount: Int = 0,
    val availableTags: List<TagEntity> = emptyList(),
    val selectedTagIds: List<String> = emptyList(),
    val isLoading: Boolean = false,
    val isProcessing: Boolean = false,
    val error: String? = null
)

sealed class BatchTagEvent {
    data class TagsApplied(val message: String) : BatchTagEvent()
    data class Error(val message: String) : BatchTagEvent()
    data class ValidationError(val message: String) : BatchTagEvent()
}