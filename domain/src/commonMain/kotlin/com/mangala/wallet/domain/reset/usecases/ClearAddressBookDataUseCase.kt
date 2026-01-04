package com.mangala.wallet.domain.reset.usecases

/**
 * Domain interface for clearing AddressBook data during wallet reset
 * This provides a clean interface for the domain layer to interact with AddressBook clearing
 */
interface ClearAddressBookDataUseCase {
    /**
     * Clear all AddressBook data for wallet reset
     * This removes all user contacts, groups, user tags, transaction history,
     * settings, and cache data while preserving default blockchain types and tags
     * @return true if clearing was successful
     */
    suspend operator fun invoke(): Boolean
}