package com.mangala.wallet.features.chains.antelope_base.domain.usecase

import com.mangala.wallet.domain.reset.usecases.ClearAntelopeImportedAccountUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.repository.AccountRepository
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.account.DeleteAccountUseCase
import com.mangala.wallet.model.blockchain.BlockchainNetworkData
import com.mangala.wallet.utils.BuildEnvironmentProvider
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext

class ClearAntelopeImportedAccountUseCaseImpl(
    private val accountRepository: AccountRepository,
    private val deleteAccountUseCase: DeleteAccountUseCase,
    private val environmentProvider: BuildEnvironmentProvider,
) : ClearAntelopeImportedAccountUseCase {

    override suspend operator fun invoke(): Result<Unit> = withContext(Dispatchers.IO) {
        val resList = BlockchainNetworkData
            .getAllBlockchainNetworkSupported(environmentProvider.isDevelopmentEnvironment())
            .map {
                async {
                    try {
                        val accounts = accountRepository.getAccounts(
                            blockchainType = it.blockchainType,
                            includeTempAccounts = false,
                            includeIapInitializedAccounts = false
                        )

                        val deleteAccountJobs = accounts.map { account ->
                            async {
                                deleteAccountUseCase(
                                    blockchainType = it.blockchainType,
                                    accountName = account.accountName
                                )
                            }
                        }

                        deleteAccountJobs.awaitAll()

                        Result.success(Unit)
                    } catch (e: Exception) {
                        if (e is CancellationException) throw e
                        Result.failure(e)
                    }
                }
            }
            .awaitAll()
        resList
            .firstOrNull { it.isFailure }
            ?: Result.success(Unit)
    }
}