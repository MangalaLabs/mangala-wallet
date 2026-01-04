package com.mangala.wallet.core.ai.domain.model.function.definition

/**
 * Default implementation of [FunctionRegistry] that stores functions in memory
 */
open class DefaultFunctionRegistry(
    private val plugins: List<FunctionPlugin>
) : FunctionRegistry {
    private val functions = mutableMapOf<String, FunctionDefinition>()
    protected val functionsByModule = mutableMapOf<String, MutableList<FunctionDefinition>>()

    init {
        // Register all functions from plugins
        println("DefaultFunctionRegistry init plugins $plugins")
        plugins.forEach { plugin ->
            plugin.registerTo(this)
        }
    }

    /**
     * Register additional plugins after initialization
     *
     * @param plugin The plugin to register
     */
    fun registerPlugin(plugin: FunctionPlugin) {
        plugin.registerTo(this)
    }

    /**
     * Register multiple additional plugins after initialization
     *
     * @param plugins The plugins to register
     */
    fun registerPlugins(plugins: List<FunctionPlugin>) {
        plugins.forEach { plugin ->
            plugin.registerTo(this)
        }
    }

    override fun registerFunction(function: FunctionDefinition) {
        functions[function.name] = function

        val moduleFunctions = functionsByModule.getOrPut(function.moduleId) { mutableListOf() }
        moduleFunctions.add(function)
    }

    override fun getFunctions(): List<FunctionDefinition> {
        println("DefaultFunctionRegistry functions registered ${functions.values.toList()}")
        return functions.values.toList()
    }

    override fun getFunctionsByModule(moduleId: String): List<FunctionDefinition> {
        return functionsByModule[moduleId] ?: emptyList()
    }

    override fun getFunctionByName(name: String): FunctionDefinition? {
        return functions[name]
    }
}