package com.mangala.wallet.features.addressbook.data.model

import com.mangala.wallet.features.addressbook.data.model.enum.DisplayMode

data class ContactModel(
    val contactId: String,
    val contactName: String,
    val walletAddress: String,
    val walletAddressId: String,
    val walletAlias: String,
    val walletSensitive: Boolean?,
    val blockchainName: String,
    val blockchainSymbol: String,
    val blockchainIcon: String,
    val blockChainColor: String,
    val isFavorite: Boolean,
    val addedTime: Long? = 0,
    val isSensitive: Boolean,
    val avatar: String? = null,
    val tagId: String? = null,
    val privacyDisplayMode: DisplayMode = DisplayMode.FULL,
) {

    fun displayAddress(): String {
        val maskedAddress = if (isSensitive) {
            "********"
        } else {
            maskAddress(walletAddress)
        }

        return maskedAddress
    }

    private fun maskAddress(address: String): String {
        val firstPart = address.take(4)
        val lastPart = address.takeLast(4)

        return "$firstPart...$lastPart"
    }
}