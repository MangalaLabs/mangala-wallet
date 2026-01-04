package com.mangala.wallet.features.addressbook.data.local.blockchain

import com.benasher44.uuid.uuid4
import com.mangala.wallet.features.addressbook.data.local.AddressBookDatabaseWrapper
import com.mangala.wallet.features.addressbook.data.model.blockchain.BlockchainTypeEntity
import com.mangala.wallet.features.addressbook.data.model.blockchain.TokenInformationEntity
import com.mangala.wallet.features.addressbook.data.model.blockchain.WalletAddressEntity
import com.mangala.wallet.features.addressbook.data.model.contact.ContactEntity
import com.mangala.wallet.features.addressbook.data.model.enum.AuthRequirement
import com.mangala.wallet.features.addressbook.data.model.enum.DisplayMode
import com.mangala.wallet.features.addressbook.data.model.enum.SecurityLevel
import com.mangala.wallet.features.addressbook.data.model.enum.SyncStatus
import com.mangala.wallet.utils.localDateNow
import com.mangala.wallet.utils.localDateTimeNow
import com.mangala.wallet.utils.localDateTimeToMillis
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate

/**
 * Implementation của BlockchainLocalDataSource sử dụng SQLDelight database
 */
class BlockchainLocalDataSourceImpl(
    databaseWrapper: AddressBookDatabaseWrapper,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : BlockchainLocalDataSource {

    private val database = databaseWrapper.database
    private val dbQuery = database.addressBookDatabaseQueries

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
        is_active: Boolean?,  // Thay đổi từ Boolean sang Boolean?
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
        is_sensitive: Boolean?,  // Thay đổi từ Boolean sang Boolean?
        is_primary: Boolean?,  // Thay đổi từ Boolean sang Boolean?
        is_verified: Boolean?,  // Thay đổi từ Boolean sang Boolean?
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
     * Chuyển đổi từ database TokenInformation row sang TokenInformationEntity
     */
    private fun mapToTokenInformationEntity(
        id: String,
        blockchain_type_id: String,
        token_name: String,
        token_symbol: String,
        contract_address: String?,
        icon: String?,
        decimals: Long,
        is_native: Boolean?,  // Thay đổi từ Boolean sang Boolean?
        created_at: Long,
        updated_at: Long
    ): TokenInformationEntity {
        return TokenInformationEntity(
            id = id,
            blockchainTypeId = blockchain_type_id,
            tokenName = token_name,
            tokenSymbol = token_symbol,
            contractAddress = contract_address,
            icon = icon,
            decimals = decimals.toInt(),
            isNative = is_native ?: false,  // Xử lý null
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
        is_sensitive: Boolean?,  // Thay đổi từ Boolean sang Boolean?
        security_level: String?,  // Thay đổi từ String sang String?
        privacy_display_mode: String?,  // Thay đổi từ String sang String?
        auth_requirement: String?,  // Thay đổi từ String sang String?
        created_at: Long,
        updated_at: Long,
        last_viewed_at: Long?,
        sync_status: String?,  // Thay đổi từ String sang String?
        encrypted_data: String?
    ): ContactEntity {
        return ContactEntity(
            id = id,
            name = name,
            notes = notes,
            solarBirthday = solar_birthday?.let { LocalDate.parse(it) },
            lunarBirthday = lunar_birthday?.let { LocalDate.parse(it) },
            isSensitive = is_sensitive ?: false,  // Xử lý null
            securityLevel = SecurityLevel.fromString(security_level),  // fromString() đã xử lý null
            privacyDisplayMode = DisplayMode.fromString(privacy_display_mode),  // fromString() đã xử lý null
            authRequirement = AuthRequirement.fromString(auth_requirement),  // fromString() đã xử lý null
            createdAt = Instant.fromEpochMilliseconds(created_at),
            updatedAt = Instant.fromEpochMilliseconds(updated_at),
            lastViewedAt = last_viewed_at?.let { Instant.fromEpochMilliseconds(it) },
            syncStatus = SyncStatus.fromString(sync_status),  // fromString() đã xử lý null
            encryptedData = encrypted_data
        )
    }

    override suspend fun getBlockchainTypeById(id: String): BlockchainTypeEntity? =
        withContext(ioDispatcher) {
            dbQuery.getBlockchainTypeById(id).executeAsOneOrNull()?.let { row ->
                mapToBlockchainTypeEntity(
                    id = row.id,
                    name = row.name,
                    symbol = row.symbol,
                    address_format = row.address_format,
                    validation_regex = row.validation_regex,
                    icon = row.icon,
                    color = row.color,
                    network_type = row.network_type,
                    is_active = row.is_active,
                    created_at = row.created_at,
                    updated_at = row.updated_at
                )
            }
        }

    override suspend fun getAllBlockchainTypes(): List<BlockchainTypeEntity> =
        withContext(ioDispatcher) {
            dbQuery.getAllBlockchainTypes()
                .executeAsList()
                .map { row ->
                    mapToBlockchainTypeEntity(
                        id = row.id,
                        name = row.name,
                        symbol = row.symbol,
                        address_format = row.address_format,
                        validation_regex = row.validation_regex,
                        icon = row.icon,
                        color = row.color,
                        network_type = row.network_type,
                        is_active = row.is_active,
                        created_at = row.created_at,
                        updated_at = row.updated_at
                    )
                }
        }

    override suspend fun insertBlockchainType(blockchainType: BlockchainTypeEntity): String =
        withContext(ioDispatcher) {
            val id = blockchainType.id.ifBlank { uuid4().toString() }

            // Thêm blockchain type mới
            // Giả định có phương thức insertBlockchainType trong database
            dbQuery.transaction {
                dbQuery.insertBlockchainType(
                    id = id,
                    name = blockchainType.name,
                    symbol = blockchainType.symbol,
                    address_format = blockchainType.addressFormat,
                    validation_regex = blockchainType.validationRegex,
                    icon = blockchainType.icon,
                    color = blockchainType.color,
                    network_type = blockchainType.networkType,
                    is_active = blockchainType.isActive,
                    created_at = blockchainType.createdAt.toEpochMilliseconds(),
                    updated_at = blockchainType.updatedAt.toEpochMilliseconds()
                )
            }

            return@withContext id
        }

    override suspend fun updateBlockchainType(blockchainType: BlockchainTypeEntity): Boolean =
        withContext(ioDispatcher) {
            try {
                dbQuery.transaction {
                    dbQuery.updateBlockchainType(
                        name = blockchainType.name,
                        symbol = blockchainType.symbol,
                        address_format = blockchainType.addressFormat,
                        validation_regex = blockchainType.validationRegex,
                        icon = blockchainType.icon,
                        color = blockchainType.color,
                        network_type = blockchainType.networkType,
                        is_active = blockchainType.isActive,
                        updated_at = blockchainType.updatedAt.toEpochMilliseconds(),
                        id = blockchainType.id
                    )
                }
                return@withContext true
            } catch (e: Exception) {
                return@withContext false
            }
        }

    override suspend fun deleteBlockchainType(id: String): Boolean = withContext(ioDispatcher) {
        try {
            dbQuery.transaction {
                // Xóa tất cả token information liên quan đến blockchain type này
                dbQuery.deleteTokenInformationByBlockchainTypeId(id)

                // Cuối cùng xóa blockchain type
                dbQuery.deleteBlockchainType(id)
            }
            return@withContext true
        } catch (e: Exception) {
            return@withContext false
        }
    }

    override suspend fun getWalletAddressesByContactId(contactId: String): List<WalletAddressEntity> =
        withContext(ioDispatcher) {
            dbQuery.getWalletAddressesByContactId(contactId)
                .executeAsList()
                .map { row ->
                    mapToWalletAddressEntity(
                        id = row.id,
                        contact_id = row.contact_id,
                        blockchain_type_id = row.blockchain_type_id,
                        address = row.address,
                        alias = row.alias,
                        wallet_type = row.wallet_type,
                        is_sensitive = row.is_sensitive,
                        is_primary = row.is_primary,
                        is_verified = row.is_verified,
                        verified_date = row.verified_date,
                        created_at = row.created_at,
                        updated_at = row.updated_at
                    )
                }
        }

    override suspend fun getWalletAddressesByBlockchainTypeId(blockchainTypeId: String): List<WalletAddressEntity> =
        withContext(ioDispatcher) {
            // Sửa: Thay thế dbQuery.getWalletAddressesByBlockchainTypeId bằng một query có sẵn
            // Giả sử có truy vấn getWalletAddressesByBlockchainType thay vì getWalletAddressesByBlockchainTypeId

            dbQuery.getWalletAddressesByBlockchainType(blockchainTypeId, Long.MAX_VALUE, 0)
                .executeAsList()
                .map { row ->
                    mapToWalletAddressEntity(
                        id = row.id,
                        contact_id = row.contact_id,
                        blockchain_type_id = row.blockchain_type_id,
                        address = row.address,
                        alias = row.alias,
                        wallet_type = row.wallet_type,
                        is_sensitive = row.is_sensitive,
                        is_primary = row.is_primary,
                        is_verified = row.is_verified,
                        verified_date = row.verified_date,
                        created_at = row.created_at,
                        updated_at = row.updated_at
                    )
                }
        }

    override suspend fun insertWalletAddress(walletAddress: WalletAddressEntity): String =
        withContext(ioDispatcher) {
            val id = walletAddress.id.ifBlank { uuid4().toString() }

            // Nếu đánh dấu là primary, cập nhật các wallet address khác không phải primary
            if (walletAddress.isPrimary) {
                dbQuery.transaction {
                    // Sửa: Thay vì gọi hàm suspend, truy cập trực tiếp database
                    // Đặt tất cả các wallet address của liên hệ và blockchain này thành non-primary
                    dbQuery.clearPrimaryWalletForContactAndBlockchainType(
                        localDateTimeToMillis(localDateTimeNow()),
                        walletAddress.contactId,
                        walletAddress.blockchainTypeId
                    )

                    // Thêm wallet address mới
                    dbQuery.insertWalletAddress(
                        id = id,
                        contact_id = walletAddress.contactId,
                        blockchain_type_id = walletAddress.blockchainTypeId,
                        address = walletAddress.address,
                        alias = walletAddress.alias,
                        wallet_type = walletAddress.walletType,
                        is_sensitive = walletAddress.isSensitive,
                        is_primary = true,
                        is_verified = walletAddress.isVerified,
                        verified_date = walletAddress.verifiedDate?.toString(),
                        created_at = walletAddress.createdAt.toEpochMilliseconds(),
                        updated_at = walletAddress.updatedAt.toEpochMilliseconds()
                    )
                }
            } else {
                // Thêm wallet address mới không phải primary
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
        }

    override suspend fun updateWalletAddress(walletAddress: WalletAddressEntity): Boolean =
        withContext(ioDispatcher) {
            // Sử dụng delete và insert do SQLDelight chưa có hàm cập nhật riêng
            dbQuery.transaction {
                // Nếu đánh dấu là primary, cập nhật các wallet address khác không phải primary
                if (walletAddress.isPrimary) {
                    // Sửa: Thay vì gọi hàm suspend, truy cập trực tiếp database
                    dbQuery.clearPrimaryWalletForContactAndBlockchainType(
                        localDateTimeToMillis(localDateTimeNow()),
                        walletAddress.contactId,
                        walletAddress.blockchainTypeId
                    )
                }

                // Cập nhật wallet address hiện tại
                dbQuery.deleteWalletAddress(walletAddress.id)
                dbQuery.insertWalletAddress(
                    id = walletAddress.id,
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

            return@withContext true
        }

    override suspend fun deleteWalletAddress(id: String): Boolean = withContext(ioDispatcher) {
        dbQuery.deleteWalletAddress(id)
        return@withContext true
    }

    override suspend fun markWalletAddressAsPrimary(walletAddressId: String): Boolean =
        withContext(ioDispatcher) {
            // Sửa: Thay thế dbQuery.getWalletAddressById bằng truy vấn có sẵn
            // Giả sử chúng ta có truy vấn từ database để lấy wallet address theo ID
            // Ví dụ: getWalletAddressesByContactId hoặc có thể tạo một truy vấn mới

            val walletAddress = dbQuery.getWalletAddressById(walletAddressId).executeAsOneOrNull()
                ?: return@withContext false

            // Tạo query để cập nhật là primary
            dbQuery.transaction {
                // Đặt tất cả wallet address khác của cùng contact và blockchain thành non-primary
                dbQuery.clearPrimaryWalletForContactAndBlockchainType(
                    localDateTimeToMillis(localDateTimeNow()),
                    walletAddress.contact_id,
                    walletAddress.blockchain_type_id
                )

                // Cập nhật wallet address hiện tại thành primary
                dbQuery.updateWalletAddressPrimaryStatus(
                    is_primary = true,
                    updated_at = localDateTimeToMillis(localDateTimeNow()),
                    id = walletAddressId
                )
            }

            return@withContext true
        }

    override suspend fun verifyWalletAddress(walletAddressId: String): Boolean =
        withContext(ioDispatcher) {
            try {
                // Kiểm tra xem wallet address có tồn tại không
                val exists =
                    dbQuery.getWalletAddressById(walletAddressId).executeAsOneOrNull() != null
                if (!exists) {
                    return@withContext false
                }

                // Tạo query để cập nhật trạng thái verified
                val today = localDateNow().toString()

                dbQuery.transaction {
                    // Cập nhật wallet address hiện tại thành verified
                    dbQuery.updateWalletAddressVerificationStatus(
                        is_verified = true,
                        verified_date = today,
                        updated_at = localDateTimeToMillis(localDateTimeNow()),
                        id = walletAddressId
                    )
                }
                true
            } catch (e: Exception) {
                false
            }
        }

    override suspend fun findContactByWalletAddress(address: String): ContactEntity? =
        withContext(ioDispatcher) {
            dbQuery.findContactByWalletAddress(address).executeAsList().firstOrNull()?.let { row ->
                mapToContactEntity(
                    id = row.id,
                    name = row.name,
                    notes = row.notes,
                    solar_birthday = row.solar_birthday,
                    lunar_birthday = row.lunar_birthday,
                    is_sensitive = row.is_sensitive,
                    security_level = row.security_level,
                    privacy_display_mode = row.privacy_display_mode,
                    auth_requirement = row.auth_requirement,
                    created_at = row.created_at,
                    updated_at = row.updated_at,
                    last_viewed_at = row.last_viewed_at,
                    sync_status = row.sync_status,
                    encrypted_data = row.encrypted_data
                )
            }
        }

    override suspend fun getTokenInformationById(id: String): TokenInformationEntity? =
        withContext(ioDispatcher) {
            dbQuery.getTokenInformationById(
                id
            ).executeAsOneOrNull()?.let { row ->
                mapToTokenInformationEntity(
                    id = row.id,
                    blockchain_type_id = row.blockchain_type_id,
                    token_name = row.token_name,
                    token_symbol = row.token_symbol,
                    contract_address = row.contract_address,
                    icon = row.icon,
                    decimals = row.decimals,
                    is_native = row.is_native,
                    created_at = row.created_at,
                    updated_at = row.updated_at
                )
            }
            return@withContext null
        }

    override suspend fun getTokenInformationByBlockchainType(blockchainTypeId: String): List<TokenInformationEntity> =
        withContext(ioDispatcher) {
            // Giả định có phương thức getTokenInformationByBlockchainType trong database
            // Trả về kết quả giả
            dbQuery.getTokenInformationByBlockchainType(
                blockchainTypeId
            ).executeAsList().map { row ->
                mapToTokenInformationEntity(
                    id = row.id,
                    blockchain_type_id = row.blockchain_type_id,
                    token_name = row.token_name,
                    token_symbol = row.token_symbol,
                    contract_address = row.contract_address,
                    icon = row.icon,
                    decimals = row.decimals,
                    is_native = row.is_native,
                    created_at = row.created_at,
                    updated_at = row.updated_at
                )
            }

            return@withContext emptyList()
        }

    override suspend fun getNativeTokenForBlockchain(blockchainTypeId: String): TokenInformationEntity? =
        withContext(ioDispatcher) {
            // Lấy token là native token của blockchain
            val query = """
            SELECT * FROM token_information 
            WHERE blockchain_type_id = ? AND is_native = 1
            LIMIT 1
        """

            // Giả định có phương thức getNativeTokenForBlockchain trong database
            // Trả về kết quả giả
            return@withContext null
        }

    override suspend fun insertTokenInformation(tokenInformation: TokenInformationEntity): String =
        withContext(ioDispatcher) {
            val id = tokenInformation.id.ifBlank { uuid4().toString() }
            dbQuery.transaction {
                dbQuery.insertTokenInformation(
                    id = id,
                    blockchain_type_id = tokenInformation.blockchainTypeId,
                    token_name = tokenInformation.tokenName,
                    token_symbol = tokenInformation.tokenSymbol,
                    contract_address = tokenInformation.contractAddress,
                    icon = tokenInformation.icon,
                    decimals = tokenInformation.decimals.toLong(),
                    is_native = tokenInformation.isNative,
                    created_at = tokenInformation.createdAt.toEpochMilliseconds(),
                    updated_at = tokenInformation.updatedAt.toEpochMilliseconds()
                )
            }

            return@withContext id
        }

    override suspend fun updateTokenInformation(tokenInformation: TokenInformationEntity): Boolean =
        withContext(ioDispatcher) {
            // Giả định có phương thức updateTokenInformation trong database

            return@withContext true
        }

    override suspend fun deleteTokenInformation(id: String): Boolean = withContext(ioDispatcher) {
        // Giả định có phương thức deleteTokenInformation trong database

        return@withContext true
    }

    override suspend fun deleteTokenInformationByBlockchainTypeId(blockchainTypeId: String): Boolean =
        withContext(ioDispatcher) {
            dbQuery.deleteTokenInformationByBlockchainTypeId(
                blockchainTypeId
            )
            return@withContext true
        }

    override suspend fun validateAddress(address: String, blockchainTypeId: String): Boolean =
        withContext(ioDispatcher) {
            // Lấy blockchain type
            val blockchainType = getBlockchainTypeById(blockchainTypeId) ?: return@withContext false

            // Nếu không có regex, chấp nhận tất cả địa chỉ không rỗng
            if (blockchainType.validationRegex.isNullOrEmpty()) {
                return@withContext address.isNotBlank()
            }

            // Kiểm tra theo regex
            try {
                return@withContext address.matches(blockchainType.validationRegex.toRegex())
            } catch (e: Exception) {
                // Nếu regex không hợp lệ, chấp nhận tất cả địa chỉ không rỗng
                return@withContext address.isNotBlank()
            }
        }
}