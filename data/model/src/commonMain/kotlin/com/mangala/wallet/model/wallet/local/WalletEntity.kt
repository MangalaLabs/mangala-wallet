package com.mangala.wallet.model.wallet.local

import com.mangala.wallet.model.Dto
import com.mangala.wallet.model.Model
import com.mangala.wallet.model.wallet.domain.WalletModel

data class WalletEntity(
    val id: String,
    val name: String?,
    val words: String?,
    val passphrase: String?,
    val key: String?,
    val isBackedUp: Boolean?,
    val isSelected: Boolean?
): Dto {

    override fun mapToDomainModel(): WalletModel {
        return WalletModel(
            id = id,
            name = name.orEmpty(),
            words = words.orEmpty(),
            passphrase = "",
            key = "",
            isBackedUp = isBackedUp ?: false,
            isSelected = isSelected ?: false
        )
    }
}