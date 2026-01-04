package com.mangala.wallet.features.chains.antelope_base.domain.usecase.ram

import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.mangala.wallet.features.chains.antelope_base.domain.model.ram.AntelopeRamMarketInfo
import com.mangala.wallet.features.chains.antelope_base.domain.repository.ram.AntelopeRamMarketRepository
import com.mangala.wallet.model.blockchain.BlockchainType
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Test
import kotlin.test.assertEquals

class GetRamPriceUseCaseTest {

    private val antelopeRamMarketRepository = mockk<AntelopeRamMarketRepository>()
    val sut = GetRamPriceUseCase(antelopeRamMarketRepository)

    @Test
    fun `Given RAM data API calls success, when calculate RAM price, then return correct RAM price`() = runTest {
        val blockchainType = BlockchainType.Eos
        val forceRefresh = true
        val supply = "10000000000.0000 RAMCORE"
        val base = AntelopeRamMarketInfo.Pair(
            balance = "59527891562 RAM",
            weight = 0.5
        )
        val quote = AntelopeRamMarketInfo.Pair(
            balance = "11544085.4082 EOS",
            weight = 0.5
        )
        coEvery { antelopeRamMarketRepository.getRamPrice(blockchainType, forceRefresh) } returns Result.success(
            AntelopeRamMarketInfo(
                supply = supply,
                base = base,
                quote = quote
            )
        )

        val result = sut(BlockchainType.Eos, forceRefresh)

        assertEquals(BigDecimal.parseString("0.19858159171797209235"), result?.price)
        assertEquals("EOS", result?.currency)
        assertEquals(59527891562, result?.unallocatedRam)
        assertEquals(BigDecimal.parseString("11544085.4082"), result?.eosPool)
        assertEquals(BigDecimal.parseString("10000000000.0000"), result?.supplyRamCore)
    }

    @Test
    fun `Given RAM data API calls success 2, when calculate RAM price, then return correct RAM price`() = runTest {
        val blockchainType = BlockchainType.Eos
        val forceRefresh = true
        val supply = "10000000000.0000 RAMCORE"
        val base = AntelopeRamMarketInfo.Pair(
            balance = "61787465527 RAM",
            weight = 0.5
        )
        val quote = AntelopeRamMarketInfo.Pair(
            balance = "30866593.6732 EOS",
            weight = 0.5
        )
        coEvery { antelopeRamMarketRepository.getRamPrice(blockchainType, forceRefresh) } returns Result.success(
            AntelopeRamMarketInfo(
                supply = supply,
                base = base,
                quote = quote
            )
        )

        val result = sut(BlockchainType.Eos, forceRefresh)

        assertEquals(BigDecimal.parseString("0.51155022546676467757"), result?.price)
        assertEquals("EOS", result?.currency)
        assertEquals(61787465527, result?.unallocatedRam)
        assertEquals(BigDecimal.parseString("30866593.6732"), result?.eosPool)
        assertEquals(BigDecimal.parseString("10000000000.0000"), result?.supplyRamCore)
    }
}