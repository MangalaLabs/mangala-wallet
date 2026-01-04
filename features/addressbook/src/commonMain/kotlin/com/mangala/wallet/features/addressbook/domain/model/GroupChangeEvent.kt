package com.mangala.wallet.features.addressbook.domain.model

/**
 * Events emitted when group data changes
 */
sealed class GroupChangeEvent {
    object Created : GroupChangeEvent()
    object Updated : GroupChangeEvent()
    object Deleted : GroupChangeEvent()
}