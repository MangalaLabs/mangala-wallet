package com.mangala.wallet.core.ai.domain.model.dialog

import androidx.compose.runtime.Composable

interface DialogProvider {
    fun getSupportedDialogTypes(): Set<String>
    fun canProvideDialog(type: String, context: Map<String, Any>): Boolean
    
    @Composable
    fun ProvideDialog(
        type: String,
        context: Map<String, Any>,
        onAction: (String, Map<String, Any>) -> Unit,
        onDismiss: () -> Unit
    )
}