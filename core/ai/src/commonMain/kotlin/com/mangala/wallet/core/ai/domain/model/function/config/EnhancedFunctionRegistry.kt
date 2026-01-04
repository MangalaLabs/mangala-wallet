package com.mangala.wallet.core.ai.domain.model.function.config

import com.mangala.wallet.core.ai.domain.model.function.definition.FunctionDefinition
import com.mangala.wallet.core.ai.domain.model.function.definition.FunctionPlugin
import com.mangala.wallet.core.ai.domain.model.function.definition.FunctionRegistry
import com.mangala.wallet.core.ai.domain.model.function.handler.FunctionHandlerRegistry
import com.mangala.wallet.core.security.models.SecurityLevel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * An enhanced function registry that integrates deeply with the configuration system.
 * This implementation adds:
 * - Validation against handlers (ensuring functions have corresponding handlers)
 * - Configuration state tracking
 * - Fallback mechanisms for configuration failures
 * - Configuration events for monitoring
 */
class EnhancedFunctionRegistry(
    plugins: List<FunctionPlugin>,
    private val configSource: FunctionConfigSource,
    private val handlerRegistry: FunctionHandlerRegistry,
    private val configValidationEnabled: Boolean = true,
    private val allowFunctionsWithoutHandlers: Boolean = true, // New parameter to control behavior
    private val logDebugInfo: Boolean = true
) : ConfigurableFunctionRegistry(plugins, configSource) {

    private val _configurationState = MutableStateFlow(ConfigurationState.INITIAL)
    val configurationState: StateFlow<ConfigurationState> = _configurationState

    private val _configurationEvents = MutableStateFlow<List<ConfigurationEvent>>(emptyList())
    val configurationEvents: StateFlow<List<ConfigurationEvent>> = _configurationEvents

    private val eventMutex = Mutex()

    /**
     * State of the configuration system
     */
    enum class ConfigurationState {
        INITIAL, // Initial state before any configuration has been loaded
        LOADING, // Currently loading configurations
        LOADED,  // Successfully loaded configurations
        ERROR    // Error loading configurations
    }

    /**
     * Event representing a change in the configuration system
     */
    sealed class ConfigurationEvent {
        data class ConfigLoaded(val configCount: Int) : ConfigurationEvent()
        data class ConfigApplied(val functionName: String) : ConfigurationEvent()
        data class ConfigError(val error: Throwable) : ConfigurationEvent()
        data class ValidationError(val functionName: String, val reason: String) : ConfigurationEvent()
        data class ConfigSkipped(val functionName: String, val reason: String) : ConfigurationEvent()
        data class InformationOnly(val functionName: String) : ConfigurationEvent()
    }
    
    /**
     * Track functions that are allowed without handlers (informational only)
     */
    private val informationalFunctions = mutableSetOf<String>()

    /**
     * Add a configuration event to the event stream
     */
    private suspend fun addEvent(event: ConfigurationEvent) {
        eventMutex.withLock {
            _configurationEvents.value = _configurationEvents.value + event
        }
    }

    /**
     * Validate a function definition against its handler.
     * This ensures that configured functions actually have handlers to execute them.
     */
    private fun validateFunctionHandler(function: FunctionDefinition): Boolean {
        // Check if there's a handler for this function
        if (!handlerRegistry.hasHandler(function.name)) {
            // If we allow functions without handlers and this is just for information
            if (allowFunctionsWithoutHandlers) {
                if (logDebugInfo) {
                    println("Function ${function.name} has no handler but is allowed for informational purposes")
                }
                
                // Mark this as an informational function
                informationalFunctions.add(function.name)
                
                coroutineScope.launch {
                    addEvent(
                        ConfigurationEvent.InformationOnly(
                            function.name
                        )
                    )
                }
                
                // Return true to allow the function, even without a handler
                return true
            }
            
            // Otherwise, fail validation
            coroutineScope.launch {
                addEvent(
                    ConfigurationEvent.ValidationError(
                        function.name,
                        "No handler found for function"
                    )
                )
            }
            return false
        }
        return true
    }

    /**
     * Create a function definition directly from a configuration.
     * Used for functions that don't have a corresponding handler but are 
     * allowed for informational purposes.
     */
    private fun createFunctionFromConfig(config: FunctionConfig): FunctionDefinition? {
        try {
            // Get parameter information
            val parameterMap = config.parameters.mapValues { (paramName, paramConfig) ->
                com.mangala.wallet.core.ai.domain.model.function.FunctionParameter(
                    name = paramName,
                    type = inferTypeFromConfig(paramConfig),
                    description = paramConfig.description,
                    required = paramConfig.required ?: false,
                    enumValues = paramConfig.enumValues
                )
            }
            
            // Create the function definition
            return FunctionDefinition(
                name = config.name,
                description = config.description,
                parameters = parameterMap,
                requiredParameters = config.requiredParameters ?: emptyList(),
                moduleId = "informational",
                securityLevel = SecurityLevel.parseSecurityLevel(config.securityLevel.orEmpty())
            )
        } catch (e: Exception) {
            println("Error creating function from config for ${config.name}: ${e.message}")
            return null
        }
    }
    
    /**
     * Infer a parameter type from the configuration
     */
    private fun inferTypeFromConfig(paramConfig: ParameterConfig): com.mangala.wallet.core.ai.domain.model.function.ParameterType {
        return when {
            paramConfig.enumValues != null -> com.mangala.wallet.core.ai.domain.model.function.ParameterType.STRING
            paramConfig.properties != null -> com.mangala.wallet.core.ai.domain.model.function.ParameterType.OBJECT
            else -> com.mangala.wallet.core.ai.domain.model.function.ParameterType.STRING  // Default to string
        }
    }

    /**
     * Enhanced version of replaceFunction that includes validation
     */
    override fun replaceFunction(function: FunctionDefinition) {
        // Validate the function if validation is enabled
        if (configValidationEnabled && !validateFunctionHandler(function)) {
            coroutineScope.launch {
                addEvent(
                    ConfigurationEvent.ConfigSkipped(
                        function.name,
                        "Failed handler validation"
                    )
                )
            }
            return
        }

        // Call the parent implementation to actually replace the function
        super.replaceFunction(function)

        // Add an event for the applied configuration
        coroutineScope.launch {
            addEvent(ConfigurationEvent.ConfigApplied(function.name))
        }
    }

    /**
     * Enhanced version of loadAndApplyConfigurations with better error handling
     * and state management
     */
    override suspend fun loadAndApplyConfigurations() {
        try {
            _configurationState.value = ConfigurationState.LOADING
            
            // Get all configurations
            val configurations = configSource.getAllConfigurations()
            
            // Record the loading event
            addEvent(ConfigurationEvent.ConfigLoaded(configurations.size))
            
            // Apply each configuration
            configurations.forEach { config ->
                val functionName = config.name
                val function = super.getFunctionByName(functionName)
                
                if (function != null) {
                    try {
                        // Apply the configuration to the function
                        val updatedFunction = configSource.applyConfiguration(function, config)
                        
                        // Replace the function in the registry
                        replaceFunction(updatedFunction)
                    } catch (e: Exception) {
                        addEvent(ConfigurationEvent.ConfigError(e))
                    }
                } else {
                    // Function doesn't exist yet - this is where we can create a new one
                    // if informational functions are allowed
                    if (allowFunctionsWithoutHandlers) {
                        val newFunction = createFunctionFromConfig(config)
                        if (newFunction != null) {
                            replaceFunction(newFunction)
                            if (logDebugInfo) {
                                println("Created new informational function: ${newFunction.name}")
                            }
                        } else {
                            addEvent(
                                ConfigurationEvent.ConfigSkipped(
                                    functionName,
                                    "Failed to create informational function"
                                )
                            )
                        }
                    } else {
                        addEvent(
                            ConfigurationEvent.ConfigSkipped(
                                functionName,
                                "Function not found in registry"
                            )
                        )
                    }
                }
            }
            
            _configurationState.value = ConfigurationState.LOADED
        } catch (e: Exception) {
            println("EnhancedFunctionRegistry error $e")
            addEvent(ConfigurationEvent.ConfigError(e))
            _configurationState.value = ConfigurationState.ERROR
            throw e
        }
    }
    
    /**
     * Dump the current configuration state for debugging
     */
    fun dumpConfigurationState(): String {
        val configState = _configurationState.value.name
        val eventCount = _configurationEvents.value.size
        val appliedCount = _configurationEvents.value.count { it is ConfigurationEvent.ConfigApplied }
        val informationalCount = _configurationEvents.value.count { it is ConfigurationEvent.InformationOnly }
        val errorCount = _configurationEvents.value.count { it is ConfigurationEvent.ConfigError }
        
        return """
            Configuration State: $configState
            Total Events: $eventCount
            Applied Configurations: $appliedCount
            Informational Functions: $informationalCount
            Errors: $errorCount
            
            Functions without handlers: ${informationalFunctions.joinToString(", ")}
        """.trimIndent()
    }
}