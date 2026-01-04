package com.mangala.wallet.features.addressbook.presentation.tag

import cafe.adriel.voyager.core.model.screenModelScope
import com.mangala.wallet.features.addressbook.data.model.tag.TagEntity
import com.mangala.wallet.features.addressbook.data.model.enum.USER_SETTING_ID
import com.mangala.wallet.features.addressbook.domain.usecase.setting.GetUserSubscriptionUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.tag.CreateTagUseCase
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
 * ScreenModel for quick tag creation during contact add/edit
 * Used in a popup/dialog for fast tag creation without leaving the contact form
 */
class QuickTagCreationScreenModel(
    private val createTagUseCase: CreateTagUseCase,
    private val getUserSubscriptionUseCase: GetUserSubscriptionUseCase,
    private val getActiveTagsUseCase: GetActiveTagsUseCase
): BaseScreenModel() {
    private val _state = MutableStateFlow(QuickTagCreationState())
    val state: StateFlow<QuickTagCreationState> = _state.asStateFlow()

    private val _events = MutableSharedFlow<QuickTagEvent>()
    val events: SharedFlow<QuickTagEvent> = _events.asSharedFlow()

    // Color palette for quick selection
    private val colorPalette = listOf(
        "#A5D6A7", // Light green
        "#CE93D8", // Light purple
        "#90CAF9", // Light blue
        "#FFCC80", // Light orange
        "#B39DDB", // Light deep purple
        "#EF9A9A", // Light red
        "#80DEEA", // Light cyan
        "#FFE082", // Light amber
        "#C5E1A5", // Light lime
        "#F48FB1"  // Light pink
    )

    init {
        _state.update { it.copy(colorPalette = colorPalette) }
        checkSubscriptionStatus()
        checkCurrentTagCount()
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
                println("QuickTagCreation, Failed to check subscription status $e")
            }
        }
    }

    private fun checkCurrentTagCount() {
        screenModelScope.launch {
            try {
                getActiveTagsUseCase().fold(
                    onSuccess = { tags ->
                        val count = tags.size
                        _state.update {
                            it.copy(
                                currentTagCount = count,
                                canCreateMoreTags = it.isPremium || count < MAX_FREE_TAGS
                            )
                        }
                    },
                    onFailure = { exception ->
                        println("QuickTagCreation, Failed to count tags: ${exception.message}")
                        // Assume we can create more to avoid blocking the user
                        _state.update { it.copy(canCreateMoreTags = true) }
                    }
                )
            } catch (e: Exception) {
                println("QuickTagCreation, Failed to count tags: $e")
                // Assume we can create more to avoid blocking the user
                _state.update { it.copy(canCreateMoreTags = true) }
            }
        }
    }

    fun createTag(name: String, color: String, textColor: String? = null) {
        if (name.isBlank()) {
            screenModelScope.launch {
                _events.emit(QuickTagEvent.ValidationError("Tag name cannot be empty"))
            }
            return
        }

        // Check subscription status for non-premium users
        if (!_state.value.isPremium && _state.value.currentTagCount >= MAX_FREE_TAGS) {
            screenModelScope.launch {
                _events.emit(QuickTagEvent.PremiumRequired(
                    "Free users can only create $MAX_FREE_TAGS tags. " +
                            "Upgrade to premium for unlimited tags."
                ))
            }
            return
        }

        screenModelScope.launch {
            try {
                _state.update { it.copy(isCreating = true) }

                val result = createTagUseCase(name.trim(), color, textColor)
                if (result.isSuccess) {
                    _events.emit(QuickTagEvent.TagCreated(result.getOrNull()!!))
                } else {
                    val error = result.exceptionOrNull()?.message ?: "Failed to create tag"
                    _state.update { it.copy(error = error) }
                    _events.emit(QuickTagEvent.Error(error))
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        error = e.message ?: "Failed to create tag"
                    )
                }
                _events.emit(QuickTagEvent.Error(e.message ?: "Failed to create tag"))
            } finally {
                _state.update { it.copy(isCreating = false) }
            }
        }
    }

    fun selectColor(color: String) {
        _state.update { it.copy(selectedColor = color) }
    }

    fun clearError() {
        _state.update { it.copy(error = null) }
    }

    companion object {
        private const val MAX_FREE_TAGS = 20
    }
}

data class QuickTagCreationState(
    val colorPalette: List<String> = emptyList(),
    val selectedColor: String = "#A5D6A7", // Default color
    val isPremium: Boolean = false,
    val currentTagCount: Int = 0,
    val canCreateMoreTags: Boolean = true,
    val isCreating: Boolean = false,
    val error: String? = null
)

sealed class QuickTagEvent {
    data class TagCreated(val createdTag: TagEntity) : QuickTagEvent()
    data class Error(val message: String) : QuickTagEvent()
    data class ValidationError(val message: String) : QuickTagEvent()
    data class PremiumRequired(val message: String) : QuickTagEvent()
}