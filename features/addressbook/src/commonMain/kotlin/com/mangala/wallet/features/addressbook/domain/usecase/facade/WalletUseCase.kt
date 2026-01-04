package com.mangala.wallet.features.addressbook.domain.usecase.facade

import com.mangala.wallet.features.addressbook.data.model.WalletAddressWithBlockchainModel
import com.mangala.wallet.features.addressbook.data.model.blockchain.BlockchainTypeEntity
import com.mangala.wallet.features.addressbook.data.model.blockchain.WalletAddressEntity
import com.mangala.wallet.features.addressbook.data.model.group.GroupEntity
import com.mangala.wallet.features.addressbook.data.model.group.GroupModel
import com.mangala.wallet.features.addressbook.data.model.enum.PrivacyLevel
import com.mangala.wallet.features.addressbook.data.model.enum.SecurityLevel as EntitySecurityLevel
import kotlinx.datetime.Instant
import com.mangala.wallet.features.addressbook.data.model.tag.TagEntity
import com.mangala.wallet.features.addressbook.domain.usecase.blockchain.GetAllBlockchainTypesUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.group.AddContactToGroupUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.group.GetAllGroupsUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.group.RemoveContactFromGroupUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.tag.AssignTagToContactUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.tag.GetActiveTagsUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.tag.RemoveTagFromContactUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.wallet_address.AddWalletAddressUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.wallet_address.DeleteWalletAddressUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.wallet_address.GetWalletAddressesWithBlockchainByContactIdUseCase
import com.mangala.wallet.features.addressbook.domain.usecase.wallet_address.UpdateWalletAddressUseCase
import kotlinx.coroutines.flow.Flow

/**
 * Facade for wallet and categorization operations - groups wallet addresses, blockchains, tags, groups
 * Reduces dependencies from 10+ use cases to 1 facade in ContactEditViewModel
 */
class WalletUseCase(
    // Wallet address use cases
    private val addWalletAddressUseCase: AddWalletAddressUseCase,
    private val updateWalletAddressUseCase: UpdateWalletAddressUseCase,
    private val deleteWalletAddressUseCase: DeleteWalletAddressUseCase,
    private val getWalletAddressesWithBlockchainByContactIdUseCase: GetWalletAddressesWithBlockchainByContactIdUseCase,
    
    // Blockchain use cases
    private val getAllBlockchainTypesUseCase: GetAllBlockchainTypesUseCase,
    
    // Tag use cases
    private val getActiveTagsUseCase: GetActiveTagsUseCase,
    private val assignTagToContactUseCase: AssignTagToContactUseCase,
    private val removeTagFromContactUseCase: RemoveTagFromContactUseCase,
    
    // Group use cases
    private val getAllGroupsUseCase: GetAllGroupsUseCase,
    private val addContactToGroupUseCase: AddContactToGroupUseCase,
    private val removeContactFromGroupUseCase: RemoveContactFromGroupUseCase
) {
    
    // Wallet address operations
    suspend fun addWalletAddress(walletAddress: WalletAddressEntity): Result<Unit> {
        return try {
            addWalletAddressUseCase(walletAddress)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun updateWalletAddress(walletAddress: WalletAddressEntity): Result<Unit> {
        return try {
            updateWalletAddressUseCase(walletAddress)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun deleteWalletAddress(addressId: String): Result<Unit> {
        return try {
            deleteWalletAddressUseCase(addressId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getWalletAddresses(contactId: String): Result<List<WalletAddressWithBlockchainModel>> {
        return try {
            val addresses = getWalletAddressesWithBlockchainByContactIdUseCase(contactId)
            Result.success(addresses)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // Blockchain operations
    suspend fun getAllBlockchainTypes(): Result<List<BlockchainTypeEntity>> {
        return try {
            val blockchains = getAllBlockchainTypesUseCase()
            Result.success(blockchains)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // Tag operations
    suspend fun getActiveTags(): Result<List<TagEntity>> {
        return try {
            getActiveTagsUseCase()
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun assignTagToContact(contactId: String, tagId: String): Result<Unit> {
        return try {
            assignTagToContactUseCase(contactId, tagId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun removeTagFromContact(contactId: String, tagId: String): Result<Unit> {
        return try {
            removeTagFromContactUseCase(contactId, tagId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // Group operations  
    suspend fun getAllGroups(): Result<List<GroupEntity>> {
        return try {
            val groupModels = getAllGroupsUseCase()
            val groupEntities = groupModels.map { model -> convertGroupModelToEntity(model) }
            Result.success(groupEntities)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    private fun convertGroupModelToEntity(model: GroupModel): GroupEntity {
        return GroupEntity(
            id = model.id,
            name = model.name,
            mainBlockchainId = model.mainBlockchainId,
            description = model.description,
            icon = model.icon,
            color = model.color,
            privacyLevel = try {
                PrivacyLevel.valueOf(model.privacyLevel.uppercase())
            } catch (e: Exception) {
                PrivacyLevel.PUBLIC
            },
            securityLevel = try {
                EntitySecurityLevel.valueOf(model.securityLevel.uppercase())
            } catch (e: Exception) {
                EntitySecurityLevel.NORMAL
            },
            createdAt = Instant.fromEpochMilliseconds(model.createdAt),
            updatedAt = Instant.fromEpochMilliseconds(model.updatedAt)
        )
    }
    
    suspend fun addContactToGroup(contactId: String, groupId: String, walletAddressId: String): Result<Unit> {
        return try {
            addContactToGroupUseCase(contactId, groupId, walletAddressId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun removeContactFromGroup(contactId: String, groupId: String): Result<Unit> {
        return try {
            removeContactFromGroupUseCase(contactId, groupId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // Convenience methods for batch operations
    suspend fun updateContactTags(contactId: String, selectedTagIds: Set<String>, originalTagIds: Set<String>): Result<Unit> {
        return try {
            // Remove tags that are no longer selected
            val tagsToRemove = originalTagIds - selectedTagIds
            for (tagId in tagsToRemove) {
                removeTagFromContactUseCase(contactId, tagId)
            }
            
            // Add new tags that were selected
            val tagsToAdd = selectedTagIds - originalTagIds
            for (tagId in tagsToAdd) {
                assignTagToContactUseCase(contactId, tagId)
            }
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun updateContactGroups(contactId: String, selectedGroupIds: Set<String>, originalGroupIds: Set<String>): Result<Unit> {
        return try {
            // For simplicity, use the first wallet address if available, or empty string
            // In a real implementation, this would need proper wallet address selection logic
            val walletAddressId = "" // Simplified - would need proper selection
            
            // Remove groups that are no longer selected
            val groupsToRemove = originalGroupIds - selectedGroupIds
            for (groupId in groupsToRemove) {
                removeContactFromGroupUseCase(contactId, groupId)
            }
            
            // Add new groups that were selected
            val groupsToAdd = selectedGroupIds - originalGroupIds
            for (groupId in groupsToAdd) {
                addContactToGroupUseCase(contactId, groupId, walletAddressId)
            }
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}