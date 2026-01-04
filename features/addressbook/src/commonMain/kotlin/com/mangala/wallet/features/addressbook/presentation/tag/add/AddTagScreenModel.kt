package com.mangala.wallet.features.addressbook.presentation.tag.add

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import cafe.adriel.voyager.core.model.screenModelScope
import com.mangala.wallet.common.mokoresources.ColorsNew
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.awaitAll
import com.mangala.wallet.features.addressbook.data.model.WalletAddressWithNetworkModel
import com.mangala.wallet.features.addressbook.data.model.contact.ContactEntity
import com.mangala.wallet.features.addressbook.domain.repository.contact.ContactRepository
import com.mangala.wallet.features.addressbook.domain.repository.contact.WalletAddressRepository
import com.mangala.wallet.features.addressbook.domain.repository.tag.TagRepository
import com.mangala.wallet.features.addressbook.domain.repository.tag.ObservableTagRepository
import com.mangala.wallet.features.addressbook.domain.usecase.contact.SearchContactsUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.contact.IsContactFavoriteUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.tag.CreateTagUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.tag.UpdateTagUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.wallet_address.GetWalletAddressesForContactUseCase
import com.mangala.wallet.features.addressbook.presentation.tag.ContactWithAddresses
import com.mangala.wallet.features.addressbook.utils.stringToColor
import com.mangala.wallet.features.addressbook.utils.colorToIndexString
import com.mangala.wallet.features.addressbook.utils.OptimizedColorUtils
import com.mangala.wallet.ui.utils.screenmodel.BaseScreenModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.koin.core.component.KoinComponent
import kotlin.coroutines.suspendCoroutine
import kotlin.coroutines.resume


/**
 * ViewModel for create/edit tag screen
 */
