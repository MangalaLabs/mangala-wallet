package com.mangala.wallet.features.wallet.domain.usecases

import com.mangala.wallet.model.qr.SyncAccountRequest
import com.mangala.wallet.domain.account.usecases.GetAccountByIdUseCase
import com.mangala.wallet.domain.wallet.usecases.GetSelectedWalletUseCase
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class GetSyncAccountQrUseCase(
    private val getSelectedWalletUseCase: GetSelectedWalletUseCase,
    private val getAccountByIdUseCase: GetAccountByIdUseCase,
    private val json: Json
) {
    operator fun invoke(accountId: String): String? {
        // Set empty string for mnemonic because we only want it to only be saved here in the cold wallet app
        val selectedWallet = getSelectedWalletUseCase()?.copy(words = "", passphrase = "") ?: return null
        val account = getAccountByIdUseCase(accountId)

        val request = SyncAccountRequest(
            walletId = selectedWallet.id,
            walletName = selectedWallet.name,
            walletPublicKey = "", // TODO: Include public key in SyncAccountRequest
            accountId = account.id,
            accountName = account.name,
            derivationPathIndex = account.derivationPathIndex,
            bip44Address = account.bip44Address,
            bip49Address = account.bip49Address,
            bip84Address = account.bip84Address
        )

        return json.encodeToString(request)
    }
}