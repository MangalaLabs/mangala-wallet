package com.mangala.wallet.features.addressbook.data.local.security

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.benasher44.uuid.uuid4
import com.mangala.wallet.features.addressbook.data.local.AddressBookDatabaseWrapper
import com.mangala.wallet.features.addressbook.data.model.contact.ContactEntity
import com.mangala.wallet.features.addressbook.data.model.enum.AuthRequirement
import com.mangala.wallet.features.addressbook.data.model.enum.DisplayMode
import com.mangala.wallet.features.addressbook.data.model.enum.SecurityLevel
import com.mangala.wallet.features.addressbook.data.model.enum.SyncStatus
import com.mangala.wallet.features.addressbook.data.model.security.SecurityAuditLogEntity
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
 * Implementation của SecurityLocalDataSource sử dụng SQLDelight database
 */
class SecurityLocalDataSourceImpl(
    databaseWrapper: AddressBookDatabaseWrapper,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : SecurityLocalDataSource {

    private val database = databaseWrapper.database
    private val dbQuery = database.addressBookDatabaseQueries

    /**
     * Chuyển đổi từ database SecurityAuditLog row sang SecurityAuditLogEntity
     */
    private fun mapToSecurityAuditLogEntity(
        id: String,
        contact_id: String?,
        wallet_address_id: String?,
        action: String,
        details: String?,
        device_info: String?,
        timestamp: Long
    ): SecurityAuditLogEntity {
        return SecurityAuditLogEntity(
            id = id,
            contactId = contact_id,
            walletAddressId = wallet_address_id,
            action = action,
            details = details,
            deviceInfo = device_info,
            timestamp = Instant.fromEpochMilliseconds(timestamp)
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
        is_sensitive: Boolean,
        security_level: String,
        privacy_display_mode: String,
        auth_requirement: String,
        created_at: Long,
        updated_at: Long,
        last_viewed_at: Long?,
        sync_status: String,
        encrypted_data: String?
    ): ContactEntity {
        return ContactEntity(
            id = id,
            name = name,
            notes = notes,
            solarBirthday = solar_birthday?.let { LocalDate.parse(it) },
            lunarBirthday = lunar_birthday?.let { LocalDate.parse(it) },
            isSensitive = is_sensitive,
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
     * Lấy thông tin thiết bị hiện tại
     * @return Chuỗi chứa thông tin thiết bị
     */
    private suspend fun getDeviceInfo(): String = withContext(ioDispatcher) {
        // Trong môi trường thực tế, điều này sẽ lấy thông tin như loại thiết bị, phiên bản OS, v.v.
        return@withContext "Unknown Device"
    }

    override suspend fun getAuditLogById(id: String): SecurityAuditLogEntity? = withContext(ioDispatcher) {
        dbQuery.getAuditLogById(id).executeAsOneOrNull()?.let { log ->
            mapToSecurityAuditLogEntity(
                id = log.id,
                contact_id = log.contact_id,
                wallet_address_id = log.wallet_address_id,
                action = log.action,
                details = log.details,
                device_info = log.device_info,
                timestamp = log.timestamp
            )
        }
    }

    override fun getAuditLogsByContactId(contactId: String, limit: Int, offset: Int): Flow<List<SecurityAuditLogEntity>> {
        return dbQuery.getAuditLogsByContactId(contactId, limit.toLong(), offset.toLong())
            .asFlow()
            .mapToList(ioDispatcher)
            .map { logs ->
                logs.map { log ->
                    mapToSecurityAuditLogEntity(
                        id = log.id,
                        contact_id = log.contact_id,
                        wallet_address_id = log.wallet_address_id,
                        action = log.action,
                        details = log.details,
                        device_info = log.device_info,
                        timestamp = log.timestamp
                    )
                }
            }
    }

    override fun getAllAuditLogs(limit: Int, offset: Int): Flow<List<SecurityAuditLogEntity>> {
        // Giả định có query getAllAuditLogs trong database
        // Trả về Flow rỗng nếu chưa triển khai
        return flow { emit(emptyList()) }
    }

    override fun getAuditLogsByAction(action: String, limit: Int, offset: Int): Flow<List<SecurityAuditLogEntity>> {
        // Giả định có query getAuditLogsByAction trong database
        // Trả về Flow rỗng nếu chưa triển khai
        return flow { emit(emptyList()) }
    }

    override suspend fun insertAuditLog(securityAuditLog: SecurityAuditLogEntity): String = withContext(ioDispatcher) {
        val id = securityAuditLog.id.ifBlank { uuid4().toString() }

        // Thêm audit log vào database
        // Giả định có phương thức insertAuditLog trong database
        /*
        dbQuery.insertAuditLog(
            id = id,
            contact_id = securityAuditLog.contactId,
            wallet_address_id = securityAuditLog.walletAddressId,
            action = securityAuditLog.action,
            details = securityAuditLog.details,
            device_info = securityAuditLog.deviceInfo,
            timestamp = securityAuditLog.timestamp.toEpochMilliseconds()
        )
        */

        return@withContext id
    }

    override suspend fun deleteAuditLog(id: String): Boolean = withContext(ioDispatcher) {
        // Xóa audit log từ database
        // Giả định có phương thức deleteAuditLog trong database
        // dbQuery.deleteAuditLog(id)

        return@withContext true
    }

    override suspend fun purgeOldAuditLogs(timestamp: Long): Int = withContext(ioDispatcher) {
        // Xóa các audit logs cũ hơn timestamp
        // Giả định có phương thức purgeOldAuditLogs trong database
        // val result = dbQuery.purgeOldAuditLogs(timestamp).executeAsOne()

        return@withContext 0
    }

    override suspend fun logCreate(contactId: String, details: String?): String = withContext(ioDispatcher) {
        val id = uuid4().toString()
        val now = localDateTimeToMillis(localDateTimeNow())
        val deviceInfo = getDeviceInfo()

        // Thêm audit log với action = CREATE
        val auditLog = SecurityAuditLogEntity(
            id = id,
            contactId = contactId,
            walletAddressId = null,
            action = SecurityAuditLogEntity.ACTION_CREATE,
            details = details,
            deviceInfo = deviceInfo,
            timestamp = Instant.fromEpochMilliseconds(now)
        )

        return@withContext insertAuditLog(auditLog)
    }

    override suspend fun logView(contactId: String, details: String?): String = withContext(ioDispatcher) {
        val id = uuid4().toString()
        val now = localDateTimeToMillis(localDateTimeNow())
        val deviceInfo = getDeviceInfo()

        // Thêm audit log với action = VIEW
        val auditLog = SecurityAuditLogEntity(
            id = id,
            contactId = contactId,
            walletAddressId = null,
            action = SecurityAuditLogEntity.ACTION_VIEW,
            details = details,
            deviceInfo = deviceInfo,
            timestamp = Instant.fromEpochMilliseconds(now)
        )

        return@withContext insertAuditLog(auditLog)
    }

    override suspend fun logEdit(contactId: String, details: String?): String = withContext(ioDispatcher) {
        val id = uuid4().toString()
        val now = localDateTimeToMillis(localDateTimeNow())
        val deviceInfo = getDeviceInfo()

        // Thêm audit log với action = EDIT
        val auditLog = SecurityAuditLogEntity(
            id = id,
            contactId = contactId,
            walletAddressId = null,
            action = SecurityAuditLogEntity.ACTION_EDIT,
            details = details,
            deviceInfo = deviceInfo,
            timestamp = Instant.fromEpochMilliseconds(now)
        )

        return@withContext insertAuditLog(auditLog)
    }

    override suspend fun logDelete(contactId: String, details: String?): String = withContext(ioDispatcher) {
        val id = uuid4().toString()
        val now = localDateTimeToMillis(localDateTimeNow())
        val deviceInfo = getDeviceInfo()

        // Thêm audit log với action = DELETE
        val auditLog = SecurityAuditLogEntity(
            id = id,
            contactId = contactId,
            walletAddressId = null,
            action = SecurityAuditLogEntity.ACTION_DELETE,
            details = details,
            deviceInfo = deviceInfo,
            timestamp = Instant.fromEpochMilliseconds(now)
        )

        return@withContext insertAuditLog(auditLog)
    }

    override suspend fun logExport(contactId: String?, details: String?): String = withContext(ioDispatcher) {
        val id = uuid4().toString()
        val now = localDateTimeToMillis(localDateTimeNow())
        val deviceInfo = getDeviceInfo()

        // Thêm audit log với action = EXPORT
        val auditLog = SecurityAuditLogEntity(
            id = id,
            contactId = contactId,
            walletAddressId = null,
            action = SecurityAuditLogEntity.ACTION_EXPORT,
            details = details,
            deviceInfo = deviceInfo,
            timestamp = Instant.fromEpochMilliseconds(now)
        )

        return@withContext insertAuditLog(auditLog)
    }

    override suspend fun logViewSensitive(contactId: String, details: String?): String = withContext(ioDispatcher) {
        val id = uuid4().toString()
        val now = localDateTimeToMillis(localDateTimeNow())
        val deviceInfo = getDeviceInfo()

        // Thêm audit log với action = VIEW_SENSITIVE
        val auditLog = SecurityAuditLogEntity(
            id = id,
            contactId = contactId,
            walletAddressId = null,
            action = SecurityAuditLogEntity.ACTION_VIEW_SENSITIVE,
            details = details,
            deviceInfo = deviceInfo,
            timestamp = Instant.fromEpochMilliseconds(now)
        )

        return@withContext insertAuditLog(auditLog)
    }

    override suspend fun logPrivacyChange(enabled: Boolean, details: String?): String = withContext(ioDispatcher) {
        val id = uuid4().toString()
        val now = localDateTimeToMillis(localDateTimeNow())
        val deviceInfo = getDeviceInfo()

        // Thêm audit log với action = PRIVACY_CHANGE
        val enhancedDetails = "Privacy mode ${if (enabled) "enabled" else "disabled"}. ${details ?: ""}"
        val auditLog = SecurityAuditLogEntity(
            id = id,
            contactId = null,
            walletAddressId = null,
            action = SecurityAuditLogEntity.ACTION_PRIVACY_CHANGE,
            details = enhancedDetails,
            deviceInfo = deviceInfo,
            timestamp = Instant.fromEpochMilliseconds(now)
        )

        return@withContext insertAuditLog(auditLog)
    }

    override suspend fun logAuthFailure(contactId: String?, details: String?): String = withContext(ioDispatcher) {
        val id = uuid4().toString()
        val now = localDateTimeToMillis(localDateTimeNow())
        val deviceInfo = getDeviceInfo()

        // Thêm audit log với action = AUTH_FAILURE
        val auditLog = SecurityAuditLogEntity(
            id = id,
            contactId = contactId,
            walletAddressId = null,
            action = SecurityAuditLogEntity.ACTION_AUTH_FAILURE,
            details = details,
            deviceInfo = deviceInfo,
            timestamp = Instant.fromEpochMilliseconds(now)
        )

        return@withContext insertAuditLog(auditLog)
    }

    override suspend fun getSensitiveContacts(): List<ContactEntity> = withContext(ioDispatcher) {
        // Lấy danh sách contacts có is_sensitive = true
        // Giả định query có sẵn trong database
        // Trả về danh sách rỗng nếu chưa triển khai
        return@withContext emptyList()
    }

    override suspend fun encryptData(data: String): String = withContext(ioDispatcher) {
        // Trong môi trường thực tế, điều này sẽ sử dụng một thư viện mã hóa
        // Đây chỉ là một ví dụ giả định
        return@withContext "encrypted:$data"
    }

    override suspend fun decryptData(encryptedData: String): String = withContext(ioDispatcher) {
        // Trong môi trường thực tế, điều này sẽ sử dụng một thư viện giải mã
        // Đây chỉ là một ví dụ giả định
        if (encryptedData.startsWith("encrypted:")) {
            return@withContext encryptedData.substring("encrypted:".length)
        }
        return@withContext encryptedData
    }
}