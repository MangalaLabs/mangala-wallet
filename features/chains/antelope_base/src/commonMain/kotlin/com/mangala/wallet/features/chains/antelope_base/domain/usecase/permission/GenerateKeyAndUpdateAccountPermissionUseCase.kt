package com.mangala.wallet.features.chains.antelope_base.domain.usecase.permission

import com.mangala.wallet.antelope_key_manager.EosKeyManager
import com.mangala.wallet.features.chains.antelope_base.domain.model.AntelopeAccount
import com.mangala.wallet.features.chains.antelope_base.domain.model.AntelopePermissionType
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.account.GetAccountInfoUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.account.SaveAccountUseCase
import com.mangala.wallet.model.blockchain.BlockchainType

class GenerateKeyAndUpdateAccountPermissionUseCase(
    private val updateAccountPermissionUseCase: UpdateAccountPermissionUseCase,
    private val keyManager: EosKeyManager,
    private val saveAccountUseCase: SaveAccountUseCase,
    private val getAccountInfoUseCase: GetAccountInfoUseCase
) {
    suspend operator fun invoke(
        accountName: String,
        blockchainType: BlockchainType
    ): Result<String> {
        val activePrivateKey = keyManager.createEosPrivateKey()
        val activePublicKey = activePrivateKey.publicKey.toString()

        val account = getAccountInfoUseCase(blockchainType, accountName)

        val activePermission =
            account?.permissions?.find { it.permName == AntelopePermissionType.Active.permissionName }
                ?: return Result.failure(Exception("Active permission not found in account"))
        val existingKeys = activePermission.requiredAuth?.keys
        val existingThreshold = activePermission.requiredAuth?.threshold
        val existingAccounts = activePermission.requiredAuth?.accounts
        val existingWaits = activePermission.requiredAuth?.waits

        val keys = listOf(
            AccountKey(
                key = activePublicKey,
                weight = 1
            )
        ) + existingKeys?.map {
            AccountKey(
                it.key.orEmpty(),
                it.weight?.toLong() ?: 0L
            )
        }.orEmpty()

        return updateAccountPermissionUseCase(
            accountName = accountName,
            accountPermissionExecuted = AntelopePermissionType.Owner.permissionName,
            permissionUpdated = AntelopePermissionType.Active.permissionName,
            permissionParentUpdated = AntelopePermissionType.Owner.permissionName,
            threshold = existingThreshold ?: 1,
            keys = keys.sortedBy { it.key },
            accounts = existingAccounts?.map {
                AccountAuthAccount(
                    it.permission?.actor.orEmpty(),
                    it.permission?.permission.orEmpty(),
                    it.weight ?: 1L
                )
            }.orEmpty(),
            waits = existingWaits?.map {
                AccountAuthWait(
                    it.waitSec?.toInt() ?: 0,
                    it.weight ?: 1L
                )
            }.orEmpty(),
            blockchainType = blockchainType
        ).onSuccess {
            saveAccountUseCase(
                accountName = accountName,
                activePrivateKey = activePrivateKey,
                ownerPrivateKey = null,
                isTemp = false,
                createAccountState = AntelopeAccount.CreateAccountState.DONE,
                purchaseToken = null,
                isReplace = false,
                blockchainType = blockchainType
            )
        }
    }
}