package com.mangala.wallet.wallet.presentation.conversation

import com.mangala.wallet.core.ai.domain.model.function.renderer.ConfirmationRenderer
import com.mangala.wallet.core.ai.domain.model.function.renderer.ConfirmationRendererPlugin

class CoreWaleltConfirmationRendererPlugin: ConfirmationRendererPlugin {
    override fun getRenderers(): List<ConfirmationRenderer> {
        return listOf(ImportAccountConfirmationRenderer())
    }
}