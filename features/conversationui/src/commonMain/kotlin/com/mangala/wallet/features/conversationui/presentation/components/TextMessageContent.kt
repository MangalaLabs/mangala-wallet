package com.mangala.wallet.features.conversationui.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.mangala.wallet.core.ai.domain.model.message.TextMessage
import com.mangala.wallet.core.ai.domain.model.message.UiTag
import com.mangala.wallet.features.conversationui.presentation.components.input.NetworkSelector
import com.mangala.wallet.model.blockchain.BlockchainNetworkData
import com.mangala.wallet.ui.theme.mangalaColors
import com.mikepenz.markdown.compose.components.markdownComponents
import com.mikepenz.markdown.compose.elements.highlightedCodeBlock
import com.mikepenz.markdown.compose.elements.highlightedCodeFence
import com.mikepenz.markdown.m3.Markdown
import com.mikepenz.markdown.m3.markdownColor
import com.mikepenz.markdown.model.rememberMarkdownState

@Composable
fun TextMessageContent(
    textMessage: TextMessage,
    onUiTagAction: (UiTag, Any) -> Unit = { _, _ -> }
) {
    Column {
        val markdownState = rememberMarkdownState(textMessage.text.trimIndent())

        Markdown(
            markdownState,
            components = markdownComponents(
                codeBlock = highlightedCodeBlock,
                codeFence = highlightedCodeFence
            ),
            colors = markdownColor(
                text = MaterialTheme.mangalaColors.textPrimary
            )
        )
        
        textMessage.uiTags.forEach { uiTag ->
            Spacer(modifier = Modifier.height(8.dp))
            UiTagContent(
                uiTag = uiTag,
                onAction = { data -> onUiTagAction(uiTag, data) }
            )
        }
    }
}

@Composable
private fun UiTagContent(
    uiTag: UiTag,
    onAction: (Any) -> Unit
) {
    when (uiTag) {
        UiTag.SelectNetwork -> {
            // For Option B, we don't render the selector here
            // The network selector is handled by the screen state
            // Show a message indicating network selection is needed
            NetworkSelectionNotice()
        }

        UiTag.RequestAddressInput -> TODO()
        is UiTag.EnterAddress -> TODO()
    }
}

@Composable
private fun NetworkSelectionNotice() {
    androidx.compose.material3.Card(
        modifier = Modifier.fillMaxWidth(),
        colors = androidx.compose.material3.CardDefaults.cardColors(
            containerColor = Color(0xFF227BFF).copy(alpha = 0.1f)
        ),
        border = androidx.compose.foundation.BorderStroke(
            width = 1.dp,
            color = Color(0xFF227BFF).copy(alpha = 0.3f)
        )
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            androidx.compose.material3.Icon(
                imageVector = androidx.compose.material.icons.Icons.Default.Info,
                contentDescription = null,
                tint = Color(0xFF8CC8FF),
                modifier = Modifier.size(16.dp)
            )
            androidx.compose.material3.Text(
                text = "Network selection required - check input area below",
                style = androidx.compose.material3.MaterialTheme.typography.bodySmall,
                color = Color(0xFF8CC8FF)
            )
        }
    }
}