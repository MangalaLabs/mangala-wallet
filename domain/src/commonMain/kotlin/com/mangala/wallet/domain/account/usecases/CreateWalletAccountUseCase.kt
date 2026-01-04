package com.mangala.wallet.domain.account.usecases

import com.mangala.wallet.domain.account.repository.AccountRepository
import com.mangala.wallet.domain.wallet.repository.WalletRepository
import com.mangala.wallet.domain.wallet.usecases.MapAccountToAccountBlockchainUseCase
import com.mangala.wallet.domain.wallet.usecases.account.AccountCreator
import com.mangala.wallet.model.account.domain.AccountModel
import com.mangala.wallet.model.account.domain.AccountType
import com.mangala.wallet.model.blockchain.BlockchainType

class CreateWalletAccountUseCase(
    private val accountRepository: AccountRepository,
    private val walletRepository: WalletRepository,
    private val mapAccountToAccountBlockchainUseCase: MapAccountToAccountBlockchainUseCase,
    private val accountCreators: List<AccountCreator>
) {
    suspend operator fun invoke(
        name: String,
        accountId: String,
        walletId: String,
        derivationPathIndex: Int,
        bip44Address: String,
        bip49Address: String,
        bip84Address: String
    ): Result<Unit> {
        val existingAccountsForWallet = accountRepository.getAllAccountsByWalletId(walletId, filterHiddenAccounts = false)

        if (existingAccountsForWallet.any { it.id == accountId }) {
            return Result.failure(
                IllegalArgumentException("Account with id $accountId already exists in wallet with id $walletId")
            )
        }
        val sortingOrder = existingAccountsForWallet.size

        val account = AccountModel(
            id = accountId,
            name = name,
            type = AccountType.NORMAL,
            walletId = walletId,
            derivationPathIndex = derivationPathIndex,
            sortingOrder = sortingOrder,
            isHidden = false,
            bip44Address = bip44Address,
            bip49Address = bip49Address,
            bip84Address = bip84Address
        )

        accountRepository.saveAccount(account)

        return Result.success(Unit)
    }

    suspend operator fun invoke(
        name: String,
        walletId: String,
        blockchainType: BlockchainType
    ): Result<Unit> {
        val wallet = walletRepository.getWalletById(walletId) ?: return Result.failure(
            IllegalArgumentException("Wallet with id $walletId does not exist")
        )
        val existingAccountsForWallet = accountRepository.getAllAccountsByWalletId(walletId, filterHiddenAccounts = false)

        val newDerivationPathIndex = existingAccountsForWallet.maxOf { it.derivationPathIndex } + 1
        val sortingOrder = existingAccountsForWallet.size

        val addresses = mapAccountToAccountBlockchainUseCase(newDerivationPathIndex, wallet, BlockchainType.Ethereum) // TODO: Support for different chains
        val account = AccountModel(
            id = addresses.publicKey,
            name = name,
            type = AccountType.NORMAL,
            walletId = walletId,
            derivationPathIndex = newDerivationPathIndex,
            sortingOrder = sortingOrder,
            isHidden = false,
            bip44Address = addresses.bip44Address,
            bip49Address = addresses.bip49Address,
            bip84Address = addresses.bip84Address
        )

        accountRepository.saveAccount(account)

        accountCreators.forEach {
            it.createAccount(
                accountId = addresses.publicKey,
                derivationPathIndex = newDerivationPathIndex,
                wallet = wallet
            )
        }

        return Result.success(Unit)
    }

    suspend operator fun invoke(name: String, blockchainType: BlockchainType): Result<Unit> {
        val selectedWallet = walletRepository.getSelectedWallet() ?: return Result.success(Unit)

        return invoke(name, selectedWallet.id, blockchainType)
    }
}