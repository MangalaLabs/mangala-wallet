package com.mangala.wallet.features.addressbook.domain.model

/**
 * Events emitted when contact data changes
 */
sealed class ContactChangeEvent {
    object Created : ContactChangeEvent()
    object Updated : ContactChangeEvent()
    object Deleted : ContactChangeEvent()
    data class BatchOperation(val operation: String) : ContactChangeEvent()
}