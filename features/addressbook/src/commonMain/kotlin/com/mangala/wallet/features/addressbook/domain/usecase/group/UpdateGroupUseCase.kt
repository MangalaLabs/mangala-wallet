package com.mangala.wallet.features.addressbook.domain.usecase.group

import com.mangala.wallet.features.addressbook.data.model.group.GroupEntity
import com.mangala.wallet.features.addressbook.data.model.group.GroupWalletEntity
import com.mangala.wallet.features.addressbook.domain.model.group.GroupWallet
import com.mangala.wallet.features.addressbook.domain.repository.group.GroupWalletRepository
import com.mangala.wallet.features.addressbook.domain.repository.group.GroupRepository
import com.mangala.wallet.features.addressbook.presentation.group.create.ContactWithAddress
import com.mangala.wallet.features.addressbook.domain.exceptions.DuplicateNameException
import com.mangala.wallet.features.addressbook.domain.exceptions.ValidationException
import com.mangala.wallet.features.addressbook.domain.exceptions.ImmutableDataException
import com.mangala.wallet.features.addressbook.domain.validation.ValidationConstants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

class UpdateGroupUseCase(
    private val groupRepository: GroupRepository,
    private val groupWalletRepository: GroupWalletRepository
) {
    suspend operator fun invoke(
        group: GroupEntity,
        selectedWallets: List<GroupWallet>
    ) = withContext(Dispatchers.IO) {
        // Validate group name
        if (group.name.isBlank()) {
            throw ValidationException("Group name cannot be empty")
        }
        
        if (group.name.length > ValidationConstants.MAX_GROUP_NAME_LENGTH) {
            throw ValidationException("Group name cannot exceed ${ValidationConstants.MAX_GROUP_NAME_LENGTH} characters")
        }
        
        // Check if group exists
        val existingGroup = groupRepository.getGroupById(group.id)
            ?: throw ValidationException("Group not found")
        
        // Check for duplicate name (excluding current group)
        val groupWithSameName = groupRepository.findGroupByName(group.name)
        if (groupWithSameName != null && groupWithSameName.id != group.id) {
            throw DuplicateNameException("Another group with name '${group.name}' already exists")
        }
        
        // Check if network is being changed
        if (existingGroup.mainBlockchainId != group.mainBlockchainId) {
            throw ImmutableDataException("Cannot change network for existing group")
        }
        
        // Update the group
        groupRepository.updateGroup(group)

        // Delete existing group-wallet relationships
        groupWalletRepository.deleteGroupWalletsByGroupId(group.id)

        // Create new group-wallet relationships
        val groupWallets = selectedWallets.map { wallet ->
            GroupWalletEntity.create(
                groupId = group.id,
                walletAddressId = wallet.walletId,
                contactId = wallet.contactId // Use the contactId from the GroupWallet object
            )
        }

        groupWalletRepository.insertGroupWallets(groupWallets)
    }
}