package com.mangala.wallet.core.ai.domain.model.function.renderer

interface ConfirmationRendererPlugin {
    fun getRenderers(): List<ConfirmationRenderer>
}