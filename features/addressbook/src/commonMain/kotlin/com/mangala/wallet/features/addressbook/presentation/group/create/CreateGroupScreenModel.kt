package com.mangala.wallet.features.addressbook.presentation.group.create

import cafe.adriel.voyager.core.model.screenModelScope
import com.mangala.wallet.features.addressbook.data.model.group.GroupEntity
import com.mangala.wallet.features.addressbook.data.model.blockchain.BlockchainTypeEntity
import com.mangala.wallet.features.addressbook.data.model.blockchain.WalletAddressEntity
import com.mangala.wallet.features.addressbook.data.model.contact.ContactEntity
import com.mangala.wallet.features.addressbook.data.model.enum.PrivacyLevel
import com.mangala.wallet.features.addressbook.data.model.enum.SecurityLevel
import com.mangala.wallet.features.addressbook.domain.model.AvatarSource
import com.mangala.wallet.features.addressbook.domain.model.group.GroupWallet
import com.mangala.wallet.features.addressbook.domain.usecase.avatar.AvatarUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.blockchain.GetAllBlockchainTypesUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.blockchain.GetAvailableContactsForBlockchainUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.contact.GetContactsWithWalletAddressPaginatedUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.group.CreateGroupUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.group.GetGroupUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.group.UpdateGroupUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.wallet_address.GetContactWalletByWalletIdsUseCase
import com.mangala.wallet.features.addressbook.domain.validation.ValidationConstants
import com.mangala.wallet.ui.utils.screenmodel.BaseScreenModel
import com.mangala.wallet.utils.localDateTimeNow
import com.mangala.wallet.utils.localDateTimeToMillis
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.launch
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import app.cash.paging.PagingData
import app.cash.paging.Pager
import app.cash.paging.PagingConfig
import app.cash.paging.cachedIn
import app.cash.paging.map
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import com.mangala.wallet.features.addressbook.presentation.tag.model.AddressSelectionContactModel
import com.mangala.wallet.features.addressbook.data.model.ContactWithMultipleBlockchainsModel

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
class CreateGroupScreenModel(
    private val getBlockchainTypesUseCase: GetAllBlockchainTypesUseCase,
    private val createGroupUseCase: CreateGroupUseCase,
    val getAvailableContactsForBlockchainUseCase: GetAvailableContactsForBlockchainUseCase, // Expose for bottom sheet
    private val updateGroupUseCase: UpdateGroupUseCase,
    private val getGroupUseCase: GetGroupUseCase,
    private val groupId: String? = null, // If provided, we're in edit mode
    private val avatarUseCase: AvatarUseCase,
    val getContactWalletByWalletIdsUseCase: GetContactWalletByWalletIdsUseCase,
    private val groupWalletRepository: com.mangala.wallet.features.addressbook.domain.repository.group.GroupWalletRepository,
    private val getContactsWithWalletAddressPaginatedUseCase: GetContactsWithWalletAddressPaginatedUseCase
) : BaseScreenModel() {
    private val modelInstanceId =
        kotlinx.datetime.Clock.System.now().toEpochMilliseconds().toString().takeLast(6)

    // StateFlow để quản lý trạng thái hiển thị bottom sheet
    private val _showContactBottomSheet = MutableStateFlow(false)
    val showContactBottomSheet: StateFlow<Boolean> = _showContactBottomSheet.asStateFlow()

    private val stateMutex = Mutex()
    private var hasLoaded = false

    private var stateUpdateJob: Job? = null
    private val stateDebounceMs = 50L // Prevent flashes from rapid changes

    private var loadingTimeoutJob: Job? = null

    private var saveJob: Job? = null

    private val _uiState = MutableStateFlow(
        CreateGroupUiState(
            isInitialized = true,
            loadingState = if (groupId != null) CreateGroupUiState.LoadingState.InitialLoad else CreateGroupUiState.LoadingState.None,
            isEditMode = groupId != null
        )
    )
    val uiState: StateFlow<CreateGroupUiState> = _uiState.asStateFlow()

    /**
     * Reactive flow of selected wallet IDs
     * Automatically updates when selectedWallets changes in _uiState
     * Bottom Sheet will observe this flow for real-time synchronization
     */
    val selectedWalletIdsFlow: StateFlow<List<String>> = _uiState
        .map { state ->
            state.selectedWallets.map { wallet -> wallet.walletId }
        }
        .stateIn(
            scope = screenModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    // Search query for contact selection
    private val _contactSearchQuery = MutableStateFlow("")
    val contactSearchQuery: StateFlow<String> = _contactSearchQuery.asStateFlow()

    // Local state for temporary contact selections (before applying)
    private val _tempSelectedContactIds = MutableStateFlow<Set<String>>(emptySet())

    private fun updateStateWithLogging(
        operation: String,
        update: CreateGroupUiState.() -> CreateGroupUiState
    ) {
        val oldState = _uiState.value
        val newState = oldState.update()

        if (oldState.loadingState != newState.loadingState) {
            println("   ⚠️ LOADING STATE CHANGED: ${oldState.loadingState} → ${newState.loadingState}")
        }

        _uiState.value = newState
    }

    private fun updateUiStateDebounced(update: CreateGroupUiState.() -> CreateGroupUiState) {
        val currentState = _uiState.value
        val newState = currentState.update()

        stateUpdateJob?.cancel()

        // If transitioning TO loading, update immediately
        if (newState.isAnyLoading && !currentState.isAnyLoading) {
            _uiState.value = newState
            return
        }

        // If transitioning FROM loading, debounce to prevent flash
        if (!newState.isAnyLoading && currentState.isAnyLoading) {
            stateUpdateJob = screenModelScope.launch {
                delay(stateDebounceMs)
                _uiState.value = newState
            }
            return
        }
        _uiState.value = newState
    }

    // Pagination state for selected wallets
    private var selectedWalletsPage = 0
    private val walletsPageSize = 20

    // ✅ FIX: Improved lifecycle handling to prevent double flash
    override fun doOnComposableStarted() {
        val timestamp = kotlinx.datetime.Clock.System.now().toEpochMilliseconds()

        // Only load once when composable starts to prevent double flash
        if (!hasLoaded) {
            hasLoaded = true
            screenModelScope.launch {
                if (groupId != null) {
                    // Xóa phần loadingTimeoutJob
                    loadBlockchainTypes()
                    loadExistingGroup(groupId)
                } else {
                    loadBlockchainTypes()
                }
            }
        } else {
            println("🔁 [${timestamp}] CreateGroupModel: Already loaded, skipping initialization")
        }
    }

    private suspend fun loadBlockchainTypes() {
        try {
            val blockchainTypes = getBlockchainTypesUseCase()
            updateStateWithLogging("BLOCKCHAIN_TYPES_LOADED") {
                copy(blockchainTypes = blockchainTypes)
            }
        } catch (e: Exception) {
            updateStateWithLogging("BLOCKCHAIN_TYPES_ERROR") {
                copy(
                    error = ErrorState.LoadBlockchainTypesError(
                        e.message ?: "Failed to load blockchain types"
                    )
                )
            }
        }
    }

    private suspend fun loadExistingGroup(groupId: String) {
        try {
            println("=== loadExistingGroup: $groupId")
            val group = getGroupUseCase(groupId)
            println("=== group: $group")

            // Reset pagination
            selectedWalletsPage = 0

            // Load first page of wallets
            val groupWallets = getGroupUseCase.getGroupWallets(
                groupId = groupId,
                limit = walletsPageSize,
                offset = 0
            )

            // Get total count of wallets in the group
            val totalWalletsCount = groupWalletRepository.getGroupWalletsByGroupId(groupId).size
            println("=== DEBUG: Group has total $totalWalletsCount wallets, loaded first ${groupWallets.size} ===")

            if (group != null) {
                updateStateWithLogging("GROUP_LOADED") {
                    copy(
                        isEditMode = true,
                        groupName = group.name,
                        selectedBlockchainId = group.mainBlockchainId,
                        description = group.description ?: "",
                        color = group.color,
                        icon = group.icon,
                        privacyLevel = group.privacyLevel,
                        securityLevel = group.securityLevel,
                        selectedWallets = groupWallets,
                        totalWalletsInDb = totalWalletsCount,
                        hasMoreSelectedWallets = groupWallets.size < totalWalletsCount,
                        loadingState = CreateGroupUiState.LoadingState.None,
                        existingGroup = group
                    )
                }

                // Increment page for next load
                selectedWalletsPage = 1

                println("=== DEBUG: Loaded existing group with ${groupWallets.size} wallets (total: $totalWalletsCount) ===")

                // If blockchain is selected, load available contacts for this blockchain
                group.mainBlockchainId?.let { blockchainId ->
                    loadAvailableWallets(blockchainId)
                }
            } else {
                updateStateWithLogging("GROUP_NOT_FOUND") {
                    copy(
                        error = ErrorState.GroupNotFoundError("Group not found"),
                        loadingState = CreateGroupUiState.LoadingState.None
                    )
                }
            }
        } catch (e: Exception) {
            updateStateWithLogging("LOAD_GROUP_ERROR") {
                copy(
                    error = ErrorState.LoadGroupError(e.message ?: "Failed to load group"),
                    loadingState = CreateGroupUiState.LoadingState.None
                )
            }
        }
    }


    /**
     * Loads more selected wallets for pagination
     */
    fun loadMoreSelectedWallets() {
        if (groupId == null || _uiState.value.isLoadingMoreWallets || !_uiState.value.hasMoreSelectedWallets) {
            return
        }

        screenModelScope.launch {
            try {
                _uiState.update { it.copy(isLoadingMoreWallets = true) }

                val offset = selectedWalletsPage * walletsPageSize
                val moreWallets = getGroupUseCase.getGroupWallets(
                    groupId = groupId,
                    limit = walletsPageSize,
                    offset = offset
                )

                _uiState.update { currentState ->
                    val updatedWallets = currentState.selectedWallets + moreWallets
                    currentState.copy(
                        selectedWallets = updatedWallets,
                        hasMoreSelectedWallets = updatedWallets.size < currentState.totalWalletsInDb,
                        isLoadingMoreWallets = false
                    )
                }

                selectedWalletsPage++

                println("=== DEBUG: Loaded ${moreWallets.size} more wallets, total: ${_uiState.value.selectedWallets.size} ===")
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        error = ErrorState.LoadContactsError(
                            e.message ?: "Failed to load more wallets"
                        ),
                        isLoadingMoreWallets = false
                    )
                }
            }
        }
    }

    fun onGroupNameChanged(name: String) {
        // Enhanced validation for group name
        val trimmedName = name.trim()
        val validationError = validateGroupNameInput(trimmedName)

        if (validationError == null && name.length <= ValidationConstants.MAX_GROUP_NAME_LENGTH) {
            _uiState.update { it.copy(groupName = name, groupNameError = null) }
        } else if (validationError != null) {
            _uiState.update { it.copy(groupName = name, groupNameError = validationError) }
        }
    }

    private fun validateGroupNameInput(name: String): String? {
        return when {
            name.isBlank() -> null // Allow empty for real-time validation
            name.length > ValidationConstants.MAX_GROUP_NAME_LENGTH -> "Group name cannot exceed ${ValidationConstants.MAX_GROUP_NAME_LENGTH} characters"
            name.contains(Regex("[<>:\"/\\|?*]")) -> "Group name contains invalid characters"
            name.all { it.isWhitespace() } -> "Group name cannot be only whitespace"
            isReservedName(name.lowercase()) -> "This name is reserved"
            else -> null
        }
    }

    private fun isReservedName(name: String): Boolean {
        val reservedNames = setOf("admin", "system", "default", "null", "undefined")
        return reservedNames.contains(name)
    }

    fun onDescriptionChanged(description: String) {
        _uiState.update { it.copy(description = description) }
    }

    fun onBlockchainSelected(blockchainId: String) {
        println("=== DEBUG: Blockchain selected with ID: $blockchainId ===")

        // Xử lý trường hợp fallback network
        val isFallbackNetwork = blockchainId.startsWith("fallback_")

        if (isFallbackNetwork) {
            println("=== DEBUG: Using fallback network with ID: $blockchainId ===")
            // Tạo một blockchain mới để hiển thị trên UI
            val networkSymbol = blockchainId.substringAfter("fallback_").uppercase()
            val networkName = when (networkSymbol) {
                "ETH" -> "Ethereum"
                "BTC" -> "Bitcoin"
                "BNB" -> "Binance Smart Chain"
                "SOL" -> "Solana"
                "AVAX" -> "Avalanche"
                "MATIC" -> "Polygon"
                else -> networkSymbol
            }

            val fallbackBlockchain = BlockchainTypeEntity(
                id = blockchainId,
                name = networkName,
                symbol = networkSymbol,
                isActive = true,
                networkType = BlockchainTypeEntity.NETWORK_MAINNET
            )

            // Cập nhật danh sách blockchainTypes nếu chưa có blockchain này
            val currentTypes = _uiState.value.blockchainTypes
            if (currentTypes.none { it.id == blockchainId }) {
                _uiState.update {
                    it.copy(
                        blockchainTypes = currentTypes + fallbackBlockchain
                    )
                }
            }
        }

        _uiState.update {
            it.copy(
                selectedBlockchainId = blockchainId,
                // Clear selected wallets when blockchain changes
                selectedWallets = emptyList(),
                // Clear available contacts to trigger reload
                availableContacts = emptyList()
            )
        }

        screenModelScope.launch {
//            val shouldShowLoading = _uiState.value.availableGroupWallets.isNotEmpty() ||
//                    _uiState.value.selectedWallets.isNotEmpty()
//
//            if (shouldShowLoading) {
////                setLoadingWithTimeout(CreateGroupUiState.LoadingState.LoadingContacts)
//            }

            loadAvailableWallets(blockchainId)
        }
    }

    private suspend fun loadAvailableWallets(blockchainId: String) {
        try {
            // Load contacts using the new GroupWallet method instead of the old ContactWithAddress method
            val groupWallets =
                getAvailableContactsForBlockchainUseCase.getGroupWallets(blockchainId)
            // Still populate the availableContacts field for backward compatibility
            val contacts = getAvailableContactsForBlockchainUseCase(blockchainId)
            _uiState.update {
                it.copy(
                    availableContacts = contacts,
                    availableGroupWallets = groupWallets,  // Store the group wallets separately
                    loadingState = CreateGroupUiState.LoadingState.None
                )
            }
        } catch (e: Exception) {
            _uiState.update {
                it.copy(
                    error = ErrorState.LoadContactsError(e.message ?: "Failed to load contacts"),
                    loadingState = CreateGroupUiState.LoadingState.None
                )
            }
        }
    }

    // Thêm các phương thức để quản lý bottom sheet
    fun showContactBottomSheet() {
        println("=== DEBUG: Showing contact bottom sheet ===")
        _showContactBottomSheet.value = true
    }

    fun hideContactBottomSheet() {
        println("=== DEBUG: Hiding contact bottom sheet ===")
        _showContactBottomSheet.value = false
    }


    fun onWalletsSelected(walletIdsSelected: List<String>) {
        screenModelScope.launch {
            stateMutex.withLock {
                println("=== DEBUG: Processing selection of ${walletIdsSelected.size} wallet IDs ===")

                try {
                    val groupWallets = if (walletIdsSelected.isEmpty()) {
                        emptyList()
                    } else {
                        getContactWalletByWalletIdsUseCase.getGroupWallets(walletIdsSelected)
                    }

                    println("=== DEBUG: Retrieved ${groupWallets.size} wallets ===")

                    // Single atomic update - better for Compose
                    _uiState.update { currentState ->
                        currentState.copy(
                            selectedWallets = groupWallets,
                            loadingState = CreateGroupUiState.LoadingState.None
                        )
                    }

                    println("=== DEBUG: Updated state with ${groupWallets.size} wallets ===")
                } catch (e: Exception) {
                    println("=== ERROR: Failed to update selected wallets: ${e.message} ===")
                    _uiState.update {
                        it.copy(
                            error = ErrorState.LoadContactsError(
                                e.message ?: "Failed to load wallets"
                            ),
                            loadingState = CreateGroupUiState.LoadingState.None
                        )
                    }
                }
            }
        }
    }

    // Separate function to handle bottom sheet dismissal
    fun onWalletSelectionCompleted() {
        hideContactBottomSheet()
    }

    /**
     * Removes a wallet address from the selected wallets list
     * @param index The index of the wallet address to remove
     */
    fun deleteWallet(index: Int) {
        val currentWallets = _uiState.value.selectedWallets
        if (index in currentWallets.indices) {
            val walletToDelete = currentWallets[index]
            val updatedWallets = currentWallets.toMutableList().apply {
                removeAt(index)
            }
            println("=== DEBUG: Deleting wallet at index $index, ID: ${walletToDelete.walletId} ===")

            // Single atomic update - Compose will handle recomposition automatically
            _uiState.update { currentState ->
                currentState.copy(
                    selectedWallets = updatedWallets
                )
            }

            println("=== DEBUG: Deleted wallet at index $index, remaining: ${updatedWallets.size} ===")
            println("=== DEBUG: selectedWalletIdsFlow will auto-emit new IDs count: ${updatedWallets.size} ===")
        }
    }

    fun onSecurityLevelChanged(securityLevel: SecurityLevel) {
        _uiState.update { it.copy(securityLevel = securityLevel) }
    }

    fun onPrivacyLevelChanged(privacyLevel: PrivacyLevel) {
        _uiState.update { it.copy(privacyLevel = privacyLevel) }
    }

    fun onIconSelected(icon: String?) {
        _uiState.update { it.copy(icon = icon) }
    }

    fun onColorSelected(color: String) {
        _uiState.update { it.copy(color = color) }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    fun validateGroupName(): Boolean {
        val name = _uiState.value.groupName.trim()
        val validationError = when {
            name.isEmpty() -> "Group name cannot be empty"
            name.length > ValidationConstants.MAX_GROUP_NAME_LENGTH -> "Group name cannot exceed ${ValidationConstants.MAX_GROUP_NAME_LENGTH} characters"
            name.contains(Regex("[<>:\"/\\|?*]")) -> "Group name contains invalid characters"
            name.all { it.isWhitespace() } -> "Group name cannot be only whitespace"
            isReservedName(name.lowercase()) -> "This name is reserved"
            else -> null
        }

        return if (validationError != null) {
            _uiState.update { it.copy(groupNameError = validationError) }
            false
        } else {
            _uiState.update { it.copy(groupNameError = null) }
            true
        }
    }

    fun validateBlockchain(): Boolean {
        println("=== DEBUG: Validating blockchain selection: ${_uiState.value.selectedBlockchainId} ===")
        val selectedBlockchainId = _uiState.value.selectedBlockchainId
        val availableBlockchains = _uiState.value.blockchainTypes

        return when {
            selectedBlockchainId == null -> {
                _uiState.update { it.copy(error = ErrorState.ValidationError("Please select a blockchain network")) }
                false
            }

            availableBlockchains.none { it.id == selectedBlockchainId && it.isActive } -> {
                _uiState.update { it.copy(error = ErrorState.ValidationError("Selected blockchain is not available")) }
                false
            }

            else -> {
                _uiState.update { it.copy(error = null) }
                true
            }
        }
    }

    fun saveGroup() {
        if (saveJob?.isActive == true) {
            return
        }

        saveJob = screenModelScope.launch {
            if (!validateGroupName() || !validateBlockchain()) {
                return@launch
            }

            try {
//                setLoadingWithTimeout(CreateGroupUiState.LoadingState.Saving,)

                val state = _uiState.value

                // Xử lý upload icon nếu cần
                var processedIcon = state.icon

                // Nếu icon là content URI, cần upload trước
                if (state.icon != null && state.icon.startsWith("content://")) {
                    println("CreateGroupScreenModel: Processing avatar from content URI: ${state.icon}")
                    val result = avatarUseCase.processAndSaveImage(
                        entityId = "temp_group_${localDateTimeToMillis(localDateTimeNow())}",
                        imagePath = state.icon
                    )
                    when (result) {
                        is AvatarUseCase.Result.Success -> {
                            processedIcon = AvatarSource.toString(result.avatarSource)
                            println("CreateGroupScreenModel: Avatar processed successfully: $processedIcon")
                        }

                        is AvatarUseCase.Result.Error -> {
                            throw Exception("Failed to process avatar: ${result.message}")
                        }
                    }
                } else if (state.icon != null && !state.icon.startsWith("emoji:")) {
                    // Icon đã là đường dẫn local hoặc URL, sử dụng trực tiếp
                    processedIcon = state.icon
                    println("CreateGroupScreenModel: Using existing icon: $processedIcon")
                }

                val group = if (state.isEditMode && groupId != null) {
                    // Update existing group
                    val groupToUpdate = GroupEntity.create(
                        id = groupId,
                        name = state.groupName.trim(),
                        mainBlockchainId = state.selectedBlockchainId,
                        description = state.description.takeIf { it.isNotBlank() },
                        icon = processedIcon,  // Sử dụng icon đã xử lý
                        color = state.color,
                        privacyLevel = state.privacyLevel,
                        securityLevel = state.securityLevel
                    )
                    updateGroupUseCase(groupToUpdate, state.selectedWallets)
                    groupToUpdate
                } else {
                    // Create new group
                    val newGroup = GroupEntity.create(
                        name = state.groupName.trim(),
                        mainBlockchainId = state.selectedBlockchainId,
                        description = state.description.takeIf { it.isNotBlank() },
                        icon = processedIcon,  // Sử dụng icon đã xử lý
                        color = state.color,
                        privacyLevel = state.privacyLevel,
                        securityLevel = state.securityLevel
                    )
                    createGroupUseCase(newGroup, state.selectedWallets)
                    newGroup
                }

                _uiState.update {
                    it.copy(
                        loadingState = CreateGroupUiState.LoadingState.Saving,
                        saveCompleted = true,
                        savedGroup = group
                    )
                }
            } catch (e: IllegalArgumentException) {
                // Handle duplicate name error specifically
                _uiState.update {
                    it.copy(
                        error = ErrorState.ValidationError(
                            e.message ?: "Group with this name already exists"
                        ),
                        loadingState = CreateGroupUiState.LoadingState.None
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        error = ErrorState.SaveGroupError(e.message ?: "Failed to save group"),
                        loadingState = CreateGroupUiState.LoadingState.None
                    )
                }
            }
        }
    }

    fun applyContactFilter(query: String) {
        _uiState.update { it.copy(contactSearchQuery = query) }
    }

    /**
     * Provides paging data for contacts based on the selected blockchain
     * This method creates a reactive paging flow that:
     * - Filters contacts by blockchain
     * - Supports search functionality with debouncing
     * - Maps contacts to AddressSelectionContactModel with selection state
     * - Caches data for performance
     */
    fun getContactPagingFlow(): Flow<PagingData<AddressSelectionContactModel>> {
        return combine(
            _contactSearchQuery,
            _uiState.map { it.selectedBlockchainId },
            _tempSelectedContactIds
        ) { searchQuery, blockchainId, tempSelectedIds ->
            Triple(searchQuery, blockchainId, tempSelectedIds)
        }
            .debounce(SEARCH_DEBOUNCE_TIME_MS)
            .distinctUntilChanged()
            .flatMapLatest { (searchQuery, blockchainId, tempSelectedIds) ->
                if (blockchainId == null) {
                    // Return empty paging data if no blockchain is selected
                    kotlinx.coroutines.flow.flowOf(PagingData.empty<AddressSelectionContactModel>())
                } else {
                    // Create paging source for the selected blockchain
                    Pager(
                        config = PagingConfig(
                            pageSize = CONTACTS_PAGE_SIZE,
                            prefetchDistance = CONTACTS_PAGE_SIZE / 2,
                            initialLoadSize = CONTACTS_PAGE_SIZE,
                            enablePlaceholders = false
                        ),
                        pagingSourceFactory = {
                            // Create a custom paging source that uses the blockchain-filtered contacts
                            GroupContactsPagingSource(
                                blockchainId = blockchainId,
                                searchQuery = searchQuery.takeIf { it.isNotBlank() },
                                getContactsWithWalletAddressPaginatedUseCase = getContactsWithWalletAddressPaginatedUseCase,
                                existingSelectedWalletIds = _uiState.value.selectedWallets.map { it.walletId }.toSet()
                            )
                        }
                    ).flow
                        .map { pagingData ->
                            pagingData.map { groupWallet ->
                                // Convert GroupWallet to ContactWithMultipleBlockchainsModel
                                val contactModel = ContactWithMultipleBlockchainsModel(
                                    contactId = groupWallet.contactId,
                                    contactName = groupWallet.contactName,
                                    primaryWalletAddress = groupWallet.walletAddress,
                                    primaryWalletAddressId = groupWallet.walletId,
                                    primaryWalletAlias = groupWallet.walletAlias ?: "",
                                    primaryWalletSensitive = groupWallet.isSensitive,
                                    primaryBlockchainName = blockchainId, // Use blockchain ID as name for now
                                    primaryBlockchainSymbol = groupWallet.blockchainTypeSymbol,
                                    primaryBlockchainIcon = "", // Not available in GroupWallet
                                    primaryBlockChainColor = "#000000", // Not available in GroupWallet
                                    isFavorite = false,
                                    isSensitive = groupWallet.isSensitive,
                                )
                                
                                // Check if this wallet is selected (either in temp selections or existing selections)
                                val isSelected = tempSelectedIds.contains(groupWallet.walletId) ||
                                        _uiState.value.selectedWallets.any { it.walletId == groupWallet.walletId }
                                
                                AddressSelectionContactModel(
                                    contactWithMultipleBlockchainsModel = contactModel,
                                    isSelected = isSelected
                                )
                            }
                        }
                }
            }
            .cachedIn(screenModelScope)
    }

    /**
     * Toggles the selection state of a contact
     * This manages temporary selections before they are applied
     * @param contact The contact model to toggle
     */
    fun toggleContactSelection(contact: AddressSelectionContactModel) {
        val walletId = contact.contactWithMultipleBlockchainsModel.primaryWalletAddressId
        
        _tempSelectedContactIds.update { currentIds ->
            if (contact.isSelected) {
                // If currently selected, remove it
                currentIds - walletId
            } else {
                // If not selected, add it (check max limit)
                if (currentIds.size + _uiState.value.selectedWallets.size < MAX_CONTACTS_PER_GROUP) {
                    currentIds + walletId
                } else {
                    // Show error if max limit reached
                    _uiState.update { 
                        it.copy(
                            error = ErrorState.MaxContactsLimitError(
                                "Maximum $MAX_CONTACTS_PER_GROUP contacts allowed per group"
                            )
                        )
                    }
                    currentIds
                }
            }
        }
    }

    /**
     * Applies the selected contacts from the temporary selection
     * This method:
     * - Retrieves full GroupWallet data for selected IDs
     * - Updates the main UI state with selected wallets
     * - Clears temporary selections
     * - Hides the bottom sheet
     */
    fun applyContactSelections() {
        screenModelScope.launch {
            try {
                val tempSelectedIds = _tempSelectedContactIds.value
                val existingSelectedIds = _uiState.value.selectedWallets.map { it.walletId }.toSet()
                
                // Combine temporary and existing selections
                val allSelectedIds = (existingSelectedIds + tempSelectedIds).toList()
                
                if (allSelectedIds.isNotEmpty()) {
                    // Get full wallet data for all selected IDs
                    val groupWallets = getContactWalletByWalletIdsUseCase.getGroupWallets(allSelectedIds)
                    
                    // Update UI state with selected wallets
                    _uiState.update { currentState ->
                        currentState.copy(
                            selectedWallets = groupWallets,
                            hasMoreSelectedWallets = false // Reset pagination for selected wallets
                        )
                    }
                }
                
                // Clear temporary selections
                _tempSelectedContactIds.value = emptySet()
                
                // Hide bottom sheet
                hideContactBottomSheet()
                
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        error = ErrorState.LoadContactsError(
                            e.message ?: "Failed to apply contact selections"
                        )
                    )
                }
            }
        }
    }

    /**
     * Updates the search query for contact filtering
     * @param query The search query string
     */
    fun updateContactSearchQuery(query: String) {
        _contactSearchQuery.value = query
    }

    fun resetState() {
        // Cancel any pending operations
        stateUpdateJob?.cancel()

        // Clear state but keep loadingState as None to prevent flash
        _uiState.value = CreateGroupUiState(
            loadingState = CreateGroupUiState.LoadingState.None,
            isInitialized = true, // Keep initialized to prevent flash
            isEditMode = false // Reset to create mode
        )
        _showContactBottomSheet.value = false // Reset bottom sheet state

        // Reset other state variables
        hasLoaded = false
        selectedWalletsPage = 0
        _tempSelectedContactIds.value = emptySet()
        _contactSearchQuery.value = ""

        // Cancel any loading timeouts
        loadingTimeoutJob?.cancel()

        // Cancel any active save operation
        saveJob?.cancel()
    }

    // ✅ SAFEGUARD: Automatic timeout for loading states
