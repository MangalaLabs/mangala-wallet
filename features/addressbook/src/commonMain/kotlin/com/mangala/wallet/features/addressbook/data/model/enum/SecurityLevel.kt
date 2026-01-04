package com.mangala.wallet.features.addressbook.data.model.enum

enum class SecurityLevel {
    NORMAL,
    HIGH,
    MAXIMUM;

    companion object {
        /**
         * Chuyển đổi từ chuỗi sang SecurityLevel
         * @param value Chuỗi cần chuyển đổi
         * @return SecurityLevel tương ứng, NORMAL nếu không khớp
         */
        fun fromString(value: String?): SecurityLevel {
            return try {
                if (value == null) return NORMAL
                valueOf(value)
            } catch (e: IllegalArgumentException) {
                NORMAL
            }
        }
    }
}