package com.mangala.wallet.features.addressbook.domain.usecase.clipboard

import com.mangala.wallet.features.addressbook.utils.clipboard.ClipboardManager

/**
 * Use case for copying wallet addresses to clipboard.
 * Follows clean architecture pattern by placing business logic in domain layer.
 */
class CopyToClipboardUseCase(private val clipboardManager: ClipboardManager) {
    
    /**
     * Copies a wallet address to clipboard with appropriate label.
     *
     * @param address The wallet address to copy
     */
    operator fun invoke(address: String) {
        clipboardManager.copyToClipboard("Wallet Address", address)
    }
    
    /**
     * Copies text to clipboard with custom label.
     *
     * @param label The label for the clipboard content
     * @param text The text to copy
     */
    operator fun invoke(label: String, text: String) {
        clipboardManager.copyToClipboard(label, text)
    }
}
