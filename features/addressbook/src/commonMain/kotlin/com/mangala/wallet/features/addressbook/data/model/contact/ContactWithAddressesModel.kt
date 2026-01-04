package com.mangala.wallet.features.addressbook.data.model.contact

import com.mangala.wallet.features.addressbook.data.model.ContactModel

/**
 * Represents a contact with multiple wallet addresses for display in the UI
 */
data class ContactWithAddressesModel(
    val contactId: String,
    val contactName: String,
    val isFavorite: Boolean,
    val addresses: List<AddressInfo>
)

/**
 * Represents a single wallet address with its associated blockchain information
 */
data class AddressInfo(
    val walletAddressId: String,
    val walletAddress: String,
    val walletAlias: String,
    val walletSensitive: Boolean?,
    val blockchainName: String,
    val blockchainSymbol: String,
    val blockchainIcon: String,
    val blockChainColor: String,
    val addedTime: Long? = 0
)

/**
 * Result class for paginated contacts with grouped addresses
 */
data class PaginatedContactsResult(
    val contacts: List<ContactWithAddressesModel>,
    val hasMoreData: Boolean,
    val nextOffset: Int?
)

/**
 * Extension function to convert a list of ContactModel to ContactWithAddressesModel
 * by grouping addresses with the same contactId
 */
fun List<ContactModel>.groupByContactId(): List<ContactWithAddressesModel> {
    return this.groupBy { it.contactId }
        .map { (contactId, models) ->
            val firstModel = models.first()
            ContactWithAddressesModel(
                contactId = contactId,
                contactName = firstModel.contactName ?: "",
                isFavorite = firstModel.isFavorite,
                addresses = models.map { model ->
                    AddressInfo(
                        walletAddressId = model.walletAddressId,
                        walletAddress = model.walletAddress,
                        walletAlias = model.walletAlias ?: "",
                        walletSensitive = model.walletSensitive,
                        blockchainName = model.blockchainName,
                        blockchainSymbol = model.blockchainSymbol,
                        blockchainIcon = model.blockchainIcon,
                        blockChainColor = model.blockChainColor,
                        addedTime = model.addedTime
                    )
                }
            )
        }
}