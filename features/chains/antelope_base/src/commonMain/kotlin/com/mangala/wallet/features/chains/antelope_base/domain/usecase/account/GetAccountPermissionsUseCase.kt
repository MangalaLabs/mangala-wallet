package com.mangala.wallet.features.chains.antelope_base.domain.usecase.account

import com.mangala.wallet.domain.datastore.usecases.GetSelectedNetworkUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.model.AntelopeAccountPermission
import com.mangala.wallet.features.chains.antelope_base.domain.repository.AccountPermissionRepository

class GetAccountPermissionsUseCase(
    private val accountPermissionRepository: AccountPermissionRepository,
    private val getSelectedNetworkUseCase: GetSelectedNetworkUseCase
) {
    suspend operator fun invoke(accountName: String, blockchainUid: String): List<AntelopeAccountPermission> {
        return accountPermissionRepository.getAccountPermissionsByAccountName(accountName, blockchainUid)
    }

    suspend operator fun invoke(accountName: String): List<AntelopeAccountPermission> {
        val blockchainUid = getSelectedNetworkUseCase().blockChainUid
        return accountPermissionRepository.getAccountPermissionsByAccountName(accountName, blockchainUid)
    }
}