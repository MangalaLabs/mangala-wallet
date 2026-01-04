package com.mangala.wallet.features.addressbook.domain.model

data class ContactInfo(
    val id: String,
    val name: String,
    val notes: String?,
    val addresses: List<ContactAddress> = emptyList(),
    val importantDates: List<ImportantDate> = emptyList()
)

data class ContactAddress(
    val address: String,
    val network: String
)