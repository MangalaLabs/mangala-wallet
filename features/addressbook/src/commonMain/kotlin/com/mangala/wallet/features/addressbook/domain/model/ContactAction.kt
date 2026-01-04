package com.mangala.wallet.features.addressbook.domain.model

sealed class ContactAction(val actionName: String) {
    data object ViewDetails : ContactAction("view_contact_details")
    data object Edit : ContactAction("edit_contact")
    data object SendCrypto : ContactAction("send_crypto")
    data object Delete : ContactAction("delete_contact")
}