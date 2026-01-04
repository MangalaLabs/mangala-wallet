package com.mangala.wallet.features.addressbook.data.model.enum

enum class DisplayMode {
    FULL,    // Public - Show full information
    HIDDEN,  // Private - Hide sensitive information
    SECRET;  // Secret - Completely hidden from normal view

    companion object {
        /**
         * Chuyển đổi từ chuỗi sang DisplayMode
         * @param value Chuỗi cần chuyển đổi
         * @return DisplayMode tương ứng, FULL nếu không khớp
         */
        fun fromString(value: String?): DisplayMode {
            return try {
                if (value == null) return FULL
                valueOf(value)
            } catch (e: IllegalArgumentException) {
                FULL
            }
        }
    }
}