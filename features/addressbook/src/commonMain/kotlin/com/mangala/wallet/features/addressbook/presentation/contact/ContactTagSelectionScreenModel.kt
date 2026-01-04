package com.mangala.wallet.features.addressbook.presentation.contact

import cafe.adriel.voyager.core.model.screenModelScope
import com.mangala.wallet.features.addressbook.data.model.tag.TagEntity
import com.mangala.wallet.features.addressbook.data.model.enum.USER_SETTING_ID
import com.mangala.wallet.features.addressbook.domain.usecase.setting.GetUserSubscriptionUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.tag.AssignTagToContactUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.tag.GetActiveTagsUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.tag.GetTagsForContactUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.tag.RemoveTagFromContactUseCase
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
 * ScreenModel for managing tags on a contact
 * Used in the contact creation/edit flow
 */
class ContactTagSelectionScreenModel(
    private val contactId: String,
    private val getTagsForContactUseCase: GetTagsForContactUseCase,
    private val getActiveTagsUseCase: GetActiveTagsUseCase,
    private val assignTagToContactUseCase: AssignTagToContactUseCase,
    private val removeTagFromContactUseCase: RemoveTagFromContactUseCase,
    private val getUserSubscriptionUseCase: GetUserSubscriptionUseCase
): BaseScreenModel() {
    private val _state = MutableStateFlow(ContactTagSelectionState())
    val state: StateFlow<ContactTagSelectionState> = _state.asStateFlow()

    private val _events = MutableSharedFlow<ContactTagEvent>()
    val events: SharedFlow<ContactTagEvent> = _events.asSharedFlow()

    init {
        loadData()
    }

    private fun loadData() {
        screenModelScope.launch {
            // Load in parallel for better performance
            launch { loadContactTags() }
            launch { loadAvailableTags() }
            launch { checkSubscriptionStatus() }
        }
    }

    private fun loadContactTags() {
        screenModelScope.launch {
            try {
                _state.update { it.copy(isLoadingContactTags = true) }

                getTagsForContactUseCase(contactId).fold(
                    onSuccess = { tags ->
                        _state.update {
                            it.copy(
                                contactTags = tags,
                                isLoadingContactTags = false
                            )
                        }
                    },
                    onFailure = { exception ->
                        val errorMessage = exception.message ?: "Failed to load contact tags"
                        _state.update {
                            it.copy(
                                isLoadingContactTags = false,
                                error = errorMessage
                            )
                        }
                        _events.emit(ContactTagEvent.Error(errorMessage))
                    }
                )
            } catch (e: Exception) {
                val errorMessage = e.message ?: "Failed to load contact tags"
                _state.update {
                    it.copy(
                        isLoadingContactTags = false,
                        error = errorMessage
                    )
                }
                _events.emit(ContactTagEvent.Error(errorMessage))
            }
        }
    }

    private fun loadAvailableTags() {
        screenModelScope.launch {
            try {
                _state.update { it.copy(isLoadingAvailableTags = true) }

                getActiveTagsUseCase().fold(
                    onSuccess = { allTags ->
                        _state.update { currentState ->
                            // Filter out tags that are already assigned to the contact
                            val availableTags = allTags.filter { tag ->
                                !currentState.contactTags.any { it.id == tag.id }
                            }

                            currentState.copy(
                                availableTags = availableTags,
                                isLoadingAvailableTags = false
                            )
                        }
                    },
                    onFailure = { exception ->
                        val errorMessage = exception.message ?: "Failed to load available tags"
                        _state.update {
                            it.copy(
                                isLoadingAvailableTags = false,
                                error = errorMessage
                            )
                        }
                        _events.emit(ContactTagEvent.Error(errorMessage))
                    }
                )
            } catch (e: Exception) {
                val errorMessage = e.message ?: "Failed to load available tags"
                _state.update {
                    it.copy(
                        isLoadingAvailableTags = false,
                        error = errorMessage
                    )
                }
                _events.emit(ContactTagEvent.Error(errorMessage))
            }
        }
    }

    private fun checkSubscriptionStatus() {
        screenModelScope.launch {
            try {
                val subscription = getUserSubscriptionUseCase(USER_SETTING_ID).getOrNull()
                val isPremium = subscription?.isValid() ?: false

                _state.update { it.copy(isPremium = isPremium) }
            } catch (e: Exception) {
                // Default to non-premium if we can't verify
                _state.update { it.copy(isPremium = false) }
                println("ContactTagSelection, Failed to check subscription status $e")
            }
        }
    }

    fun assignTagToContact(tagId: String) {
        // Check tag limit for free users
        if (!_state.value.isPremium &&
            _state.value.contactTags.size >= MAX_FREE_TAGS_PER_CONTACT
        ) {
            screenModelScope.launch {
                _events.emit(
                    ContactTagEvent.PremiumRequired(
                        "Free users can only add $MAX_FREE_TAGS_PER_CONTACT tags per contact. " +
                                "Upgrade to premium for unlimited tags."
                    )
                )
            }
            return
        }

        screenModelScope.launch {
            try {
                _state.update { it.copy(isProcessing = true) }
                val result = assignTagToContactUseCase(contactId, tagId)

                if (result.isSuccess) {
                    _events.emit(ContactTagEvent.TagAdded)
                    // Refresh both lists
                    loadContactTags()
                    loadAvailableTags()
                } else {
                    val error = result.exceptionOrNull()?.message ?: "Failed to add tag"
                    _state.update { it.copy(error = error) }
                    _events.emit(ContactTagEvent.Error(error))
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        error = e.message ?: "Failed to add tag"
                    )
                }
                _events.emit(ContactTagEvent.Error(e.message ?: "Failed to add tag"))
            } finally {
                _state.update { it.copy(isProcessing = false) }
            }
        }
    }

    fun removeTagFromContact(tagId: String) {
        screenModelScope.launch {
            try {
                _state.update { it.copy(isProcessing = true) }
                val result = removeTagFromContactUseCase(contactId, tagId)

                if (result.isSuccess) {
                    _events.emit(ContactTagEvent.TagRemoved)
                    // Refresh both lists
                    loadContactTags()
                    loadAvailableTags()
                } else {
                    val error = result.exceptionOrNull()?.message ?: "Failed to remove tag"
                    _state.update { it.copy(error = error) }
                    _events.emit(ContactTagEvent.Error(error))
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        error = e.message ?: "Failed to remove tag"
                    )
                }
                _events.emit(ContactTagEvent.Error(e.message ?: "Failed to remove tag"))
            } finally {
                _state.update { it.copy(isProcessing = false) }
            }
        }
    }

    fun filterAvailableTags(query: String) {
        screenModelScope.launch {
            if (query.isBlank()) {
                // Just reload all available tags
                loadAvailableTags()
                return@launch
            }

            _state.update { currentState ->
                // Local filtering for better performance (avoid database call)
                val filteredTags = currentState.allAvailableTags.filter {
                    it.name.contains(query, ignoreCase = true)
                }
                currentState.copy(availableTags = filteredTags)
            }
        }
    }

    fun clearError() {
        _state.update { it.copy(error = null) }
    }

    companion object {
        private const val MAX_FREE_TAGS_PER_CONTACT = 5
    }
}

data class ContactTagSelectionState(
    val contactTags: List<TagEntity> = emptyList(),
    val availableTags: List<TagEntity> = emptyList(),
    val allAvailableTags: List<TagEntity> = emptyList(),
    val isPremium: Boolean = false,
    val isLoadingContactTags: Boolean = false,
    val isLoadingAvailableTags: Boolean = false,
    val isProcessing: Boolean = false,
    val error: String? = null
) {
    val canAddMoreTags: Boolean
        get() = isPremium || contactTags.size < MAX_FREE_TAGS_PER_CONTACT

    companion object {
        private const val MAX_FREE_TAGS_PER_CONTACT = 5
    }
}

sealed class ContactTagEvent {
    data object TagAdded : ContactTagEvent()
    data object TagRemoved : ContactTagEvent()
    data class Error(val message: String) : ContactTagEvent()
    data class PremiumRequired(val message: String) : ContactTagEvent()
}