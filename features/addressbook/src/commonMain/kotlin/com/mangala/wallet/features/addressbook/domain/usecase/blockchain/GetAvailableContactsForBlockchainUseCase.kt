package com.mangala.wallet.features.addressbook.domain.usecase.blockchain

import com.mangala.wallet.features.addressbook.domain.model.group.GroupWallet
import com.mangala.wallet.features.addressbook.domain.repository.contact.WalletAddressRepository
import com.mangala.wallet.features.addressbook.presentation.group.create.ContactWithAddress
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

class GetAvailableContactsForBlockchainUseCase(
    private val walletAddressRepository: WalletAddressRepository
) {
    /**
     * Original method that returns ContactWithAddress objects
     * Kept for backward compatibility
     */
    suspend operator fun invoke(blockchainId: String): List<ContactWithAddress> = withContext(
        Dispatchers.IO
    ) {
        walletAddressRepository.getContactsWithAddressesByBlockchainType(blockchainId)
    }
    
    /**
     * New method that returns GroupWallet objects directly
     * This is more efficient as it avoids intermediate mapping operations
     */
    suspend fun getGroupWallets(blockchainId: String, limit: Int = 50, offset: Int = 0, searchQuery: String = ""): List<GroupWallet> = withContext(
        Dispatchers.IO
    ) {
        walletAddressRepository.getGroupWalletsByBlockchainAndAlias(blockchainId, limit, offset, searchQuery)
    }
}