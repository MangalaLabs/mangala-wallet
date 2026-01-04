package com.mangala.wallet.features.chains.antelope_base.domain.usecase.permission

import com.mangala.wallet.domain.datastore.usecases.GetSelectedNetworkUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.account.GetAccountInfoUseCase

class MultiSignAccountCheckingUseCase(
    private val getSelectedNetworkUseCase: GetSelectedNetworkUseCase,
    private val getAccountUseCase: GetAccountInfoUseCase
) {
    suspend fun invoke(
        accountName: String,
        accountPermission: String
    ): Boolean? {
        val blockchainType = getSelectedNetworkUseCase.invoke().blockchainType

        val account = getAccountUseCase.invoke(blockchainType, accountName)
        val accountThreshold =
            account?.permissions?.find { it.permName == accountPermission }?.requiredAuth?.threshold
        return accountThreshold?.let { it > 1 } ?: run { null }
    }
}