package com.mangala.wallet.features.addressbook.presentation.group.create

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.mangala.wallet.features.addressbook.domain.model.group.GroupWallet
import com.mangala.wallet.features.addressbook.domain.usecase.blockchain.GetAvailableContactsForBlockchainUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel for WalletAddressBottomSheet that handles paging and database search
 */
class WalletAddressBottomSheetViewModel(
    private var blockchainId: String, // Mutable to support refreshing with a new blockchain ID
    private val getAvailableContactsForBlockchainUseCase: GetAvailableContactsForBlockchainUseCase,
    private val onWalletsSelected: (List<String>) -> Unit,
    private val initialSelectedWalletIds: List<String> = emptyList()
) : ScreenModel {

    private val _uiState = MutableStateFlow(WalletBottomSheetUiState())
    val uiState: StateFlow<WalletBottomSheetUiState> = _uiState.asStateFlow()

    // Constants for pagination
    private val PAGE_SIZE = 30
    private var currentPage = 0
    private var hasMoreData = true

    init {
        println("=== INIT: WalletAddressBottomSheetViewModel with blockchainId: $blockchainId ===")
        // Initialize with selected wallet IDs
        _uiState.update { it.copy(
            selectedWalletAddressIds = initialSelectedWalletIds.toSet(),
            selectedCount = initialSelectedWalletIds.size
        ) }
        // Load initial data
        loadWallets(refresh = true)
    }
    
    /**
     * Refreshes the ViewModel with a new blockchain ID and selected wallet IDs
     * This is used when the user switches between different blockchains or edits selected wallets
     */
    fun refreshForBlockchain(newBlockchainId: String, selectedWalletIds: List<String>) {
        if (newBlockchainId != blockchainId) {
            println("=== BLOCKCHAIN CHANGED: $blockchainId -> $newBlockchainId ===")
            blockchainId = newBlockchainId
            
            // Update selected wallet IDs
            _uiState.update { it.copy(
                selectedWalletAddressIds = selectedWalletIds.toSet(),
                selectedCount = selectedWalletIds.size
            )}
            
            // Reset and reload data for new blockchain
            loadWallets(refresh = true)
        } else if (selectedWalletIds.toSet() != _uiState.value.selectedWalletAddressIds) {
            // Only selected wallet IDs changed
            println("=== SELECTED WALLETS CHANGED: ${_uiState.value.selectedWalletAddressIds.size} -> ${selectedWalletIds.size} ===")
            _uiState.update { it.copy(
                selectedWalletAddressIds = selectedWalletIds.toSet(),
                selectedCount = selectedWalletIds.size
            )}
        }
    }

    /**
     * Loads wallets from the database with pagination
     * @param refresh If true, clears existing data and loads from page 0
     */
    fun loadWallets(refresh: Boolean = false) {
        if (refresh) {
            currentPage = 0
            _uiState.update { it.copy(
                wallets = emptyList(),
                isLoading = true,
                hasReachedEnd = false
            ) }
        } else {
            // If already loading or no more data, don't load
            if (_uiState.value.isLoadingMore || !hasMoreData) return
            _uiState.update { it.copy(isLoadingMore = true) }
        }

        screenModelScope.launch {
            try {
                // Calculate offset based on current page
                val offset = currentPage * PAGE_SIZE
                val searchQuery = _uiState.value.searchQuery

                println("=== LOADING: Wallets for blockchainId: $blockchainId, offset: $offset, query: $searchQuery ===")
                // Get wallets from database with pagination and search
                val newWallets = getAvailableContactsForBlockchainUseCase.getGroupWallets(
                    blockchainId = blockchainId,
                    limit = PAGE_SIZE,
                    offset = offset,
                    searchQuery = searchQuery
                )
                println("=== LOADED: ${newWallets.size} wallets, first blockchain symbol: ${newWallets.firstOrNull()?.blockchainTypeSymbol ?: "none"} ===")

                // Determine if there's more data to load
                hasMoreData = newWallets.size == PAGE_SIZE

                // Update state with new data
                _uiState.update { currentState ->
                    val updatedWallets = if (refresh) {
                        newWallets
                    } else {
                        currentState.wallets + newWallets
                    }

                    currentState.copy(
                        wallets = updatedWallets,
                        isLoading = false,
                        isLoadingMore = false,
                        hasReachedEnd = !hasMoreData
                    )
                }

                // Increment page for next load
                currentPage++
            } catch (e: Exception) {
                _uiState.update { it.copy(
                    isLoading = false,
                    isLoadingMore = false,
                    error = e.message ?: "Failed to load wallets"
                ) }
            }
        }
    }

    /**
     * Called when the user changes the search query
     */
    fun onSearchQueryChanged(query: String) {
        // Update state with new query
        _uiState.update { it.copy(searchQuery = query) }
        
        // Reset and reload with new query
        loadWallets(refresh = true)
    }

    /**
     * Toggles the selection of a wallet
     */
    fun toggleWalletAddressSelection(walletId: String) {
        val currentSelectedIds = _uiState.value.selectedWalletAddressIds
        val updatedSelectedIds = if (currentSelectedIds.contains(walletId)) {
            currentSelectedIds - walletId
        } else {
            currentSelectedIds + walletId
        }

        _uiState.update { it.copy(
            selectedWalletAddressIds = updatedSelectedIds,
            selectedCount = updatedSelectedIds.size
        ) }
    }

    /**
     * Confirms the selection and passes it to the callback
     */
    fun confirmSelection() {
        onWalletsSelected(_uiState.value.selectedWalletAddressIds.toList())
    }

    /**
     * Called when the bottom sheet is shown to initialize or update selected wallet IDs
     */
    fun onSheetShown(selectedWalletIds: List<String>) {
        _uiState.update { it.copy(
            selectedWalletAddressIds = selectedWalletIds.toSet(),
            selectedCount = selectedWalletIds.size
        ) }
    }

    /**
     * Checks if a wallet is selected
     */
    fun isWalletSelected(walletId: String): Boolean {
        return _uiState.value.selectedWalletAddressIds.contains(walletId)
    }

    /**
     * Loads more wallets when the user scrolls to the end of the list
     */
    fun loadMoreWallets() {
        if (!_uiState.value.isLoadingMore && !_uiState.value.hasReachedEnd) {
            loadWallets(refresh = false)
        }
    }
}

/**
 * UI state for the wallet address bottom sheet
 */
data class WalletBottomSheetUiState(
    val wallets: List<GroupWallet> = emptyList(),
    val selectedWalletAddressIds: Set<String> = emptySet(),
    val searchQuery: String = "",
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val hasReachedEnd: Boolean = false,
    val error: String? = null,
    val selectedCount: Int = 0
)