package com.mangala.wallet.features.chains.antelope.create_account.domain.usecase

import com.mangala.wallet.domain.datastore.usecases.GetSelectedNetworkUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.model.AntelopeAccount
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.account.GetAccountInfoUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.account.GetAccountPermissionsUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.account.UpdateAccountStatusUseCase

class CheckCreateByFriendAccountCreatedUseCase(
    private val getSelectedNetworkUseCase: GetSelectedNetworkUseCase,
    private val getAccountInfoUseCase: GetAccountInfoUseCase,
    private val getAccountPermissionsUseCase: GetAccountPermissionsUseCase,
    private val updateAccountStatusUseCase: UpdateAccountStatusUseCase
) {

    suspend operator fun invoke(accountName: String): Boolean {
        val blockchainType = getSelectedNetworkUseCase().blockchainType

        val remotePermissions =
            getAccountInfoUseCase(blockchainType, accountName)?.permissions ?: return false

        val permissions = getAccountPermissionsUseCase(accountName, blockchainType.uid)

        val allPermissionInAccount = permissions.all { permission ->
            remotePermissions.any { remotePermission ->
                remotePermission.permName == permission.permissionType.permissionName && permission.requiredAuth.keys.all { localKey ->
                    remotePermission.requiredAuth?.keys?.any { it.key == localKey.key } == true
                }
            }
        }

        if (allPermissionInAccount.not()) {
            return false
        }

        updateAccountStatusUseCase(
            accountName,
            false,
            blockchainType,
            AntelopeAccount.CreateAccountState.DONE
        )

        return true
    }
}