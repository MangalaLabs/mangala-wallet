package com.mangala.wallet.model.wallet.domain

import com.mangala.wallet.model.Dto
import com.mangala.wallet.model.Model
import com.mangala.wallet.model.wallet.local.WalletEntity

data class WalletModel(
    val id: String,
    val name: String,
    val words: String,
    val passphrase: String,
    val key: String,
    val isBackedUp: Boolean,
    val isSelected: Boolean
): Model {
    override fun toLocalDto(): WalletEntity {
        return WalletEntity(
            id = id,
            name = name,
            words = words,
            passphrase = passphrase,
            key = key,
            isBackedUp = isBackedUp,
            isSelected = isSelected
        )
    }

    override fun toRemoteDto(): Dto {
        TODO("Not yet implemented")
    }
}