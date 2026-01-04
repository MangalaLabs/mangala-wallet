package com.mangala.wallet.features.addressbook.data.local.contact

import app.cash.paging.PagingSource
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOneOrNull
import app.cash.sqldelight.paging3.QueryPagingSource
import com.benasher44.uuid.uuid4
import com.mangala.wallet.features.addressbook.data.local.AddressBookDatabaseWrapper
import com.mangala.wallet.features.addressbook.data.model.ContactModel
import com.mangala.wallet.features.addressbook.data.model.contact.ContactEntity
import com.mangala.wallet.features.addressbook.data.model.contact.EmailAddressEntity
import com.mangala.wallet.features.addressbook.data.model.contact.ImportantDateEntity
import com.mangala.wallet.features.addressbook.domain.model.CalendarType
import com.mangala.wallet.features.addressbook.data.model.contact.PhoneNumberEntity
import com.mangala.wallet.features.addressbook.data.model.contact.PhysicalAddressEntity
import com.mangala.wallet.features.addressbook.data.model.contact.RelatedNameEntity
import com.mangala.wallet.features.addressbook.data.model.contact.SocialProfileEntity
import com.mangala.wallet.features.addressbook.data.model.enum.AuthRequirement
import com.mangala.wallet.features.addressbook.data.model.enum.DisplayMode
import com.mangala.wallet.features.addressbook.data.model.enum.SecurityLevel
import com.mangala.wallet.features.addressbook.data.model.enum.SyncStatus
import com.mangala.wallet.features.addressbook.data.model.tag.TagEntity
import com.mangala.wallet.features.addressbook.database.SelectRecentContactsWithSearch
import com.mangala.wallet.utils.localDateTimeNow
import com.mangala.wallet.utils.localDateTimeToMillis
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate

/**
 * Implementation của ContactLocalDataSource sử dụng SQLDelight database
 */
