package com.mangala.wallet.features.chains.antelope_base.domain.usecase.account

import com.benasher44.uuid.uuid4
import com.mangala.antelope.base.api.model.GetAccountResponse
import com.mangala.antelope.base.api.model.Permission
import com.mangala.wallet.features.chains.antelope_base.domain.model.AntelopeAccount
import com.mangala.wallet.features.chains.antelope_base.domain.model.AntelopeAccountPermission
import com.mangala.wallet.features.chains.antelope_base.domain.model.AntelopeKey
import com.mangala.wallet.features.chains.antelope_base.domain.model.AntelopePermissionType
import com.mangala.wallet.features.chains.antelope_base.domain.model.AntelopeRequiredAuth
import com.mangala.wallet.model.blockchain.BlockchainType
import com.memtrip.eos.core.crypto.EosPublicKey
import kotlinx.datetime.Clock

class CheckPublicKeyLinkedToAccountNameUseCase(
    private val getAccountInfoUseCase: GetAccountInfoUseCase
) {

    suspend operator fun invoke(
        activePublicKey: EosPublicKey,
        ownerPublicKey: EosPublicKey?,
        accountName: String,
        blockchainType: BlockchainType
    ): Result<AntelopeAccount> {
        val accountInfo =
            getAccountInfo(accountName, blockchainType).getOrElse { return Result.failure(it) }

        val activePermission = getPermissionByName(
            accountInfo.permissions!!,
            AntelopePermissionType.Active.permissionName
        )
            ?: return Result.failure(CheckPublicKeyLinkedToAccountNameException.InvalidAccountException())
        val accountHasActivePublicKey = checkKeyInPermission(activePermission, activePublicKey)

        if (!accountHasActivePublicKey) return Result.failure(
            CheckPublicKeyLinkedToAccountNameException.InvalidActivePublicKeyException()
        )

        val accountHasOwnerPublicKey = if (ownerPublicKey != null) {
            val ownerPermission = getPermissionByName(
                accountInfo.permissions!!,
                AntelopePermissionType.Owner.permissionName
            )
            checkKeyInPermission(ownerPermission!!, ownerPublicKey)
        } else {
            null
        }
        if (accountHasOwnerPublicKey == false) return Result.failure(
            CheckPublicKeyLinkedToAccountNameException.InvalidOwnerPublicKeyException()
        )

        val permissions = accountInfo.permissions!!.map {
            AntelopeAccountPermission(
                permissionType = AntelopePermissionType.fromName(it.permName.orEmpty()),
                parent = AntelopePermissionType.fromName(it.parent.orEmpty()),
                linkedActions = emptyList(),
                requiredAuth = AntelopeRequiredAuth(
                    threshold = 0,
                    keys = it.requiredAuth?.keys?.map { key ->
                        AntelopeKey(
                            id = uuid4().toString(),
                            key.key.orEmpty(),
                            key.weight ?: 1,
                            isSynced = key.key == activePublicKey.toString() || key.key == ownerPublicKey.toString()
                        )
                    } ?: emptyList(),
                    accounts = emptyList(),
                    waits = emptyList()
                )
            )
        }
        return Result.success(
            AntelopeAccount(
                accountName = accountName,
                permissions = permissions,
                isActive = true,
                isTemp = false,
                createAccountState = AntelopeAccount.CreateAccountState.DONE,
                coreLiquidBalance = null,
                cpuLimit = null,
                netLimit = null,
                ramQuota = null,
                ramUsage = null,
                rexBalance = null,
                selfDelegatedBandwidthCpuWeight = null,
                selfDelegatedBandwidthNetWeight = null,
                totalResources = null,
                lastUpdated = Clock.System.now(),
                isNotificationRegistered = false // TODO: CHeck if we need to register notification in this case?
            )
        )
    }

    /**
     * Check if the public key is linked to the account name.
     * @param publicKey The public key to check.
     * @param accountName The account name to check.
     * @return The [AntelopeAccount] if the public key is linked to the account name, null otherwise.
     */
    suspend operator fun invoke(
        publicKey: EosPublicKey,
        accountName: String,
        blockchainType: BlockchainType
    ): Result<AntelopeAccount> {
        val accountInfo =
            getAccountInfo(accountName, blockchainType).getOrElse { return Result.failure(it) }

        val accountHasPublicKey =
            accountInfo.permissions!!.any { checkKeyInPermission(it, publicKey) }

        if (accountHasPublicKey) {
            val permissions = accountInfo.permissions!!.map {
                AntelopeAccountPermission(
                    permissionType = AntelopePermissionType.fromName(it.permName.orEmpty()),
                    parent = AntelopePermissionType.fromName(it.parent.orEmpty()),
                    linkedActions = emptyList(),
                    requiredAuth = AntelopeRequiredAuth(
                        threshold = 0,
                        keys = it.requiredAuth?.keys?.map { key ->
                            AntelopeKey(
                                id = uuid4().toString(),
                                key.key.orEmpty(),
                                key.weight ?: 1,
                                isSynced = key.key == publicKey.toString()
                            )
                        } ?: emptyList(),
                        accounts = emptyList(),
                        waits = emptyList()
                    )
                )
            }
            return Result.success(
                AntelopeAccount(
                    accountName = accountName,
                    permissions = permissions,
                    isActive = true,
                    isTemp = false,
                    createAccountState = AntelopeAccount.CreateAccountState.DONE,
                    coreLiquidBalance = null,
                    cpuLimit = null,
                    netLimit = null,
                    ramQuota = null,
                    ramUsage = null,
                    rexBalance = null,
                    selfDelegatedBandwidthCpuWeight = null,
                    selfDelegatedBandwidthNetWeight = null,
                    totalResources = null,
                    lastUpdated = Clock.System.now(),
                    isNotificationRegistered = false // TODO: CHeck if we need to register notification in this case?
                )
            )
        }

        return Result.failure(CheckPublicKeyLinkedToAccountNameException.InvalidPublicKeyException())
    }

    private fun checkKeyInPermission(
        permission: Permission,
        publicKey: EosPublicKey
    ) =
        permission.requiredAuth?.keys?.any { it.key == publicKey.toString() || it.key == publicKey.toLegacyString() } == true

    private suspend fun getAccountInfo(
        accountName: String,
        blockchainType: BlockchainType
    ): Result<GetAccountResponse> {
        val accountInfo =
            getAccountInfoUseCase(blockchainType, accountName.trim()) ?: return Result.failure(
                CheckPublicKeyLinkedToAccountNameException.InvalidAccountNameException()
            )
        if (accountInfo.permissions.isNullOrEmpty()) return Result.failure(
            CheckPublicKeyLinkedToAccountNameException.InvalidAccountException()
        )

        return Result.success(accountInfo)
    }

    private fun getPermissionByName(
        permissions: List<Permission>,
        permissionName: String
    ): Permission? {
        return permissions.find { it.permName == permissionName }
    }
}

sealed class CheckPublicKeyLinkedToAccountNameException(message: String) : Exception(message) {
    class InvalidOwnerPublicKeyException :
        CheckPublicKeyLinkedToAccountNameException("Invalid owner public key")

    class InvalidActivePublicKeyException :
        CheckPublicKeyLinkedToAccountNameException("Invalid active public key")

    class InvalidPublicKeyException :
        CheckPublicKeyLinkedToAccountNameException("Invalid public key")

    class InvalidAccountNameException :
        CheckPublicKeyLinkedToAccountNameException("Invalid account name or account does not exist")

    class InvalidAccountException :
        CheckPublicKeyLinkedToAccountNameException("Invalid account")
}