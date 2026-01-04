package com.mangala.wallet.features.addressbook.data.model.enum

enum class AuthRequirement {
    NONE,
    BIOMETRIC,
    BIOMETRIC_PIN;

    companion object {
        /**
         * Chuyển đổi từ chuỗi sang AuthRequirement
         * @param value Chuỗi cần chuyển đổi
         * @return AuthRequirement tương ứng, NONE nếu không khớp
         */
        fun fromString(value: String?): AuthRequirement {
            return try {
                if (value == null) return NONE
                valueOf(value)
            } catch (e: IllegalArgumentException) {
                NONE
            }
        }
    }
}