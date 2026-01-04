package com.mangala.wallet.features.addressbook.domain.usecase.contact

import com.mangala.wallet.features.addressbook.data.model.ContactWithMultipleBlockchainsModel
import com.mangala.wallet.features.addressbook.domain.repository.contact.ContactRepository
import com.mangala.wallet.features.addressbook.domain.usecase.wallet_address.GetWalletAddressesWithBlockchainByContactIdUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Use case to get contacts with all their blockchain networks
 * This fixes the issue where ContactList only shows one blockchain per contact
 */
class GetContactsWithMultipleBlockchainsUseCase(
    private val contactRepository: ContactRepository,
    private val getWalletAddressesWithBlockchainByContactIdUseCase: GetWalletAddressesWithBlockchainByContactIdUseCase
) {
    
    /**
     * Get contacts with multiple blockchains (one-time fetch)
     */
    suspend operator fun invoke(
        filterCriteria: FilterCriteria,
        page: Int,
        pageSize: Int
    ): Result<List<ContactWithMultipleBlockchainsModel>> {
        return try {
            // First get the basic contacts
            val basicContacts = contactRepository.filterContacts(
                query = filterCriteria.searchQuery,
                tagIds = filterCriteria.tagIds,
                groupIds = filterCriteria.groupIds,
                blockchainIds = filterCriteria.blockchainIds,
                onlyFavorites = filterCriteria.onlyFavorites,
                sortOrder = filterCriteria.sortOrder,
                limit = pageSize,
                offset = page * pageSize
            )
            
            // Group contacts by contactId to avoid duplicates and enhance with multiple blockchains
            val contactsWithMultipleBlockchains = basicContacts
                .groupBy { it.contactId }
                .map { (contactId, contactEntries) ->
                    val primaryContact = contactEntries.first()
                    
                    // Get all wallet addresses for this contact
                    val allWalletAddresses = getWalletAddressesWithBlockchainByContactIdUseCase(contactId)
                    
                    // Extract additional blockchain symbols (excluding the primary one)
                    val additionalBlockchainTypes = allWalletAddresses
                        .asSequence()
                        .map { it.blockchainType }
                        .distinct()
                        .filter { it.symbol != primaryContact.blockchainSymbol }
                    
                    ContactWithMultipleBlockchainsModel(
                        contactId = primaryContact.contactId,
                        contactName = primaryContact.contactName,
                        primaryWalletAddress = primaryContact.walletAddress,
                        primaryWalletAddressId = primaryContact.walletAddressId,
                        primaryWalletAlias = primaryContact.walletAlias,
                        primaryWalletSensitive = primaryContact.walletSensitive,
                        primaryBlockchainName = primaryContact.blockchainName,
                        primaryBlockchainSymbol = primaryContact.blockchainSymbol,
                        primaryBlockchainIcon = primaryContact.blockchainIcon,
                        primaryBlockChainColor = primaryContact.blockChainColor,
                        isFavorite = primaryContact.isFavorite,
                        addedTime = primaryContact.addedTime,
                        isSensitive = primaryContact.isSensitive,
                        avatar = primaryContact.avatar,
                        privacyDisplayMode = primaryContact.privacyDisplayMode,
                        additionalBlockchainsSymbol = additionalBlockchainTypes.map { it.symbol }.toList(),
                        additionalBlockchainTypes = additionalBlockchainTypes.toList()
                    )
                }
            
            Result.success(contactsWithMultipleBlockchains)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}