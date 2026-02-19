package com.mangala.wallet.domain.account.usecases

import com.mangala.wallet.core.hdwallet.domain.usecases.GenerateHDKeyUseCase
import com.mangala.wallet.domain.wallet.usecases.GetWalletByIdUseCase
import com.mangala.wallet.model.blockchain.AddressType
import com.mangala.wallet.model.blockchain.Blockchain
import com.mangala.wallet.model.blockchain.BlockchainType
import com.mangala.wallet.utils.ext.toHexString2

class GetEvmAccountPrivateKeyUseCase(
    private val getWalletByIdUseCase: GetWalletByIdUseCase,
    private val getAccountByIdUseCase: GetAccountByIdUseCase,
    private val generateHDKeyUseCase: GenerateHDKeyUseCase
) {
    suspend operator fun invoke(
        walletId: String,
        accountId: String
    ): String {
        val wallet = getWalletByIdUseCase(walletId)
            ?: throw IllegalArgumentException("Wallet with id $walletId does not exist")
        val account = getAccountByIdUseCase.invokeSuspend(accountId)

        require(account.walletId == walletId) {
            "Account with id $accountId does not belong to wallet with id $walletId"
        }

        val hdKey = generateHDKeyUseCase(
            seedPhrase = wallet.words.split(" "),
            passphrase = wallet.passphrase,
            blockchain = Blockchain(BlockchainType.Ethereum, "", null),
            addressType = AddressType.Bip44,
            derivationPathIndex = account.derivationPathIndex
        )

        return hdKey.privateKey.toHexString2()
    }
}
