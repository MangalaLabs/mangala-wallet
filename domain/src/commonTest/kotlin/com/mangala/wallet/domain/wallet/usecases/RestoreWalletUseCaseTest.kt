package com.mangala.wallet.domain.wallet.usecases

import com.mangala.wallet.domain.account.repository.AccountRepository
import com.mangala.wallet.domain.wallet.repository.WalletRepository
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class RestoreWalletUseCaseTest {

    lateinit var walletRepository: WalletRepository
    lateinit var accountRepository: AccountRepository

    @Test
    fun `Given valid 12-word mnemonic_When restore wallet_Then call saveWallet in walletRepository`() = runTest {
        val useCase = initUseCase()
        val mnemonic =
            "lady uncle little surprise brief border marriage ahead labor easily elbow burden".split(" ")

        useCase(mnemonicWords = mnemonic, name = "")

        coVerify { walletRepository.saveWallet(any()) }
    }

    @Test
    fun `Given valid 12-word mnemonic 2_When restore wallet_Then call saveWallet in walletRepository`() = runTest {
        val useCase = initUseCase()
        val mnemonic =
            "outside shiver cruel resource spell certain bridge already plunge permit slice emerge".split(" ")

        useCase(mnemonicWords = mnemonic, name = "")

        coVerify { walletRepository.saveWallet(any()) }
    }

    @Test
    fun `Given valid 12-word mnemonic 3_When restore wallet_Then call saveWallet in walletRepository`() = runTest {
        val useCase = initUseCase()
        val mnemonic =
            "marine child crystal robust expose neglect loan since rookie wise stick magic".split(" ")

        useCase(mnemonicWords = mnemonic, name = "")

        coVerify { walletRepository.saveWallet(any()) }
    }

    @Test
    fun `Given 13-word mnemonic_When restore wallet_Then return fail result with InvalidLength exception`() = runTest {
        val useCase = initUseCase()
        val mnemonic =
            "turkey palace bench shaft laptop race motion device unhappy coffee shed action palace".split(" ")

        val result = useCase(mnemonicWords = mnemonic, name = "")

        assertTrue(result.isFailure)
        assertEquals(RestoreWalletUseCase.Error.InvalidLength, result.exceptionOrNull()!!)
    }

    @Test
    fun `Given 12-word mnemonic with words not in BIP39 word list_When restore wallet_Then return fail result with InvalidWord exception`() = runTest {
        val useCase = initUseCase()
        val mnemonic =
            "turkey palace bench shaft laptop race motion device unhappy coffee shed abcde".split(" ")

        val result = useCase(mnemonicWords = mnemonic, name = "")

        assertTrue(result.isFailure)
        assertEquals(RestoreWalletUseCase.Error.InvalidWord, result.exceptionOrNull()!!)
    }

    @Test
    fun `Given 12-word mnemonic with invalid checksum word_When restore wallet_Then return fail result with InvalidChecksum exception`() = runTest {
        val useCase = initUseCase()
        val mnemonic =
            "turkey palace bench shaft laptop race motion device unhappy coffee shed turkey".split(" ")

        val result = useCase(mnemonicWords = mnemonic, name = "")

        assertTrue(result.isFailure)
        assertEquals(RestoreWalletUseCase.Error.InvalidChecksum, result.exceptionOrNull()!!)
    }

    private fun initUseCase(): RestoreWalletUseCase {
        walletRepository = mockk(relaxed = true)
        accountRepository = mockk(relaxed = true)

        return RestoreWalletUseCase(
            walletRepository = walletRepository,
            accountRepository = accountRepository,
            mapAccountToAccountBlockchainUseCase = mockk(relaxed = true),
            accountCreators = emptyList()
        )
    }
}