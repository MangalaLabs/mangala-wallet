package com.mangala.wallet.domain.reset.usecases

import com.mangala.wallet.domain.reset.model.ResetResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import kotlinx.coroutines.CancellationException

class ResetWalletUseCase(
    private val clearAntelopeImportedAccountUseCase: ClearAntelopeImportedAccountUseCase,
    private val clearAntelopeCacheDataUseCase: ClearAntelopeCacheDataUseCase,
    private val clearSecureStorageUseCase: ClearSecureStorageUseCase,
    private val clearDataStoreUseCase: ClearDataStoreUseCase,
    private val clearWalletsUseCase: ClearWalletsUseCase,
    private val clearAccountsUseCase: ClearAccountsUseCase,
    private val clearEVMTransactionHistoryUseCase: ClearEVMTransactionHistoryUseCase,
    private val clearTokenBalancesUseCase: ClearTokenBalancesUseCase,
    private val clearConversationHistoryUseCase: ClearConversationHistoryUseCase,
    private val clearWebSocketChatUseCase: ClearWebSocketChatUseCase,
    private val clearBitcoinDataUseCase: ClearBitcoinDataUseCase,
    private val clearETicketDataUseCase: ClearETicketDataUseCase,
    private val clearNFTDataUseCase: ClearNFTDataUseCase,
    private val clearAddressBookDataUseCase: ClearAddressBookDataUseCase,
    private val clearPasskeyAndSessionUseCase: ClearPasskeyAndSessionUseCase,
) {

    suspend operator fun invoke(): ResetResult = withContext(Dispatchers.IO) {
        try {
            clearAntelopeImportedAccountUseCase().getOrThrow()
            clearAntelopeCacheDataUseCase().getOrThrow()
            clearSecureStorageUseCase().getOrThrow()
            clearPasskeyAndSessionUseCase().getOrThrow()
            clearDataStoreUseCase().getOrThrow()
            clearEVMTransactionHistoryUseCase().getOrThrow()
            clearTokenBalancesUseCase().getOrThrow()
            clearConversationHistoryUseCase().getOrThrow()
            clearWebSocketChatUseCase().getOrThrow()
            clearBitcoinDataUseCase().getOrThrow()
            clearETicketDataUseCase().getOrThrow()
            clearNFTDataUseCase().getOrThrow()
            clearAddressBookDataUseCase()
            clearWalletsUseCase().getOrThrow()
            clearAccountsUseCase().getOrThrow()
            
            ResetResult.Success
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            ResetResult.Error(e.message ?: "Reset failed")
        }
    }
}