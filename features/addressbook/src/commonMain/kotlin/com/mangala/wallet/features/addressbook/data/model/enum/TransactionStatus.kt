package com.mangala.wallet.features.addressbook.data.model.enum

enum class TransactionStatus {
    DRAFT,
    PENDING,
    CONFIRMED,
    FAILED;

    companion object {
        /**
         * Chuyển đổi từ chuỗi sang TransactionStatus
         * @param value Chuỗi cần chuyển đổi
         * @return TransactionStatus tương ứng, PENDING nếu không khớp
         */
        fun fromString(value: String?): TransactionStatus {
            return try {
                if (value == null) return PENDING
                valueOf(value)
            } catch (e: IllegalArgumentException) {
                PENDING
            }
        }
    }
}