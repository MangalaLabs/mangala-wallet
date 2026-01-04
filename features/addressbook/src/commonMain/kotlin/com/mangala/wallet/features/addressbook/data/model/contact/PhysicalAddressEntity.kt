package com.mangala.wallet.features.addressbook.data.model.contact

import com.benasher44.uuid.uuid4
import com.mangala.wallet.utils.localDateTimeNow
import com.mangala.wallet.utils.localDateTimeToMillis
import kotlinx.datetime.Instant

/**
 * Entity cho địa chỉ vật lý của một contact
 * Tương ứng với bảng 'physical_addresses' trong database
 */
data class PhysicalAddressEntity(
    val id: String, // UUID
    val contactId: String,
    val streetAddress: String?,
    val ward: String?,
    val district: String?,
    val city: String?,
    val stateProvince: String?,
    val postalCode: String?,
    val country: String?,
    val isPrimary: Boolean,
    val addressType: String?, // Ví dụ: "Home", "Work", "Mailing"
    val createdAt: Instant,
    val updatedAt: Instant
) {
    /**
     * Trả về địa chỉ đầy đủ định dạng theo một dòng
     * @return Chuỗi địa chỉ đầy đủ
     */
    fun getFormattedAddress(): String {
        val parts = mutableListOf<String>()

        streetAddress?.takeIf { it.isNotBlank() }?.let { parts.add(it) }
        ward?.takeIf { it.isNotBlank() }?.let { parts.add(it) }
        district?.takeIf { it.isNotBlank() }?.let { parts.add(it) }
        city?.takeIf { it.isNotBlank() }?.let { parts.add(it) }
        stateProvince?.takeIf { it.isNotBlank() }?.let { parts.add(it) }
        postalCode?.takeIf { it.isNotBlank() }?.let { parts.add(it) }
        country?.takeIf { it.isNotBlank() }?.let { parts.add(it) }

        return parts.joinToString(", ")
    }

    /**
     * Kiểm tra liệu địa chỉ có đủ thông tin cần thiết không
     * @return true nếu có đủ thông tin cơ bản
     */
    fun isComplete(): Boolean {
        // Định nghĩa một địa chỉ cơ bản phải có street, city và country
        return !streetAddress.isNullOrBlank() &&
                (!city.isNullOrBlank() || !district.isNullOrBlank()) &&
                !country.isNullOrBlank()
    }

    companion object {
        /**
         * Tạo một đối tượng PhysicalAddressEntity mới
         */
        fun create(
            id: String = uuid4().toString(), // UUID được tạo từ repository
            contactId: String,
            streetAddress: String? = null,
            ward: String? = null,
            district: String? = null,
            city: String? = null,
            stateProvince: String? = null,
            postalCode: String? = null,
            country: String? = null,
            isPrimary: Boolean = true,
            addressType: String? = null
        ): PhysicalAddressEntity {
            val now = Instant.fromEpochMilliseconds(localDateTimeToMillis(localDateTimeNow()))
            return PhysicalAddressEntity(
                id = id,
                contactId = contactId,
                streetAddress = streetAddress,
                ward = ward,
                district = district,
                city = city,
                stateProvince = stateProvince,
                postalCode = postalCode,
                country = country,
                isPrimary = isPrimary,
                addressType = addressType,
                createdAt = now,
                updatedAt = now
            )
        }
    }
}