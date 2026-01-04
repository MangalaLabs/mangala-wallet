package com.mangala.wallet.features.send_base.conversation

import com.mangala.wallet.core.ai.domain.model.function.renderer.ConfirmationRenderer
import com.mangala.wallet.core.ai.domain.model.function.renderer.ConfirmationRendererPlugin

class SendConfirmationRendererPlugin: ConfirmationRendererPlugin {

    override fun getRenderers(): List<ConfirmationRenderer> {
        return listOf(
            SelectAssetForTransactionConfirmationRenderer(),
        )
    }
}