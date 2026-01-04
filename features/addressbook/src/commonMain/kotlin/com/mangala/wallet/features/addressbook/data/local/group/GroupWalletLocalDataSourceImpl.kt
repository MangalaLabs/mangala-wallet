package com.mangala.wallet.features.addressbook.data.local.group

import com.mangala.wallet.features.addressbook.data.local.AddressBookDatabaseWrapper
import com.mangala.wallet.features.addressbook.data.model.blockchain.WalletAddressEntity
import com.mangala.wallet.features.addressbook.data.model.contact.ContactEntity
import com.mangala.wallet.features.addressbook.data.model.enum.AuthRequirement
import com.mangala.wallet.features.addressbook.data.model.enum.DisplayMode
import com.mangala.wallet.features.addressbook.data.model.enum.SecurityLevel
import com.mangala.wallet.features.addressbook.data.model.enum.SyncStatus
import com.mangala.wallet.features.addressbook.data.model.group.GroupWalletEntity
import com.mangala.wallet.features.addressbook.domain.model.group.GroupWallet
import com.mangala.wallet.features.addressbook.presentation.group.create.ContactWithAddress
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate

class GroupWalletLocalDataSourceImpl(
    databaseWrapper: AddressBookDatabaseWrapper,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : GroupWalletLocalDataSource {

    private val database = databaseWrapper.database
    private val dbQuery = database.addressBookDatabaseQueries

    override suspend fun insertGroupWallets(groupWallets: List<GroupWalletEntity>): Boolean = withContext(ioDispatcher) {
        dbQuery.transaction {
            groupWallets.forEach { groupWallet ->
                // Check if the relationship already exists
                val count = dbQuery.countWalletInGroup(groupWallet.walletAddressId, groupWallet.groupId).executeAsOne().toInt()

                if (count > 0) {
                    // If exists, remove the old relationship first
                    dbQuery.removeWalletFromGroup(groupWallet.walletAddressId, groupWallet.groupId)
                }

                // Insert the new relationship
                dbQuery.addWalletToGroup(
                    group_id = groupWallet.groupId,
                    wallet_address_id = groupWallet.walletAddressId,
                    contact_id = groupWallet.contactId,
                    created_at = groupWallet.createdAt.toEpochMilliseconds()
                )
            }
        }
        return@withContext true
    }

    override suspend fun deleteGroupWalletsByGroupId(groupId: String): Boolean = withContext(ioDispatcher) {
        dbQuery.deleteGroupWalletsByGroupId(groupId)
        return@withContext true
    }

    override suspend fun getGroupWalletsByGroupId(groupId: String): List<GroupWalletEntity> = withContext(ioDispatcher) {
        return@withContext dbQuery.getGroupWalletsByGroupId(groupId)
            .executeAsList()
            .map { row ->
                GroupWalletEntity(
                    groupId = row.group_id,
                    walletAddressId = row.wallet_address_id,
                    contactId = row.contact_id,
                    createdAt = Instant.fromEpochMilliseconds(row.created_at)
                )
            }
    }

    override suspend fun getContactsWithAddressesByGroupId(groupId: String): List<ContactWithAddress> = withContext(ioDispatcher) {
        return@withContext dbQuery.getContactsWithAddressesByGroupId(groupId)
            .executeAsList()
            .map { row ->
                val contact = mapToContactEntity(
                    id = row.contact_id,
                    name = row.name,
                    notes = row.notes,
                    solar_birthday = row.solar_birthday,
                    lunar_birthday = row.lunar_birthday,
                    is_sensitive = row.is_sensitive,
                    security_level = row.security_level,
                    privacy_display_mode = row.privacy_display_mode,
                    auth_requirement = row.auth_requirement,
                    created_at = row.contact_created_at,
                    updated_at = row.contact_updated_at,
                    last_viewed_at = row.last_viewed_at,
                    sync_status = row.sync_status,
                    encrypted_data = row.encrypted_data
                )

                val walletAddress = mapToWalletAddressEntity(
                    id = row.wallet_id,
                    contact_id = row.wallet_contact_id,
                    blockchain_type_id = row.blockchain_type_id,
                    address = row.address,
                    alias = row.alias,
                    wallet_type = row.wallet_type,
                    is_sensitive = row.wallet_is_sensitive,
                    is_primary = row.is_primary,
                    is_verified = row.is_verified,
                    verified_date = row.verified_date,
                    created_at = row.wallet_created_at,
                    updated_at = row.wallet_updated_at
                )

                ContactWithAddress(contact, walletAddress)
            }
    }

    override suspend fun getGroupWallets(groupId: String, limit: Int, offset: Int): List<GroupWallet> = withContext(ioDispatcher) {
        return@withContext dbQuery.getGroupWalletsForDisplay(
            groupId = groupId,
            limit = limit.toLong(),
            offset = offset.toLong()
        )
            .executeAsList()
            .map { row ->
                GroupWallet(
                    walletId = row.wallet_id,
                    walletAlias = row.wallet_alias,
                    walletType = row.wallet_type,
                    walletAddress = row.wallet_address,
                    contactName = row.contact_name,
                    blockchainTypeSymbol = row.blockchain_type_symbol,
                    isSensitive = row.is_sensitive ?: false,
                    contactId = row.contact_id
                )
            }
    }
    
    /**
     * Gets group wallet information for a list of wallet IDs
     * This is used for the AddWalletToGroupBottomSheet
     */
    override suspend fun getGroupWalletsByWalletIds(walletIds: List<String>): List<GroupWallet> = withContext(ioDispatcher) {
        if (walletIds.isEmpty()) {
            return@withContext emptyList()
        }
        
        return@withContext dbQuery.getGroupWalletsByWalletIds(
            walletIds = walletIds
        )
            .executeAsList()
            .map { row ->
                GroupWallet(
                    walletId = row.wallet_id,
                    walletAlias = row.wallet_alias,
                    walletType = row.wallet_type,
                    walletAddress = row.wallet_address,
                    contactName = row.contact_name,
                    blockchainTypeSymbol = row.blockchain_symbol,
                    isSensitive = row.is_sensitive ?: false,
                    contactId = row.contact_id
                )
            }
    }

    /**
     * Maps database row data to a ContactEntity object
     */
    private fun mapToContactEntity(
        id: String,
        name: String,
        notes: String?,
        solar_birthday: String?,
        lunar_birthday: String?,
        is_sensitive: Boolean?,
        security_level: String?,
        privacy_display_mode: String?,
        auth_requirement: String?,
        created_at: Long,
        updated_at: Long,
        last_viewed_at: Long?,
        sync_status: String?,
        encrypted_data: String?
    ): ContactEntity {
        return ContactEntity(
            id = id,
            name = name,
            notes = notes,
            solarBirthday = solar_birthday?.let { LocalDate.parse(it) },
            lunarBirthday = lunar_birthday?.let { LocalDate.parse(it) },
            isSensitive = is_sensitive ?: false,
            securityLevel = SecurityLevel.fromString(security_level),
            privacyDisplayMode = DisplayMode.fromString(privacy_display_mode),
            authRequirement = AuthRequirement.fromString(auth_requirement),
            createdAt = created_at?.let { Instant.fromEpochMilliseconds(it) },
            updatedAt = Instant.fromEpochMilliseconds(updated_at),
            lastViewedAt = last_viewed_at?.let { Instant.fromEpochMilliseconds(it) },
            syncStatus = SyncStatus.fromString(sync_status),
            encryptedData = encrypted_data
        )
    }

    /**
     * Maps database row data to a WalletAddressEntity object
     */
    private fun mapToWalletAddressEntity(
        id: String,
        contact_id: String,
        blockchain_type_id: String,
        address: String,
        alias: String?,
        wallet_type: String?,
        is_sensitive: Boolean?,
        is_primary: Boolean?,
        is_verified: Boolean?,
        verified_date: String?,
        created_at: Long,
        updated_at: Long
    ): WalletAddressEntity {
        return WalletAddressEntity(
            id = id,
            contactId = contact_id,
            blockchainTypeId = blockchain_type_id,
            address = address,
            alias = alias,
            walletType = wallet_type,
            isSensitive = is_sensitive ?: false,
            isPrimary = is_primary ?: false,
            isVerified = is_verified ?: false,
            verifiedDate = verified_date?.let { LocalDate.parse(it) },
            createdAt = Instant.fromEpochMilliseconds(created_at),
            updatedAt = Instant.fromEpochMilliseconds(updated_at)
        )
    }
}