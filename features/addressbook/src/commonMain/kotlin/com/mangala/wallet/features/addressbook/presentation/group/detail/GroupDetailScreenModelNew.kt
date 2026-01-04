package com.mangala.wallet.features.addressbook.presentation.group.detail

import cafe.adriel.voyager.core.model.screenModelScope
import com.mangala.wallet.features.addressbook.data.model.contact.ContactWithAddressesModel
import com.mangala.wallet.features.addressbook.data.model.contact.PaginatedContactsResult
import com.mangala.wallet.features.addressbook.data.model.group.GroupModel
import com.mangala.wallet.features.addressbook.domain.model.group.GroupWallet
import com.mangala.wallet.features.addressbook.domain.usecase.clipboard.CopyToClipboardUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.group.DeleteGroupUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.group.GetGroupUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.group.GetGroupModelByIdUseCase
import com.mangala.wallet.ui.utils.screenmodel.BaseScreenModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import com.mangala.wallet.features.addressbook.utils.Constants

sealed class GroupDetailNavigationEvent {
    object NavigateBack : GroupDetailNavigationEvent()
}

class GroupDetailScreenModelNew(
    private val getGroupUseCase: GetGroupUseCase,
    private val getGroupModelByIdUseCase: GetGroupModelByIdUseCase,
    private val deleteGroupUseCase: DeleteGroupUseCase,
    private val copyToClipboardUseCase: CopyToClipboardUseCase,
    private val privacyModeViewModel: com.mangala.wallet.features.addressbook.presentation.privacy.PrivacyModeViewModel
) : BaseScreenModel() {

    companion object {
        private const val NAVIGATION_EVENT_REPEAT_COUNT = 3
        private const val NAVIGATION_EVENT_DELAY_MS = 100L
    }

    val privacyModeEnabled = privacyModeViewModel.isEnabled

    private val _state = MutableStateFlow(GroupDetailState())
    val state: StateFlow<GroupDetailState> = _state
    
    // Navigation events flow
    private val _navigationEvents = MutableSharedFlow<GroupDetailNavigationEvent>()
    val navigationEvents: SharedFlow<GroupDetailNavigationEvent> = _navigationEvents
    
    // Track deletion state to prevent race conditions
    var isDeleting = false
        private set
    private fun updateState(newState: GroupDetailState, operation: String) {
        _state.value = newState
    }

    // Constants
    private val PAGE_SIZE = Constants.Pagination.DEFAULT_PAGE_SIZE

    // State for pagination
    private var currentOffset = 0
    private var totalWalletCount = 0
    private var hasMoreData = true

    fun fetchGroupDetails(groupId: String) {
        screenModelScope.launch {
            updateState(_state.value.copy(isLoading = true), "SET_LOADING_TRUE")
            
            try {
                // Load group details
                val groupModel = getGroupModelByIdUseCase(groupId)
                updateState(_state.value.copy(group = groupModel), "GROUP_LOADED")
                // Load initial set of wallet addresses
                loadGroupWallets(groupId, 0)
            } catch (e: Exception) {
                // Handle errors
                e.printStackTrace()
                updateState(_state.value.copy(
                    error = "Error loading group details: ${e.message}"
                ), "ERROR_OCCURRED")
            } finally {
                updateState(_state.value.copy(isLoading = false), "SET_LOADING_FALSE")
            }
        }
    }

    fun loadMore(groupId: String) {
        if (hasMoreData && !_state.value.isLoadingMore) {
            screenModelScope.launch {
                _state.value = _state.value.copy(isLoadingMore = true)
                
                try {
                    loadGroupWallets(groupId, currentOffset + PAGE_SIZE)
                } catch (e: Exception) {
                    _state.value = _state.value.copy(
                        error = "Error loading more wallets: ${e.message}",
                        isLoadingMore = false
                    )
                } finally {
                    _state.value = _state.value.copy(isLoadingMore = false)
                }
            }
        }
    }
    
    private suspend fun loadGroupWallets(groupId: String, offset: Int) {
        try {
            // Get wallet addresses directly from GetGroupUseCase
            val wallets = getGroupUseCase.getGroupWallets(groupId, PAGE_SIZE, offset)
            
            // Update current offset
            currentOffset = offset
            
            // Calculate if we have more data based on returned results
            // If we get fewer items than PAGE_SIZE, we've reached the end
            hasMoreData = wallets.size == PAGE_SIZE
            
            // Update wallet list
            val updatedWallets = if (offset == 0) {
                // First page - replace the list
                wallets
            } else {
                // Additional pages - append to the list
                _state.value.wallets + wallets
            }
            
            // Update UI state
            _state.value = _state.value.copy(
                wallets = updatedWallets,
                canLoadMore = hasMoreData,
                allWallets = if (_state.value.allWallets.isEmpty()) updatedWallets else _state.value.allWallets,
                isLoading = false,
                totalWalletCount = updatedWallets.size
            )
        } catch (e: Exception) {
            println("[GroupDetailScreenModel] ERROR: ${e.message}")
            e.printStackTrace()
            _state.value = _state.value.copy(
                error = "Error loading wallet addresses: ${e.message}",
                isLoading = false
            )
        }
    }
    
    fun copyAddressToClipboard(address: String) {
        copyToClipboardUseCase(address)
        
        // Optional: Show a success message
        _state.value = _state.value.copy(
            clipboardMessage = "Address copied to clipboard"
        )
        
        // Clear message after delay
        screenModelScope.launch {
            kotlinx.coroutines.delay(Constants.Timing.TOAST_DURATION_MS)
            _state.value = _state.value.copy(clipboardMessage = null)
        }
    }
    
    fun searchWallets(query: String) {
        val currentState = _state.value
        
        // Save original list when starting search
        val originalWallets = if (currentState.allWallets.isEmpty() && currentState.searchQuery.isEmpty()) {
            currentState.wallets
        } else {
            currentState.allWallets.ifEmpty { currentState.wallets }
        }
        
        val filteredWallets = if (query.isBlank()) {
            // Return to original list when search is cleared
            originalWallets
        } else {
            originalWallets.filter { wallet ->
                listOf(
                    wallet.contactName,
                    wallet.walletAddress,
                    wallet.walletAlias,
                    wallet.blockchainTypeSymbol
                ).any { field ->
                    field?.contains(query, ignoreCase = true) == true
                }
            }
        }
        
        _state.value = currentState.copy(
            wallets = filteredWallets,
            allWallets = originalWallets, // Always maintain original list
            searchQuery = query,
            // Reset pagination when searching
            canLoadMore = query.isBlank() && hasMoreData
        )
    }
    
    fun togglePrivacyMode() {
        privacyModeViewModel.toggle()
    }
    
    fun deleteGroup(groupId: String) {
        screenModelScope.launch {
            try {
                // Set deletion flag to prevent reload
                isDeleting = true
                
                val result = deleteGroupUseCase(groupId)
                
                if (result) {
                    // Emit navigation event multiple times to ensure it's caught
                    // The repository will automatically emit GroupChangeEvent.Deleted
                    repeat(NAVIGATION_EVENT_REPEAT_COUNT) {
                        _navigationEvents.emit(GroupDetailNavigationEvent.NavigateBack)
                        delay(NAVIGATION_EVENT_DELAY_MS)
                    }
                } else {
                    _state.value = _state.value.copy(
                        error = "Failed to delete group"
                    )
                    isDeleting = false
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    error = "Failed to delete group: ${e.message}"
                )
                isDeleting = false
            }
        }
    }

    data class GroupDetailState(
        val group: GroupModel? = null,
        val wallets: List<GroupWallet> = emptyList(),
        val allWallets: List<GroupWallet> = emptyList(),
        val isLoading: Boolean = true,
        val isLoadingMore: Boolean = false,
        val canLoadMore: Boolean = false,
        val totalWalletCount: Int = 0,
        val error: String? = null,
        val clipboardMessage: String? = null,
        val searchQuery: String = ""
    )
}