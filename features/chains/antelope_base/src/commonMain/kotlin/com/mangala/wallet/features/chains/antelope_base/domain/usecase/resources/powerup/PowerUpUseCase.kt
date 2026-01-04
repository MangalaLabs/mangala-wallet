package com.mangala.wallet.features.chains.antelope_base.domain.usecase.resources.powerup

import com.ionspin.kotlin.bignum.decimal.toBigDecimal
import com.mangala.antelope.base.domain.model.ResourceProviderResponse
import com.mangala.antelope.base.domain.model.Transaction
import com.mangala.wallet.antelope_balance.Balance
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.account.AccountBalanceRefresher
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.account.GetAccountPermissionsUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.resources.powerup.GetPowerUpRateUseCase.PowerUpRate
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.transaction.BaseTransactUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.transaction.ResourceProviderRequestTransactionUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.transaction.SignAndComputeTransactionUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.transaction.SignAndPushResourceProvidedTransactionUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.transaction.SignAndPushTransactionUseCase
import com.mangala.wallet.model.blockchain.BlockchainType
import kotlin.math.max

class PowerUpUseCase(
    private val generatePowerUpSignRequestUseCase: GeneratePowerUpSignRequestUseCase,
    private val getPowerUpRateUseCase: GetPowerUpRateUseCase,
    signAndPushTransactionUseCase: SignAndPushTransactionUseCase,
    signAndComputeTransactionUseCase: SignAndComputeTransactionUseCase,
    getAccountPermissionsUseCase: GetAccountPermissionsUseCase,
    resourceProviderRequestTransactionUseCase: ResourceProviderRequestTransactionUseCase,
    signAndPushResourceProvidedTransactionUseCase: SignAndPushResourceProvidedTransactionUseCase,
    accountBalanceRefresher: AccountBalanceRefresher,
): BaseTransactUseCase(
    signAndPushTransactionUseCase,
    signAndComputeTransactionUseCase,
    getAccountPermissionsUseCase,
    resourceProviderRequestTransactionUseCase,
    signAndPushResourceProvidedTransactionUseCase,
    accountBalanceRefresher
) {
    override val shouldRefreshTokenBalanceAfterTransaction: Boolean = false

    suspend fun requestPowerUpNet(
        blockchainType: BlockchainType,
        powerUpRate: PowerUpRate,
        amount: String,
        senderAccountName: String,
        receiverAccountName: String
    ): Result<ResourceProviderResponse> {
        if (blockchainType == BlockchainType.EosJungleTestnet) {
            return requestPowerUpJungleTestnet(
                blockchainType,
                senderAccountName,
                receiverAccountName,
                powerUpRate.minPowerUpFee.symbol
            )
        }

        val netFrac = getPowerUpRateUseCase.netFracByKilobytes(powerUpRate, amount.toBigDecimal())
        return requestPowerUpNet(
            blockchainType,
            senderAccountName,
            receiverAccountName,
            netFrac.longValue(exactRequired = false),
            getMaxPayment(powerUpRate, amount)
        )
    }

    suspend fun pushPowerUpNet(
        blockchainType: BlockchainType,
        powerUpRate: PowerUpRate,
        amount: String,
        senderAccountName: String,
        receiverAccountName: String,
    ): Result<String> {
        if (blockchainType == BlockchainType.EosJungleTestnet) {
            return pushPowerUpJungleTestnet(
                blockchainType,
                senderAccountName,
                receiverAccountName,
                powerUpRate.minPowerUpFee.symbol
            )
        }

        val netFrac = getPowerUpRateUseCase.netFracByKilobytes(powerUpRate, amount.toBigDecimal())
        return pushPowerUpNet(
            blockchainType,
            senderAccountName,
            receiverAccountName,
            netFrac.longValue(exactRequired = false),
            getMaxPayment(powerUpRate, amount)
        )
    }

    suspend fun requestPowerUpCpu(
        blockchainType: BlockchainType,
        powerUpRate: PowerUpRate,
        amount: String,
        senderAccountName: String,
        receiverAccountName: String
    ): Result<ResourceProviderResponse> {
        if (blockchainType == BlockchainType.EosJungleTestnet) {
            return requestPowerUpJungleTestnet(
                blockchainType,
                senderAccountName,
                receiverAccountName,
                powerUpRate.minPowerUpFee.symbol
            )
        }

        val cpuFrac = getPowerUpRateUseCase.cpuFracByMs(powerUpRate, amount.toBigDecimal())
        return requestPowerUpCpu(
            blockchainType,
            senderAccountName,
            receiverAccountName,
            cpuFrac.longValue(exactRequired = false),
            getMaxPayment(powerUpRate, amount)
        )
    }

    suspend fun pushPowerUpCpu(
        blockchainType: BlockchainType,
        powerUpRate: PowerUpRate,
        amount: String,
        senderAccountName: String,
        receiverAccountName: String,
    ): Result<String> {
        if (blockchainType == BlockchainType.EosJungleTestnet) {
            return pushPowerUpJungleTestnet(
                blockchainType,
                senderAccountName,
                receiverAccountName,
                powerUpRate.minPowerUpFee.symbol
            )
        }

        val cpuFrac = getPowerUpRateUseCase.cpuFracByMs(powerUpRate, amount.toBigDecimal())
        return pushPowerUpCpu(
            blockchainType,
            senderAccountName,
            receiverAccountName,
            cpuFrac.longValue(exactRequired = false),
            getMaxPayment(powerUpRate, amount)
        )
    }

    private suspend fun requestPowerUpNet(
        blockchainType: BlockchainType,
        senderAccountName: String,
        receiverAccountName: String,
        netFrac: Long,
        maxPayment: Balance
    ): Result<ResourceProviderResponse> {
        return requestPowerUp(blockchainType, senderAccountName, receiverAccountName, netFrac, 0, maxPayment)
    }

    private suspend fun pushPowerUpNet(
        blockchainType: BlockchainType,
        senderAccountName: String,
        receiverAccountName: String,
        netFrac: Long,
        maxPayment: Balance
    ): Result<String> {
        return pushPowerUp(blockchainType, senderAccountName, receiverAccountName, netFrac, 0, maxPayment)
    }

    private suspend fun requestPowerUpCpu(
        blockchainType: BlockchainType,
        senderAccountName: String,
        receiverAccountName: String,
        cpuFrac: Long,
        maxPayment: Balance
    ): Result<ResourceProviderResponse> {
        return requestPowerUp(blockchainType, senderAccountName, receiverAccountName, 0, cpuFrac, maxPayment)
    }

    private suspend fun pushPowerUpCpu(
        blockchainType: BlockchainType,
        senderAccountName: String,
        receiverAccountName: String,
        cpuFrac: Long,
        maxPayment: Balance
    ): Result<String> {
        return pushPowerUp(blockchainType, senderAccountName, receiverAccountName, 0, cpuFrac, maxPayment)
    }

    private fun getMaxPayment(powerUpRate: PowerUpRate, amount: String): Balance {
        val calculatedPowerUpRate = powerUpRate.rate
            .times(amount.toBigDecimal())
            .times(MAX_PAYMENT_BUFFER.toBigDecimal())
            .doubleValue(exactRequired = false)

        return Balance(
            max(calculatedPowerUpRate, powerUpRate.minPowerUpFee.amount),
            powerUpRate.minPowerUpFee.symbol
        )
    }

    private suspend fun pushPowerUpJungleTestnet(
        blockchainType: BlockchainType,
        senderAccountName: String,
        receiverAccountName: String,
        currencySymbol: String
    ): Result<String> {
        return pushPowerUp(
            blockchainType,
            senderAccountName,
            receiverAccountName,
            JUNGLE_TESTNET_NET_FRAC,
            JUNGLE_TESTNET_CPU_FRAC,
            Balance(JUNGLE_TESTNET_MAX_PAYMENT_AMOUNT, currencySymbol)
        )
    }

    private suspend fun requestPowerUpJungleTestnet(
        blockchainType: BlockchainType,
        senderAccountName: String,
        receiverAccountName: String,
        currencySymbol: String
    ): Result<ResourceProviderResponse> {
        return requestPowerUp(
            blockchainType,
            senderAccountName,
            receiverAccountName,
            JUNGLE_TESTNET_NET_FRAC,
            JUNGLE_TESTNET_CPU_FRAC,
            Balance(JUNGLE_TESTNET_MAX_PAYMENT_AMOUNT, currencySymbol)
        )
    }

    private suspend fun pushPowerUp(
        blockchainType: BlockchainType,
        senderAccountName: String,
        receiverAccountName: String,
        netFrac: Long,
        cpuFrac: Long,
        maxPayment: Balance
    ): Result<String> {
        return constructAndPushTransaction(
            blockchainType,
            senderAccountName
        ) { permission ->
            generatePowerUpSignRequestUseCase(
                blockchainType = blockchainType,
                senderAccountName = senderAccountName.trim(),
                signingPermissionName = permission.permissionType.permissionName,
                receiverAccountName = receiverAccountName,
                days = 1,
                netFrac,
                cpuFrac,
                maxPayment
            )
        }
    }

    private suspend fun requestPowerUp(
        blockchainType: BlockchainType,
        senderAccountName: String,
        receiverAccountName: String,
        netFrac: Long,
        cpuFrac: Long,
        maxPayment: Balance
    ): Result<ResourceProviderResponse> {
        return constructAndRequestTransaction(
            blockchainType,
            senderAccountName
        ) { permission ->
            generatePowerUpSignRequestUseCase(
                blockchainType = blockchainType,
                senderAccountName = senderAccountName.trim(),
                signingPermissionName = permission.permissionType.permissionName,
                receiverAccountName = receiverAccountName,
                days = 1,
                netFrac,
                cpuFrac,
                maxPayment
            )
        }
    }

    companion object {
        private const val JUNGLE_TESTNET_NET_FRAC = 20000000000L
        private const val JUNGLE_TESTNET_CPU_FRAC = 80000000000L
        private const val JUNGLE_TESTNET_MAX_PAYMENT_AMOUNT = 10.0
        private const val MAX_PAYMENT_BUFFER = 1.2
    }
}