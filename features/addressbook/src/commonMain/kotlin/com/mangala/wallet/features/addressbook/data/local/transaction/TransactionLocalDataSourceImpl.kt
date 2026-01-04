package com.mangala.wallet.features.addressbook.data.local.transaction

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.benasher44.uuid.uuid4
import com.mangala.wallet.features.addressbook.data.local.AddressBookDatabaseWrapper
import com.mangala.wallet.features.addressbook.data.model.TransactionDetailModel
import com.mangala.wallet.features.addressbook.data.model.blockchain.BlockchainTypeEntity
import com.mangala.wallet.features.addressbook.data.model.contact.ContactEntity
import com.mangala.wallet.features.addressbook.data.model.enum.AuthRequirement
import com.mangala.wallet.features.addressbook.data.model.enum.DisplayMode
import com.mangala.wallet.features.addressbook.data.model.enum.SecurityLevel
import com.mangala.wallet.features.addressbook.data.model.enum.SyncStatus
import com.mangala.wallet.features.addressbook.data.model.enum.TransactionStatus
import com.mangala.wallet.features.addressbook.data.model.transaction.TransactionHistoryEntity
import com.mangala.wallet.utils.ext.toBoolean
import com.mangala.wallet.utils.ext.toLong
import com.mangala.wallet.utils.localDateTimeNow
import com.mangala.wallet.utils.localDateTimeToMillis
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate

/**
 * Implementation của TransactionLocalDataSource sử dụng SQLDelight database
 */
