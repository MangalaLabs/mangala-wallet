package com.mangala.wallet.features.addressbook.data.local.group

import app.cash.paging.PagingSource
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.benasher44.uuid.uuid4
import com.mangala.wallet.features.addressbook.data.local.AddressBookDatabaseWrapper
import com.mangala.wallet.features.addressbook.data.model.ContactModel
import com.mangala.wallet.features.addressbook.data.model.ContactWithWalletAddressModel
import com.mangala.wallet.features.addressbook.data.model.GroupDetailModel
import com.mangala.wallet.features.addressbook.data.model.contact.AddressInfo
import com.mangala.wallet.features.addressbook.data.model.contact.ContactWithAddressesModel
import com.mangala.wallet.features.addressbook.data.model.contact.PaginatedContactsResult
import com.mangala.wallet.features.addressbook.data.model.contact.groupByContactId
import com.mangala.wallet.features.addressbook.data.model.group.GroupEntity
import com.mangala.wallet.features.addressbook.data.model.blockchain.BlockchainTypeEntity
import com.mangala.wallet.features.addressbook.data.model.blockchain.WalletAddressEntity
import com.mangala.wallet.features.addressbook.data.model.contact.ContactEntity
import com.mangala.wallet.features.addressbook.data.model.enum.AuthRequirement
import com.mangala.wallet.features.addressbook.data.model.enum.DisplayMode
import com.mangala.wallet.features.addressbook.data.model.enum.PrivacyLevel
import com.mangala.wallet.features.addressbook.data.model.enum.SecurityLevel
import com.mangala.wallet.features.addressbook.data.model.enum.SyncStatus
import com.mangala.wallet.features.addressbook.data.model.group.GroupModel
import com.mangala.wallet.utils.localDateTimeNow
import com.mangala.wallet.utils.localDateTimeToMillis
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate

/**
 * Implementation của GroupLocalDataSource sử dụng SQLDelight database
 */
