package com.mangala.wallet.core.ai.domain.model.function.renderer

class DefaultConfirmationRendererRegistry(plugins: List<ConfirmationRendererPlugin>) :
    ConfirmationRendererRegistry {
    private val renderers = mutableMapOf<String, ConfirmationRenderer>()

    init {
        println("DefaultConfirmationRendererRegistry registered plugins $plugins")
        plugins.forEach { plugin ->
            registerPlugin(plugin)
        }
    }

    override fun registerPlugin(plugin: ConfirmationRendererPlugin) {
        plugin.getRenderers().forEach { renderer ->
            registerRenderer(renderer)
        }
    }
    
    override fun registerRenderer(renderer: ConfirmationRenderer) {
        renderers[renderer.functionName] = renderer
    }
    
    override fun getRenderer(functionName: String): ConfirmationRenderer? {
        return renderers[functionName]
    }
    
    override fun getAllRenderers(): List<ConfirmationRenderer> {
        return renderers.values.toList()
    }
}