package com.mangala.wallet.domain.wallet.usecases

import com.mangala.wallet.domain.account.repository.AccountRepository
import com.mangala.wallet.domain.wallet.repository.WalletRepository
import com.mangala.wallet.domain.wallet.usecases.account.AccountCreator
import com.mangala.wallet.model.account.domain.AccountModel
import com.mangala.wallet.model.account.domain.AccountType
import com.mangala.wallet.model.blockchain.BlockchainType
import com.mangala.wallet.model.wallet.domain.WalletModel
import com.mangala.wallet.utils.bip39.BIP39_WORDLIST_ENGLISH
import com.soywiz.krypto.sha256
import kotlin.experimental.or

class RestoreWalletUseCase(
    private val accountRepository: AccountRepository,
    private val walletRepository: WalletRepository,
    private val mapAccountToAccountBlockchainUseCase: MapAccountToAccountBlockchainUseCase,
    private val accountCreators: List<AccountCreator>
) {
    suspend operator fun invoke(
        mnemonicWords: List<String>,
        name: String,
        blockchainType: BlockchainType = BlockchainType.Ethereum
    ): Result<WalletModel> {
        val verifyWalletResult = verifyWallet(mnemonicWords)
        if (verifyWalletResult.isFailure) return Result.failure(verifyWalletResult.exceptionOrNull() ?: Error.Unknown)

        val formattedMnemonicWords = verifyWalletResult.getOrNull() ?: mnemonicWords.map { it.trim().lowercase() }

        val walletWithoutId = WalletModel(
            id = "",
            name = name,
            words = formattedMnemonicWords.joinToString(" "),
            passphrase = "",
            key = "",
            isBackedUp = false,
            isSelected = true
        )

        val derivationPathIndex = 0
        val addresses = mapAccountToAccountBlockchainUseCase(derivationPathIndex, walletWithoutId, BlockchainType.Ethereum)
        val account = AccountModel(
            id = addresses.publicKey,
            name = name,
            type = AccountType.NORMAL,
            walletId = addresses.publicKey,
            derivationPathIndex = 0,
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

        return Result.success(wallet)
    }

    fun verifyWallet(mnemonicWords: List<String>): Result<List<String>> {
        val formattedMnemonicWords = mnemonicWords.map { it.trim().lowercase() }
        if (isValidMnemonicLength(formattedMnemonicWords).not()) return Result.failure(Error.InvalidLength)
        if (isValidMnemonicWords(formattedMnemonicWords).not()) return Result.failure(Error.InvalidWord)
        if (isValidMnemonicChecksum(formattedMnemonicWords).not()) return Result.failure(Error.InvalidChecksum)

        return Result.success(formattedMnemonicWords)
    }

    private fun isValidMnemonicLength(mnemonicWords: List<String>): Boolean {
        return mnemonicWords.size in setOf(12, 15, 18, 21, 24)
    }

    private fun isValidMnemonicWords(mnemonicWords: List<String>): Boolean {
        return BIP39_WORDLIST_ENGLISH.containsAll(mnemonicWords)
    }

    private fun isValidMnemonicChecksum(mnemonicWords: List<String>): Boolean {
        val entropyLength = (mnemonicWords.size * 11) * 32 / 33
        val checksumLength = entropyLength / 32

        val entropy = ByteArray(entropyLength / 8)
        val checksumBits = mutableListOf<Boolean>()

        var bitsProcessed = 0
        var nextByte = 0.toByte()
        mnemonicWords.forEach {
            BIP39_WORDLIST_ENGLISH.indexOf(it).let { phraseIndex ->
                // for each of the 11 bits of the phraseIndex
                (10 downTo 0).forEach { i ->
                    // isolate the next bit (starting from the big end)
                    val bit = phraseIndex and (1 shl i) != 0
                    // if the bit is set, then update the corresponding bit in the nextByte
                    if (bit) nextByte = nextByte or (1 shl 7 - (bitsProcessed).rem(8)).toByte()
                    val entropyIndex = ((++bitsProcessed) - 1) / 8
                    // if we're at a byte boundary (excluding the extra checksum bits)
                    if (bitsProcessed.rem(8) == 0 && entropyIndex < entropy.size) {
                        // then set the byte and prepare to process the next byte
                        entropy[entropyIndex] = nextByte
                        nextByte = 0.toByte()
                        // if we're now processing checksum bits, then track them for later
                    } else if (entropyIndex >= entropy.size) {
                        checksumBits.add(bit)
                    }
                }
            }
        }

        // Check each required checksum bit, against the first byte of the sha256 of entropy
        entropy.sha256().bytes[0].toBits().let { hashFirstByteBits ->
            repeat(checksumLength) { i ->
                // failure means that each word was valid BUT they were in the wrong order
                if (hashFirstByteBits[i] != checksumBits[i]) return false
            }
        }

        return true
    }

    private fun Byte.toBits(): List<Boolean> = (7 downTo 0).map { (toInt() and (1 shl it)) != 0 }

    sealed class Error: Throwable() {
        data object InvalidLength: Error()
        data object InvalidWord: Error()
        data object InvalidChecksum: Error()
        data object Unknown: Error()
    }
}