package com.mangala.wallet.features.addressbook.presentation.tag

import com.mangala.wallet.features.addressbook.data.model.WalletAddressWithNetworkModel
import com.mangala.wallet.features.addressbook.data.model.contact.ContactEntity

/**
 * UI state for create/edit tag screen with enhanced address search functionality
 */


/**
 * Model cho Contact kèm theo danh sách địa chỉ ví
 */
data class ContactWithAddresses(
    val contact: ContactEntity,
    val addresses: List<WalletAddressWithNetworkModel> = emptyList()
)