package com.mangala.wallet.features.addressbook.domain.usecase.contact

import app.cash.paging.PagingData
import app.cash.paging.map
import com.mangala.wallet.features.addressbook.data.model.ContactWithMultipleBlockchainsModel
import com.mangala.wallet.features.addressbook.data.model.contact.ContactEntity
import com.mangala.wallet.features.addressbook.domain.repository.contact.ContactRepository
import com.mangala.wallet.features.addressbook.domain.usecase.wallet_address.GetWalletAddressesWithBlockchainByContactIdUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetAllContactsUseCase(
    private val contactRepository: ContactRepository,
    private val getWalletAddressesWithBlockchainByContactIdUseCase: GetWalletAddressesWithBlockchainByContactIdUseCase,
) {
    operator fun invoke(limit: Int = 50, offset: Int = 0, sortOrder: String = "A_Z"): Flow<List<ContactEntity>> {
        return contactRepository.getAllContacts(limit, offset, sortOrder)
    }

    fun getPaginatedContacts(
        searchQuery: String? = null,
        tagIds: List<String>? = null,
        checkTagId: String? = null,
    ): Flow<PagingData<ContactWithMultipleBlockchainsModel>> =
        contactRepository
            .getPaginatedContacts(
                searchQuery = searchQuery,
                tagIds = tagIds,
                checkTagId = checkTagId,
                isFavoriteOnly = false
            )
            .map {
                it.map { contact ->
                    val allWalletAddresses = getWalletAddressesWithBlockchainByContactIdUseCase(contact.contactId)

                    val additionalBlockchains = allWalletAddresses
                        .map { it.blockchainType.symbol }
                        .distinct()
                        .filter { it != contact.blockchainSymbol }

                    ContactWithMultipleBlockchainsModel(
                        contactId = contact.contactId,
                        contactName = contact.contactName,
                        primaryWalletAddress = contact.walletAddress,
                        primaryWalletAddressId = contact.walletAddressId,
                        primaryWalletAlias = contact.walletAlias,
                        primaryWalletSensitive = contact.walletSensitive,
                        primaryBlockchainName = contact.blockchainName,
                        primaryBlockchainSymbol = contact.blockchainSymbol,
                        primaryBlockchainIcon = contact.blockchainIcon,
                        primaryBlockChainColor = contact.blockChainColor,
                        isFavorite = contact.isFavorite,
                        addedTime = contact.addedTime,
                        isSensitive = contact.isSensitive,
                        avatar = contact.avatar,
                        tagId = contact.tagId,
                        privacyDisplayMode = contact.privacyDisplayMode,
                        additionalBlockchainsSymbol = additionalBlockchains
                    )
                }
            }

    /**
     * Observe all contacts for reactive updates
     * @return Flow with list of all contacts
     */
    fun observeContacts(): Flow<List<ContactEntity>> {
        return contactRepository.observeContacts()
    }
}