//    private fun setLoadingWithTimeout(
//        loadingState: CreateGroupUiState.LoadingState,
//    ) {
//        loadingTimeoutJob?.cancel()
//        updateStateWithLogging("SET_LOADING_WITH_TIMEOUT") { copy(loadingState = loadingState) }
//
//        // Start timeout job
//        if (loadingState != CreateGroupUiState.LoadingState.None) {
//            loadingTimeoutJob = screenModelScope.launch {
//                // If still in the same loading state after timeout, clear it
//                if (_uiState.value.loadingState == loadingState) {
//                    val timeoutTime = kotlinx.datetime.Clock.System.now().toEpochMilliseconds()
//                    println("⚠️ [${timeoutTime}] Loading timeout reached for $loadingState")
//                    updateStateWithLogging("LOADING_TIMEOUT") {
//                        copy(
//                            loadingState = CreateGroupUiState.LoadingState.None,
//                            error = ErrorState.ValidationError("Operation timed out. Please try again.")
//                        )
//                    }
//                }
//            }
//        }
//    }

    override fun onDispose() {
        super.onDispose()
        // Cleanup resources when ScreenModel is disposed
        resetState()
    }

    companion object {
        const val MAX_CONTACTS_PER_GROUP = 50
        private const val SEARCH_DEBOUNCE_TIME_MS = 300L
        private const val CONTACTS_PAGE_SIZE = 20
    }
}

