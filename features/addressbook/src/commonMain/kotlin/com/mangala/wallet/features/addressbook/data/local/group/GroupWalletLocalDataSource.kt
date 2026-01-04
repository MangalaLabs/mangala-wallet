package com.mangala.wallet.features.addressbook.data.local.group

import com.mangala.wallet.features.addressbook.data.model.group.GroupWalletEntity
import com.mangala.wallet.features.addressbook.domain.model.group.GroupWallet
import com.mangala.wallet.features.addressbook.presentation.group.create.ContactWithAddress

interface GroupWalletLocalDataSource {
    suspend fun insertGroupWallets(groupWallets: List<GroupWalletEntity>): Boolean
    suspend fun deleteGroupWalletsByGroupId(groupId: String): Boolean
    suspend fun getGroupWalletsByGroupId(groupId: String): List<GroupWalletEntity>
    suspend fun getContactsWithAddressesByGroupId(groupId: String): List<ContactWithAddress>
    suspend fun getGroupWallets(groupId: String, limit: Int, offset: Int): List<GroupWallet>
    suspend fun getGroupWalletsByWalletIds(walletIds: List<String>): List<GroupWallet>
}