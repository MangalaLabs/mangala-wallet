package com.mangala.wallet.features.addressbook.data.local.contact

import com.benasher44.uuid.uuid4
import com.mangala.wallet.features.addressbook.data.local.AddressBookDatabaseWrapper
import com.mangala.wallet.features.addressbook.data.model.WalletAddressWithNetworkModel
import com.mangala.wallet.features.addressbook.data.model.blockchain.WalletAddressEntity
import com.mangala.wallet.features.addressbook.data.model.contact.ContactEntity
import com.mangala.wallet.features.addressbook.data.model.enum.AuthRequirement
import com.mangala.wallet.features.addressbook.data.model.enum.DisplayMode
import com.mangala.wallet.features.addressbook.data.model.enum.SecurityLevel
import com.mangala.wallet.features.addressbook.data.model.enum.SyncStatus
import com.mangala.wallet.features.addressbook.domain.model.group.GroupWallet
import com.mangala.wallet.features.addressbook.presentation.group.create.ContactWithAddress
import com.mangala.wallet.utils.localDateTimeNow
import com.mangala.wallet.utils.localDateTimeToMillis
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate

/**
 * Implementation của WalletAddressLocalDataSource sử dụng SQLDelight database
 */
class WalletAddressLocalDataSourceImpl(
    databaseWrapper: AddressBookDatabaseWrapper,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : WalletAddressLocalDataSource {

    private val database = databaseWrapper.database
    private val dbQuery = database.addressBookDatabaseQueries

    override suspend fun getWalletAddressesForContact(contactId: String, limit: Int, offset: Int): List<WalletAddressWithNetworkModel> =
        withContext(ioDispatcher) {
            dbQuery.getWalletAddressesForContact(contactId, limit.toLong(), offset.toLong())
                .executeAsList()
                .map { row ->
                    // Truy cập trực tiếp vào thuộc tính của row thay vì sử dụng method reference
                    mapWalletAddressWithNetworkSelecting(
                        id = row.id,
                        contact_id = row.contact_id,
                        blockchain_type_id = row.blockchain_type_id,
                        address = row.address,
                        alias = row.alias,
                        wallet_type = row.wallet_type,
                        is_primary = row.is_primary,
                        is_sensitive = row.is_sensitive,
                        is_verified = row.is_verified,
                        verified_date = row.verified_date,
                        created_at = row.created_at,
                        updated_at = row.updated_at,
                        network_name = row.network_name,
                        network_symbol = row.network_symbol
                    )
                }
        }

    override suspend fun getWalletAddressesById(walletId: String): WalletAddressWithNetworkModel =
        withContext(ioDispatcher) {
            dbQuery.getWalletAddressesById(walletId).executeAsOneOrNull()?.let { row ->
                mapWalletAddressWithNetworkSelecting(
                    id = row.id,
                    contact_id = row.contact_id,
                    blockchain_type_id = row.blockchain_type_id,
                    address = row.address,
                    alias = row.alias,
                    wallet_type = row.wallet_type,
                    is_primary = row.is_primary,
                    is_sensitive = row.is_sensitive,
                    is_verified = row.is_verified,
                    verified_date = row.verified_date,
                    created_at = row.created_at,
                    updated_at = row.updated_at,
                    network_name = row.network_name,
                    network_symbol = row.network_symbol
                )
            }!!
        }

    override suspend fun getWalletAddressByAddress(address: String): WalletAddressEntity? =
        withContext(ioDispatcher) {
            dbQuery.getWalletAddressByAddress(address).executeAsOneOrNull()?.let { row ->
                mapWalletAddressSelecting(
                    id = row.id,
                    contact_id = row.contact_id,
                    blockchain_type_id = row.blockchain_type_id,
                    address = row.address,
                    alias = row.alias,
                    wallet_type = row.wallet_type,
                    is_primary = row.is_primary,
                    is_sensitive = row.is_sensitive,
                    is_verified = row.is_verified,
                    verified_date = row.verified_date,
                    created_at = row.created_at,
                    updated_at = row.updated_at
                )
            }
        }

    override suspend fun countWalletAddressesForContact(contactId: String): Int = withContext(ioDispatcher) {
        dbQuery.countWalletAddressesForContact(contactId).executeAsOne().toInt()
    }

    override suspend fun getDefaultWalletAddress(contactId: String, blockchainTypeId: String): WalletAddressEntity? =
        withContext(ioDispatcher) {
            dbQuery.getDefaultWalletAddress(contactId, blockchainTypeId)
                .executeAsOneOrNull()?.let { row ->
                    // Truy cập trực tiếp vào thuộc tính của row thay vì sử dụng method reference
                    mapWalletAddressSelecting(
                        id = row.id,
                        contact_id = row.contact_id,
                        blockchain_type_id = row.blockchain_type_id,
                        address = row.address,
                        alias = row.alias,
                        wallet_type = row.wallet_type,
                        is_primary = row.is_primary,
                        is_sensitive = row.is_sensitive,
                        is_verified = row.is_verified,
                        verified_date = row.verified_date,
                        created_at = row.created_at,
                        updated_at = row.updated_at
                    )
                }
        }

    override suspend fun setWalletAddressAsDefault(id: String, contactId: String, blockchainTypeId: String): Boolean =
        withContext(ioDispatcher) {
            try {
                dbQuery.transaction {
                    // Đầu tiên, đặt tất cả wallet address của contact và blockchain này thành non-primary
                    dbQuery.clearPrimaryWalletForContactAndBlockchainType(
                        localDateTimeToMillis(localDateTimeNow()),
                        contactId,
                        blockchainTypeId
                    )

                    // Cập nhật wallet address đích thành primary
                    dbQuery.updateWalletAddressPrimaryStatus(
                        is_primary = true,
                        updated_at = localDateTimeToMillis(localDateTimeNow()),
                        id = id
                    )
                }
                true
            } catch (e: Exception) {
                false
            }
        }

    private fun mapWalletAddressSelecting(
        id: String,
        contact_id: String,
        blockchain_type_id: String,
        address: String,
        alias: String?,
        wallet_type: String?,
        is_primary: Boolean?, // Thay đổi từ Boolean sang Boolean?
        is_sensitive: Boolean?, // Thay đổi từ Boolean sang Boolean?
        is_verified: Boolean?, // Thay đổi từ Boolean sang Boolean?
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
            isSensitive = is_sensitive ?: false, // Xử lý null
            isPrimary = is_primary ?: false, // Xử lý null
            isVerified = is_verified ?: false, // Xử lý null
            verifiedDate = verified_date?.let { LocalDate.parse(it) },
            createdAt = Instant.fromEpochMilliseconds(created_at),
            updatedAt = Instant.fromEpochMilliseconds(updated_at)
        )
    }

    override suspend fun getWalletAddressesByNetwork(networkId: String, limit: Int, offset: Int): List<WalletAddressWithNetworkModel> =
        withContext(ioDispatcher) {
            dbQuery.getWalletAddressesByBlockchainType(networkId, limit.toLong(), offset.toLong())
                .executeAsList()
                .map { row ->
                    // Truy cập trực tiếp vào thuộc tính của row thay vì sử dụng method reference
                    mapWalletAddressWithNetworkSelecting(
                        id = row.id,
                        contact_id = row.contact_id,
                        blockchain_type_id = row.blockchain_type_id,
                        address = row.address,
                        alias = row.alias,
                        wallet_type = row.wallet_type,
                        is_primary = row.is_primary,
                        is_sensitive = row.is_sensitive,
                        is_verified = row.is_verified,
                        verified_date = row.verified_date,
                        created_at = row.created_at,
                        updated_at = row.updated_at,
                        network_name = row.network_name,
                        network_symbol = row.network_symbol
                    )
                }
        }

    override suspend fun insertWalletAddress(walletAddress: WalletAddressEntity): String = withContext(ioDispatcher) {
        try {
            val id = walletAddress.id.takeIf { it.isNotBlank() } ?: uuid4().toString()

            dbQuery.transaction {
                // Nếu wallet này được đặt làm primary, đặt các wallet khác của contact và blockchain thành non-primary
                if (walletAddress.isPrimary) {
                    dbQuery.clearPrimaryWalletForContactAndBlockchainType(
                        localDateTimeToMillis(localDateTimeNow()),
                        walletAddress.contactId,
                        walletAddress.blockchainTypeId
                    )
                }

                // Thêm wallet address mới
                dbQuery.insertWalletAddress(
                    id = id,
                    contact_id = walletAddress.contactId,
                    blockchain_type_id = walletAddress.blockchainTypeId,
                    address = walletAddress.address,
                    alias = walletAddress.alias,
                    wallet_type = walletAddress.walletType,
                    is_sensitive = walletAddress.isSensitive,
                    is_primary = walletAddress.isPrimary,
                    is_verified = walletAddress.isVerified,
                    verified_date = walletAddress.verifiedDate?.toString(),
                    created_at = walletAddress.createdAt.toEpochMilliseconds(),
                    updated_at = walletAddress.updatedAt.toEpochMilliseconds()
                )
            }

            return@withContext id
        } catch (e: Exception) {
            println("insertWalletAddress $e")
            return@withContext ""
        }
    }

    override suspend fun updateWalletAddress(walletAddress: WalletAddressEntity): Boolean = withContext(ioDispatcher) {
        try {
            dbQuery.transaction {
                // Nếu wallet này được đặt làm primary, đặt các wallet khác của contact và blockchain thành non-primary
                if (walletAddress.isPrimary) {
                    dbQuery.clearPrimaryWalletForContactAndBlockchainType(
                        localDateTimeToMillis(localDateTimeNow()),
                        walletAddress.contactId,
                        walletAddress.blockchainTypeId
                    )
                }

                // Cập nhật wallet address
                dbQuery.updateWalletAddress(
                    address = walletAddress.address,
                    alias = walletAddress.alias,
                    wallet_type = walletAddress.walletType,
                    is_primary = walletAddress.isPrimary,
                    is_sensitive = walletAddress.isSensitive,
                    is_verified = walletAddress.isVerified,
                    verified_date = walletAddress.verifiedDate?.toString(),
                    updated_at = walletAddress.updatedAt.toEpochMilliseconds(),
                    id = walletAddress.id,
                    blockchain_type_id = walletAddress.blockchainTypeId
                )
            }

            true
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun deleteWalletAddress(id: String): Boolean = withContext(ioDispatcher) {
        try {
            dbQuery.deleteWalletAddress(id)
            true
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun deleteWalletAddressesByContactId(contactId: String): Boolean = withContext(ioDispatcher) {
        try {
            dbQuery.deleteWalletAddressesByContactId(contactId)
            true
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Mapper cho kết quả join giữa wallet_addresses và blockchain_types
     */
    private fun mapWalletAddressWithNetworkSelecting(
        id: String,
        contact_id: String,
        blockchain_type_id: String,
        address: String,
        alias: String?,
        wallet_type: String?,
        is_primary: Boolean?,  // Thay đổi từ Boolean sang Boolean?
        is_sensitive: Boolean?,  // Thay đổi từ Boolean sang Boolean?
        is_verified: Boolean?,  // Thay đổi từ Boolean sang Boolean?
        verified_date: String?,
        created_at: Long,
        updated_at: Long,
        network_name: String?,
        network_symbol: String?
    ): WalletAddressWithNetworkModel {
        return WalletAddressWithNetworkModel(
            id = id,
            contactId = contact_id,
            blockchainNetworkId = blockchain_type_id,
            address = address,
            alias = alias,
            walletType = wallet_type,
            isDefault = is_primary ?: false,  // Sử dụng toán tử ?: để xử lý null
            networkName = network_name,
            networkSymbol = network_symbol,
            createdAt = created_at,
            updatedAt = updated_at,
            isSensitive = is_sensitive ?: false,  // Sử dụng toán tử ?: để xử lý null
            isVerified = is_verified ?: false,  // Sử dụng toán tử ?: để xử lý null
        )
    }


    override suspend fun insertWalletAddressesBatch(walletAddresses: List<WalletAddressEntity>): Map<WalletAddressEntity, String> =
        withContext(ioDispatcher) {
            val result = mutableMapOf<WalletAddressEntity, String>()

            dbQuery.transaction {
                walletAddresses.forEach { walletAddress ->
                    val id = walletAddress.id.takeIf { it.isNotBlank() } ?: uuid4().toString()

                    // Nếu wallet này được đặt làm primary, đặt các wallet khác của contact và blockchain thành non-primary
                    if (walletAddress.isPrimary) {
                        dbQuery.clearPrimaryWalletForContactAndBlockchainType(
                            localDateTimeToMillis(localDateTimeNow()),
                            walletAddress.contactId,
                            walletAddress.blockchainTypeId
                        )
                    }

                    // Thêm wallet address
                    dbQuery.insertWalletAddress(
                        id = id,
                        contact_id = walletAddress.contactId,
                        blockchain_type_id = walletAddress.blockchainTypeId,
                        address = walletAddress.address,
                        alias = walletAddress.alias,
                        wallet_type = walletAddress.walletType,
                        is_sensitive = walletAddress.isSensitive,
                        is_primary = walletAddress.isPrimary,
                        is_verified = walletAddress.isVerified,
                        verified_date = walletAddress.verifiedDate?.toString(),
                        created_at = walletAddress.createdAt.toEpochMilliseconds(),
                        updated_at = walletAddress.updatedAt.toEpochMilliseconds()
                    )

                    result[walletAddress] = id
                }
            }

            return@withContext result
        }

    override suspend fun getContactsWithAddressesByBlockchainType(blockchainId: String): List<ContactWithAddress> = withContext(ioDispatcher) {
        return@withContext dbQuery.getContactsWithAddressesByBlockchainType(blockchainId)
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

    override suspend fun getGroupWalletsByWalletIds(walletIds: List<String>): List<GroupWallet> = withContext(ioDispatcher) {
        if (walletIds.isEmpty()) {
            return@withContext emptyList()
        }

        return@withContext dbQuery.getGroupWalletsByWalletIds(walletIds)
            .executeAsList()
            .map { row ->
                GroupWallet(
                    walletId = row.wallet_id,
                    walletAlias = row.wallet_alias,
                    walletType = row.wallet_type,
                    walletAddress = row.wallet_address,
                    contactId = row.contact_id, // Include the contactId for the foreign key constraint
                    contactName = row.contact_name,
                    blockchainTypeSymbol = row.blockchain_symbol,
                    isSensitive = row.is_sensitive ?: false
                )
            }
    }
    
    /**
     * Implement the new optimized method to get GroupWallet objects directly from the database
     * filtered by blockchain and optional search on alias, address, or contact name
     */
    override suspend fun getGroupWalletsByBlockchainAndAlias(
        blockchainId: String,
        limit: Int,
        offset: Int,
        searchQuery: String
    ): List<GroupWallet> = withContext(ioDispatcher) {
        return@withContext dbQuery.getGroupWalletsByBlockchainAndAlias(
            blockchainId = blockchainId,
            searchQuery = searchQuery,
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
                contactId = row.contact_id,
                contactName = row.contact_name,
                blockchainTypeSymbol = row.blockchain_symbol,
                isSensitive = row.is_sensitive ?: false
            )
        }
    }
    
    /**
     * Count the total number of wallet addresses matching the blockchain and search criteria
     * for pagination purposes
     */
    override suspend fun countGroupWalletsByBlockchainAndAlias(
        blockchainId: String,
        searchQuery: String
    ): Int = withContext(ioDispatcher) {
        return@withContext dbQuery.countGroupWalletsByBlockchainAndAlias(
            blockchainId = blockchainId,
            searchQuery = searchQuery
        ).executeAsOne().toInt()
    }
}