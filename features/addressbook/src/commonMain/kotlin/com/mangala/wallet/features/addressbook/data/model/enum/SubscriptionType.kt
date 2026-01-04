package com.mangala.wallet.features.addressbook.data.model.enum

/**
 * Represents the types of subscription available in the system.
 */
enum class SubscriptionType(val value: String) {
    FREE("FREE"),
    PREMIUM("PREMIUM"),
    ENTERPRISE("ENTERPRISE");

    companion object {
        fun fromString(value: String): SubscriptionType {
            return entries.find { it.value == value } ?: FREE
        }
    }
}