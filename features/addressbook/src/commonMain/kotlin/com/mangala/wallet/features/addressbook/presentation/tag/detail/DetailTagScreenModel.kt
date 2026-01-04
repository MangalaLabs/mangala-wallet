package com.mangala.wallet.features.addressbook.presentation.tag.detail

import app.cash.paging.PagingData
import app.cash.paging.cachedIn
import app.cash.paging.filter
import cafe.adriel.voyager.core.model.screenModelScope
import com.mangala.wallet.features.addressbook.data.model.ContactWithMultipleBlockchainsModel
import com.mangala.wallet.features.addressbook.data.model.tag.TagEntity
import com.mangala.wallet.features.addressbook.domain.repository.tag.TagRepository
import com.mangala.wallet.features.addressbook.domain.usecase.contact.GetAllContactsUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.tag.DeleteTagUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.tag.RemoveTagFromContactUseCase
import com.mangala.wallet.features.addressbook.presentation.privacy.PrivacyModeViewModel
import com.mangala.wallet.features.addressbook.presentation.tag.detail.model.DetailTagLocalChangesState
import com.mangala.wallet.model.util.Resource
import com.mangala.wallet.ui.utils.screenmodel.BaseScreenModel
import com.mangala.wallet.utils.currentTimeInMillis
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.IO
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import org.koin.core.component.KoinComponent