class ContactLocalDataSourceImpl(
    databaseWrapper: AddressBookDatabaseWrapper,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) : ContactLocalDataSource {

    private val database = databaseWrapper.database
    private val dbQuery = database.addressBookDatabaseQueries

    /**
     * Chuyển đổi từ database Contact row sang ContactEntity
     */
    private fun mapToContactEntity(
        id: String,
        name: String,
        notes: String?,
        solar_birthday: String?,
        lunar_birthday: String?,
        is_sensitive: Boolean?,
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
            isSensitive = is_sensitive,
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
     * Chuyển đổi từ database PhoneNumber row sang PhoneNumberEntity
     */
    private fun mapToPhoneNumberEntity(
        id: String,
        contact_id: String,
        phone_number: String,
        label: String?,
        is_primary: Boolean?,
        created_at: Long,
        updated_at: Long,
    ): PhoneNumberEntity {
        return PhoneNumberEntity(
            id = id,
            contactId = contact_id,
            phoneNumber = phone_number,
            label = label,
            isPrimary = is_primary ?: false,
            createdAt = Instant.fromEpochMilliseconds(created_at),
            updatedAt = Instant.fromEpochMilliseconds(updated_at)
        )
    }

    /**
     * Chuyển đổi từ database EmailAddress row sang EmailAddressEntity
     */
    private fun mapToEmailAddressEntity(
        id: String,
        contact_id: String,
        email: String,
        label: String?,
        is_primary: Boolean?,
        created_at: Long,
        updated_at: Long,
    ): EmailAddressEntity {
        return EmailAddressEntity(
            id = id,
            contactId = contact_id,
            email = email,
            label = label,
            isPrimary = is_primary ?: false,
            createdAt = Instant.fromEpochMilliseconds(created_at),
            updatedAt = Instant.fromEpochMilliseconds(updated_at)
        )
    }

    /**
     * Chuyển đổi từ database PhysicalAddress row sang PhysicalAddressEntity
     */
    private fun mapToPhysicalAddressEntity(
        id: String,
        contact_id: String,
        street_address: String?,
        ward: String?,
        district: String?,
        city: String?,
        state_province: String?,
        postal_code: String?,
        country: String?,
        is_primary: Boolean?,
        address_type: String?,
        created_at: Long,
        updated_at: Long,
    ): PhysicalAddressEntity {
        return PhysicalAddressEntity(
            id = id,
            contactId = contact_id,
            streetAddress = street_address,
            ward = ward,
            district = district,
            city = city,
            stateProvince = state_province,
            postalCode = postal_code,
            country = country,
            isPrimary = is_primary ?: false,
            addressType = address_type,
            createdAt = Instant.fromEpochMilliseconds(created_at),
            updatedAt = Instant.fromEpochMilliseconds(updated_at)
        )
    }

    /**
     * Chuyển đổi từ database RelatedName row sang RelatedNameEntity
     */
    private fun mapToRelatedNameEntity(
        id: String,
        contact_id: String,
        name: String,
        relationship: String,
        created_at: Long,
        updated_at: Long,
    ): RelatedNameEntity {
        return RelatedNameEntity(
            id = id,
            contactId = contact_id,
            name = name,
            relationship = relationship,
            createdAt = Instant.fromEpochMilliseconds(created_at),
            updatedAt = Instant.fromEpochMilliseconds(updated_at)
        )
    }

    /**
     * Chuyển đổi từ database ImportantDate row sang ImportantDateEntity
     */
    private fun mapToImportantDateEntity(
        id: String,
        contact_id: String,
        date: Long,
        description: String,
        calendar_type: String?,
        created_at: Long,
        updated_at: Long,
    ): ImportantDateEntity {
        val calendarType = try {
            CalendarType.valueOf(calendar_type ?: "SOLAR")
        } catch (e: Exception) {
            CalendarType.SOLAR // Default fallback
        }
        
        return ImportantDateEntity(
            id = id,
            contactId = contact_id,
            date = Instant.fromEpochMilliseconds(date),
            description = description,
            calendarType = calendarType,
            createdAt = Instant.fromEpochMilliseconds(created_at),
            updatedAt = Instant.fromEpochMilliseconds(updated_at)
        )
    }

    /**
     * Chuyển đổi từ database SocialProfile row sang SocialProfileEntity
     */
    private fun mapToSocialProfileEntity(
        id: String,
        contact_id: String,
        platform: String,
        username: String,
        url: String?,
        created_at: Long,
        updated_at: Long,
    ): SocialProfileEntity {
        return SocialProfileEntity(
            id = id,
            contactId = contact_id,
            platform = platform,
            username = username,
            url = url,
            createdAt = Instant.fromEpochMilliseconds(created_at),
            updatedAt = Instant.fromEpochMilliseconds(updated_at)
        )
    }

    override suspend fun getContactById(id: String): ContactEntity? = withContext(ioDispatcher) {
        dbQuery.getContactById(id).executeAsOneOrNull()?.let { contact ->
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

    override fun getAllContacts(
        limit: Int,
        offset: Int,
        sortOrder: String,
    ): Flow<List<ContactEntity>> {
        return dbQuery.getAllContactsPaged(sortOrder, limit.toLong(), offset.toLong())
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

    override fun searchContacts(query: String, limit: Int, offset: Int): Flow<List<ContactEntity>> {
        return dbQuery.searchContacts(query, limit.toLong(), offset.toLong())
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

    override suspend fun insertContact(contact: ContactEntity): String = withContext(ioDispatcher) {
        val id = contact.id.ifBlank { uuid4().toString() }
        val now = Instant.fromEpochMilliseconds(localDateTimeToMillis(localDateTimeNow()))
        dbQuery.insertContact(
            id = id,
            name = contact.name,
            notes = contact.notes,
            solar_birthday = contact.solarBirthday?.toString(),
            lunar_birthday = contact.lunarBirthday?.toString(),
            is_sensitive = contact.isSensitive,
            security_level = contact.securityLevel.name,
            privacy_display_mode = contact.privacyDisplayMode.name,
            auth_requirement = contact.authRequirement.name,
            created_at = contact.createdAt?.toEpochMilliseconds() ?: now.toEpochMilliseconds(),
            updated_at = contact.updatedAt.toEpochMilliseconds(),
            last_viewed_at = contact.lastViewedAt?.toEpochMilliseconds(),
            sync_status = contact.syncStatus.name,
            encrypted_data = contact.encryptedData,
            avatar = contact.avatar
        )

        return@withContext id
    }

    override suspend fun updateContact(contact: ContactEntity): Boolean =
        withContext(ioDispatcher) {
            dbQuery.updateContact(
                name = contact.name,
                notes = contact.notes,
                solar_birthday = contact.solarBirthday?.toString(),
                lunar_birthday = contact.lunarBirthday?.toString(),
                is_sensitive = contact.isSensitive,
                security_level = contact.securityLevel.name,
                privacy_display_mode = contact.privacyDisplayMode.name,
                auth_requirement = contact.authRequirement.name,
                updated_at = contact.updatedAt.toEpochMilliseconds(),
                sync_status = contact.syncStatus.name,
                encrypted_data = contact.encryptedData,
                avatar = contact.avatar,
                id = contact.id
            )

            return@withContext true
        }

    override suspend fun deleteContact(id: String): Boolean = withContext(ioDispatcher) {
        // With CASCADE DELETE constraints in place, we only need to delete the contact
        // All related records will be automatically deleted by the database
        dbQuery.deleteContact(id)
        return@withContext true
    }

    override suspend fun updateLastViewedAt(id: String): Boolean = withContext(ioDispatcher) {
        val now = localDateTimeToMillis(localDateTimeNow())
        dbQuery.updateContactLastViewedAt(now, id)
        return@withContext true
    }

    override suspend fun getPhoneNumbersByContactId(contactId: String): List<PhoneNumberEntity> =
        withContext(ioDispatcher) {
            dbQuery.getPhoneNumbersByContactId(contactId)
                .executeAsList()
                .map { phone ->
                    mapToPhoneNumberEntity(
                        id = phone.id,
                        contact_id = phone.contact_id,
                        phone_number = phone.phone_number,
                        label = phone.label,
                        is_primary = phone.is_primary,
                        created_at = phone.created_at,
                        updated_at = phone.updated_at
                    )
                }
        }

    override suspend fun insertPhoneNumber(phoneNumber: PhoneNumberEntity): String =
        withContext(ioDispatcher) {
            val id = phoneNumber.id.ifBlank { uuid4().toString() }

            // Nếu đánh dấu là primary, cập nhật các số khác không phải primary
            if (phoneNumber.isPrimary) {
                dbQuery.transaction {
                    // Lấy các số điện thoại hiện tại trực tiếp từ database thay vì gọi hàm suspend
                    val existingNumbers =
                        dbQuery.getPhoneNumbersByContactId(phoneNumber.contactId).executeAsList()

                    // Cập nhật các số primary hiện có
                    existingNumbers.forEach { existingPhone ->
                        if (existingPhone.is_primary == true && existingPhone.id != id) {
                            // Xóa số cũ
                            dbQuery.deletePhoneNumber(existingPhone.id)

                            // Thêm lại với isPrimary = false
                            dbQuery.insertPhoneNumber(
                                id = existingPhone.id,
                                contact_id = existingPhone.contact_id,
                                phone_number = existingPhone.phone_number,
                                label = existingPhone.label,
                                is_primary = false,
                                created_at = existingPhone.created_at,
                                updated_at = localDateTimeToMillis(localDateTimeNow()) // Cập nhật thời gian hiện tại
                            )
                        }
                    }

                    // Thêm số mới
                    dbQuery.insertPhoneNumber(
                        id = id,
                        contact_id = phoneNumber.contactId,
                        phone_number = phoneNumber.phoneNumber,
                        label = phoneNumber.label,
                        is_primary = true, // Đảm bảo là primary
                        created_at = phoneNumber.createdAt.toEpochMilliseconds(),
                        updated_at = phoneNumber.updatedAt.toEpochMilliseconds()
                    )
                }
            } else {
                // Thêm số mới không phải primary
                dbQuery.insertPhoneNumber(
                    id = id,
                    contact_id = phoneNumber.contactId,
                    phone_number = phoneNumber.phoneNumber,
                    label = phoneNumber.label,
                    is_primary = phoneNumber.isPrimary,
                    created_at = phoneNumber.createdAt.toEpochMilliseconds(),
                    updated_at = phoneNumber.updatedAt.toEpochMilliseconds()
                )
            }

            return@withContext id
        }

    override suspend fun updatePhoneNumber(phoneNumber: PhoneNumberEntity): Boolean =
        withContext(ioDispatcher) {
            // Sử dụng delete và insert do SQLDelight chưa có hàm cập nhật
            dbQuery.transaction {
                // Nếu đánh dấu là primary, cập nhật các số khác không phải primary
                if (phoneNumber.isPrimary) {
                    // Truy vấn trực tiếp các số điện thoại thay vì gọi hàm suspend
                    val existingPhones =
                        dbQuery.getPhoneNumbersByContactId(phoneNumber.contactId).executeAsList()

                    existingPhones.forEach { existingPhone ->
                        if (existingPhone.is_primary == true && existingPhone.id != phoneNumber.id) {
                            // Xóa số cũ
                            dbQuery.deletePhoneNumber(existingPhone.id)

                            // Thêm lại với isPrimary = false
                            dbQuery.insertPhoneNumber(
                                id = existingPhone.id,
                                contact_id = existingPhone.contact_id,
                                phone_number = existingPhone.phone_number,
                                label = existingPhone.label,
                                is_primary = false,
                                created_at = existingPhone.created_at,
                                updated_at = localDateTimeToMillis(localDateTimeNow()) // Cập nhật thời gian hiện tại
                            )
                        }
                    }
                }

                // Cập nhật số hiện tại
                dbQuery.deletePhoneNumber(phoneNumber.id)
                dbQuery.insertPhoneNumber(
                    id = phoneNumber.id,
                    contact_id = phoneNumber.contactId,
                    phone_number = phoneNumber.phoneNumber,
                    label = phoneNumber.label,
                    is_primary = phoneNumber.isPrimary,
                    created_at = phoneNumber.createdAt.toEpochMilliseconds(),
                    updated_at = phoneNumber.updatedAt.toEpochMilliseconds()
                )
            }

            return@withContext true
        }

    override suspend fun deletePhoneNumber(id: String): Boolean = withContext(ioDispatcher) {
        dbQuery.deletePhoneNumber(id)
        return@withContext true
    }

    override suspend fun getEmailAddressesByContactId(contactId: String): List<EmailAddressEntity> =
        withContext(ioDispatcher) {
            dbQuery.getEmailAddressesByContactId(contactId)
                .executeAsList()
                .map { email ->
                    mapToEmailAddressEntity(
                        id = email.id,
                        contact_id = email.contact_id,
                        email = email.email,
                        label = email.label,
                        is_primary = email.is_primary,
                        created_at = email.created_at,
                        updated_at = email.updated_at
                    )
                }
        }

    override suspend fun insertEmailAddress(emailAddress: EmailAddressEntity): String =
        withContext(ioDispatcher) {
            val id = emailAddress.id.ifBlank { uuid4().toString() }

            // Nếu đánh dấu là primary, cập nhật các email khác không phải primary
            if (emailAddress.isPrimary) {
                dbQuery.transaction {
                    // Lấy email hiện có trực tiếp từ database thay vì gọi hàm suspend
                    val existingEmails =
                        dbQuery.getEmailAddressesByContactId(emailAddress.contactId).executeAsList()

                    existingEmails.forEach { existingEmail ->
                        if (existingEmail.is_primary == true && existingEmail.id != id) {
                            // Xóa email cũ
                            dbQuery.deleteEmailAddress(existingEmail.id)

                            // Thêm lại với isPrimary = false
                            dbQuery.insertEmailAddress(
                                id = existingEmail.id,
                                contact_id = existingEmail.contact_id,
                                email = existingEmail.email,
                                label = existingEmail.label,
                                is_primary = false,
                                created_at = existingEmail.created_at,
                                updated_at = localDateTimeToMillis(localDateTimeNow()) // Cập nhật thời gian hiện tại
                            )
                        }
                    }

                    // Thêm email mới
                    dbQuery.insertEmailAddress(
                        id = id,
                        contact_id = emailAddress.contactId,
                        email = emailAddress.email,
                        label = emailAddress.label,
                        is_primary = true, // Đảm bảo là primary
                        created_at = emailAddress.createdAt.toEpochMilliseconds(),
                        updated_at = emailAddress.updatedAt.toEpochMilliseconds()
                    )
                }
            } else {
                // Thêm email mới không phải primary
                dbQuery.insertEmailAddress(
                    id = id,
                    contact_id = emailAddress.contactId,
                    email = emailAddress.email,
                    label = emailAddress.label,
                    is_primary = emailAddress.isPrimary,
                    created_at = emailAddress.createdAt.toEpochMilliseconds(),
                    updated_at = emailAddress.updatedAt.toEpochMilliseconds()
                )
            }

            return@withContext id
        }

    override suspend fun updateEmailAddress(emailAddress: EmailAddressEntity): Boolean =
        withContext(ioDispatcher) {
            // Sử dụng delete và insert do SQLDelight chưa có hàm cập nhật
            dbQuery.transaction {
                // Nếu đánh dấu là primary, cập nhật các email khác không phải primary
                if (emailAddress.isPrimary) {
                    // Truy vấn trực tiếp thay vì gọi hàm suspend
                    val existingEmails =
                        dbQuery.getEmailAddressesByContactId(emailAddress.contactId).executeAsList()

                    existingEmails.forEach { existingEmail ->
                        if (existingEmail.is_primary == true && existingEmail.id != emailAddress.id) {
                            // Xóa email cũ
                            dbQuery.deleteEmailAddress(existingEmail.id)

                            // Thêm lại với isPrimary = false
                            dbQuery.insertEmailAddress(
                                id = existingEmail.id,
                                contact_id = existingEmail.contact_id,
                                email = existingEmail.email,
                                label = existingEmail.label,
                                is_primary = false,
                                created_at = existingEmail.created_at,
                                updated_at = localDateTimeToMillis(localDateTimeNow()) // Cập nhật thời gian
                            )
                        }
                    }
                }

                // Cập nhật email hiện tại
                dbQuery.deleteEmailAddress(emailAddress.id)
                dbQuery.insertEmailAddress(
                    id = emailAddress.id,
                    contact_id = emailAddress.contactId,
                    email = emailAddress.email,
                    label = emailAddress.label,
                    is_primary = emailAddress.isPrimary,
                    created_at = emailAddress.createdAt.toEpochMilliseconds(),
                    updated_at = emailAddress.updatedAt.toEpochMilliseconds()
                )
            }

            return@withContext true
        }

    override suspend fun deleteEmailAddress(id: String): Boolean = withContext(ioDispatcher) {
        dbQuery.deleteEmailAddress(id)
        return@withContext true
    }

    override suspend fun getPhysicalAddressesByContactId(contactId: String): List<PhysicalAddressEntity> =
        withContext(ioDispatcher) {
            dbQuery.getPhysicalAddressesByContactId(contactId)
                .executeAsList()
                .map { address ->
                    mapToPhysicalAddressEntity(
                        id = address.id,
                        contact_id = address.contact_id,
                        street_address = address.street_address,
                        ward = address.ward,
                        district = address.district,
                        city = address.city,
                        state_province = address.state_province,
                        postal_code = address.postal_code,
                        country = address.country,
                        is_primary = address.is_primary,
                        address_type = address.address_type,
                        created_at = address.created_at,
                        updated_at = address.updated_at
                    )
                }
        }

    override suspend fun insertPhysicalAddress(physicalAddress: PhysicalAddressEntity): String =
        withContext(ioDispatcher) {
            val id = physicalAddress.id.ifBlank { uuid4().toString() }

            // Tương tự như phoneNumber và emailAddress, xử lý primary flag
            if (physicalAddress.isPrimary) {
                dbQuery.transaction {
                    // Truy vấn trực tiếp thay vì gọi hàm suspend
                    val existingAddresses =
                        dbQuery.getPhysicalAddressesByContactId(physicalAddress.contactId)
                            .executeAsList()

                    existingAddresses.forEach { existingAddress ->
                        if (existingAddress.is_primary == true && existingAddress.id != id) {
                            // Cập nhật các địa chỉ khác không phải primary
                            dbQuery.deletePhysicalAddress(existingAddress.id)

                            // Thêm lại với isPrimary = false
                            dbQuery.insertPhysicalAddress(
                                id = existingAddress.id,
                                contact_id = existingAddress.contact_id,
                                street_address = existingAddress.street_address,
                                ward = existingAddress.ward,
                                district = existingAddress.district,
                                city = existingAddress.city,
                                state_province = existingAddress.state_province,
                                postal_code = existingAddress.postal_code,
                                country = existingAddress.country,
                                is_primary = false, // Đặt là false
                                address_type = existingAddress.address_type,
                                created_at = existingAddress.created_at,
                                updated_at = localDateTimeToMillis(localDateTimeNow()) // Cập nhật thời gian
                            )
                        }
                    }

                    // Thêm địa chỉ mới
                    dbQuery.insertPhysicalAddress(
                        id = id,
                        contact_id = physicalAddress.contactId,
                        street_address = physicalAddress.streetAddress,
                        ward = physicalAddress.ward,
                        district = physicalAddress.district,
                        city = physicalAddress.city,
                        state_province = physicalAddress.stateProvince,
                        postal_code = physicalAddress.postalCode,
                        country = physicalAddress.country,
                        is_primary = true, // Đảm bảo là primary
                        address_type = physicalAddress.addressType,
                        created_at = physicalAddress.createdAt.toEpochMilliseconds(),
                        updated_at = physicalAddress.updatedAt.toEpochMilliseconds()
                    )
                }
            } else {
                // Thêm địa chỉ mới không phải primary
                dbQuery.insertPhysicalAddress(
                    id = id,
                    contact_id = physicalAddress.contactId,
                    street_address = physicalAddress.streetAddress,
                    ward = physicalAddress.ward,
                    district = physicalAddress.district,
                    city = physicalAddress.city,
                    state_province = physicalAddress.stateProvince,
                    postal_code = physicalAddress.postalCode,
                    country = physicalAddress.country,
                    is_primary = physicalAddress.isPrimary,
                    address_type = physicalAddress.addressType,
                    created_at = physicalAddress.createdAt.toEpochMilliseconds(),
                    updated_at = physicalAddress.updatedAt.toEpochMilliseconds()
                )
            }

            return@withContext id
        }

    override suspend fun updatePhysicalAddress(physicalAddress: PhysicalAddressEntity): Boolean =
        withContext(ioDispatcher) {
            val now = localDateTimeToMillis(localDateTimeNow())

            dbQuery.transaction {
                // Update the physical address using existing query
                dbQuery.updatePhysicalAddress(
                    street_address = physicalAddress.streetAddress,
                    ward = physicalAddress.ward,
                    district = physicalAddress.district,
                    city = physicalAddress.city,
                    state_province = physicalAddress.stateProvince,
                    postal_code = physicalAddress.postalCode,
                    country = physicalAddress.country,
                    is_primary = physicalAddress.isPrimary,
                    address_type = physicalAddress.addressType,
                    updated_at = now,
                    id = physicalAddress.id
                )

                // If this is set as primary, reset other addresses of same contact to non-primary
                if (physicalAddress.isPrimary) {
                    // Get the physical address to determine the contact_id
                    val address =
                        dbQuery.getPhysicalAddressById(physicalAddress.id).executeAsOneOrNull()
                    if (address != null) {
                        // Clear primary status for all other addresses of this contact
                        dbQuery.clearPrimaryPhysicalAddressForContact(
                            now,
                            address.contact_id,
                            physicalAddress.id
                        )
                    }
                }
            }

            return@withContext true
        }

    override suspend fun markAddressAsPrimary(
        contactId: String,
        addressId: String,
    ): Boolean = withContext(ioDispatcher) {
        dbQuery.transaction {
            // Lấy địa chỉ hiện tại
            val currentAddress = dbQuery.getPhysicalAddressById(addressId).executeAsOneOrNull()
            val now = localDateTimeToMillis(localDateTimeNow())
            if (currentAddress != null) {
                // Đánh dấu địa chỉ hiện tại là primary
                dbQuery.markAddressAsPrimary(
                    now,
                    addressId
                )

                // Xóa primary cho các địa chỉ khác của cùng một contact
                dbQuery.clearPrimaryPhysicalAddressForContact(
                    localDateTimeToMillis(localDateTimeNow()),
                    contactId,
                    addressId
                )
            }
        }
        return@withContext true
    }

    override suspend fun deletePhysicalAddress(id: String): Boolean = withContext(ioDispatcher) {
        dbQuery.deletePhysicalAddress(id)
        return@withContext true
    }

    override suspend fun getRelatedNamesByContactId(contactId: String): List<RelatedNameEntity> =
        withContext(ioDispatcher) {
            dbQuery.getRelatedNamesByContactId(contactId)
                .executeAsList()
                .map { related ->
                    mapToRelatedNameEntity(
                        id = related.id,
                        contact_id = related.contact_id,
                        name = related.name,
                        relationship = related.relationship,
                        created_at = related.created_at,
                        updated_at = related.updated_at
                    )
                }
        }

    override suspend fun insertRelatedName(relatedName: RelatedNameEntity): String =
        withContext(ioDispatcher) {
            val id = relatedName.id.ifBlank { uuid4().toString() }

            dbQuery.transaction {
                // Thêm related name mới
                dbQuery.insertRelatedName(
                    id = id,
                    contact_id = relatedName.contactId,
                    name = relatedName.name,
                    relationship = relatedName.relationship,
                    created_at = relatedName.createdAt.toEpochMilliseconds(),
                    updated_at = relatedName.updatedAt.toEpochMilliseconds()
                )
            }

            return@withContext id
        }

    override suspend fun insertRelatedNamesBatch(relatedNames: List<RelatedNameEntity>): Map<RelatedNameEntity, String> =
        withContext(ioDispatcher) {
            val result = mutableMapOf<RelatedNameEntity, String>()

            dbQuery.transaction {
                relatedNames.forEach { relatedName ->
                    val id = relatedName.id.ifBlank { uuid4().toString() }

                    dbQuery.insertRelatedName(
                        id = id,
                        contact_id = relatedName.contactId,
                        name = relatedName.name,
                        relationship = relatedName.relationship,
                        created_at = relatedName.createdAt.toEpochMilliseconds(),
                        updated_at = relatedName.updatedAt.toEpochMilliseconds()
                    )

                    result[relatedName] = id
                }
            }

            return@withContext result
        }

    override suspend fun updateRelatedName(relatedName: RelatedNameEntity): Boolean =
        withContext(ioDispatcher) {
            val now = localDateTimeToMillis(localDateTimeNow())

            dbQuery.transaction {
                dbQuery.updateRelatedName(
                    name = relatedName.name,
                    relationship = relatedName.relationship,
                    updated_at = now,
                    id = relatedName.id
                )
            }

            return@withContext true
        }

    override suspend fun deleteRelatedName(id: String): Boolean = withContext(ioDispatcher) {
        dbQuery.deleteRelatedName(id)
        return@withContext true
    }

    override suspend fun getImportantDatesByContactId(contactId: String): List<ImportantDateEntity> =
        withContext(ioDispatcher) {
            dbQuery.getImportantDatesByContactId(contactId)
                .executeAsList()
                .map { date ->
                    mapToImportantDateEntity(
                        id = date.id,
                        contact_id = date.contact_id,
                        date = date.date,
                        description = date.description,
                        calendar_type = date.calendar_type,
                        created_at = date.created_at,
                        updated_at = date.updated_at
                    )
                }
        }

    override suspend fun insertImportantDate(importantDate: ImportantDateEntity): String =
        withContext(ioDispatcher) {
            val id = importantDate.id.ifBlank { uuid4().toString() }

            dbQuery.transaction {
                dbQuery.insertImportantDate(
                    id = id,
                    contact_id = importantDate.contactId,
                    date = importantDate.date.toEpochMilliseconds(),
                    description = importantDate.description,
                    calendar_type = importantDate.calendarType.name,
                    created_at = importantDate.createdAt.toEpochMilliseconds(),
                    updated_at = importantDate.updatedAt.toEpochMilliseconds()
                )
            }

            return@withContext id
        }

    override suspend fun updateImportantDate(importantDate: ImportantDateEntity): Boolean =
        withContext(ioDispatcher) {
            val now = localDateTimeToMillis(localDateTimeNow())
            dbQuery.transaction {
                dbQuery.updateImportantDate(
                    date = importantDate.date.toEpochMilliseconds(),
                    description = importantDate.description,
                    calendar_type = importantDate.calendarType.name,
                    updated_at = now,
                    id = importantDate.id
                )
            }
            return@withContext true
        }

    override suspend fun deleteImportantDate(id: String): Boolean = withContext(ioDispatcher) {
        dbQuery.deleteImportantDate(id)
        return@withContext true
    }

    override suspend fun getSocialProfilesByContactId(contactId: String): List<SocialProfileEntity> =
        withContext(ioDispatcher) {
            dbQuery.getSocialProfilesByContactId(contactId)
                .executeAsList()
                .map { profile ->
                    mapToSocialProfileEntity(
                        id = profile.id,
                        contact_id = profile.contact_id,
                        platform = profile.platform,
                        username = profile.username,
                        url = profile.url,
                        created_at = profile.created_at,
                        updated_at = profile.updated_at
                    )
                }
        }

    override suspend fun insertSocialProfile(socialProfile: SocialProfileEntity): String =
        withContext(ioDispatcher) {
            val id = socialProfile.id.ifBlank { uuid4().toString() }

            dbQuery.insertSocialProfile(
                id = id,
                contact_id = socialProfile.contactId,
                platform = socialProfile.platform,
                username = socialProfile.username,
                url = socialProfile.url,
                created_at = socialProfile.createdAt.toEpochMilliseconds(),
                updated_at = socialProfile.updatedAt.toEpochMilliseconds()
            )

            return@withContext id
        }

    override suspend fun updateSocialProfile(socialProfile: SocialProfileEntity): Boolean =
        withContext(ioDispatcher) {
            dbQuery.updateSocialProfile(
                platform = socialProfile.platform,
                username = socialProfile.username,
                url = socialProfile.url,
                updated_at = socialProfile.updatedAt.toEpochMilliseconds(),
                id = socialProfile.id
            )

            return@withContext true
        }

    override suspend fun deleteSocialProfile(id: String): Boolean = withContext(ioDispatcher) {
        dbQuery.deleteSocialProfile(id)
        return@withContext true
    }

    override suspend fun deleteSocialProfilesByContactId(contactId: String): Boolean = withContext(ioDispatcher) {
        dbQuery.deleteSocialProfileByContactId(contactId)
        return@withContext true
    }

    override suspend fun isContactFavorite(contactId: String): Boolean = withContext(ioDispatcher) {
        val result = dbQuery.isContactFavorite(contactId).executeAsOne()
        return@withContext result
    }

    override suspend fun addFavorite(contactId: String): Boolean =
        withContext(ioDispatcher) {
            val id = uuid4().toString()
            val now = localDateTimeToMillis(localDateTimeNow())
            // Get the current maximum display order
            val maxOrder = dbQuery.getMaxDisplayOrderFromFavorites().executeAsOne()

            dbQuery.addFavorite(
                id = id,
                contact_id = contactId,
                display_order = maxOrder + 1,
                created_at = now,
                updated_at = now
            )

            return@withContext true
        }

    override suspend fun removeFavorite(contactId: String): Boolean = withContext(ioDispatcher) {
        dbQuery.removeFavorite(contactId)
        return@withContext true
    }

    override suspend fun getRecentContacts(limit: Int): List<ContactEntity> =
        withContext(ioDispatcher) {
            dbQuery.getRecentContacts(limit.toLong())
                .executeAsList()
                .map { contact ->
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

    override suspend fun countAllContacts(): Int = withContext(ioDispatcher) {
        dbQuery.countAllContacts().executeAsOne().toInt()
    }

    override suspend fun findContactByEmail(email: String): ContactEntity? =
        withContext(ioDispatcher) {
            dbQuery.findContactByEmail(email).executeAsOneOrNull()?.let { contact ->
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

    override suspend fun findContactByPhoneNumber(phoneNumber: String): ContactEntity? =
        withContext(ioDispatcher) {
            dbQuery.findContactByPhoneNumber(phoneNumber).executeAsOneOrNull()?.let { contact ->
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
    
    override suspend fun findContactByName(name: String): ContactEntity? =
        withContext(ioDispatcher) {
            dbQuery.findContactByName(name).executeAsOneOrNull()?.let { contact ->
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

    override suspend fun insertEmailAddressesBatch(emailAddresses: List<EmailAddressEntity>): Map<EmailAddressEntity, String> =
        withContext(ioDispatcher) {
            val result = mutableMapOf<EmailAddressEntity, String>()

            dbQuery.transaction {
                emailAddresses.forEach { email ->
                    val id = email.id.ifBlank { uuid4().toString() }

                    dbQuery.insertEmailAddress(
                        id = id,
                        contact_id = email.contactId,
                        email = email.email,
                        label = email.label,
                        is_primary = email.isPrimary,
                        created_at = email.createdAt.toEpochMilliseconds(),
                        updated_at = email.updatedAt.toEpochMilliseconds()
                    )

                    result[email] = id
                }
            }

            return@withContext result
        }

    override suspend fun insertPhoneNumbersBatch(phoneNumbers: List<PhoneNumberEntity>): Map<PhoneNumberEntity, String> =
        withContext(ioDispatcher) {
            val result = mutableMapOf<PhoneNumberEntity, String>()

            dbQuery.transaction {
                phoneNumbers.forEach { phone ->
                    val id = phone.id.ifBlank { uuid4().toString() }

                    dbQuery.insertPhoneNumber(
                        id = id,
                        contact_id = phone.contactId,
                        phone_number = phone.phoneNumber,
                        label = phone.label,
                        is_primary = phone.isPrimary,
                        created_at = phone.createdAt.toEpochMilliseconds(),
                        updated_at = phone.updatedAt.toEpochMilliseconds()
                    )

                    result[phone] = id
                }
            }

            return@withContext result
        }

    override suspend fun insertSocialProfilesBatch(socialProfiles: List<SocialProfileEntity>): Map<SocialProfileEntity, String> =
        withContext(ioDispatcher) {
            val result = mutableMapOf<SocialProfileEntity, String>()

            dbQuery.transaction {
                socialProfiles.forEach { profile ->
                    val id = profile.id.ifBlank { uuid4().toString() }

                    dbQuery.insertSocialProfile(
                        id = id,
                        contact_id = profile.contactId,
                        platform = profile.platform,
                        username = profile.username,
                        url = profile.url,
                        created_at = profile.createdAt.toEpochMilliseconds(),
                        updated_at = profile.updatedAt.toEpochMilliseconds()
                    )

                    result[profile] = id
                }
            }

            return@withContext result
        }


    override suspend fun insertImportantDatesBatch(importantDates: List<ImportantDateEntity>): Map<ImportantDateEntity, String> =
        withContext(ioDispatcher) {
            val result = mutableMapOf<ImportantDateEntity, String>()

            dbQuery.transaction {
                importantDates.forEach { date ->
                    val id = date.id.ifBlank { uuid4().toString() }

                    dbQuery.insertImportantDate(
                        id = id,
                        contact_id = date.contactId,
                        date = date.date.toEpochMilliseconds(),
                        description = date.description,
                        calendar_type = date.calendarType.name,
                        created_at = date.createdAt.toEpochMilliseconds(),
                        updated_at = date.updatedAt.toEpochMilliseconds()
                    )

                    result[date] = id
                }
            }

            return@withContext result
        }

    override suspend fun insertPhysicalAddressesBatch(physicalAddresses: List<PhysicalAddressEntity>): Map<PhysicalAddressEntity, String> =
        withContext(ioDispatcher) {
            val result = mutableMapOf<PhysicalAddressEntity, String>()

            dbQuery.transaction {
                physicalAddresses.forEach { address ->
                    val id = address.id.ifBlank { uuid4().toString() }

                    dbQuery.insertPhysicalAddress(
                        id = id,
                        contact_id = address.contactId,
                        street_address = address.streetAddress,
                        ward = address.ward,
                        district = address.district,
                        city = address.city,
                        state_province = address.stateProvince,
                        postal_code = address.postalCode,
                        country = address.country,
                        is_primary = address.isPrimary,
                        address_type = address.addressType,
                        created_at = address.createdAt.toEpochMilliseconds(),
                        updated_at = address.updatedAt.toEpochMilliseconds()
                    )

                    result[address] = id
                }
            }

            return@withContext result
        }

    override suspend fun filterContacts(
        query: String,
        tagIds: List<String>,
        groupIds: List<String>,
        blockchainIds: List<String>,
        onlyFavorites: Boolean,
        sortOrder: String,
        limit: Int,
        offset: Int,
    ): List<ContactModel> = withContext(ioDispatcher) {
        dbQuery.filterContacts(
            query = query,
            onlyFavorites = if (onlyFavorites) 1L else 0L,
            hasTagFilters = if (tagIds.isEmpty()) 0L else 1L,
            tagIds = tagIds,
            hasGroupFilters = if (groupIds.isEmpty()) 0L else 1L,
            groupIds = groupIds,
            hasBlockchainFilters = if (blockchainIds.isEmpty()) 0L else 1L,
            blockchainIds = blockchainIds,
            sortOrder = sortOrder,
            limit = limit.toLong(),
            offset = offset.toLong(),
            checkTagId = null,
        ).executeAsList().map { contact ->
            ContactModel(
                contactId = contact.contact_id,
                contactName = contact.contact_name,
                walletAddress = contact.wallet_address.toString(),
                walletAlias = contact.wallet_alias.toString(),
                walletAddressId = contact.wallet_address_id.toString(),
                blockchainName = contact.blockchain_name.toString(),
                blockchainSymbol = contact.blockchain_symbol.toString(),
                blockchainIcon = contact.blockchain_icon.toString(),
                walletSensitive = contact.is_sensitive,
                isFavorite = contact.is_favorite == 1L,
                blockChainColor = contact.blockchain_color.toString(),
                isSensitive = contact.isSensitive == true,
                avatar = contact.avatar,
                privacyDisplayMode = DisplayMode.fromString(contact.privacy_display_mode)
            )
        }
    }


    override suspend fun getContactsByGroupIdIn(groupIds: List<String>): List<ContactEntity> =
        withContext(ioDispatcher) {
            dbQuery.getContactsByGroupIdIn(
                groupIds,
                5,
                0
            ).executeAsList().map { contact ->
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

    override fun getContactRecentTransactionPagingSource(
        searchQuery: String?,
        statuses: List<String>,
    ): PagingSource<Int, SelectRecentContactsWithSearch> {
//        return ContactRecentTransactionPagingSource(
//            dbQuery = dbQuery,
//            searchQuery = searchQuery,
//            statuses = statuses,
//            ioDispatcher = ioDispatcher
//        )
        return QueryPagingSource(
            countQuery = dbQuery.countTransactions(),
            transacter = dbQuery,
            context = ioDispatcher,
            queryProvider = { limit, offset ->
                dbQuery.selectRecentContactsWithSearch(
                    statuses = statuses,
                    searchQuery = searchQuery,
                    limit = limit,
                    offset = offset
                )
            }
        )
    }

    override fun getContactsPagingSource(
        searchQuery: String?,
        tagIds: List<String>?,
        checkTagId: String?,
        isFavoriteOnly: Boolean,
    ): PagingSource<Int, ContactModel> {
        return ContactsPagingSource(
            dbQuery = dbQuery,
            searchQuery = searchQuery,
            tagIds = tagIds,
            checkTagId = checkTagId,
            isFavoriteOnly = isFavoriteOnly,
            ioDispatcher = ioDispatcher
        )
    }

    override suspend fun getFavoriteContactsFlow(
        limit: Int,
        offset: Int,
    ): Flow<List<ContactModel>> = withContext(ioDispatcher) {
        dbQuery.getFavoritesContactsWithSearch(searchQuery = null, limit.toLong(), offset.toLong())
            .asFlow()
            .mapToList(ioDispatcher)
            .map { favoriteContacts ->
                favoriteContacts.map { favoriteContact ->
                    ContactModel(
                        contactId = favoriteContact.contact_id,
                        contactName = favoriteContact.contact_name,
                        walletAddress = favoriteContact.wallet_address.toString(),
                        walletAlias = favoriteContact.wallet_alias.toString(),
                        walletAddressId = favoriteContact.wallet_address_id.toString(),
                        blockchainName = favoriteContact.blockchain_name.toString(),
                        blockchainSymbol = favoriteContact.blockchain_symbol.toString(),
                        blockchainIcon = favoriteContact.blockchain_icon.toString(),
                        walletSensitive = favoriteContact.is_sensitive,
                        isFavorite = true,
                        blockChainColor = favoriteContact.blockchain_color.toString(),
                        isSensitive = favoriteContact.isSensitive == true,
                        avatar = favoriteContact.avatar,
                        privacyDisplayMode = DisplayMode.fromString(favoriteContact.privacy_display_mode)
                    )
                }
            }
    }

    override fun observeContactById(id: String): Flow<ContactEntity?> {
        return dbQuery.getContactById(id)
            .asFlow()
            .mapToOneOrNull(ioDispatcher)
            .map { contact ->
                contact?.let {
                    mapToContactEntity(
                        id = it.id,
                        name = it.name,
                        notes = it.notes,
                        solar_birthday = it.solar_birthday,
                        lunar_birthday = it.lunar_birthday,
                        is_sensitive = it.is_sensitive,
                        security_level = it.security_level,
                        privacy_display_mode = it.privacy_display_mode,
                        auth_requirement = it.auth_requirement,
                        created_at = it.created_at,
                        updated_at = it.updated_at,
                        last_viewed_at = it.last_viewed_at,
                        sync_status = it.sync_status,
                        encrypted_data = it.encrypted_data
                    )
                }
            }
    }

    override fun getTagsByContactId(contactId: String): List<TagEntity> {
        return dbQuery.getTagsByContactId(contactId)
            .executeAsList()
            .map { tag ->
                TagEntity(
                    id = tag.id,
                    name = tag.name,
                    color = tag.color,
                    createdAt = Instant.fromEpochMilliseconds(tag.created_at),
                    updatedAt = Instant.fromEpochMilliseconds(tag.updated_at),
                )
            }
    }

    override suspend fun clearAllContacts(): Boolean = withContext(ioDispatcher) {
        return@withContext try {
            dbQuery.clearAllContacts()
            true
        } catch (e: Exception) {
            false
        }
    }
}