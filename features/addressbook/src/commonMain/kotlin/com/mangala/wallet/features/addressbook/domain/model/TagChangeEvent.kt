package com.mangala.wallet.features.addressbook.domain.model

/**
 * Events emitted when tag data changes
 */
sealed class TagChangeEvent {
    object Created : TagChangeEvent()
    object Updated : TagChangeEvent()
    object Deleted : TagChangeEvent()
}