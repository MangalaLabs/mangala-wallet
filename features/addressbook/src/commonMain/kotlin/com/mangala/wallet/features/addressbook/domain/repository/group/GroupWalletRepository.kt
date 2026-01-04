package com.mangala.wallet.features.addressbook.domain.repository.group

import com.mangala.wallet.features.addressbook.data.model.group.GroupWalletEntity
import com.mangala.wallet.features.addressbook.domain.model.group.GroupWallet
import com.mangala.wallet.features.addressbook.presentation.group.create.ContactWithAddress
import kotlinx.coroutines.flow.Flow

interface GroupWalletRepository {
    suspend fun getGroupWalletsByGroupId(groupId: String): List<GroupWalletEntity>
    suspend fun getGroupWalletsByContactId(contactId: String): List<GroupWalletEntity>
    suspend fun insertGroupWallet(groupWallet: GroupWalletEntity)
    suspend fun insertGroupWallets(groupWallets: List<GroupWalletEntity>)
    suspend fun deleteGroupWallet(walletAddressId: String, groupId: String)
    suspend fun deleteGroupWalletsByGroupId(groupId: String)
    suspend fun deleteGroupWalletsByContactId(contactId: String)
    fun observeGroupWalletsByGroupId(groupId: String): Flow<List<GroupWalletEntity>>
    fun observeGroupWalletsByContactId(contactId: String): Flow<List<GroupWalletEntity>>
    suspend fun getContactsWithAddressesByGroupId(groupId: String): List<ContactWithAddress>
    suspend fun getGroupWallets(groupId: String, limit: Int, offset: Int): List<GroupWallet>
    suspend fun getGroupWalletsByWalletIds(walletIds: List<String>): List<GroupWallet>
}