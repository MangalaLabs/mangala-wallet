package com.mangala.wallet.features.addressbook.presentation.contact.list

import app.cash.paging.PagingData
import app.cash.paging.cachedIn
import app.cash.paging.filter
import app.cash.paging.insertSeparators
import app.cash.paging.map
import cafe.adriel.voyager.core.model.screenModelScope
import com.mangala.wallet.features.addressbook.data.model.ContactModel
import com.mangala.wallet.features.addressbook.data.model.ContactRecentTransactionModel
import com.mangala.wallet.features.addressbook.data.model.ContactWithMultipleBlockchainsModel
import com.mangala.wallet.features.addressbook.data.model.blockchain.BlockchainTypeEntity
import com.mangala.wallet.features.addressbook.data.model.enum.TransactionStatus
import com.mangala.wallet.features.addressbook.domain.usecase.blockchain.GetAllBlockchainTypesUseCase
import com.mangala.wallet.features.addressbook.domain.repository.contact.WalletAddressRepository
import com.mangala.wallet.features.addressbook.domain.usecase.contact.DeleteContactUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.contact.GetAllContactsUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.contact.GetRecentContactsUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.contact.RemoveFavoriteUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.contact.favorite.AddFavoriteUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.contact.favorite.GetFavoriteContactsUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.wallet_address.CountWalletAddressesForContactUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.wallet_address.GetWalletAddressesForContactUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.group.DeleteGroupUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.group.GetAllGroupsUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.tag.DeleteTagUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.tag.GetActiveTagsUseCase
import com.mangala.wallet.features.addressbook.presentation.contact.list.model.ContactGroupedByAlphabetUiModel
import com.mangala.wallet.features.addressbook.presentation.contact.list.model.ContactLocalChangesState
import com.mangala.wallet.features.addressbook.presentation.group.model.GroupGroupedByAlphabetUiModel
import com.mangala.wallet.features.addressbook.presentation.group.model.GroupLocalChangesState
import com.mangala.wallet.features.addressbook.presentation.privacy.PrivacyModeViewModel
import com.mangala.wallet.features.addressbook.presentation.tag.model.TagGroupedByAlphabetUiModel
import com.mangala.wallet.features.addressbook.presentation.tag.model.TagLocalChangesState
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.account.CountImportedAntelopeAccount
import com.mangala.wallet.model.blockchain.BlockchainType
import com.mangala.wallet.model.blockchain.NetworkType
import com.mangala.wallet.ui.utils.screenmodel.BaseScreenModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// UI state for contact list screen
data class ContactListScreenUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val hasAnyImportedAccount: Boolean = false,
)

// Navigation events for send flow
sealed class SendContactNavigationEvent {
    data class NavigateToSelectAddress(
        val contactId: String,
        val accountId: String
    ) : SendContactNavigationEvent()

    data class NavigateToStep3(
        val accountId: String,
        val address: String,
        val blockchainUid: String,
    ) : SendContactNavigationEvent()
}

