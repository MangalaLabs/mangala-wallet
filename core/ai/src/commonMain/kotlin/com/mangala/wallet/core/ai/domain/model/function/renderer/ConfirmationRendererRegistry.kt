package com.mangala.wallet.core.ai.domain.model.function.renderer

/**
 * Registry for managing confirmation renderers
 */
interface ConfirmationRendererRegistry {
    /**
     * Register a plugin that provides confirmation renderers
     */
    fun registerPlugin(plugin: ConfirmationRendererPlugin)
    
    /**
     * Register a single confirmation renderer
     */
    fun registerRenderer(renderer: ConfirmationRenderer)
    
    /**
     * Get a renderer for a specific function name
     *
     * @param functionName The name of the function
     * @return The renderer for the function, or null if not found
     */
    fun getRenderer(functionName: String): ConfirmationRenderer?
    
    /**
     * Get all registered renderers
     */
    fun getAllRenderers(): List<ConfirmationRenderer>
}