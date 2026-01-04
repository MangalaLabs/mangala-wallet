package com.mangala.wallet.core.ai.domain.model.renderer

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.mangala.wallet.core.ai.domain.model.message.Message

interface MessageRenderer {
    fun getSupportedMessageTypes(): Set<String>
    fun canRender(message: Message): Boolean
    
    @Composable
    fun RenderMessage(
        message: Message,
        onAction: (String, Map<String, Any>) -> Unit,
        modifier: Modifier
    )
}