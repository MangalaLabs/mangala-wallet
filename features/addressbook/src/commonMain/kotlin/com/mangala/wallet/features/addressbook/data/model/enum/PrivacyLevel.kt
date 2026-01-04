package com.mangala.wallet.features.addressbook.data.model.enum

enum class PrivacyLevel {
    PUBLIC,
    PRIVATE,
    SECRET;


    companion object {
        /**
         * Chuyển đổi từ chuỗi sang PrivacyLevel
         * @param value Chuỗi cần chuyển đổi
         * @return PrivacyLevel tương ứng, PUBLIC nếu không khớp
         */
        fun fromString(value: String?): PrivacyLevel {
            return try {
                if (value == null) return PUBLIC
                valueOf(value)
            } catch (e: IllegalArgumentException) {
                PUBLIC
            }
        }
    }
}