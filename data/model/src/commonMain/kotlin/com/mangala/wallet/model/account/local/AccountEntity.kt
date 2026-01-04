package com.mangala.wallet.model.account.local

import com.mangala.wallet.model.Dto
import com.mangala.wallet.model.account.domain.AccountModel
import com.mangala.wallet.model.account.domain.AccountType

data class AccountEntity(
    val id: String,
    val name: String,
    val type: String,
    val walletId: String,
    val derivationPathIndex: Int,
    val sortingOrder: Int,
    val isHidden: Boolean,
    val bip44Address: String,
    val bip49Address: String,
    val bip84Address: String
): Dto {

    override fun mapToDomainModel(): AccountModel {
        return AccountModel(
            id = id,
            name = name,
            type = AccountType.valueOf(type),
            walletId = walletId,
            derivationPathIndex = derivationPathIndex,
            sortingOrder = sortingOrder,
            isHidden = isHidden,
            bip44Address = bip44Address,
            bip49Address = bip49Address,
            bip84Address = bip84Address
        )
    }
}