package com.mangala.wallet.features.chains.antelope_base.domain.model

import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.mangala.wallet.features.chains.antelope_base.domain.model.AntelopeAccount.CreateAccountState
import com.mangala.wallet.features.chains.antelope_base.domain.model.AntelopeAccount.ResourceLimit
import com.mangala.wallet.features.chains.antelope_base.domain.model.AntelopeAccount.TotalResources
import kotlinx.datetime.Instant
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class AntelopeAccountTest {
    @Test
    fun `Given AntelopeAccount with null ramUsage, when calculating ramAvailableKilobytes, then return null`() {
        val sut = buildAntelopeAccount(
            ramUsage = null,
            ramQuota = 1234
        )

        val result = sut.ramAvailableKilobytes

        assertNull(sut.ramAvailable)
        assertNull(result)
    }

    @Test
    fun `Given AntelopeAccount with null ramUsage, when calculating ramUsageKilobytes, then return null`() {
        val sut = buildAntelopeAccount(
            ramUsage = null
        )

        assertNull(sut.ramUsageKilobytes)
    }

    @Test
    fun `Given AntelopeAccount with ramUsage, when calculating ramUsageKilobytes, then return null`() {
        val sut = buildAntelopeAccount(
            ramUsage = 7589
        )

        assertEquals(BigDecimal.parseString("7.4111328125"), sut.ramUsageKilobytes)
    }

    @Test
    fun `Given AntelopeAccount with null ramQuota, when calculating ramAvailableKilobytes, then return null`() {
        val sut = buildAntelopeAccount(
            ramQuota = null,
            ramUsage = 7589
        )

        val result = sut.ramAvailableKilobytes

        assertNull(sut.ramAvailable)
        assertNull(result)
    }

    @Test
    fun `Given AntelopeAccount with ramQuota and ramUsage, when calculating ramAvailableKilobytes and ramAvailablePercentage, then return correct result`() {
        val sut = buildAntelopeAccount(
            ramQuota = 107923,
            ramUsage = 7589
        )

        val result = sut.ramAvailableKilobytes
        val percentRamUsed = sut.percentRamUsage

        assertEquals(100334, sut.ramAvailable)
        assertEquals(BigDecimal.parseString("97.982421875"), result)
//        assertEquals(BigDecimal.parseString("0.07318653113794093937"), percentRamUsed)
    }

    private fun buildAntelopeAccount(
        accountName: String = "",
        permissions: List<AntelopeAccountPermission> = emptyList(),
        isActive: Boolean = false,
        isTemp: Boolean = false,
        createAccountState: CreateAccountState = CreateAccountState.DONE,
        purchaseToken: String? = null,
        coreLiquidBalance: String? = null,
        cpuLimit: ResourceLimit? = null,
        netLimit: ResourceLimit? = null,
        ramQuota: Long? = null,
        ramUsage: Long? = null,
        rexBalance: String? = null,
        selfDelegatedBandwidthCpuWeight: String? = null,
        selfDelegatedBandwidthNetWeight: String? = null,
        totalResources: TotalResources? = null,
        lastUpdated: Instant? = null,
        isNotificationRegistered: Boolean = true
    ) = AntelopeAccount(
        accountName = accountName,
        permissions = permissions,
        isActive = isActive,
        isTemp = isTemp,
        createAccountState = createAccountState,
        purchaseToken = purchaseToken,
        purchaseId = null,
        coreLiquidBalance,
        cpuLimit,
        netLimit,
        ramQuota,
        ramUsage,
        rexBalance,
        selfDelegatedBandwidthCpuWeight,
        selfDelegatedBandwidthNetWeight,
        totalResources,
        lastUpdated,
        isNotificationRegistered
    )
}