package com.mangala.wallet.domain.wallet.usecases

import com.mangala.wallet.cryptography.generateSecureRandomBytes
import com.mangala.wallet.domain.account.repository.AccountRepository
import com.mangala.wallet.domain.wallet.repository.WalletRepository
import com.mangala.wallet.domain.wallet.usecases.account.AccountCreator
import com.mangala.wallet.model.account.domain.AccountModel
import com.mangala.wallet.model.account.domain.AccountType
import com.mangala.wallet.model.blockchain.BlockchainType
import com.mangala.wallet.model.wallet.domain.WalletModel
import com.mangala.wallet.utils.bip39.BIP39_WORDLIST_ENGLISH
import com.soywiz.krypto.sha256
import kotlin.experimental.and

class CreateWalletUseCase(
    private val walletRepository: WalletRepository,
    private val accountRepository: AccountRepository,
    private val mapAccountToAccountBlockchainUseCase: MapAccountToAccountBlockchainUseCase,
    private val accountCreators: List<AccountCreator>
) {

    suspend operator fun invoke(wordsCount: Int, passphrase: String, blockchainType: BlockchainType): WalletModel {
        val seedPhrase = generateBip39SeedPhrase(wordsCount)

        val existingWalletCount = walletRepository.getAllWallets().size + 1

        val walletWithoutId = WalletModel(
            id = "",
            name = "Wallet $existingWalletCount", // TODO: Localization
            words = seedPhrase.joinToString(" "),
            passphrase = "",
            key = "",
            isBackedUp = false,
            isSelected = true
        )

        val derivationPathIndex = 0
        val addresses = mapAccountToAccountBlockchainUseCase(derivationPathIndex, walletWithoutId, BlockchainType.Ethereum) // TODO: Support for different chains
        val account = AccountModel(
            id = addresses.publicKey,
            name = "Account 1",
            type = AccountType.NORMAL,
            walletId = addresses.publicKey,
            derivationPathIndex = derivationPathIndex,
            sortingOrder = 0,
            isHidden = false,
            bip44Address = addresses.bip44Address,
            bip49Address = addresses.bip49Address,
            bip84Address = addresses.bip84Address
        )
        val wallet = walletWithoutId.copy(id = addresses.publicKey)

        walletRepository.saveWallet(wallet)
        accountRepository.saveAccount(account)

        accountCreators.forEach {
            it.createAccount(
                accountId = addresses.publicKey,
                derivationPathIndex = 0,
                wallet = wallet
            )
        }

        return wallet
    }

    private fun generateBip39SeedPhrase(wordsCount: Int): List<String> {
        val entropyBytesLength = wordsCount / 3 * 4
        val entropyBytes = generateSecureRandomBytes(entropyBytesLength)
        val entropyBits = entropyBytes.bytesToBits()

        val checksumBytes = entropyBytes.sha256().bytes
        val checksumBits = checksumBytes.bytesToBits()
        val checksumBitsLength = entropyBits.size / 32

        val entropyWithChecksum = BooleanArray(entropyBits.size + checksumBitsLength)
        entropyBits.copyInto(entropyWithChecksum)
        checksumBits.copyInto(entropyWithChecksum, entropyBits.size, endIndex = checksumBitsLength)

        val words = mutableListOf<String>()
        for (i in 0 until wordsCount) {
            // Splitting the entropy + checksum into chunks 11-bit each, then map to BIP39 wordlist
            var index = 0
            for (j in 0..10) {
                index = index shl 1
                if (entropyWithChecksum[i * 11 + j])
                    index = index or 0x1
            }
            words.add(BIP39_WORDLIST_ENGLISH[index])
        }

        return words
    }

    private fun ByteArray.bytesToBits(): BooleanArray {
        val bits = BooleanArray(this.size * 8)
        for (i in this.indices)
            for (j in 0..7) {
                val tmp1 = 1 shl (7 - j)
                val tmp2 = this[i] and tmp1.toByte()

                bits[i * 8 + j] = tmp2 != 0.toByte()
            }
        return bits
    }
}