@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
class ContactListScreenModel(
    private val getActiveTagsUseCase: GetActiveTagsUseCase,
    private val getFavoriteContactsUseCase: GetFavoriteContactsUseCase,
    private val getRecentContactsUseCase: GetRecentContactsUseCase,
    private val getAllBlockchainTypesUseCase: GetAllBlockchainTypesUseCase,
    private val getAllGroupsUseCase: GetAllGroupsUseCase,
    private val deleteContactUseCase: DeleteContactUseCase,
    private val deleteGroupUseCase: DeleteGroupUseCase,
    private val deleteTagUseCase: DeleteTagUseCase,
    private val getAllContactsUseCase: GetAllContactsUseCase,
    private val addFavoriteUseCase: AddFavoriteUseCase,
    private val removeFavoriteUseCase: RemoveFavoriteUseCase,
    private val privacyModeViewModel: PrivacyModeViewModel,
    private val countWalletAddressesForContactUseCase: CountWalletAddressesForContactUseCase,
    private val getWalletAddressesForContactUseCase: GetWalletAddressesForContactUseCase,
    private val countImportedAntelopeAccount: CountImportedAntelopeAccount,
) : BaseScreenModel() {

    companion object {
        private const val SEARCH_DEBOUNCE_TIME_MS = 300L
    }

    // Group local changes for optimistic updates
    private val groupLocalChanges = MutableStateFlow(GroupLocalChangesState())

    // Tag local changes for optimistic updates
    private val tagLocalChanges = MutableStateFlow(TagLocalChangesState())

    // Groups search query for paging
    private val _groupsSearchQuery = MutableStateFlow<String?>(null)
    val groupsSearchQuery = _groupsSearchQuery.asStateFlow()

    // Tags search query for paging
    private val _tagsSearchQuery = MutableStateFlow<String?>(null)
    val tagsSearchQuery = _tagsSearchQuery.asStateFlow()

    // Groups paging flow with alphabet separators and local changes applied
    val groupsPagingFlow: Flow<PagingData<GroupGroupedByAlphabetUiModel>> = _groupsSearchQuery
        .debounce(SEARCH_DEBOUNCE_TIME_MS)
        .distinctUntilChanged()
        .flatMapLatest { query ->
            getAllGroupsUseCase.getPaginatedGroups(query)
        }
        .cachedIn(screenModelScope)
        .combine(groupLocalChanges) { pagingData, groupLocalChanges ->
            pagingData
                .filter { group ->
                    // Filter out locally deleted groups
                    !groupLocalChanges.deletedGroupIds.contains(group.id)
                }
                .map { group ->
                    GroupGroupedByAlphabetUiModel.GroupItem(group)
                }
                .insertSeparators { before, after ->
                    if (after == null) return@insertSeparators null

                    val afterChar = after.group.name.firstOrNull()?.uppercaseChar() ?: '#'

                    if (before == null) {
                        return@insertSeparators GroupGroupedByAlphabetUiModel.AlphabetHeader(afterChar.toString())
                    }

                    val beforeChar = before.group.name.firstOrNull()?.uppercaseChar() ?: '#'

                    if (afterChar != beforeChar) {
                        GroupGroupedByAlphabetUiModel.AlphabetHeader(afterChar.toString())
                    } else {
                        null
                    }
                }
        }

    // Tags paging flow with alphabet separators and local changes applied
    val tagsPagingFlow: Flow<PagingData<TagGroupedByAlphabetUiModel>> = _tagsSearchQuery
        .debounce(SEARCH_DEBOUNCE_TIME_MS)
        .distinctUntilChanged()
        .flatMapLatest { query ->
            getActiveTagsUseCase.getPaginatedTags(query)
        }
        .cachedIn(screenModelScope)
        .combine(tagLocalChanges) { pagingData, tagLocalChanges ->
            pagingData
                .filter { tag ->
                    // Filter out locally deleted tags
                    !tagLocalChanges.deletedTagIds.contains(tag.id)
                }
                .map { tag ->
                    TagGroupedByAlphabetUiModel.TagItem(tag)
                }
                .insertSeparators { before, after ->
                    if (after == null) return@insertSeparators null

                    val afterChar = after.tag.name.firstOrNull()?.uppercaseChar() ?: '#'

                    if (before == null) {
                        return@insertSeparators TagGroupedByAlphabetUiModel.AlphabetHeader(afterChar.toString())
                    }

                    val beforeChar = before.tag.name.firstOrNull()?.uppercaseChar() ?: '#'

                    if (afterChar != beforeChar) {
                        TagGroupedByAlphabetUiModel.AlphabetHeader(afterChar.toString())
                    } else {
                        null
                    }
                }
        }

    private val _availableBlockchains = MutableStateFlow<List<BlockchainTypeEntity>>(emptyList())
    val availableBlockchains: StateFlow<List<BlockchainTypeEntity>> =
        _availableBlockchains.asStateFlow()

    // Tab hiện tại
    private val _currentTabIndex = MutableStateFlow(1) // Mặc định là tab Recent
    val currentTabIndex: StateFlow<Int> = _currentTabIndex.asStateFlow()

    // Privacy mode state
    val privacyModeEnabled: StateFlow<Boolean> = privacyModeViewModel.isEnabled

    // UI state for error handling
    private val _uiState = MutableStateFlow(ContactListScreenUiState())
    val uiState: StateFlow<ContactListScreenUiState> = _uiState.asStateFlow()

    // Navigation events for send flow
    private val _navigationEvents = MutableSharedFlow<SendContactNavigationEvent>()
    val navigationEvents: Flow<SendContactNavigationEvent> = _navigationEvents.asSharedFlow()

    // Search query for recent transactions with Cash App Paging
    private val _recentTransactionsSearchQuery = MutableStateFlow<String?>(null)
    val recentTransactionsSearchQuery = _recentTransactionsSearchQuery.asStateFlow()

    // Cash App Paging flow for recent transactions
    val recentTransactionsPagingFlow: Flow<PagingData<ContactRecentTransactionModel>> =
        _recentTransactionsSearchQuery
            .debounce(SEARCH_DEBOUNCE_TIME_MS)
            .distinctUntilChanged()
            .flatMapLatest { query ->
                getRecentContactsUseCase.getPaginatedContactRecentTransactions(
                    searchQuery = query,
                    statuses = TransactionStatus.entries
                )
            }
            .cachedIn(screenModelScope)

    // Local changes for optimistic updates
    private val contactLocalChanges = MutableStateFlow(ContactLocalChangesState())
    
    // Expose a way to force refresh after external changes
    private val _forceRefresh = MutableSharedFlow<Unit>()
    
    init {
        // Listen to force refresh events
        screenModelScope.launch {
            _forceRefresh.collect {
                // Clear only deleted contacts from local state
                // This preserves other optimistic updates
                contactLocalChanges.update { 
                    it.copy(deletedContactIds = emptySet())
                }
            }
        }

        screenModelScope.launch {
            val count = countImportedAntelopeAccount()
            _uiState.update { it.copy(hasAnyImportedAccount = count > 0) }
        }
    }
    
    /**
     * Call this when returning from detail screen after delete
     * to ensure the list is refreshed
     */
    fun refreshAfterExternalChange() {
        screenModelScope.launch {
            _forceRefresh.emit(Unit)
        }
    }

    // Search query for favorite contacts with Cash App Paging
    private val _favoriteContactsSearchQuery = MutableStateFlow<String?>(null)
    val favoriteContactsSearchQuery = _favoriteContactsSearchQuery.asStateFlow()

    // Cash App Paging flow for favorite contacts with local changes applied
    val favoriteContactsPagingFlow: Flow<PagingData<ContactWithMultipleBlockchainsModel>> =
        _favoriteContactsSearchQuery
            .debounce(SEARCH_DEBOUNCE_TIME_MS)
            .distinctUntilChanged()
            .flatMapLatest { query ->
                getFavoriteContactsUseCase.getPaginatedFavoriteContacts(
                    searchQuery = query
                )
            }
            .cachedIn(screenModelScope)
            .combine(contactLocalChanges) { pagingData, localChanges ->
                pagingData
                    .filter { contact ->
                        // Filter out locally deleted contacts
                        if (localChanges.deletedContactIds.contains(contact.contactId)) {
                            return@filter false
                        }

                        // Filter out contacts that were unfavorited locally
                        val localFavoriteStatus = localChanges.favoriteChanges[contact.contactId]
                        if (localFavoriteStatus == false) {
                            return@filter false
                        }

                        true
                    }
            }

    // Search query for contacts with Cash App Paging
    private val _contactsSearchQuery = MutableStateFlow<String?>(null)
    val contactsSearchQuery = _contactsSearchQuery.asStateFlow()

    // Cash App Paging flow for contacts with local changes applied
    val contactsPagingFlow: Flow<PagingData<ContactGroupedByAlphabetUiModel>> =
        _contactsSearchQuery
            .debounce(SEARCH_DEBOUNCE_TIME_MS)
            .distinctUntilChanged()
            .flatMapLatest { query ->
                getAllContactsUseCase.getPaginatedContacts(searchQuery = query)
            }
            .cachedIn(screenModelScope)
            .combine(contactLocalChanges) { pagingData, localChanges ->
                pagingData
                    .filter { contact ->
                        // Filter out locally deleted contacts
                        !localChanges.deletedContactIds.contains(contact.contactId)
                    }
                    .map { contact ->
                        // Apply local favorite changes
                        val localFavoriteStatus = localChanges.favoriteChanges[contact.contactId]
                        val updatedContact = if (localFavoriteStatus != null) {
                            contact.copy(isFavorite = localFavoriteStatus)
                        } else {
                            contact
                        }
                        ContactGroupedByAlphabetUiModel.ContactItem(updatedContact)
                    }
                    .insertSeparators { before, after ->
                        if (after == null) return@insertSeparators null

                        val afterChar = after.contact.contactName.firstOrNull()?.uppercaseChar() ?: '#'

                        if (before == null) {
                            return@insertSeparators ContactGroupedByAlphabetUiModel.AlphabetHeader(afterChar.toString())
                        }

                        val beforeChar = before.contact.contactName.firstOrNull()?.uppercaseChar() ?: '#'

                        if (afterChar != beforeChar) {
                            ContactGroupedByAlphabetUiModel.AlphabetHeader(afterChar.toString())
                        } else {
                            null
                        }
                    }
            }

    // Reactive flow for top 15 favorite contacts for recent transaction tab
    // Only loads when there are active collectors (when recent transaction tab is visible)
    val topFavoriteContacts: StateFlow<List<ContactModel>?> = flow {
        emitAll(getFavoriteContactsUseCase.invokeFlow(limit = 15))
    }
        .catch { emit(emptyList()) }
        .stateIn(
            scope = screenModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
            initialValue = null
        )

    init {
        // Khởi tạo chỉ load những dữ liệu cần thiết
        loadAvailableBlockchains()
    }

    /**
     * Chuyển tab và cập nhật dữ liệu thông qua Flow
     * Cải tiến để giữ lại dữ liệu đã tải cho các tab khác nhau, tránh hiệu ứng giật màn hình
     */
    fun setCurrentTab(tabIndex: Int) {
        _currentTabIndex.value = tabIndex
    }

    private fun loadAvailableBlockchains() {
        screenModelScope.launch {
            try {
                val blockchains = getAllBlockchainTypesUseCase()
                _availableBlockchains.value = blockchains
            } catch (e: Exception) {
                // Error loading blockchain types - log for debugging
                println("Error loading blockchain types: ${e.message}")
            }
        }
    }

    /**
     * Update search query for recent transactions with Cash App Paging
     * This will trigger the paging flow to reload with the new search query
     */
    fun updateRecentTransactionsSearchQuery(query: String) {
        _recentTransactionsSearchQuery.value = query.takeIf { it.isNotBlank() }
    }

    /**
     * Update search query for favorite contacts with Cash App Paging
     * This will trigger the paging flow to reload with the new search query
     */
    fun updateFavoriteContactsSearchQuery(query: String) {
        _favoriteContactsSearchQuery.value = query.takeIf { it.isNotBlank() }
    }

    /**
     * Update search query for contacts with Cash App Paging
     * This will trigger the paging flow to reload with the new search query
     */
    fun updateContactsSearchQuery(query: String) {
        _contactsSearchQuery.value = query.takeIf { it.isNotBlank() }
    }

    fun updateGroupsSearchQuery(query: String) {
        _groupsSearchQuery.value = query.takeIf { it.isNotBlank() }
    }

    /**
     * Update search query for tags with Cash App Paging
     * This will trigger the paging flow to reload with the new search query
     */
    fun updateTagsSearchQuery(query: String) {
        _tagsSearchQuery.value = query.takeIf { it.isNotBlank() }
    }

    /**
     * Optimistically delete a contact from local state without refreshing paging
     * Real deletion will happen in background and clear on refresh/navigation
     */
    fun deleteContactOptimistically(contactId: String) {
        // Add to deleted contacts in local state
        contactLocalChanges.update { currentState ->
            currentState.copy(
                deletedContactIds = currentState.deletedContactIds + contactId
            )
        }

        // Perform actual deletion in background
        screenModelScope.launch {
            try {
                val result = deleteContactUseCase(contactId)

                if (!result) {
                    // Nếu xóa thất bại, khôi phục lại dữ liệu UI
                    contactLocalChanges.update { currentState ->
                        currentState.copy(
                            deletedContactIds = currentState.deletedContactIds - contactId
                        )
                    }
                }
            } catch (e: Exception) {
                println("delete contact error: $e")
                // If deletion fails, remove from local deleted list
                contactLocalChanges.update { currentState ->
                    currentState.copy(
                        deletedContactIds = currentState.deletedContactIds - contactId
                    )
                }
            }
        }
    }


    /**
     * Optimistically toggle favorite status in local state without refreshing paging
     * Real favorite change will happen in background and clear on refresh/navigation
     */
    fun toggleContactFavoriteOptimistically(contact: ContactModel) {
        val newIsFavorite = !contact.isFavorite

        // Update local changes
        contactLocalChanges.update { currentState ->
            currentState.copy(
                favoriteChanges = currentState.favoriteChanges + (contact.contactId to newIsFavorite)
            )
        }

        // Perform actual favorite toggle in background
        screenModelScope.launch {
            try {
                val result = if (contact.isFavorite) {
                    removeFavoriteUseCase(contact.contactId)
                } else {
                    addFavoriteUseCase(contact.contactId)
                }

                // If operation fails, remove from local changes
                if (!result) {
                    contactLocalChanges.update { currentState ->
                        currentState.copy(
                            favoriteChanges = currentState.favoriteChanges - contact.contactId
                        )
                    }
                }
            } catch (e: Exception) {
                // If operation fails, remove from local changes
                contactLocalChanges.update { currentState ->
                    currentState.copy(
                        favoriteChanges = currentState.favoriteChanges - contact.contactId
                    )
                }
            }
        }
    }

    /**
     * Clear local changes - called on pull-to-refresh or navigation
     * This will show the real data from the database
     */
    fun clearLocalChanges() {
        contactLocalChanges.update { ContactLocalChangesState() }
    }

    /**
     * Clear group local changes - called on pull-to-refresh or navigation
     * This will show the real data from the database
     */
    fun clearGroupLocalChanges() {
        groupLocalChanges.update { GroupLocalChangesState() }
    }

    /**
     * Clear tag local changes - called on pull-to-refresh or navigation
     * This will show the real data from the database
     */
    fun clearTagLocalChanges() {
        tagLocalChanges.update { TagLocalChangesState() }
    }

    /**
     * Optimistically delete a group from local state without refreshing paging
     * Real deletion will happen in background and clear on refresh/navigation
     */
    fun deleteGroupOptimistically(groupId: String) {
        // Add to deleted groups in local state
        groupLocalChanges.update { currentState ->
            currentState.copy(
                deletedGroupIds = currentState.deletedGroupIds + groupId
            )
        }

        // Perform actual deletion in background
        screenModelScope.launch {
            try {
                val result = deleteGroupUseCase(groupId)

                if (!result) {
                    // Nếu xóa thất bại, khôi phục lại dữ liệu UI
                    groupLocalChanges.update { currentState ->
                        currentState.copy(
                            deletedGroupIds = currentState.deletedGroupIds - groupId
                        )
                    }
                }
            } catch (e: Exception) {
                println("delete group error: $e")
                // If deletion fails, remove from local deleted list
                groupLocalChanges.update { currentState ->
                    currentState.copy(
                        deletedGroupIds = currentState.deletedGroupIds - groupId
                    )
                }
            }
        }
    }

    /**
     * Optimistically delete a tag from local state without refreshing paging
     * Real deletion will happen in background and clear on refresh/navigation
     */
    fun deleteTagOptimistically(tagId: String) {
        // Add to deleted tags in local state
        tagLocalChanges.update { currentState ->
            currentState.copy(
                deletedTagIds = currentState.deletedTagIds + tagId
            )
        }

        // Perform actual deletion in background
        screenModelScope.launch {
            try {
                val result = deleteTagUseCase.hardDeleteTag(tagId)

                if (!result) {
                    // Nếu xóa thất bại, khôi phục lại dữ liệu UI
                    tagLocalChanges.update { currentState ->
                        currentState.copy(
                            deletedTagIds = currentState.deletedTagIds - tagId
                        )
                    }
                }
            } catch (e: Exception) {
                println("delete tag error: $e")
                // If deletion fails, remove from local deleted list
                tagLocalChanges.update { currentState ->
                    currentState.copy(
                        deletedTagIds = currentState.deletedTagIds - tagId
                    )
                }
            }
        }
    }

    /**
     * Toggle privacy mode
     */
    fun togglePrivacyMode() {
        privacyModeViewModel.toggle()
    }

    /**
     * Smart routing for contact send flow - determines navigation based on contact's address count
     */
    fun onContactSelected(contact: ContactModel) {
        screenModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true, error = null)

                // Smart routing logic: Check number of addresses for the contact
                val count = countWalletAddressesForContactUseCase(contact.contactId)

                when (count) {
                    0 -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = "No addresses found for this contact"
                        )
                    }

                    1 -> {
                        // Single address: Validate network support before navigation
                        val address = getWalletAddressesForContactUseCase(
                            contact.contactId,
                            limit = 1
                        ).first()

                        // Check if the blockchain network is supported for sending
                        if (isNetworkSupportedForSending(address.blockchainNetworkId)) {
                            _navigationEvents.emit(
                                SendContactNavigationEvent.NavigateToStep3(
                                    accountId = "", // No need accountId here because the send flow does not use it, also we can't get it here
                                    address = address.address,
                                    blockchainUid = address.blockchainNetworkId,
                                )
                            )
                        } else {
                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                error = "Does not support sending token on ${address.networkName}."
                            )
                        }
                    }

                    else -> {
                        // Multiple addresses: Navigate to address selection
                        _navigationEvents.emit(
                            SendContactNavigationEvent.NavigateToSelectAddress(
                                contactId = contact.contactId,
                                accountId = ""
                            )
                        )
                    }
                }

                _uiState.value = _uiState.value.copy(isLoading = false)

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Failed to process contact selection: ${e.message}"
                )
            }
        }
    }

    /**
     * Clear error state
     */
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    /**
     * Check if the network type is supported for sending transactions
     * Currently supports ANTELOPE networks
     */
    private fun isNetworkSupportedForSending(blockchainUid: String): Boolean {
        return when (BlockchainType.fromUid(blockchainUid).networkType) {
            NetworkType.ANTELOPE -> true
            else -> false
        }
    }
}