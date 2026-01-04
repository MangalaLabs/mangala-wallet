package com.mangala.wallet.domain.reset.usecases

import com.mangala.wallet.domain.reset.constants.SecureStorageKeys
import com.mangala.wallet.domain.wallet.repository.WalletRepository
import com.mangala.wallet.local.securestorage.SecureStorageWrapper
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

class ClearWalletsUseCase(
    private val walletRepository: WalletRepository,
    private val secureStorageWrapper: SecureStorageWrapper
) {
    suspend operator fun invoke(): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val wallets = walletRepository.getAllWallets()
            
            wallets.forEach { wallet ->
                try {
                    secureStorageWrapper.remove(SecureStorageKeys.getWalletWordsKey(wallet.id))
                } catch (e: Exception) {
                    if (e is CancellationException) throw e
                }
            }
            
            walletRepository.deleteAllWallets()
            
            Result.success(Unit)
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            Result.failure(e)
        }
    }
}