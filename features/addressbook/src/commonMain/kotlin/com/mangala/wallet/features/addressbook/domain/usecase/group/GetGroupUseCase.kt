package com.mangala.wallet.features.addressbook.domain.usecase.group

import com.mangala.wallet.features.addressbook.data.model.group.GroupEntity
import com.mangala.wallet.features.addressbook.domain.model.group.GroupWallet
import com.mangala.wallet.features.addressbook.domain.repository.group.GroupWalletRepository
import com.mangala.wallet.features.addressbook.domain.repository.group.GroupRepository
import com.mangala.wallet.features.addressbook.presentation.group.create.ContactWithAddress
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

class GetGroupUseCase(
    private val groupRepository: GroupRepository,
    private val groupWalletRepository: GroupWalletRepository
) {
    suspend operator fun invoke(groupId: String): GroupEntity? = withContext(Dispatchers.IO) {
        groupRepository.getGroupById(groupId)
    }

    suspend fun getGroupWallets(
        groupId: String,
        limit: Int = 20,
        offset: Int = 0
    ): List<GroupWallet> = withContext(Dispatchers.IO) {
        groupWalletRepository.getGroupWallets(groupId, limit, offset)
    }
}