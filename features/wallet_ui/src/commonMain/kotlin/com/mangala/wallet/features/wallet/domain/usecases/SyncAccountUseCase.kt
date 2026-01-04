package com.mangala.wallet.features.wallet.domain.usecases

import com.mangala.wallet.domain.account.usecases.CreateWalletAccountUseCase
import com.mangala.wallet.domain.wallet.repository.WalletRepository
import com.mangala.wallet.model.qr.SyncAccountRequest
import com.mangala.wallet.model.wallet.domain.WalletModel

class SyncAccountUseCase(
    private val walletRepository: WalletRepository,
    private val createWalletAccountUseCase: CreateWalletAccountUseCase
) {
    operator fun invoke(syncAccountRequest: SyncAccountRequest): Result<Unit> {
        with(syncAccountRequest) {
            if (walletRepository.getWalletById(walletId) == null) {
                walletRepository.saveWallet(
                    WalletModel(
                        id = walletId,
                        name = walletName,
                        words = "",
                        passphrase = "",
                        key = "",
                        isBackedUp = true,
                        isSelected = true
                    )
                )
            }
            return createWalletAccountUseCase(
                name = accountName,
                accountId = accountId,
                walletId = walletId,
                derivationPathIndex = derivationPathIndex,
                bip44Address = bip44Address,
                bip49Address = bip49Address,
                bip84Address = bip84Address
            )
        }
    }
}