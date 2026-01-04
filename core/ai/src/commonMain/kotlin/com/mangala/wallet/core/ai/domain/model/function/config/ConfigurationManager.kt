package com.mangala.wallet.core.ai.domain.model.function.config

import com.mangala.wallet.core.ai.domain.model.function.definition.FunctionRegistry
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.datetime.Clock

/**
 * Manager for function configurations that handles loading, refreshing, and applying configurations.
 * This class coordinates between configuration sources and the function registry.
 */
class ConfigurationManager(
    private val configSource: FunctionConfigSource,
    private val functionRegistry: FunctionRegistry
) {
    private val TAG = "ConfigurationManager"
    private val coroutineScope = CoroutineScope(Dispatchers.Default)
    private val mutex = Mutex()
    private var refreshJob: Job? = null
    
    /**
     * State of the configuration manager.
     */
    enum class ConfigState {
        IDLE,
        LOADING,
        ERROR,
        SUCCESS
    }
    
    /**
     * Configuration status and metrics.
     */
    data class ConfigStatus(
        val state: ConfigState = ConfigState.IDLE,
        val lastRefreshTime: Long = 0,
        val failureCount: Int = 0,
        val successCount: Int = 0,
        val appliedConfigCount: Int = 0
    )
    
    private val _status = MutableStateFlow(ConfigStatus())
    val status: StateFlow<ConfigStatus> = _status
    
    /**
     * Initialize the configuration manager by loading configurations from the source.
     */
    fun initialize() {
        coroutineScope.launch {
            try {
                loadConfigurations()
                
                // Setup periodic refresh if registry is enhanced
                if (functionRegistry is EnhancedFunctionRegistry) {
                    // Collect configuration events for metrics
                    coroutineScope.launch {
                        functionRegistry.configurationEvents.collect { events ->
                            val appliedCount = events.count { it is EnhancedFunctionRegistry.ConfigurationEvent.ConfigApplied }
                            updateStatus { it.copy(appliedConfigCount = appliedCount) }
                        }
                    }
                    
                    // Collect configuration state
                    coroutineScope.launch {
                        functionRegistry.configurationState.collect { state ->
                            when (state) {
                                EnhancedFunctionRegistry.ConfigurationState.LOADING -> 
                                    updateStatus { it.copy(state = ConfigState.LOADING) }
                                EnhancedFunctionRegistry.ConfigurationState.LOADED -> 
                                    updateStatus { it.copy(state = ConfigState.SUCCESS) }
                                EnhancedFunctionRegistry.ConfigurationState.ERROR -> 
                                    updateStatus { it.copy(state = ConfigState.ERROR) }
                                else -> { /* No change */ }
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                println("Error initializing configurations: ${e.message}")
                updateStatus { 
                    it.copy(
                        state = ConfigState.ERROR,
                        failureCount = it.failureCount + 1
                    )
                }
            }
        }
    }
    
    /**
     * Update the status flow in a thread-safe way.
     */
    private suspend fun updateStatus(update: (ConfigStatus) -> ConfigStatus) {
        mutex.withLock {
            _status.value = update(_status.value)
        }
    }
    
    /**
     * Load configurations from the source and apply them to the function registry.
     * This method is thread-safe.
     */
    suspend fun loadConfigurations() {
        mutex.withLock {
            updateStatus { it.copy(state = ConfigState.LOADING) }
            
            try {
                // Get all configurations
                val configurations = configSource.getAllConfigurations()
                
                // Log the number of configurations loaded
                println("Loaded ${configurations.size} configurations")

                // Check if the function registry is configurable
                when (functionRegistry) {
                    is EnhancedFunctionRegistry -> {
                        // Use the enhanced registry's capabilities
                        functionRegistry.refreshConfigurations()
                    }
                    is ConfigurableFunctionRegistry -> {
                        // Use the configurable registry's refresh method
                        functionRegistry.refreshConfigurations()
                    }
                    else -> {
                        // Otherwise, we need to manually apply configurations
                        // Note: This won't actually update the registry since it's not configurable
                        println("Function registry is not configurable, configurations will not be applied")
                    }
                }
                
                // Update status on success
                updateStatus { 
                    it.copy(
                        state = ConfigState.SUCCESS,
                        lastRefreshTime = Clock.System.now().toEpochMilliseconds(),
                        successCount = it.successCount + 1
                    )
                }
            } catch (e: Exception) {
                println("Error loading configurations: ${e.message}")
                updateStatus { 
                    it.copy(
                        state = ConfigState.ERROR,
                        failureCount = it.failureCount + 1
                    )
                }
                throw e
            }
        }
    }
    
    /**
     * Refresh configurations from the source.
     * This method is thread-safe and will cancel any ongoing refresh operation.
     */
    fun refreshConfigurations() {
        // Cancel any ongoing refresh
        refreshJob?.cancel()
        
        // Start a new refresh
        refreshJob = coroutineScope.launch {
            try {
                loadConfigurations()
                println("Configurations refreshed successfully")
            } catch (e: Exception) {
                println("Error refreshing configurations: ${e.message}")
            }
        }
    }
    
    /**
     * Setup automatic periodic refreshes.
     * 
     * @param intervalMs How often to refresh configurations, in milliseconds
     * @param immediate Whether to perform an immediate refresh before starting the timer
     */
    fun setupAutomaticRefresh(intervalMs: Long, immediate: Boolean = false) {
        coroutineScope.launch {
            if (immediate) {
                refreshConfigurations()
            }
            
            while (true) {
                delay(intervalMs)
                refreshConfigurations()
            }
        }
    }
    
    /**
     * Get the current configuration status information.
     */
    fun getStatusInfo(): String {
        val currentStatus = _status.value
        return """
            Configuration Manager Status:
            Current state: ${currentStatus.state}
            Last refresh: ${if (currentStatus.lastRefreshTime > 0) formatTime(currentStatus.lastRefreshTime) else "Never"}
            Success count: ${currentStatus.successCount}
            Failure count: ${currentStatus.failureCount}
            Applied configurations: ${currentStatus.appliedConfigCount}
            
            ${if (functionRegistry is EnhancedFunctionRegistry) "Registry status: ${functionRegistry.dumpConfigurationState()}" else ""}
        """.trimIndent()
    }
    
    /**
     * Format a timestamp into a human-readable string.
     */
    private fun formatTime(timestamp: Long): String {
        val now = Clock.System.now().toEpochMilliseconds()
        val diff = now - timestamp
        
        return when {
            diff < 1000 -> "Just now"
            diff < 60000 -> "${diff / 1000} seconds ago"
            diff < 3600000 -> "${diff / 60000} minutes ago"
            else -> "${diff / 3600000} hours ago"
        }
    }
}