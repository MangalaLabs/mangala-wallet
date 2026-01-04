package com.mangala.wallet.features.chains.antelope_base.domain.usecase.account

import com.mangala.wallet.features.chains.antelope_base.domain.model.AntelopeAccount
import com.mangala.wallet.features.chains.antelope_base.domain.repository.AccountRepository
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.notification.RegisterAntelopeNotificationUseCase
import com.mangala.wallet.model.blockchain.BlockchainType

class UpdateAccountStatusUseCase(
    private val accountRepository: AccountRepository,
    private val registerAntelopeNotificationUseCase: RegisterAntelopeNotificationUseCase
) {
    suspend operator fun invoke(
        accountName: String,
        isTemp: Boolean,
        blockchainType: BlockchainType,
        createAccountState: AntelopeAccount.CreateAccountState
    ) {
        accountRepository.updateAccountStatus(
            accountName,
            isTemp,
            blockchainType,
            createAccountState
        )

        if (isTemp.not()) {
            registerAntelopeNotificationUseCase(accountName, blockchainType)
        }
    }

    suspend operator fun invoke(
        accountName: String,
        isTemp: Boolean,
        blockchainType: BlockchainType,
        createAccountState: AntelopeAccount.CreateAccountState,
        purchaseToken: String,
        purchaseId: String
    ) {
        accountRepository.updateAccountStatus(
            accountName,
            isTemp,
            blockchainType,
            createAccountState,
            purchaseToken,
            purchaseId
        )

        if (isTemp.not()) {
            registerAntelopeNotificationUseCase(accountName, blockchainType)
        }
    }
}