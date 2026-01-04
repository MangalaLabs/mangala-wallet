package com.mangala.wallet.features.addressbook.domain.usecase.contact.favorite

import app.cash.paging.PagingData
import app.cash.paging.map
import com.mangala.wallet.features.addressbook.data.model.ContactModel
import com.mangala.wallet.features.addressbook.data.model.ContactWithMultipleBlockchainsModel
import com.mangala.wallet.features.addressbook.domain.repository.contact.ContactRepository
import com.mangala.wallet.features.addressbook.domain.usecase.wallet_address.GetWalletAddressesWithBlockchainByContactIdUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetFavoriteContactsUseCase(
    private val contactRepository: ContactRepository,
    private val getWalletAddressesWithBlockchainByContactIdUseCase: GetWalletAddressesWithBlockchainByContactIdUseCase,
) {
    suspend fun invokeFlow(limit: Int = 50): Flow<List<ContactModel>> {
        return contactRepository.getFavoriteContactsFlow(limit, offset = 0)
    }

    // Cash App Paging flow for automatic pagination
    fun getPaginatedFavoriteContacts(
        searchQuery: String? = null,
    ): Flow<PagingData<ContactWithMultipleBlockchainsModel>> {
        return contactRepository.getPaginatedContacts(
            searchQuery = searchQuery,
            isFavoriteOnly = true
        ).map {
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
                    privacyDisplayMode = contact.privacyDisplayMode,
                    additionalBlockchainsSymbol = additionalBlockchains
                )
            }
        }
    }
}