package com.mangala.wallet.model.account.domain

import com.mangala.wallet.model.Dto
import com.mangala.wallet.model.Model
import com.mangala.wallet.model.account.local.AccountEntity
import kotlinx.serialization.Serializable

data class AccountModel(
    val id: String,
    val name: String,
    val type: AccountType,
    val walletId: String,
    val derivationPathIndex: Int,
    val sortingOrder: Int,
    val isHidden: Boolean,
    val bip44Address: String,
    val bip49Address: String,
    val bip84Address: String
): Model {
    override fun toLocalDto(): AccountEntity {
        return AccountEntity(id, name, type.name, walletId, derivationPathIndex, sortingOrder, isHidden, bip44Address, bip49Address, bip84Address)
    }

    override fun toRemoteDto(): Dto {
        TODO("Not yet implemented")
    }
}