package com.mangala.wallet.features.chains.antelope_base.domain.usecase.notification

import com.mangala.wallet.features.chains.antelope_base.domain.repository.AccountRepository
import com.mangala.wallet.features.chains.antelope_base.domain.repository.notification.AntelopeNotificationRepository
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.account.DeleteAccountUseCase
import com.mangala.wallet.model.blockchain.BlockchainType
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.installations.installations
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

class UnregisterAntelopeNotificationUseCase(
    private val antelopeNotificationRepository: AntelopeNotificationRepository,
    private val accountRepository: AccountRepository,
) {
    suspend operator fun invoke(
        accountName: String,
        blockchainType: BlockchainType
    ): Result<Unit> {
        val result = antelopeNotificationRepository.unregisterNotification(
            accountName = accountName,
            deviceId = Firebase.installations.getToken(forceRefresh = false),
            blockchainType = blockchainType
        )

        return result
    }

    suspend fun retryAllUnregisterNotification(blockchainType: BlockchainType) = coroutineScope {
        val accounts = accountRepository.listSoftDeletedAccounts(blockchainType)
        accounts.map {
            async {
                val unregisterNotificationResult = invoke(
                    accountName = it.accountName,
                    blockchainType = blockchainType
                )
                
                if (unregisterNotificationResult.isSuccess)
                    accountRepository.deleteAccount(
                        accountName = it.accountName,
                        blockchainType = blockchainType
                    )
            }
        }.awaitAll()
    }
}