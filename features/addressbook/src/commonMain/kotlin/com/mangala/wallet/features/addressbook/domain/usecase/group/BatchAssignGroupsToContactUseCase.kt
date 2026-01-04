package com.mangala.wallet.features.addressbook.domain.usecase.group

import com.mangala.wallet.features.addressbook.domain.repository.group.GroupRepository

class BatchAssignGroupsToContactUseCase(private val groupRepository: GroupRepository) {
    /**
     * Assign multiple groups to a contact
     * @param contactId ID of the contact
     * @param groupIds List of group IDs to assign
     * @param walletAddressId ID of wallet address to use for all groups
     * @return Result indicating success or failure
     */
    suspend operator fun invoke(contactId: String, groupIds: List<String>, walletAddressId: String): Result<Unit> {
        return try {
            if (groupIds.isEmpty()) {
                return Result.success(Unit)
            }

            // Verify all groups exist
            for (groupId in groupIds) {
                groupRepository.getGroupById(groupId)
                    ?: return Result.failure(IllegalArgumentException("Group with ID $groupId not found"))
            }

            // Assign each group to the contact with the specified wallet address
            groupIds.forEach { groupId ->
                val success = groupRepository.addContactToGroup(contactId, groupId, walletAddressId)
                if (!success) {
                    return Result.failure(IllegalStateException("Failed to add contact to group $groupId"))
                }
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}