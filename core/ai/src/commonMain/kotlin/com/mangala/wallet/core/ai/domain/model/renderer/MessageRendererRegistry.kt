package com.mangala.wallet.core.ai.domain.model.renderer

import com.mangala.wallet.core.ai.domain.model.message.Message

interface MessageRendererRegistry {
    fun registerRenderer(renderer: MessageRenderer)
    fun getRenderer(message: Message): MessageRenderer?
}

class DefaultMessageRendererRegistry(renderers: List<MessageRenderer>) : MessageRendererRegistry {
    private val renderers = renderers.toMutableList()
    
    override fun registerRenderer(renderer: MessageRenderer) {
        renderers.add(renderer)
    }
    
    override fun getRenderer(message: Message): MessageRenderer? {
        return renderers.asSequence()
            .filter { renderer -> renderer.canRender(message) }
            .firstOrNull()
    }
}