data class CreateGroupUiState(
    val loadingState: LoadingState = LoadingState.None, // FIX: Start with None to prevent initial flash
    val isEditMode: Boolean = false,
    val isInitialized: Boolean = false, // Track if we've initialized
    val existingGroup: GroupEntity? = null,
    val groupName: String = "",
    val groupNameError: String? = null,
    val description: String = "",
    val blockchainTypes: List<BlockchainTypeEntity> = emptyList(),
    val selectedBlockchainId: String? = null,
    val availableContacts: List<ContactWithAddress> = emptyList(),
    val availableGroupWallets: List<GroupWallet> = emptyList(), // New field for GroupWallet results
    val selectedWallets: List<GroupWallet> = emptyList(),
    val totalWalletsInDb: Int = 0, // Total count from DB (for edit mode pagination only)
    val hasMoreSelectedWallets: Boolean = false, // Whether there are more wallets to load
    val isLoadingMoreWallets: Boolean = false, // Loading state for pagination
    val contactSearchQuery: String = "",
    val icon: String? = null,
    val color: String? = null,
    val privacyLevel: PrivacyLevel = PrivacyLevel.PUBLIC,
    val securityLevel: SecurityLevel = SecurityLevel.NORMAL,
    val error: ErrorState? = null,
    val saveCompleted: Boolean = false,
    val savedGroup: GroupEntity? = null
) {
    sealed class LoadingState {
        object None : LoadingState()
        object InitialLoad : LoadingState()
        object Saving : LoadingState()
        object LoadingContacts : LoadingState()
    }

    // Reactive computed property - always in sync with selectedWallets
    val totalSelectedWalletsCount: Int
        get() = selectedWallets.size

    val isLoading: Boolean get() = loadingState == LoadingState.InitialLoad
    val isSaving: Boolean get() = loadingState == LoadingState.Saving

    val isAnyLoading: Boolean get() = loadingState != LoadingState.None

    val loadingMessage: String
        get() = when (loadingState) {
            LoadingState.InitialLoad -> "Loading..."
            LoadingState.Saving -> if (isEditMode) "Updating group..." else "Creating group..."
            LoadingState.LoadingContacts -> "Loading contacts..."
            LoadingState.None -> ""
        }

    val filteredAvailableContacts: List<ContactWithAddress>
        get() = if (contactSearchQuery.isBlank()) {
            availableContacts
        } else {
            availableContacts.filter { contact ->
                contact.contact.name.contains(contactSearchQuery, ignoreCase = true) ||
                        contact.walletAddress.address.contains(
                            contactSearchQuery,
                            ignoreCase = true
                        ) ||
                        contact.walletAddress.alias?.contains(
                            contactSearchQuery,
                            ignoreCase = true
                        ) == true
            }
        }

    val isGroupNameValid: Boolean
        get() = groupName.isNotBlank() && groupName.length <= ValidationConstants.MAX_GROUP_NAME_LENGTH

    val canSave: Boolean
        get() = isGroupNameValid && selectedBlockchainId != null && !isAnyLoading
}

data class ContactWithAddress(
    val contact: ContactEntity,
    val walletAddress: WalletAddressEntity
)

sealed class ErrorState {
    data class LoadBlockchainTypesError(val message: String) : ErrorState()
    data class LoadContactsError(val message: String) : ErrorState()
    data class LoadGroupError(val message: String) : ErrorState()
    data class SaveGroupError(val message: String) : ErrorState()
    data class ValidationError(val message: String) : ErrorState()
    data class MaxContactsLimitError(val message: String) : ErrorState()
    data class GroupNotFoundError(val message: String) : ErrorState()
}