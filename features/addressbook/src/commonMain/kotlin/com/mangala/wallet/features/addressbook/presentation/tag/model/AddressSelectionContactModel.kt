package com.mangala.wallet.features.addressbook.presentation.tag.model

import com.mangala.wallet.features.addressbook.data.model.ContactWithMultipleBlockchainsModel

data class AddressSelectionContactModel(
    val contactWithMultipleBlockchainsModel: ContactWithMultipleBlockchainsModel,
    val isSelected: Boolean = false,
)
