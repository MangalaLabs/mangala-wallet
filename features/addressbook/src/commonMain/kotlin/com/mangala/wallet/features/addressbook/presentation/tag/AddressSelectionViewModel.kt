package com.mangala.wallet.features.addressbook.presentation.tag

import app.cash.paging.PagingData
import app.cash.paging.cachedIn
import app.cash.paging.map
import cafe.adriel.voyager.core.model.screenModelScope
import com.mangala.wallet.features.addressbook.data.model.ContactWithMultipleBlockchainsModel
import com.mangala.wallet.features.addressbook.domain.usecase.contact.GetAllContactsUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.tag.AssignTagToContactUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.tag.RemoveTagFromContactUseCase
import com.mangala.wallet.features.addressbook.domain.repository.tag.TagRepository
import com.mangala.wallet.features.addressbook.presentation.tag.model.AddressSelectionContactModel
import com.mangala.wallet.features.addressbook.presentation.tag.model.AddressSelectionLocalChangesState
import com.mangala.wallet.ui.utils.screenmodel.BaseScreenModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.update
import org.koin.core.component.KoinComponent

/**
 * ViewModel for address selection screen with paging and optimistic local changes
 * Supports both creating new tags and editing existing tags
 */
@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
class AddressSelectionViewModel(
    private val tagId: String?,
    initialSelectedContactIds: List<String> = emptyList(),
    private val getAllContactsUseCase: GetAllContactsUseCase,
    private val addContactToTagUseCase: AssignTagToContactUseCase,
    private val removeContactFromTagUseCase: RemoveTagFromContactUseCase,
    private val tagRepository: TagRepository,
) : BaseScreenModel(), KoinComponent {

    companion object {
        private const val SEARCH_DEBOUNCE_TIME_MS = 300L
    }

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    // Local changes for optimistic updates
    private val localChanges = MutableStateFlow(
        AddressSelectionLocalChangesState(
            newlySelectedContactIds = initialSelectedContactIds.toSet()
        )
    )

    // Paging flow for contacts with local changes applied via tagId mapping
    val contactPagingFlow: Flow<PagingData<AddressSelectionContactModel>> = _searchQuery
        .debounce(SEARCH_DEBOUNCE_TIME_MS)
        .distinctUntilChanged()
        .flatMapLatest { query ->
            getAllContactsUseCase.getPaginatedContacts(
                searchQuery = query.takeIf { it.isNotBlank() },
                checkTagId = tagId
            )
        }
        .cachedIn(screenModelScope)
        .combine(localChanges) { pagingData, localChanges ->
            pagingData.map { contact ->
                // Apply local changes by mapping tagId based on selection state
                val isNewlySelected = localChanges.newlySelectedContactIds.contains(contact.contactId)
                val isRemoved = localChanges.removedContactIds.contains(contact.contactId)

                AddressSelectionContactModel(
                    contactWithMultipleBlockchainsModel = contact,
                    isSelected = when {
                        isRemoved -> false

                        isNewlySelected -> true

                        else -> tagId != null && contact.tagId == tagId
                    }
                )
            }
        }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    /**
     * Handle toggle based on current contact state
     */
    fun toggleContactSelectionWithState(contactId: String, isSelected: Boolean) {
        localChanges.update { currentChanges ->
            val isNewlySelected = currentChanges.newlySelectedContactIds.contains(contactId)
            val isRemoved = currentChanges.removedContactIds.contains(contactId)

            when {
                // Contact is in tag and not in local changes -> add to removed
                isNewlySelected -> {
                    currentChanges.copy(
                        newlySelectedContactIds = currentChanges.newlySelectedContactIds - contactId
                    )
                }
                // Contact is removed -> remove from removed (restore)
                isRemoved -> {
                    currentChanges.copy(
                        removedContactIds = currentChanges.removedContactIds - contactId
                    )
                }

                isSelected -> {
                    currentChanges.copy(
                        removedContactIds = currentChanges.removedContactIds + contactId
                    )
                }

                else -> {
                    currentChanges.copy(
                        newlySelectedContactIds = currentChanges.newlySelectedContactIds + contactId
                    )
                }
            }
        }
    }

    /**
     * Apply local changes to database (persist changes)
     */
    suspend fun applyChanges(): Result<Unit> {
        return try {
            val changes = localChanges.value
            if (tagId != null) {
                // Editing existing tag - apply changes
                // Add newly selected contacts
                changes.newlySelectedContactIds.forEach { contactId ->
                    addContactToTagUseCase(contactId, tagId)
                }

                // Remove deselected contacts
                changes.removedContactIds.forEach { contactId ->
                    removeContactFromTagUseCase(contactId, tagId)
                }

                // Clear local changes after successful persistence
                localChanges.value = AddressSelectionLocalChangesState()
            }
            // For new tag creation, parent will handle with getFinalSelectedContactIds()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Get final selected contact IDs (for new tag creation)
     */
    fun getFinalSelectedContactIds(): List<String> {
        val changes = localChanges.value
        return changes.newlySelectedContactIds.toList()
    }
    
    /**
     * Get all selected contact IDs including existing ones (for tag editing)
     * This includes existing contacts in tag + newly selected - removed
     */
    suspend fun getAllSelectedContactIds(): List<String> {
        return if (tagId != null) {
            // For edit mode: get existing contacts from DB + apply local changes
            val existingContactIds = tagRepository.getContactIdsWithTag(tagId).toSet()
            val changes = localChanges.value
            
            // Start with existing contacts
            val finalSelection = existingContactIds.toMutableSet()
            // Add newly selected
            finalSelection.addAll(changes.newlySelectedContactIds)
            // Remove deselected
            finalSelection.removeAll(changes.removedContactIds)
            
            finalSelection.toList()
        } else {
            // For create mode: just return newly selected
            getFinalSelectedContactIds()
        }
    }
}