class AddTagScreenModel(
    val tagId: String? = null,
    val getWalletAddressesForContactUseCase: GetWalletAddressesForContactUseCase,
    val tagRepository: TagRepository,
    val contactRepository: ContactRepository,
    val walletAddressRepository: WalletAddressRepository,
    val createTagUseCase: CreateTagUseCase,
    val updateTagUseCase: UpdateTagUseCase,
    val searchContactsUseCase: SearchContactsUseCase,
    val isContactFavoriteUseCase: IsContactFavoriteUseCase
) : BaseScreenModel(), KoinComponent {
    private val _uiState = MutableStateFlow(CreateTagUiState(isEditing = tagId != null))
    val uiState: StateFlow<CreateTagUiState> = _uiState.asStateFlow()

    var showAddressBottomSheet by mutableStateOf(false)
        private set
        
    // Store the saved tag ID for navigation purposes
    private var _savedTagId by mutableStateOf<String?>(null)
    val savedTagId get() = _savedTagId
    
    // Mutex for synchronizing database operations
    private val databaseMutex = kotlinx.coroutines.sync.Mutex()
    
    // Job for observing tag changes
    private var observeTagChangesJob: Job? = null
    
    // Flag to track if the ScreenModel is active
    private var isActive = true

    // Color options for tag background and text
    private val textColorOptions = listOf(
        ColorsNew.textWhite,          // White
        ColorsNew.textBlack,          // Black
        ColorsNew.textBlue,           // Blue
        ColorsNew.textTeal,           // Teal
        ColorsNew.textYellow,         // Yellow
        ColorsNew.textRed,            // Red
        ColorsNew.textPurple,         // Purple
        ColorsNew.textIndigo          // Indigo
    )

    // Background color variations as seen in the Figma design
    private val backgroundColorOptions = listOf(

        // Avatar colors for more variety
        ColorsNew.textWhite,          // White
        ColorsNew.textBlack,          // Black
        ColorsNew.textBlue,           // Blue
        ColorsNew.textTeal,           // Teal
        ColorsNew.textYellow,         // Yellow
        ColorsNew.textRed,            // Red
        ColorsNew.textPurple,         // Purple
        ColorsNew.textIndigo,         // Indigo
    )

    init {
        screenModelScope.launch {
            loadContactsAndTagData()
        }
        
        // Observe tag changes in edit mode
        if (_uiState.value.isEditing && tagId != null) {
            observeTagChanges()
        } else {
        }
    }
    
    private fun observeTagChanges() {
        // Cancel previous job if exists
        observeTagChangesJob?.cancel()
        
        // Check if tagRepository is ObservableTagRepository and ScreenModel is still active
        if (tagRepository is ObservableTagRepository && tagId != null && isActive) {
            // Create a job reference to track the collection
            observeTagChangesJob = tagRepository.observeContactIdsWithTag(tagId)
                .onEach { contactIds ->
                    // Only update if ScreenModel is still active
                    if (!isActive) return@onEach
                    
                    // Always update the UI state with the latest contact IDs
                    _uiState.update { state ->
                        state.copy(selectedContactIds = contactIds)
                    }
                    
                    // If the contact list has changed, reload contacts to refresh UI
                    val currentSet = _uiState.value.availableContacts.map { it.contact.id }.toSet()
                    if (contactIds.any { it !in currentSet }) {
                        // Force reload contacts to ensure UI is fully refreshed
                        screenModelScope.launch {
                            if (isActive) {
                                loadContactsSuspend()
                            }
                        }
                    } else {
                    }
                }
                .catch { exception ->
                    if (isActive) {
                        exception.printStackTrace()
                        // Try to recover by reloading
                        screenModelScope.launch {
                            if (isActive) {
                                loadContactsAndTagData()
                            }
                        }
                    }
                }
                .launchIn(screenModelScope)
        }
    }

    override fun doOnComposableStarted() {
        super.doOnComposableStarted()
        // Only load if we don't have contacts already (avoid unnecessary reloads)
        if (_uiState.value.availableContacts.isEmpty()) {
            screenModelScope.launch {
                loadContactsAndTagData()
            }
        }
    }
    
    /**
     * Load contacts first, then load tag data if in edit mode
     */
    private suspend fun loadContactsAndTagData() {
        // First load all contacts
        loadContactsSuspend()
        
        // Then load tag data if in edit mode
        if (tagId != null) {
            loadTagData()
        }
    }

    /**
     * Load tag data (only called after contacts are loaded)
     */
    private suspend fun loadTagData() {
        if (tagId == null) return
        
        try {
            val tag = tagRepository.getTagById(tagId)
            tag?.let {
                val backgroundColor = stringToColor(tag.color)
                val hasCustomTextColor = tag.textColor != null
                
                _uiState.update { state ->
                    state.copy(
                        tagName = tag.name,
                        selectedBackgroundColor = backgroundColor,
                        selectedTextColor = if (hasCustomTextColor) {
                            stringToColor(tag.textColor!!)
                        } else {
                            // Auto-calculate text color based on background luminance
                            if (backgroundColor.luminance() > 0.5f) {
                                ColorsNew.textBlack // Dark text for light backgrounds
                            } else {
                                ColorsNew.textWhite // Light text for dark backgrounds
                            }
                        },
                        isCustomTextColorSelected = hasCustomTextColor,
                        isEditing = true,
                        icon = tag.icon // Load the icon field from tag entity
                    )
                }

                // Get contacts associated with this tag - use contact_tags table for consistency
                val contactIds = tagRepository.getContactIdsWithTag(tagId)
                // Store all contact IDs first, we'll validate them later when displaying
                _uiState.update { it.copy(selectedContactIds = contactIds) }
            }
        } catch (e: Exception) {
            _uiState.update {
                it.copy(
                    error = "Failed to load tag: ${e.message}"
                )
            }
        }
    }

    /**
     * Load contacts with immediate UI update, then load addresses in background
     */
    private suspend fun loadContactsSuspend() {
        try {
            _uiState.update { it.copy(isLoading = true) }

            // Phase 1: Load minimal contacts for immediate UI display (very fast)
            val allContacts = contactRepository.getAllContacts(limit = 50).first()
            
            // If in edit mode, load missing contacts
            val contactsToLoad = if (tagId != null && _uiState.value.selectedContactIds.isNotEmpty()) {
                val selectedIds = _uiState.value.selectedContactIds.toSet()
                val loadedIds = allContacts.map { it.id }.toSet()
                val missingIds = selectedIds - loadedIds
                
                if (missingIds.isNotEmpty()) {
                    val missingContacts = coroutineScope {
                        missingIds.map { contactId ->
                            async {
                                try { contactRepository.getContactById(contactId) }
                                catch (e: Exception) { null }
                            }
                        }.awaitAll().filterNotNull()
                    }
                    allContacts + missingContacts
                } else {
                    allContacts
                }
            } else {
                allContacts
            }
            
            // Phase 2: Create ContactWithAddresses with empty addresses first (FAST UI update)
            val contactsWithEmptyAddresses = contactsToLoad.map { contact ->
                ContactWithAddresses(contact, emptyList())
            }
            
            // Update UI immediately with contacts (no addresses yet)
            _uiState.update {
                it.copy(
                    availableContacts = contactsWithEmptyAddresses,
                    filteredContacts = contactsWithEmptyAddresses,
                    contactFavoriteStatus = emptyMap(), // Will be populated later
                    isLoading = false // UI can show contacts now
                )
            }
            
            // Phase 3: Load addresses and favorites in background (don't block UI)
            screenModelScope.launch {
                loadAddressesAndFavoritesInBackground(contactsToLoad)
            }
            
        } catch (e: Exception) {
            _uiState.update {
                it.copy(
                    error = "Failed to load contacts: ${e.message}",
                    isLoading = false
                )
            }
        }
    }
    
    /**
     * Load addresses and favorite status in background with progressive updates
     */
    private suspend fun loadAddressesAndFavoritesInBackground(contacts: List<ContactEntity>) {
        try {
            // Process in very small chunks with immediate UI updates for better UX
            contacts.chunked(5).forEach { contactChunk ->
                val chunkData = coroutineScope {
                    contactChunk.map { contact ->
                        async {
                            val addressesDeferred = async { 
                                getWalletAddressesForContactUseCase(contact.id) 
                            }
                            val favoriteDeferred = async { 
                                isContactFavoriteUseCase(contact.id) 
                            }
                            
                            Triple(contact, addressesDeferred.await(), favoriteDeferred.await())
                        }
                    }.awaitAll()
                }
                
                // Update UI immediately after each chunk
                _uiState.update { state ->
                    val existingContacts = state.availableContacts.associateBy { it.contact.id }.toMutableMap()
                    val existingFavorites = state.contactFavoriteStatus.toMutableMap()
                    
                    // Update contacts with new address data
                    chunkData.forEach { (contact, addresses, isFavorite) ->
                        existingContacts[contact.id] = ContactWithAddresses(contact, addresses)
                        existingFavorites[contact.id] = isFavorite
                    }
                    
                    val updatedContacts = existingContacts.values.toList()
                    
                    state.copy(
                        availableContacts = updatedContacts,
                        filteredContacts = if (state.addressSearchQuery.isBlank()) {
                            updatedContacts
                        } else {
                            filterContactsByQuery(updatedContacts, state.addressSearchQuery)
                        },
                        contactFavoriteStatus = existingFavorites.toMap()
                    )
                }
            }
        } catch (e: Exception) {
            _uiState.update { 
                it.copy(error = "Failed to load contact details: ${e.message}") 
            }
        }
    }
    
    /**
     * Helper method to filter contacts by search query
     */
    private fun filterContactsByQuery(
        contacts: List<ContactWithAddresses>, 
        query: String
    ): List<ContactWithAddresses> {
        if (query.isBlank()) return contacts
        
        val lowercaseQuery = query.lowercase()
        return contacts.filter { contactWithAddresses ->
            val contact = contactWithAddresses.contact
            contact.name.lowercase().contains(lowercaseQuery) ||
            contactWithAddresses.addresses.any { address ->
                address.address.lowercase().contains(lowercaseQuery) ||
                (address.alias?.lowercase()?.contains(lowercaseQuery) ?: false)
            }
        }
    }

    /**
     * Load more contacts when needed (pagination)
     */
    fun loadMoreContacts() {
        screenModelScope.launch {
            try {
                val currentContacts = _uiState.value.availableContacts
                val currentCount = currentContacts.size
                
                // Load next batch of contacts
                val additionalContacts = contactRepository.getAllContacts(
                    limit = 50, 
                    offset = currentCount
                ).first()
                
                if (additionalContacts.isNotEmpty()) {
                    // Load addresses and favorite status for new contacts
                    val newContactsWithAddresses = coroutineScope {
                        additionalContacts.chunked(10).flatMap { contactChunk ->
                            contactChunk.map { contact ->
                                async {
                                    val addressesDeferred = async { 
                                        getWalletAddressesForContactUseCase(contact.id) 
                                    }
                                    val favoriteDeferred = async { 
                                        isContactFavoriteUseCase(contact.id) 
                                    }
                                    
                                    Triple(contact, addressesDeferred.await(), favoriteDeferred.await())
                                }
                            }.awaitAll()
                        }
                    }
                    
                    val contactsWithAddressesList = newContactsWithAddresses.map { (contact, addresses, _) ->
                        ContactWithAddresses(contact, addresses)
                    }
                    
                    val newFavoriteStatusMap = newContactsWithAddresses.associate { (contact, _, isFavorite) ->
                        contact.id to isFavorite
                    }
                    
                    _uiState.update { state ->
                        state.copy(
                            availableContacts = state.availableContacts + contactsWithAddressesList,
                            filteredContacts = state.filteredContacts + contactsWithAddressesList.filter { contactWithAddresses ->
                                val query = state.addressSearchQuery
                                if (query.isBlank()) return@filter true
                                
                                val lowercaseQuery = query.lowercase()
                                val contact = contactWithAddresses.contact
                                contact.name.lowercase().contains(lowercaseQuery) ||
                                contactWithAddresses.addresses.any { address ->
                                    address.address.lowercase().contains(lowercaseQuery) ||
                                    (address.alias?.lowercase()?.contains(lowercaseQuery) ?: false)
                                }
                            },
                            contactFavoriteStatus = state.contactFavoriteStatus + newFavoriteStatusMap
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(error = "Failed to load more contacts: ${e.message}") 
                }
            }
        }
    }

    /**
     * Tìm kiếm contacts theo query với optimization
     */
    fun searchContacts(query: String) {
        _uiState.update { state ->
            state.copy(
                addressSearchQuery = query,
                filteredContacts = filterContactsByQuery(state.availableContacts, query)
            )
        }
    }

    /**
     * Update tag name
     */
    fun setTagName(name: String) {
        _uiState.update { it.copy(tagName = name, error = null) }
    }

    /**
     * Update selected text color (manually selected by user)
     */
    fun setSelectedTextColor(color: Color) {
        _uiState.update { 
            it.copy(
                selectedTextColor = color,
                isCustomTextColorSelected = true
            ) 
        }
    }

    /**
     * Update selected background color and conditionally auto-adjust text color
     */
    fun setSelectedBackgroundColor(color: Color) {
        _uiState.update { currentState ->
            // Only auto-adjust text color if user hasn't manually selected one
            if (currentState.isCustomTextColorSelected) {
                // Keep user's custom text color choice
                currentState.copy(selectedBackgroundColor = color)
            } else {
                // Auto-select appropriate text color based on background brightness
                val autoTextColor = if (color.luminance() > 0.5f) {
                    ColorsNew.textBlack // Dark text for light backgrounds
                } else {
                    ColorsNew.textWhite // Light text for dark backgrounds
                }
                
                currentState.copy(
                    selectedBackgroundColor = color,
                    selectedTextColor = autoTextColor
                )
            }
        }
    }

    /**
     * Show address selection bottom sheet
     */
    fun showAddressBottomSheet() {
        showAddressBottomSheet = true
    }

    /**
     * Hide address selection bottom sheet
     */
    fun hideAddressBottomSheet() {
        showAddressBottomSheet = false
    }

    /**
     * Toggle contact selection using contact ID
     * Only affects UI state, not database
     */
    fun toggleContactSelection(contactId: String) {
        _uiState.update { state ->
            val selectedContactIds = state.selectedContactIds.toMutableList()
            if (selectedContactIds.contains(contactId)) {
                selectedContactIds.remove(contactId)
            } else {
                selectedContactIds.add(contactId)
            }
            state.copy(selectedContactIds = selectedContactIds)
        }
        println("Contact $contactId toggled in AddTagScreen")
    }

    /**
     * Apply a complete list of selected contacts at once
     * This is called when the user clicks "Add address" in the bottom sheet
     * It only updates UI state - actual database changes happen on saveTag()
     */
    fun applyContactSelections(selectedContactIds: List<String>) {
        // Update the UI state with the complete list of selected contacts
        _uiState.update { state ->
            println("Applied contact selections: ${selectedContactIds.size} contacts selected")
            state.copy(selectedContactIds = selectedContactIds)
        }
    }

    // Helper method to get all address IDs from selected contacts
    private fun getAddressIdsFromSelectedContacts(): List<String> {
        val addressIds = mutableListOf<String>()
        
        for (contactId in _uiState.value.selectedContactIds) {
            // Find contact in available contacts
            val contact = _uiState.value.availableContacts.find { it.contact.id == contactId }
            
            // Add all addresses of this contact
            contact?.addresses?.forEach { address ->
                addressIds.add(address.id)
            }
        }
        
        return addressIds
    }

    /**
     * Save tag (create or update) and return the tag ID
     * This new version returns the tag ID for navigation purposes
     */
    suspend fun saveTagSuspend(): String? = suspendCoroutine { continuation ->
        if (_uiState.value.tagName.isBlank()) {
            _uiState.update { it.copy(error = "Tag name cannot be empty") }
            continuation.resume(null)
            return@suspendCoroutine
        }

        _uiState.update { it.copy(isSaving = true, error = null) }

        screenModelScope.launch {
            try {
                // Use optimized color conversion with caching
                val backgroundColorString = OptimizedColorUtils.colorToIndexOptimized(_uiState.value.selectedBackgroundColor)
                val textColorString = OptimizedColorUtils.colorToIndexOptimized(_uiState.value.selectedTextColor)

                // Get selected address IDs directly from UI state
                val selectedAddressIds = getAddressIdsFromSelectedContacts()

                var resultTagId: String? = null

                if (_uiState.value.isEditing && tagId != null) {
                    // Update existing tag using UpdateTagUseCase
                    val result = updateTagUseCase(
                        id = tagId,
                        name = _uiState.value.tagName,
                        color = backgroundColorString,
                        textColor = textColorString,
                        icon = _uiState.value.icon
                    )

                    result.fold(
                        onSuccess = { updatedTag ->
                            // Get current contacts with this tag
                            val currentContactIds = tagRepository.getContactIdsWithTag(tagId)
                            
                            // Use selected contact IDs directly from UI state
                            val selectedContactIds = _uiState.value.selectedContactIds.toSet()

                            // Use synchronized database operations to prevent race conditions
                            val contactsToAdd = selectedContactIds.filter { it !in currentContactIds }
                            val contactsToRemove = currentContactIds.filter { contactId ->
                                contactId !in selectedContactIds
                            }
                            
                            
                            // Use atomic transaction for all contact updates
                            databaseMutex.withLock {
                                try {
                                    // Remove contacts first to avoid constraint issues
                                    for (contactId in contactsToRemove) {
                                        tagRepository.removeTagFromContact(tagId, contactId)
                                    }
                                    
                                    // Then add new contacts
                                    for (contactId in contactsToAdd) {
                                        tagRepository.assignTagToContact(tagId, contactId)
                                    }
                                    
                                    
                                    // Single Flow update after all operations complete
                                    if ((contactsToAdd.isNotEmpty() || contactsToRemove.isNotEmpty()) && 
                                        tagRepository is ObservableTagRepository) {
                                        try {
                                            // Add small delay to ensure database writes are committed
                                            kotlinx.coroutines.delay(50)
                                            (tagRepository as ObservableTagRepository).forceUpdateTag(tagId)
                                        } catch (e: Exception) {
                                            println("ERROR: Failed to force Flow update: ${e.message}")
                                        }
                                    }
                                } catch (e: Exception) {
                                    println("ERROR: Failed to update tag contacts: ${e.message}")
                                    e.printStackTrace()
                                    throw e // Re-throw to trigger error handling
                                }
                            }
                            
                            // NOTE: Removed address_tags handling to maintain consistency
                            // All tag operations now use contact_tags table only
                            // This ensures DetailTagScreen and AddTagScreen show the same data

                            _uiState.update { it.copy(isSaving = false) }
                            resultTagId = tagId
                            _savedTagId = tagId
                        },
                        onFailure = { error ->
                            println("ERROR: Tag update failed: ${error.message}")
                            _uiState.update {
                                it.copy(
                                    error = error.message ?: "Failed to update tag",
                                    isSaving = false
                                )
                            }
                        }
                    )
                } else {
                    // Create new tag using CreateTagUseCase
                    val result = createTagUseCase(
                        name = _uiState.value.tagName,
                        color = backgroundColorString,
                        textColor = textColorString,
                        icon = _uiState.value.icon
                    )

                    result.fold(
                        onSuccess = { createdTag ->
                            
                            // Use selected contact IDs directly from UI state
                            val selectedContactIds = _uiState.value.selectedContactIds
                            
                            // Use atomic transaction for tag creation and contact assignment
                            databaseMutex.withLock {
                                try {
                                    // Assign tag to contacts atomically
                                    for (contactId in selectedContactIds) {
                                        tagRepository.assignTagToContact(createdTag.id, contactId)
                                    }
                                    
                                    // Single Flow update after all assignments complete
                                    if (selectedContactIds.isNotEmpty() && tagRepository is ObservableTagRepository) {
                                        try {
                                            // Add small delay to ensure database writes are committed
                                            kotlinx.coroutines.delay(50)
                                            (tagRepository as ObservableTagRepository).forceUpdateTag(createdTag.id)
                                        } catch (e: Exception) {
                                            println("ERROR: Failed to force Flow update: ${e.message}")
                                        }
                                    }
                                    
                                    // Verify assignments
                                    val taggedContactIds = tagRepository.getContactIdsWithTag(createdTag.id)
                                    
                                } catch (e: Exception) {
                                    println("ERROR: Failed to assign tag to contacts: ${e.message}")
                                    e.printStackTrace()
                                    throw e // Re-throw to trigger error handling
                                }
                            }

                            _uiState.update { it.copy(isSaving = false) }
                            resultTagId = createdTag.id
                            _savedTagId = createdTag.id
                        },
                        onFailure = { error ->
                            println("ERROR: Tag creation failed: ${error.message}")
                            _uiState.update {
                                it.copy(
                                    error = error.message ?: "Failed to create tag",
                                    isSaving = false
                                )
                            }
                        }
                    )
                }
                
                continuation.resume(resultTagId)
                
            } catch (e: Exception) {
                println("ERROR: Exception during tag save operation: ${e.message}")
                e.printStackTrace()
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        error = "Failed to ${if (it.isEditing) "update" else "create"} tag: ${e.message}"
                    )
                }
                continuation.resume(null)
            }
        }
    }
    
    /**
     * Non-suspending version of saveTag for backward compatibility
     * Returns immediately and sets savedTagId property that can be checked later
     */
    fun saveTag() {
        screenModelScope.launch {
            _savedTagId = saveTagSuspend()
        }
    }

    /**
     * Get available background colors for the tag
     */
    fun getAvailableColors(): List<Color> = backgroundColorOptions

    /**
     * Get available text colors for the tag
     */
    fun getAvailableTextColors(): List<Color> = textColorOptions

    /**
     * Check if a wallet address is selected
     */
    fun isAddressSelected(addressId: String): Boolean {
        return getAddressIdsFromSelectedContacts().contains(addressId)
    }

    /**
     * Lấy số lượng địa chỉ đã chọn
     */
    fun getSelectedAddressCount(): Int {
        return getAddressIdsFromSelectedContacts().size
    }

    /**
     * Update the icon for the tag
     */
    fun onIconSelected(iconPath: String) {
        _uiState.update { it.copy(icon = iconPath) }
    }

    /**
     * Directly remove a contact from selection
     * More appropriate for delete actions than toggle
     */
    fun removeContactFromSelection(contactId: String) {
        _uiState.update { state ->
            val updatedSelection = state.selectedContactIds.toMutableList()
            updatedSelection.remove(contactId)
            state.copy(selectedContactIds = updatedSelection)
        }
        println("Contact $contactId removed from selection in AddTagScreen")
    }
    
    override fun onDispose() {
        // Mark as inactive to prevent further operations
        isActive = false
        
        // Cancel all ongoing jobs
        observeTagChangesJob?.cancel()
        observeTagChangesJob = null
        
        super.onDispose()
    }
}