sealed class DetailTagNavigationEvent {
    object NavigateBack : DetailTagNavigationEvent()
}

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
class DetailTagScreenModel(
    private val tagId: String,
    private val tagRepository: TagRepository,
    private val getContactUseCase: GetAllContactsUseCase,
    private val deleteTagUseCase: DeleteTagUseCase,
    private val removeTagFromContactUseCase: RemoveTagFromContactUseCase,
    private val privacyModeViewModel: PrivacyModeViewModel
) : BaseScreenModel(), KoinComponent {
    companion object {
        private const val SEARCH_DEBOUNCE_TIME_MS = 300L
        private const val MIN_LOADING_DURATION = 500L
        private const val NAVIGATION_EVENT_REPEAT_COUNT = 3
        private const val NAVIGATION_EVENT_DELAY_MS = 100L
    }

    val privacyModeEnabled = privacyModeViewModel.isEnabled

    // Separate Resource states for better UX control
    private val _tagResource = MutableStateFlow<Resource<TagEntity>>(Resource.Loading(null))
    val tagResource = _tagResource.asStateFlow()

    private val _deleteTagResource = MutableStateFlow<Resource<Unit>>(Resource.Loading(null))
    val deleteTagResource = _deleteTagResource.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    // Navigation events flow
    private val _navigationEvents = MutableSharedFlow<DetailTagNavigationEvent>()
    val navigationEvents: SharedFlow<DetailTagNavigationEvent> = _navigationEvents

    // Track deletion state to prevent race conditions
    var isDeleting = false
        private set

    // Local changes for optimistic updates
    private val localChanges = MutableStateFlow(DetailTagLocalChangesState())
    
    // Refresh trigger for paging data
    private val _refreshTrigger = MutableStateFlow(0)

    // Cash App Paging flow for contacts with local changes applied
    val contactPagingFlow: Flow<PagingData<ContactWithMultipleBlockchainsModel>> = combine(
        _searchQuery.debounce(SEARCH_DEBOUNCE_TIME_MS).distinctUntilChanged(),
        _refreshTrigger
    ) { query, _ ->
        query
    }
        .flatMapLatest { query ->
            getContactUseCase.getPaginatedContacts(
                searchQuery = query,
                tagIds = listOf(tagId)
            )
                .cachedIn(screenModelScope)
                .combine(localChanges) { pagingData, localChanges ->
                    pagingData.filter { contact ->
                        // Filter out locally removed contacts
                        !localChanges.removedContactIds.contains(contact.contactId)
                    }
                }
        }

    init {
        loadTagDetails()
    }

    private fun loadTagDetails() {
        screenModelScope.launch(Dispatchers.IO) {
            val startTime = currentTimeInMillis()
            
            try {
                val tag = tagRepository.getTagById(tagId)
                if (tag != null) {
                    val elapsedTime = currentTimeInMillis() - startTime
                    val remainingDelay = MIN_LOADING_DURATION - elapsedTime
                    
                    if (remainingDelay > 0) {
                        delay(remainingDelay)
                    }
                    
                    _tagResource.update { Resource.Success(tag) }
                } else {
                    _tagResource.update { 
                        Resource.Error(Exception("Tag not found"))
                    }
                }
            } catch (e: Exception) {
                _tagResource.update { 
                    Resource.Error(e)
                }
            }
        }
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun deleteTag() {
        screenModelScope.launch {
            _deleteTagResource.update { Resource.Loading(null) }
            
            try {
                // Set deletion flag to prevent reload
                isDeleting = true
                
                val result = deleteTagUseCase.hardDeleteTag(tagId)
                
                if (result) {
                    _deleteTagResource.update { Resource.Success(Unit) }
                    
                    // Emit navigation event multiple times to ensure it's caught
                    repeat(NAVIGATION_EVENT_REPEAT_COUNT) {
                        _navigationEvents.emit(DetailTagNavigationEvent.NavigateBack)
                        delay(NAVIGATION_EVENT_DELAY_MS)
                    }
                } else {
                    _deleteTagResource.update { Resource.Error(Exception("Unable to delete tag")) }
                    isDeleting = false
                }
            } catch (e: Exception) {
                _deleteTagResource.update { Resource.Error(e) }
                isDeleting = false
            }
        }
    }

    /**
     * Optimistically remove contact from tag without refreshing paging
     * Real removal will happen in background and clear on refresh/navigation
     */
    fun removeContactFromTagOptimistically(contactId: String) {
        // Add to removed contacts in local state
        localChanges.update { currentState ->
            currentState.copy(
                removedContactIds = currentState.removedContactIds + contactId
            )
        }

        // Perform actual removal in background
        screenModelScope.launch {
            try {
                val result = removeTagFromContactUseCase(contactId, tagId)
                
                if (result.isFailure) {
                    // If removal fails, remove from local removed list
                    localChanges.update { currentState ->
                        currentState.copy(
                            removedContactIds = currentState.removedContactIds - contactId
                        )
                    }
                } else {
                    _tagResource.update {
                        if (it is Resource.Success) it.map { data ->
                            data!!.copy(
                                contactCount = data.contactCount?.minus(1)
                            )
                        }
                        else it
                    }
                }
            } catch (e: Exception) {
                // If removal fails, remove from local removed list
                localChanges.update { currentState ->
                    currentState.copy(
                        removedContactIds = currentState.removedContactIds - contactId
                    )
                }
            }
        }
    }

    /**
     * Generic refresh function for pull-to-refresh and after operations
     * This refreshes both tag details and clears local changes
     */
    fun refreshData() {
        clearLocalChanges()
        loadTagDetails()
        // Note: Paging data refresh is handled by the UI layer calling contactsPaging.refresh()
        // We don't trigger it here to avoid duplicate refreshes
    }

    /**
     * Clear local changes - called on pull-to-refresh or navigation
     * This will show the real data from the database
     */
    fun clearLocalChanges() {
        localChanges.update { DetailTagLocalChangesState() }
    }

    fun exportTag() {
        // TODO: Implement export functionality
        // This would typically export the tag data to a file or share it
    }

    fun togglePrivacyMode() {
        privacyModeViewModel.toggle()
    }
    
    override fun doOnComposableStarted() {
        super.doOnComposableStarted()
        // Refresh tag details when screen is displayed/resumed
        // This ensures data is updated when returning from edit screen
        refreshData()
        // Trigger paging data refresh by incrementing the trigger
        _refreshTrigger.value++
    }
}