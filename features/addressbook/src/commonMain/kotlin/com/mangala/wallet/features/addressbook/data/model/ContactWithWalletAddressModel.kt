package com.mangala.wallet.features.addressbook.data.model

import com.mangala.wallet.features.addressbook.data.model.blockchain.WalletAddressEntity
import com.mangala.wallet.features.addressbook.data.model.contact.ContactEntity

/**
 * Model kết hợp Contact với WalletAddress được sử dụng trong Group
 */
data class ContactWithWalletAddressModel(
    val contact: ContactEntity,
    val walletAddress: WalletAddressEntity?
)