class GroupLocalDataSourceImpl(
    databaseWrapper: AddressBookDatabaseWrapper,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : GroupLocalDataSource {

    private val database = databaseWrapper.database
    private val dbQuery = database.addressBookDatabaseQueries

    /**
     * Chuyển đổi từ database Group row sang GroupEntity
     */
    private fun mapToGroupEntity(
        id: String,
        name: String,
        main_blockchain_id: String?,
        description: String?,
        icon: String?,
        color: String?,
        privacy_level: String?,  // Thay đổi sang nullable
        security_level: String?,  // Thay đổi sang nullable
        created_at: Long,
        updated_at: Long
    ): GroupEntity {
        return GroupEntity(
            id = id,
            name = name,
            mainBlockchainId = main_blockchain_id,
            description = description,
            icon = icon,
            color = color,
            privacyLevel = PrivacyLevel.fromString(privacy_level),
            securityLevel = SecurityLevel.fromString(security_level),
            createdAt = Instant.fromEpochMilliseconds(created_at),
            updatedAt = Instant.fromEpochMilliseconds(updated_at)
        )
    }

    /**
     * Chuyển đổi từ database Contact row sang ContactEntity
     */
    private fun mapToContactEntity(
        id: String,
        name: String,
        notes: String?,
        solar_birthday: String?,
        lunar_birthday: String?,
        is_sensitive: Boolean?,  // Thay đổi sang nullable
        security_level: String?,  // Thay đổi sang nullable
        privacy_display_mode: String?,  // Thay đổi sang nullable
        auth_requirement: String?,  // Thay đổi sang nullable
        created_at: Long,
        updated_at: Long,
        last_viewed_at: Long?,
        sync_status: String?,  // Thay đổi sang nullable
        encrypted_data: String?
    ): ContactEntity {
        return ContactEntity(
            id = id,
            name = name,
            notes = notes,
            solarBirthday = solar_birthday?.let { LocalDate.parse(it) },
            lunarBirthday = lunar_birthday?.let { LocalDate.parse(it) },
            isSensitive = is_sensitive ?: false,  // Xử lý null
            securityLevel = SecurityLevel.fromString(security_level),
            privacyDisplayMode = DisplayMode.fromString(privacy_display_mode),
            authRequirement = AuthRequirement.fromString(auth_requirement),
            createdAt = Instant.fromEpochMilliseconds(created_at),
            updatedAt = Instant.fromEpochMilliseconds(updated_at),
            lastViewedAt = last_viewed_at?.let { Instant.fromEpochMilliseconds(it) },
            syncStatus = SyncStatus.fromString(sync_status),
            encryptedData = encrypted_data
        )
    }

    /**
     * Chuyển đổi từ database WalletAddress row sang WalletAddressEntity
     */
    private fun mapToWalletAddressEntity(
        id: String,
        contact_id: String,
        blockchain_type_id: String,
        address: String,
        alias: String?,
        wallet_type: String?,
        is_sensitive: Boolean?,  // Thay đổi sang nullable
        is_primary: Boolean?,  // Thay đổi sang nullable
        is_verified: Boolean?,  // Thay đổi sang nullable
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
            isSensitive = is_sensitive ?: false,  // Xử lý null
            isPrimary = is_primary ?: false,  // Xử lý null
            isVerified = is_verified ?: false,  // Xử lý null
            verifiedDate = verified_date?.let { LocalDate.parse(it) },
            createdAt = Instant.fromEpochMilliseconds(created_at),
            updatedAt = Instant.fromEpochMilliseconds(updated_at)
        )
    }

    /**
     * Chuyển đổi từ database BlockchainType row sang BlockchainTypeEntity
     */
    private fun mapToBlockchainTypeEntity(
        id: String,
        name: String,
        symbol: String,
        address_format: String?,
        validation_regex: String?,
        icon: String?,
        color: String?,
        network_type: String,
        is_active: Boolean?,  // Thay đổi sang nullable
        created_at: Long,
        updated_at: Long
    ): BlockchainTypeEntity {
        return BlockchainTypeEntity(
            id = id,
            name = name,
            symbol = symbol,
            addressFormat = address_format,
            validationRegex = validation_regex,
            icon = icon,
            color = color,
            networkType = network_type,
            isActive = is_active ?: true,  // Xử lý null với giá trị mặc định là true
            createdAt = Instant.fromEpochMilliseconds(created_at),
            updatedAt = Instant.fromEpochMilliseconds(updated_at)
        )
    }

    override suspend fun getGroupById(id: String): GroupEntity? = withContext(ioDispatcher) {
        // Sửa: Sử dụng truy vấn khác vì getGroupById không tồn tại
        // Giả sử có truy vấn getAllGroups, chúng ta có thể tìm theo ID
        val allGroups = dbQuery.getGroupById(id).executeAsList()
        val group = allGroups.firstOrNull() ?: return@withContext null

        mapToGroupEntity(
            id = group.id,
            name = group.name,
            main_blockchain_id = group.main_blockchain_id,
            description = group.description,
            icon = group.icon,
            color = group.color,
            privacy_level = group.privacy_level,
            security_level = group.security_level,
            created_at = group.created_at,
            updated_at = group.updated_at
        )
    }

    override suspend fun getGroupModelById(id: String): GroupModel? = withContext(ioDispatcher) {
        val groups = dbQuery.getGroupById2(id).executeAsList()
        if (groups.isEmpty()) return@withContext null

        val group = groups.first()
        return@withContext GroupModel(
            id = group.group_id,
            name = group.group_name,
            description = group.group_description,
            icon = group.group_icon,
            color = group.group_color,
            privacyLevel = group.privacy_level.toString(),
            securityLevel = group.security_level.toString(),
            createdAt = group.created_at,
            updatedAt = group.updated_at,
            mainBlockchainName = group.main_blockchain_name,
            mainBlockchainSymbol = group.main_blockchain_symbol,
            mainBlockchainIcon = group.main_blockchain_icon,
            walletAddressCount = group.wallet_address_count.toInt(),
            mainBlockchainId = group.main_blockchain_id
        )
    }

    override suspend fun getGroupDetailById(id: String): GroupDetailModel? = withContext(ioDispatcher) {
        val group = getGroupById(id) ?: return@withContext null

        // Lấy blockchain type chính của group
        val mainBlockchainType = group.mainBlockchainId?.let { blockchainId ->
            dbQuery.getBlockchainTypeById(blockchainId).executeAsOneOrNull()?.let { blockchain ->
                mapToBlockchainTypeEntity(
                    id = blockchain.id,
                    name = blockchain.name,
                    symbol = blockchain.symbol,
                    address_format = blockchain.address_format,
                    validation_regex = blockchain.validation_regex,
                    icon = blockchain.icon,
                    color = blockchain.color,
                    network_type = blockchain.network_type,
                    is_active = blockchain.is_active,
                    created_at = blockchain.created_at,
                    updated_at = blockchain.updated_at
                )
            }
        }

        // Lấy danh sách contacts trong group kèm wallet address
        val contactsWithWalletAddress = mutableListOf<ContactWithWalletAddressModel>()

        // Lấy danh sách contact_groups
        dbQuery.getContactsByGroupId(id, 1000, 0).executeAsList().forEach { contact ->
            val contactEntity = mapToContactEntity(
                id = contact.id,
                name = contact.name,
                notes = contact.notes,
                solar_birthday = contact.solar_birthday,
                lunar_birthday = contact.lunar_birthday,
                is_sensitive = contact.is_sensitive,
                security_level = contact.security_level,
                privacy_display_mode = contact.privacy_display_mode,
                auth_requirement = contact.auth_requirement,
                created_at = contact.created_at,
                updated_at = contact.updated_at,
                last_viewed_at = contact.last_viewed_at,
                sync_status = contact.sync_status,
                encrypted_data = contact.encrypted_data
            )

            // Lấy wallet address được sử dụng trong group
            var walletAddressEntity: WalletAddressEntity? = null
            dbQuery.getWalletAddressForContactInGroup(contactEntity.id, id).executeAsOneOrNull()?.let { wallet ->
                walletAddressEntity = mapToWalletAddressEntity(
                    id = wallet.id,
                    contact_id = wallet.contact_id,
                    blockchain_type_id = wallet.blockchain_type_id,
                    address = wallet.address,
                    alias = wallet.alias,
                    wallet_type = wallet.wallet_type,
                    is_sensitive = wallet.is_sensitive,
                    is_primary = wallet.is_primary,
                    is_verified = wallet.is_verified,
                    verified_date = wallet.verified_date,
                    created_at = wallet.created_at,
                    updated_at = wallet.updated_at
                )
            }

            contactsWithWalletAddress.add(
                ContactWithWalletAddressModel(
                    contact = contactEntity,
                    walletAddress = walletAddressEntity
                )
            )
        }

        return@withContext GroupDetailModel(
            group = group,
            contacts = contactsWithWalletAddress,
            mainBlockchainType = mainBlockchainType
        )
    }

    override suspend fun getAllGroups(): List<GroupModel> = withContext(ioDispatcher) {
        dbQuery.filterGroups("", Long.MAX_VALUE, 0)
            .executeAsList()
            .map { group ->
                GroupModel(
                    id = group.group_id,
                    name = group.group_name,
                    description = group.group_description,
                    icon = group.group_icon,
                    color = group.group_color,
                    privacyLevel = group.privacy_level.toString(),
                    securityLevel = group.security_level.toString(),
                    createdAt = group.created_at,
                    updatedAt = group.updated_at,
                    mainBlockchainName = group.main_blockchain_name,
                    mainBlockchainSymbol = group.main_blockchain_symbol,
                    mainBlockchainIcon = group.main_blockchain_icon,
                    walletAddressCount = group.wallet_address_count.toInt(),
                    mainBlockchainId = group.main_blockchain_id
                )
            }
    }

    override fun getGroupsPagingSource(
        searchQuery: String?
    ): PagingSource<Int, GroupModel> {
        return GroupsPagingSource(
            dbQuery = dbQuery,
            searchQuery = searchQuery,
            ioDispatcher = ioDispatcher
        )
    }

    override suspend fun getGroupsByContactId(contactId: String): List<GroupEntity> = withContext(ioDispatcher) {
        dbQuery.getGroupsByContactId(contactId)
            .executeAsList()
            .map { group ->
                mapToGroupEntity(
                    id = group.id,
                    name = group.name,
                    main_blockchain_id = group.main_blockchain_id,
                    description = group.description,
                    icon = group.icon,
                    color = group.color,
                    privacy_level = group.privacy_level,
                    security_level = group.security_level,
                    created_at = group.created_at,
                    updated_at = group.updated_at
                )
            }
    }

    override fun getContactsByGroupId(groupId: String, limit: Int, offset: Int): Flow<List<ContactEntity>> {
        return dbQuery.getContactsByGroupId(groupId, limit.toLong(), offset.toLong())
            .asFlow()
            .mapToList(ioDispatcher)
            .map { contacts ->
                contacts.map { contact ->
                    mapToContactEntity(
                        id = contact.id,
                        name = contact.name,
                        notes = contact.notes,
                        solar_birthday = contact.solar_birthday,
                        lunar_birthday = contact.lunar_birthday,
                        is_sensitive = contact.is_sensitive,
                        security_level = contact.security_level,
                        privacy_display_mode = contact.privacy_display_mode,
                        auth_requirement = contact.auth_requirement,
                        created_at = contact.created_at,
                        updated_at = contact.updated_at,
                        last_viewed_at = contact.last_viewed_at,
                        sync_status = contact.sync_status,
                        encrypted_data = contact.encrypted_data
                    )
                }
            }
    }

    override suspend fun insertGroup(group: GroupEntity): String = withContext(ioDispatcher) {
        val id = group.id.ifBlank { uuid4().toString() }

        dbQuery.insertGroup(
            id = id,
            name = group.name,
            main_blockchain_id = group.mainBlockchainId,
            description = group.description,
            icon = group.icon,
            color = group.color,
            privacy_level = group.privacyLevel.name,
            security_level = group.securityLevel.name,
            created_at = group.createdAt.toEpochMilliseconds(),
            updated_at = group.updatedAt.toEpochMilliseconds()
        )

        return@withContext id
    }

    override suspend fun updateGroup(group: GroupEntity): Boolean = withContext(ioDispatcher) {
        dbQuery.updateGroup(
            name = group.name,
            main_blockchain_id = group.mainBlockchainId,
            description = group.description,
            icon = group.icon,
            color = group.color,
            privacy_level = group.privacyLevel.name,
            security_level = group.securityLevel.name,
            updated_at = group.updatedAt.toEpochMilliseconds(),
            id = group.id
        )

        return@withContext true
    }

    override suspend fun deleteGroup(id: String): Boolean = withContext(ioDispatcher) {
        dbQuery.transaction {
            // Xóa các liên kết group_wallet trước
            dbQuery.deleteGroupWalletsByGroupId(id)

            // Sau đó xóa group
            dbQuery.deleteGroup(id)
        }

        return@withContext true
    }
    
    override suspend fun findGroupByName(name: String): GroupEntity? = withContext(ioDispatcher) {
        dbQuery.findGroupByName(name).executeAsOneOrNull()?.let { group ->
            mapToGroupEntity(
                id = group.id,
                name = group.name,
                main_blockchain_id = group.main_blockchain_id,
                description = group.description,
                icon = group.icon,
                color = group.color,
                privacy_level = group.privacy_level,
                security_level = group.security_level,
                created_at = group.created_at,
                updated_at = group.updated_at
            )
        }
    }

    override suspend fun addContactToGroup(contactId: String, groupId: String, walletAddressId: String): Boolean = withContext(ioDispatcher) {
        val now = localDateTimeToMillis(localDateTimeNow())

        // Check if this specific wallet address is already in the group
        val count = dbQuery.countWalletInGroup(walletAddressId, groupId).executeAsOne().toInt()

        if (count > 0) {
            // Wallet address already exists in this group - don't add duplicate
            return@withContext false
        }

        // Add wallet to group
        dbQuery.addWalletToGroup(groupId, walletAddressId, contactId, now)

        return@withContext true
    }

    override suspend fun removeContactFromGroup(contactId: String, groupId: String): Boolean = withContext(ioDispatcher) {
        // Lấy wallet address ID trong group
        val walletAddress = dbQuery.getWalletAddressForContactInGroup(contactId, groupId).executeAsOneOrNull()
        if (walletAddress != null) {
            dbQuery.removeWalletFromGroup(walletAddress.id, groupId)
        }
        return@withContext true
    }

    override suspend fun contactInGroup(contactId: String, groupId: String): Boolean = withContext(ioDispatcher) {
        // Kiểm tra trong bảng group_wallet
        val walletAddresses = dbQuery.getWalletAddressesByContactId(contactId).executeAsList()
        for (walletAddress in walletAddresses) {
            val count = dbQuery.countWalletInGroup(walletAddress.id, groupId).executeAsOne().toInt()
            if (count > 0) {
                return@withContext true
            }
        }
        return@withContext false
    }

    override suspend fun countContactsByGroupId(groupId: String): Int = withContext(ioDispatcher) {
        dbQuery.countWalletsByGroupId(groupId).executeAsOne().toInt()
    }



    override suspend fun updateGroupMainBlockchain(
        groupId: String,
        mainBlockchainId: String
    ): Boolean = withContext(ioDispatcher) {
        dbQuery.updateGroupMainBlockchain(
            id = groupId,
            main_blockchain_id = mainBlockchainId,
            updated_at = localDateTimeToMillis(localDateTimeNow())
        )
        return@withContext true
    }

    override suspend fun getContactAddressByGroupId(
        groupId: String,
        limit: Int,
        offset: Int
    ): Flow<List<ContactModel>> {
        return dbQuery.getWalletAddressByGroupId(groupId, limit.toLong(), offset.toLong())
            .asFlow()
            .mapToList(ioDispatcher)
            .map { contacts ->
                contacts.map { contact ->
                    ContactModel(
                        contactId = contact.contact_id,
                        contactName = contact.contact_name,
                        walletAddress = contact.wallet_address,
                        walletAddressId = contact.wallet_address_id,
                        walletAlias = contact.wallet_alias.toString(),
                        blockchainName = contact.blockchain_name,
                        blockchainSymbol = contact.blockchain_symbol,
                        blockchainIcon = contact.blockchain_icon.toString(),
                        walletSensitive = contact.contact_is_sensitive ?: false,
                        isFavorite = false,
                        blockChainColor = contact.blockchain_color.toString(),
                        addedTime = contact.added_to_group_at,
                        avatar = contact.avatar,
                        isSensitive = contact.isSensitive == true
                    )
                }
            }
    }


    override suspend fun getContactsWithAddressesByGroupId(
        groupId: String,
        limit: Int,
        offset: Int
    ): Flow<PaginatedContactsResult> {
        // Using SQL queries defined in AddressBookDatabase.sq
        return dbQuery.getContactsByGroupIdPaginated(groupId, limit.toLong(), offset.toLong())
            .asFlow()
            .mapToList(ioDispatcher)
            .map { paginatedContacts ->

                // For each contact, fetch all addresses
                val contacts = paginatedContacts.map { contact ->
                    val contactId = contact.contact_id
                    val contactName = contact.contact_name

                    // Get all wallet addresses for this contact in this group
                    val addresses = dbQuery.getWalletAddressesForContactInGroup(contactId, groupId)
                        .executeAsList()
                        .map { address ->
                            AddressInfo(
                                walletAddressId = address.wallet_address_id,
                                walletAddress = address.wallet_address,
                                walletAlias = address.wallet_alias?.toString() ?: "",
                                walletSensitive = address.wallet_is_sensitive,
                                blockchainName = address.blockchain_name,
                                blockchainSymbol = address.blockchain_symbol,
                                blockchainIcon = address.blockchain_icon?.toString() ?: "",
                                blockChainColor = address.blockchain_color?.toString() ?: "",
                                addedTime = address.added_to_group_at
                            )
                        }

                    ContactWithAddressesModel(
                        contactId = contactId,
                        contactName = contactName,
                        isFavorite = false, // Would need another query to check this
                        addresses = addresses
                    )
                }
                
                // Get total number of contacts to determine if there are more to load
                val totalContacts = dbQuery.countDistinctContactsInGroup(groupId).executeAsOne().toInt()

                val hasMoreData = (offset + contacts.size) < totalContacts
                val nextOffset = if (hasMoreData) offset + limit else null
                

                PaginatedContactsResult(
                    contacts = contacts,
                    hasMoreData = hasMoreData,
                    nextOffset = nextOffset
                )
            }
    }

    override suspend fun clearAllGroups(): Boolean = withContext(ioDispatcher) {
        return@withContext try {
            dbQuery.clearAllGroups()
            true
        } catch (e: Exception) {
            false
        }
    }
}