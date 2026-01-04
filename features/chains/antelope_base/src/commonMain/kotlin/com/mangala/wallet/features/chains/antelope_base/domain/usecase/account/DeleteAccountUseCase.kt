package com.mangala.wallet.features.chains.antelope_base.domain.usecase.account

import com.mangala.wallet.antelope_key_manager.EosKeyManager
import com.mangala.wallet.domain.datastore.usecases.GetSelectedNetworkUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.repository.AccountRepository
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.notification.UnregisterAntelopeNotificationUseCase
import com.mangala.wallet.model.blockchain.BlockchainType
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

class DeleteAccountUseCase(
    private val getSelectedNetworkUseCase: GetSelectedNetworkUseCase,
    private val accountRepository: AccountRepository,
    private val unregisterAntelopeNotificationUseCase: UnregisterAntelopeNotificationUseCase,
    private val getAccountPermissionsUseCase: GetAccountPermissionsUseCase,
    private val eosKeyManager: EosKeyManager,
) {
    suspend operator fun invoke(accountName: String) {
        val blockchainType = getSelectedNetworkUseCase().blockchainType

        invoke(accountName = accountName, blockchainType = blockchainType)
    }

    suspend operator fun invoke(blockchainType: BlockchainType, accountName: String) = coroutineScope {
        val keyDeletionHandler = CoroutineExceptionHandler { _, _ -> }

        val clearPrivateKeysJob = async(keyDeletionHandler) {
            val permissions = getAccountPermissionsUseCase(
                accountName = accountName,
                blockchainUid = blockchainType.uid
            )

            val keyDeletionJobs = permissions.flatMap { permission ->
                permission.requiredAuth.keys.map { antelopeKey ->
                    async(keyDeletionHandler) {
                        eosKeyManager.removePrivateKey(antelopeKey.key)
                    }
                }
            }
            keyDeletionJobs.awaitAll()
        }

        val softDeleteJob = async {
            accountRepository.softDeleteAccount(
                accountName = accountName,
                blockchainType = blockchainType
            )
        }

        val unregisterNotificationJob = async {
            unregisterAntelopeNotificationUseCase(
                accountName = accountName,
                blockchainType = blockchainType
            )
        }

        val unregisterNotificationResult = unregisterNotificationJob.await()
        softDeleteJob.await()

        if (unregisterNotificationResult.isSuccess) {
            accountRepository.deleteAccount(
                accountName = accountName,
                blockchainType = blockchainType
            )
        }

        clearPrivateKeysJob.await()
    }
}