class TransactionLocalDataSourceImpl(
    databaseWrapper: AddressBookDatabaseWrapper,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) : TransactionLocalDataSource {

    private val database = databaseWrapper.database
    private val dbQuery = database.addressBookDatabaseQueries

    /**
     * Chuyển đổi từ database TransactionHistory row sang TransactionHistoryEntity
     */
    private fun mapToTransactionHistoryEntity(
        id: String,
        from_address: String,
        to_address: String,
        blockchain_type_id: String,
        amount: String,
        token_symbol: String,
        transaction_hash: String,
        status: String,
        timestamp: Long,
        fee: String?,
        note: String?,
        isFromImportedWallet: Long,
    ): TransactionHistoryEntity {
        return TransactionHistoryEntity(
            id = id,
            fromAddress = from_address,
            toAddress = to_address,
            blockchainTypeId = blockchain_type_id,
            amount = amount,
            tokenSymbol = token_symbol,
            transactionHash = transaction_hash,
            status = TransactionStatus.fromString(status),
            timestamp = Instant.fromEpochMilliseconds(timestamp),
            fee = fee,
            note = note,
            isFromImportedWallet = isFromImportedWallet.toBoolean()
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
        encrypted_data: String?,
        avatar: String? = null,
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
            encryptedData = encrypted_data,
            avatar = avatar
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
        is_active: Boolean?,  // Thay đổi từ Boolean sang Boolean?
        created_at: Long,
        updated_at: Long,
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

    override suspend fun getTransactionById(id: String): TransactionHistoryEntity? = withContext(ioDispatcher) {
        dbQuery.getTransactionById(id).executeAsOneOrNull()?.let { transaction ->
            mapToTransactionHistoryEntity(
                id = transaction.id,
                from_address = transaction.from_address,
                to_address = transaction.to_address,
                blockchain_type_id = transaction.blockchain_type_id,
                amount = transaction.amount,
                token_symbol = transaction.token_symbol,
                transaction_hash = transaction.transaction_hash,
                status = transaction.status,
                timestamp = transaction.timestamp,
                fee = transaction.fee,
                note = transaction.note,
                isFromImportedWallet = transaction.is_from_imported_wallet
            )
        }

    }

    override suspend fun getTransactionDetailById(id: String): TransactionDetailModel? = withContext(ioDispatcher) {
        val transaction = getTransactionById(id) ?: return@withContext null

        // Lấy blockchain type
        val blockchainTypeDeferred = async(ioDispatcher) {
            dbQuery.getBlockchainTypeById(transaction.blockchainTypeId).executeAsOneOrNull()?.let { blockchain ->
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

        // Tìm từ contact_transactions
        val fromContactDeferred = async(ioDispatcher) {
            dbQuery.findContactByWalletAddress(transaction.fromAddress).executeAsList().firstOrNull()?.let { contact ->
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
                    encrypted_data = contact.encrypted_data,
                    avatar = contact.avatar
                )
            }
        }

        val toContactDeferred = async(ioDispatcher) {
            dbQuery.findContactByWalletAddress(transaction.toAddress).executeAsList().firstOrNull()?.let { contact ->
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
                    encrypted_data = contact.encrypted_data,
                    avatar = contact.avatar
                )
            }
        }
        val blockchainType = blockchainTypeDeferred.await()
        val fromContact = fromContactDeferred.await()
        val toContact = toContactDeferred.await()

        return@withContext TransactionDetailModel(
            transaction = transaction,
            blockchainType = blockchainType ?: throw IllegalStateException("Blockchain type not found for transaction: $id"),
            fromContact = fromContact,
            toContact = toContact
        )
    }

    override fun getTransactionHistoryByContactId(contactId: String, limit: Int, offset: Int): Flow<List<TransactionHistoryEntity>> {
        return dbQuery.getTransactionHistoryByContactId(contactId, limit.toLong(), offset.toLong())
            .asFlow()
            .mapToList(ioDispatcher)
            .map { transactions ->
                transactions.map { transaction ->
                    mapToTransactionHistoryEntity(
                        id = transaction.id,
                        from_address = transaction.from_address,
                        to_address = transaction.to_address,
                        blockchain_type_id = transaction.blockchain_type_id,
                        amount = transaction.amount,
                        token_symbol = transaction.token_symbol,
                        transaction_hash = transaction.transaction_hash,
                        status = transaction.status,
                        timestamp = transaction.timestamp,
                        fee = transaction.fee,
                        note = transaction.note,
                        isFromImportedWallet = transaction.is_from_imported_wallet
                    )
                }
            }
    }

    override fun getTransactionHistoryByWalletAddress(address: String, limit: Int, offset: Int): Flow<List<TransactionHistoryEntity>> {
        return dbQuery.getTransactionHistoryByWalletAddress(address, address, limit.toLong(), offset.toLong())
            .asFlow()
            .mapToList(ioDispatcher)
            .map { transactions ->
                transactions.map { transaction ->
                    mapToTransactionHistoryEntity(
                        id = transaction.id,
                        from_address = transaction.from_address,
                        to_address = transaction.to_address,
                        blockchain_type_id = transaction.blockchain_type_id,
                        amount = transaction.amount,
                        token_symbol = transaction.token_symbol,
                        transaction_hash = transaction.transaction_hash,
                        status = transaction.status,
                        timestamp = transaction.timestamp,
                        fee = transaction.fee,
                        note = transaction.note,
                        isFromImportedWallet = transaction.is_from_imported_wallet
                    )
                }
            }
    }

    override fun getTransactionDetailsByContactId(contactId: String, limit: Int, offset: Int): Flow<List<TransactionDetailModel>> {
        return dbQuery.getTransactionHistoryByContactId(contactId, limit.toLong(), offset.toLong())
            .asFlow()
            .mapToList(ioDispatcher)
            .map { transactions ->
                transactions.mapNotNull { transaction ->
                    getTransactionDetailById(transaction.id)
                }
            }
    }

    override suspend fun insertTransaction(transaction: TransactionHistoryEntity): String = withContext(ioDispatcher) {
        val id = transaction.id.ifBlank { uuid4().toString() }

        dbQuery.transaction {
            dbQuery.insertTransactionHistory(
                id = id,
                fromAddress = transaction.fromAddress,
                toAddress = transaction.toAddress,
                blockchainTypeId = transaction.blockchainTypeId,
                amount = transaction.amount,
                tokenSymbol = transaction.tokenSymbol,
                transactionHash = transaction.transactionHash,
                status = transaction.status.toString(),
                timestamp = transaction.timestamp.toEpochMilliseconds(),
                fee = transaction.fee,
                note = transaction.note,
                isFromImportedWallet = transaction.isFromImportedWallet.toLong()
            )
        }

        return@withContext id
    }

    override suspend fun updateTransaction(transaction: TransactionHistoryEntity): Boolean = withContext(ioDispatcher) {
        dbQuery.transaction {
            // Cập nhật transaction trong bảng transaction_history
            // Giả định có query thích hợp trong database
            // dbQuery.updateTransaction(...)
        }

        return@withContext true
    }

    override suspend fun deleteTransaction(id: String): Boolean = withContext(ioDispatcher) {
        dbQuery.transaction {
            // Xóa các liên kết contact_transactions trước
            // Giả định có query thích hợp trong database
            // dbQuery.deleteContactTransactionsByTransactionId(id)

            // Sau đó xóa transaction
            // dbQuery.deleteTransaction(id)
        }

        return@withContext true
    }

    override suspend fun linkTransactionToContact(contactId: String, transactionId: String, walletAddressId: String, isSender: Boolean): Boolean =
        withContext(ioDispatcher) {
            // Generate deterministic ID to prevent duplicates
            val id = "${contactId}_${transactionId}_${walletAddressId}_${isSender}"
            val now = localDateTimeToMillis(localDateTimeNow())

            dbQuery.transaction {
                dbQuery.insertContactTransaction(id, contactId, transactionId, walletAddressId, isSender, now)
            }

            return@withContext true
        }

    override suspend fun getPendingTransactions(): List<TransactionHistoryEntity> = withContext(ioDispatcher) {
        dbQuery.getPendingTransactions()
            .executeAsList()
            .map { transaction ->
                mapToTransactionHistoryEntity(
                    id = transaction.id,
                    from_address = transaction.from_address,
                    to_address = transaction.to_address,
                    blockchain_type_id = transaction.blockchain_type_id,
                    amount = transaction.amount,
                    token_symbol = transaction.token_symbol,
                    transaction_hash = transaction.transaction_hash,
                    status = transaction.status,
                    timestamp = transaction.timestamp,
                    fee = transaction.fee,
                    note = transaction.note,
                    isFromImportedWallet = transaction.is_from_imported_wallet
                )
            }
    }

    override suspend fun clearAllTransactionHistory(): Boolean = withContext(ioDispatcher) {
        return@withContext try {
            dbQuery.clearAllTransactionHistory()
            true
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun clearAllOfflineQueue(): Boolean = withContext(ioDispatcher) {
        return@withContext try {
            dbQuery.clearAllOfflineQueue()
            true
        } catch (e: Exception) {
            false
        }
    }
}