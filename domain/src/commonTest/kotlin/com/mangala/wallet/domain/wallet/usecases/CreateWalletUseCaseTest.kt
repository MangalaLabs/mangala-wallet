package com.mangala.wallet.domain.wallet.usecases

import com.mangala.wallet.cryptography.generateSecureRandomBytes
import com.mangala.wallet.domain.account.repository.AccountRepository
import com.mangala.wallet.domain.wallet.repository.WalletRepository
import com.mangala.wallet.model.blockchain.BlockchainType
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class CreateWalletUseCaseTest {

    lateinit var walletRepository: WalletRepository
    lateinit var accountRepository: AccountRepository

//    @Test
//    fun `Given certain secure random and wordsCount equals 12 When invoke Then generate valid mnemonic`() = runTest {
//        mockkStatic(::generateSecureRandomBytes)
//        every { generateSecureRandomBytes(16) } returns byteArrayOf(-9, -71, -102, -117, -64, 124, -62, -39, -9, 31, -126, -48, -123, 47, -75, -56)
//
//        val result = initUseCase().invoke(wordsCount = 12, passphrase = "", BlockchainType.Ethereum)
//
//        assertEquals("waste smoke pepper liberty slow hollow symbol utility spatial city uniform music", result.words)
//    }
//
//    @Test
//    fun `Given certain secure random and wordsCount equals 24 When invoke Then generate valid mnemonic`() = runTest {
//        mockkStatic(::generateSecureRandomBytes)
//        every { generateSecureRandomBytes(32) } returns byteArrayOf(56, -84, 53, -80, -5, 8, -110, -101, 31, -22, 73, -50, 42, 69, -63, 24, 122, -4, -16, -63, 35, -91, 49, 52, -125, -9, 7, -42, -70, -70, 20, 115)
//
//        val result = initUseCase().invoke(wordsCount = 24, passphrase = "", BlockchainType.Ethereum)
//
//        assertEquals("december giant history voyage matrix eternal leisure empty soda fall icon couch quit journey afraid input couple piano worry autumn hill fringe fade ice", result.words)
//    }

    private fun initUseCase(): CreateWalletUseCase {
        walletRepository = mockk(relaxed = true)
        accountRepository = mockk(relaxed = true)

        return CreateWalletUseCase(
            walletRepository = walletRepository,
            accountRepository = accountRepository,
            mapAccountToAccountBlockchainUseCase = mockk(relaxed = true),
            accountCreators = emptyList()
        )
    }
}