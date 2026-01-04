package com.mangala.wallet.features.chains.antelope_base.domain.model

import com.ionspin.kotlin.bignum.decimal.toBigDecimal
import com.mangala.wallet.antelope_balance.Balance
import com.mangala.wallet.antelope_balance.BalanceFormatter
import com.mangala.wallet.model.blockchain.BlockchainType
import com.mangala.wallet.utils.Constants
import com.mangala.wallet.utils.calculatingDecimalMode
import com.mangala.wallet.utils.ext.toBigDecimal
import kotlinx.datetime.Instant

data class AntelopeAccount(
    val accountName: String,
    val permissions: List<AntelopeAccountPermission>,
    val isActive: Boolean,
    val isTemp: Boolean, // Temporary account flag (for flow create account by friend, in case their friend generate the account successfully, but they didn't save it yet)
    val createAccountState: CreateAccountState,
    val purchaseToken: String? = null,
    val purchaseId: String? = null, // to keep track of corresponding IAP transaction on iOS
    val coreLiquidBalance: String?,
    val cpuLimit: ResourceLimit?,
    val netLimit: ResourceLimit?,
    val ramQuota: Long?,
    val ramUsage: Long?,
    val rexBalance: String?,
    val selfDelegatedBandwidthCpuWeight: String?,
    val selfDelegatedBandwidthNetWeight: String?,
    val totalResources: TotalResources?, // useful for getting native coin name & precision when account does not have core balance
    val lastUpdated: Instant?,
    val isNotificationRegistered: Boolean,
    val isDeleted: Boolean = false,
) {
    val ramAvailable: Long? =
        if (ramQuota == null || ramUsage == null) null else ramQuota - ramUsage
    val totalRamKilobytes by lazy {
        ramQuota?.toBigDecimal()?.divide(Constants.BYTES_PER_KB.toBigDecimal(), calculatingDecimalMode)
    }
    val ramAvailableKilobytes by lazy {
        ramAvailable?.toBigDecimal()?.divide(Constants.BYTES_PER_KB.toBigDecimal(), calculatingDecimalMode)
    }
    val ramUsageKilobytes by lazy {
        ramUsage?.toBigDecimal()?.divide(Constants.BYTES_PER_KB.toBigDecimal(), calculatingDecimalMode)
    }
    val percentRamUsage by lazy {
        if (ramUsage == null || ramQuota == null) null
        else try {
            ramUsage.toBigDecimal().divide(ramQuota.toBigDecimal(), calculatingDecimalMode)
        } catch (e: Exception) {
            null
        }
    }
    val safeCoreBalance = BalanceFormatter.deserializeOrNull(coreLiquidBalance.orEmpty()) ?: run {
        // For newly created account, the coreLiquidBalance is null. We can use netWeight to get the native coin name & precision instead
        val netWeight = totalResources?.netWeight

        Balance(0.0, netWeight?.symbol.orEmpty(), netWeight?.precision ?: Balance.DEFAULT_PRECISION)
    }

    val delegateCpu = BalanceFormatter.deserializeOrNull(selfDelegatedBandwidthCpuWeight.orEmpty()) ?: run {
        val cpuWeight = totalResources?.cpuWeight

        Balance(0.0, cpuWeight?.symbol.orEmpty(), cpuWeight?.precision ?: Balance.DEFAULT_PRECISION)
    }
    val delegateNet = BalanceFormatter.deserializeOrNull(selfDelegatedBandwidthNetWeight.orEmpty()) ?: run {
        val netWeight = totalResources?.netWeight

        Balance(0.0, netWeight?.symbol.orEmpty(), netWeight?.precision ?: Balance.DEFAULT_PRECISION)
    }

    data class ResourceLimit(
        val max: Long?,
        val available: Long?,
        val currentUsed: Long?
    ) {
        fun getUsedPercentage(): Double {
            if (max == null || max == 0L) return 100.0

            return (currentUsed?.toDouble() ?: 0.0) / max * 100
        }
    }

    data class TotalResources(
        val netWeight: Balance,
        val cpuWeight: Balance
    )

    enum class CreateAccountState {
        IAP_PAYMENT_INITIALIZED,
        IAP_PAYMENT_PENDING,
        IAP_CREATE_ACCOUNT_PENDING,
        IAP_CREATE_ACCOUNT_FAILED_INVALID,
        FRIEND_CREATE_ACCOUNT_PENDING,
        EVM_CREATE_ACCOUNT_INITIALIZED,
        EVM_CREATE_ACCOUNT_FAILED,
        DONE
    }

    companion object {
        private const val PREMIUM_ACCOUNT_SUFFIX = ".man"
        private const val PREMIUM_ACCOUNT_SUFFIX_TESTNET = ".sus"
        const val VALID_LETTERS = "abcdefghijklmnopqrstuvwxyz"
        const val VALID_NUMBERS = "12345"
        const val VALID_CHARACTERS = VALID_LETTERS + VALID_NUMBERS
        const val MAX_LENGTH_ACCOUNT_NAME = 12

        fun isPremiumAccountName(accountName: String): Boolean {
            return accountName.endsWith(PREMIUM_ACCOUNT_SUFFIX) || accountName.endsWith(
                PREMIUM_ACCOUNT_SUFFIX_TESTNET
            )
        }

        fun getPremiumAccountSuffix(blockchainType: BlockchainType): String {
            return if (blockchainType == BlockchainType.EosJungleTestnet) PREMIUM_ACCOUNT_SUFFIX_TESTNET else PREMIUM_ACCOUNT_SUFFIX
        }
    }
}