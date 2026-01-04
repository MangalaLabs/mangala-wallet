package com.mangala.wallet.features.addressbook.data.model.note

import kotlinx.datetime.Instant

data class NoteTemplateEntity(
    val id: String,
    val name: String,
    val content: String,
    val createdAt: Instant,
    val updatedAt: Instant?
)