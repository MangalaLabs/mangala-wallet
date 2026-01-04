package com.mangala.wallet.features.addressbook.data.model.enum

enum class SyncStatus {
    SYNCED,
    LOCAL_ONLY,
    PENDING;

    companion object {
        /**
         * Chuyển đổi từ chuỗi sang SyncStatus
         * @param value Chuỗi cần chuyển đổi
         * @return SyncStatus tương ứng, LOCAL_ONLY nếu không khớp
         */
        fun fromString(value: String?): SyncStatus {
            return try {
                if (value == null) return LOCAL_ONLY
                valueOf(value)
            } catch (e: IllegalArgumentException) {
                LOCAL_ONLY
            }
        }
    }
}