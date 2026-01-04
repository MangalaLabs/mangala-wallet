package com.mangala.wallet.features.addressbook.data.model

import androidx.compose.ui.input.pointer.PointerIcon
import com.mangala.wallet.features.addressbook.data.model.blockchain.BlockchainTypeEntity
import com.mangala.wallet.features.addressbook.data.model.enum.DisplayMode

/**
 * Enhanced ContactModel that supports multiple blockchains per contact
 * This solves the issue where ContactList only shows one blockchain while ContactDetail shows all
 */
data class ContactWithMultipleBlockchainsModel(
    val contactId: String,
    val contactName: String,
    val primaryWalletAddress: String,
    val primaryWalletAddressId: String,
    val primaryWalletAlias: String,
    val primaryWalletSensitive: Boolean?,
    val primaryBlockchainName: String,
    val primaryBlockchainSymbol: String,
    val primaryBlockchainIcon: String,
    val primaryBlockChainColor: String,
    val isFavorite: Boolean,
    val addedTime: Long? = 0,
    val isSensitive: Boolean,
    val avatar: String? = null,
    val tagId: String? = null,
    val privacyDisplayMode: DisplayMode = DisplayMode.FULL,
    val additionalBlockchainsSymbol: List<String> = emptyList(),
    val additionalBlockchainTypes: List<BlockchainTypeEntity> = emptyList(),
) {
    
    /**
     * Convert to legacy ContactModel for backward compatibility
     */
    fun toContactModel(): ContactModel {
        return ContactModel(
            contactId = contactId,
            contactName = contactName,
            walletAddress = primaryWalletAddress,
            walletAddressId = primaryWalletAddressId,
            walletAlias = primaryWalletAlias,
            walletSensitive = primaryWalletSensitive,
            blockchainName = primaryBlockchainName,
            blockchainSymbol = primaryBlockchainSymbol,
            blockchainIcon = primaryBlockchainIcon,
            blockChainColor = primaryBlockChainColor,
            isFavorite = isFavorite,
            addedTime = addedTime,
            isSensitive = isSensitive,
            avatar = avatar,
            privacyDisplayMode = privacyDisplayMode
        )
    }

    fun displayAddress(): String {
        val maskedAddress = if (isSensitive) {
            "********"
        } else {
            maskAddress(primaryWalletAddress)
        }

        return maskedAddress
    }

    private fun maskAddress(address: String): String {
        val firstPart = address.take(4)
        val lastPart = address.takeLast(4)

        return "$firstPart...$lastPart"
    }
}