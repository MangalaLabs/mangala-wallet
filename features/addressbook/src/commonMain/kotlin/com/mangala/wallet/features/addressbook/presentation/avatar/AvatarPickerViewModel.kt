package com.mangala.wallet.features.addressbook.presentation.avatar

import com.mangala.wallet.features.addressbook.data.model.avatar.AvatarHistoryEntity
import com.mangala.wallet.features.addressbook.domain.model.AvatarPickerContract
import com.mangala.wallet.features.addressbook.domain.model.AvatarSource
import com.mangala.wallet.features.addressbook.domain.repository.avatar.AvatarHistoryRepository
import com.mangala.wallet.features.addressbook.domain.repository.avatar.AvatarRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

/**
 * Enhanced ViewModel để xử lý chọn avatar với history support
 * Đảm bảo có thể serialize được
 */
class AvatarPickerViewModel(
    val avatarPickerContract: AvatarPickerContract,
    private val avatarHistoryRepository: AvatarHistoryRepository? = null,
    private val avatarRepository: AvatarRepository? = null
) : CoroutineScope {
    
    // =====================================================
    // COROUTINE SCOPE
    // =====================================================
    
    private val job = SupervisorJob()
    override val coroutineContext: CoroutineContext = Dispatchers.Main + job
    
    // =====================================================
    // STATE MANAGEMENT
    // =====================================================
    
    private val _currentAvatar = MutableStateFlow<AvatarSource>(AvatarSource.None)
    val currentAvatar: StateFlow<AvatarSource> = _currentAvatar.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _selectedTab = MutableStateFlow("gallery")
    val selectedTab: StateFlow<String> = _selectedTab.asStateFlow()
    
    // =====================================================
    // INITIALIZATION
    // =====================================================
    
    init {
        // Initialize history observers and load initial data
        println("AvatarPickerViewModel: Initializing with repository: ${avatarHistoryRepository != null}")
        initializeHistoryObservers()
        loadInitialData()
    }
    
    // =====================================================
    // HISTORY STATE
    // =====================================================
    
    private val _recentAvatars = MutableStateFlow<List<AvatarHistoryEntity>>(emptyList())
    val recentAvatars: StateFlow<List<AvatarHistoryEntity>> = _recentAvatars.asStateFlow()
    
    private val _popularAvatars = MutableStateFlow<List<AvatarHistoryEntity>>(emptyList())
    val popularAvatars: StateFlow<List<AvatarHistoryEntity>> = _popularAvatars.asStateFlow()
    
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()
    
    private val _searchResults = MutableStateFlow<List<AvatarHistoryEntity>>(emptyList())
    val searchResults: StateFlow<List<AvatarHistoryEntity>> = _searchResults.asStateFlow()
    
    // =====================================================
    // PAGINATION STATE
    // =====================================================
    
    private val _currentPage = MutableStateFlow(0)
    private val _hasMorePages = MutableStateFlow(true)
    val hasMorePages: StateFlow<Boolean> = _hasMorePages.asStateFlow()
    
    // =====================================================
    // CONFIGURATION
    // =====================================================
    
    private val pageSize = 20
    private val searchDebounceMs = 300L
    
    init {
        initializeHistoryObservers()
        loadInitialData()
    }
    
    // =====================================================
    // PUBLIC API - AVATAR SELECTION
    // =====================================================
    
    fun setInitialAvatar(avatarSource: AvatarSource?) {
        _currentAvatar.value = avatarSource ?: AvatarSource.None
    }

    fun onEmojiSelected(emoji: String) {
        val avatarSource = AvatarSource.Emoji(emoji)
        _currentAvatar.value = avatarSource
        // Không lưu history cho emoji
    }
    
    fun onEmojiWithBackgroundSelected(emoji: String, backgroundColor: String) {
        val avatarSource = AvatarSource.Emoji(emoji, backgroundColor)
        _currentAvatar.value = avatarSource
        // Không lưu history cho emoji
    }

    fun onImageSelected(path: String) {
        launch {
            _isLoading.value = true
            
            // Copy ảnh vào internal storage nếu cần
            val localPath = if (path.startsWith("content://") && avatarRepository != null) {
                println("AvatarPickerViewModel: Copying content URI to internal storage: $path")
                avatarRepository.copyImageToInternalStorage(path, "temp-avatar") ?: path
            } else {
                path
            }
            
            val avatarSource = AvatarSource.ImageUrl(localPath)
            _currentAvatar.value = avatarSource
            _isLoading.value = false
            
            println("AvatarPickerViewModel: Image selected - original: $path, local: $localPath")
        }
    }
    
    fun onDefaultAvatarSelected(resourceName: String) {
        val avatarSource = AvatarSource.DefaultAvatar(resourceName)
        _currentAvatar.value = avatarSource
        // Không lưu history cho default avatar
    }

    fun onAvatarRemoved() {
        _currentAvatar.value = AvatarSource.None
    }
    
    fun onHistoryAvatarSelected(historyEntity: AvatarHistoryEntity) {
        _currentAvatar.value = historyEntity.avatarSource
        // Không update usage count ngay khi chọn từ history
        // Sẽ được update khi thực sự apply avatar
    }
    
    /**
     * Gọi khi user thực sự apply/save avatar (ví dụ: khi save contact/group)
     * Chỉ lúc này mới lưu vào history
     * 
     * QUAN TRỌNG: Function này phải được gọi trong các ScreenModel sau khi save thành công:
     * - CreateGroupScreenModel: sau khi tạo group thành công  
     * - ContactScreenModel: sau khi save contact thành công (cả create và edit mode)
     * 
     * Ví dụ sử dụng:
     * ```kotlin
     * // Trong CreateGroupScreenModel.createGroup()
     * if (result.isSuccess) {
     *     avatarPickerViewModel.onAvatarApplied("group", groupId)
     * }
     * ```
     */
    fun onAvatarApplied(entityType: String = "contact", entityId: String? = null) {
        val currentAvatar = _currentAvatar.value
        if (currentAvatar is AvatarSource.ImageUrl) {
            println("AvatarPickerViewModel: Saving avatar to history - entityType: $entityType, entityId: $entityId")
            println("AvatarPickerViewModel: Current avatar URL being saved: ${currentAvatar.url}")
            saveAvatarUsage(currentAvatar, entityType, entityId)
        }
    }
    
    /**
     * Overload để save avatar với processed path (đã upload)
     * Sử dụng khi cần save avatar history với đường dẫn đã được xử lý
     */
    fun onAvatarApplied(processedAvatarPath: String, entityType: String = "contact", entityId: String? = null) {
        if (processedAvatarPath.isNotBlank() && !processedAvatarPath.startsWith("emoji:")) {
            val avatarSource = AvatarSource.ImageUrl(processedAvatarPath)
            println("AvatarPickerViewModel: Saving processed avatar to history - entityType: $entityType, entityId: $entityId")
            println("AvatarPickerViewModel: Processed avatar URL being saved: $processedAvatarPath")
            saveAvatarUsage(avatarSource, entityType, entityId)
        }
    }

    fun openImagePicker() {
        if (avatarPickerContract.isImagePickerSupported()) {
            avatarPickerContract.openImagePicker()
        }
    }

    fun isImagePickerSupported(): Boolean {
        return avatarPickerContract.isImagePickerSupported()
    }
    
    // =====================================================
    // PUBLIC API - HISTORY MANAGEMENT
    // =====================================================
    
    fun selectTab(tab: String) {
        _selectedTab.value = tab
        when (tab) {
            "recent" -> loadRecentAvatars(forceRefresh = false)
            "popular" -> loadPopularAvatars()
        }
    }
    
    fun searchAvatars(query: String) {
        _searchQuery.value = query
        if (query.isBlank()) {
            _searchResults.value = emptyList()
            return
        }
        
        launch {
            kotlinx.coroutines.delay(searchDebounceMs) // Simple debouncing
            if (_searchQuery.value == query) { // Still the same query
                performSearch(query)
            }
        }
    }
    
    fun loadNextPage() {
        if (!_hasMorePages.value || _isLoading.value) return
        
        launch {
            when (_selectedTab.value) {
                "recent" -> loadRecentAvatars(page = _currentPage.value + 1)
                "popular" -> loadPopularAvatars() // Popular doesn't need pagination
            }
        }
    }
    
    fun refreshCurrentTab() {
        when (_selectedTab.value) {
            "recent" -> loadRecentAvatars(forceRefresh = true)
            "popular" -> loadPopularAvatars()
        }
    }
    
    fun clearHistory() {
        launch {
            avatarHistoryRepository?.clearAvatarHistory()
            _recentAvatars.value = emptyList()
            _popularAvatars.value = emptyList()
            _searchResults.value = emptyList()
        }
    }
    
    // =====================================================
    // PRIVATE METHODS - INITIALIZATION
    // =====================================================
    
    private fun initializeHistoryObservers() {
        avatarHistoryRepository?.let { repository ->
            // Observe recent avatars
            launch {
                repository.observeRecentAvatars(pageSize)
                    .catch { /* Handle error silently */ }
                    .collect { _recentAvatars.value = it }
            }
            
            // Observe popular avatars
            launch {
                repository.observePopularAvatars(pageSize)
                    .catch { /* Handle error silently */ }
                    .collect { _popularAvatars.value = it }
            }
        }
    }
    
    private fun loadInitialData() {
        launch {
            _isLoading.value = true
            try {
                // Load recent avatars by default
                loadRecentAvatars(forceRefresh = false)
                
                // Preload popular avatars to cache
                avatarHistoryRepository?.preloadPopularAvatarsToCache()
                
                // Add some test data if no avatars exist
                addTestDataIfNeeded()
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    private fun addTestDataIfNeeded() {
        launch {
            try {
                val totalCount = avatarHistoryRepository?.getTotalAvatarCount()?.getOrElse { 0L } ?: 0L
                println("AvatarPickerViewModel: Total avatar count: $totalCount")
                
                // Không thêm test data nữa vì chỉ lưu history cho ảnh thật
                // History sẽ được tạo khi user thực sự chọn ảnh từ gallery
                
                // Debug: Kiểm tra data hiện tại
                if (totalCount > 0) {
                    val recentData = avatarHistoryRepository?.getRecentAvatars(5, 0)?.getOrNull()
                    println("AvatarPickerViewModel: Recent data sample: $recentData")
                }
            } catch (e: Exception) {
                println("AvatarPickerViewModel: Error checking avatar count: $e")
                // Nếu database corrupt, clear nó
                println("AvatarPickerViewModel: Clearing corrupted avatar history")
                avatarHistoryRepository?.clearAvatarHistory()
            }
        }
    }
    
    // =====================================================
    // PRIVATE METHODS - DATA LOADING
    // =====================================================
    
    private fun saveAvatarUsage(
        avatarSource: AvatarSource, 
        entityType: String = "avatar_picker", 
        entityId: String? = null
    ) {
        launch {
            avatarHistoryRepository?.saveAvatarUsage(
                avatarSource = avatarSource,
                entityType = entityType,
                entityId = entityId
            )
        }
    }
    
    private fun loadRecentAvatars(page: Int = 0, forceRefresh: Boolean = false) {
        launch {
            if (page == 0) _isLoading.value = true
            
            try {
                println("AvatarPickerViewModel: Loading recent avatars, page=$page")
                val result = avatarHistoryRepository?.getRecentAvatars(
                    limit = pageSize,
                    offset = page * pageSize
                )
                
                result?.fold(
                    onSuccess = { avatars ->
                        println("AvatarPickerViewModel: Loaded ${avatars.size} recent avatars")
                        if (page == 0) {
                            _recentAvatars.value = avatars
                            _currentPage.value = 0
                        } else {
                            _recentAvatars.value = _recentAvatars.value + avatars
                            _currentPage.value = page
                        }
                        _hasMorePages.value = avatars.size == pageSize
                    },
                    onFailure = { error ->
                        println("AvatarPickerViewModel: Error loading recent avatars: $error")
                    }
                )
            } finally {
                if (page == 0) _isLoading.value = false
            }
        }
    }
    
    private fun loadPopularAvatars() {
        launch {
            _isLoading.value = true
            try {
                val result = avatarHistoryRepository?.getPopularAvatars(pageSize)
                result?.fold(
                    onSuccess = { avatars ->
                        _popularAvatars.value = avatars
                    },
                    onFailure = { 
                        // Handle error silently
                    }
                )
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    private fun performSearch(query: String) {
        launch {
            _isLoading.value = true
            try {
                val result = avatarHistoryRepository?.searchAvatars(query, pageSize)
                result?.fold(
                    onSuccess = { avatars ->
                        _searchResults.value = avatars
                    },
                    onFailure = { 
                        _searchResults.value = emptyList()
                    }
                )
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    // =====================================================
    // CLEANUP
    // =====================================================
    
    fun onCleared() {
        job.cancel()
    }
}
