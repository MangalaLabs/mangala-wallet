package com.mangala.wallet.features.chains.antelope_base.domain.usecase.rex

import com.mangala.antelope.base.domain.model.ResourceProviderResponse
import com.mangala.wallet.antelope_balance.Balance
import com.mangala.wallet.antelope_balance.BalanceFormatter
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.account.AccountBalanceRefresher
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.account.GetAccountPermissionsUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.transaction.BaseTransactUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.transaction.ResourceProviderRequestTransactionUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.transaction.SignAndComputeTransactionUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.transaction.SignAndPushResourceProvidedTransactionUseCase
import com.mangala.wallet.features.chains.antelope_base.domain.usecase.transaction.SignAndPushTransactionUseCase
import com.mangala.wallet.model.blockchain.BlockchainType

class DelegateAndUnDelegateBandwidthUseCase(
    private val generateDelegateBandwidthUseCase: GenerateDelegateBandwidthUseCase,
    private val generateUnDelegateBandwidthUseCase: GenerateUnDelegateBandwidthUseCase,
    signAndPushTransactionUseCase: SignAndPushTransactionUseCase,
    signAndComputeTransactionUseCase: SignAndComputeTransactionUseCase,
    getAccountPermissionsUseCase: GetAccountPermissionsUseCase,
    resourceProviderRequestTransactionUseCase: ResourceProviderRequestTransactionUseCase,
    signAndPushResourceProvidedTransactionUseCase: SignAndPushResourceProvidedTransactionUseCase,
    accountBalanceRefresher: AccountBalanceRefresher
): BaseTransactUseCase(
    signAndPushTransactionUseCase,
    signAndComputeTransactionUseCase,
    getAccountPermissionsUseCase,
    resourceProviderRequestTransactionUseCase,
    signAndPushResourceProvidedTransactionUseCase,
    accountBalanceRefresher
) {

    override val shouldRefreshTokenBalanceAfterTransaction: Boolean = false

    suspend fun requestDelegateBandwidthCpu(
        blockchainType: BlockchainType,
        accountName: String,
        stakeCpuQuantity: Double,
        symbol: String,
        precision: Int
    ): Result<ResourceProviderResponse> {
        val netAmount = Balance(0.0, symbol, precision)
        val cpuAmount = Balance(stakeCpuQuantity, symbol, precision)

        return requestDelegateBandwidth(
            blockchainType,
            accountName,
            BalanceFormatter.formatEosBalance(cpuAmount, ignoreLocale = true),
            BalanceFormatter.formatEosBalance(netAmount, ignoreLocale = true),
            transfer = 0
        )
    }

    suspend fun pushDelegateBandwidthCpu(
        blockchainType: BlockchainType,
        accountName: String,
        stakeCpuQuantity: Double,
        symbol: String,
        precision: Int
    ): Result<String> {
        val netAmount = Balance(0.0, symbol, precision)
        val cpuAmount = Balance(stakeCpuQuantity, symbol, precision)

        return pushDelegateBandwidth(
            blockchainType,
            accountName,
            BalanceFormatter.formatEosBalance(cpuAmount, ignoreLocale = true),
            BalanceFormatter.formatEosBalance(netAmount, ignoreLocale = true),
            transfer = 0
        )
    }

    suspend fun requestDelegateBandwidthNet(
        blockchainType: BlockchainType,
        accountName: String,
        stakeNetQuantity: Double,
        symbol: String,
        precision: Int
    ): Result<ResourceProviderResponse> {
        val netAmount = Balance(stakeNetQuantity, symbol, precision)
        val cpuAmount = Balance(0.0, symbol, precision)

        return requestDelegateBandwidth(
            blockchainType,
            accountName,
            BalanceFormatter.formatEosBalance(cpuAmount, ignoreLocale = true),
            BalanceFormatter.formatEosBalance(netAmount, ignoreLocale = true),
            transfer = 0
        )
    }

    suspend fun pushDelegateBandwidthNet(
        blockchainType: BlockchainType,
        accountName: String,
        stakeNetQuantity: Double,
        symbol: String,
        precision: Int
    ): Result<String> {
        val netAmount = Balance(stakeNetQuantity, symbol, precision)
        val cpuAmount = Balance(0.0, symbol, precision)

        return pushDelegateBandwidth(
            blockchainType,
            accountName,
            BalanceFormatter.formatEosBalance(cpuAmount, ignoreLocale = true),
            BalanceFormatter.formatEosBalance(netAmount, ignoreLocale = true),
            transfer = 0
        )
    }

    suspend fun requestUnDelegateBandwidthNet(
        blockchainType: BlockchainType,
        accountName: String,
        stakeNetQuantity: Double,
        symbol: String,
        precision: Int
    ): Result<ResourceProviderResponse> {
        val netAmount = Balance(stakeNetQuantity, symbol, precision)
        val cpuAmount = Balance(0.0, symbol, precision)

        return requestUnDelegateBandwidth(
            blockchainType,
            accountName,
            BalanceFormatter.formatEosBalance(cpuAmount, ignoreLocale = true),
            BalanceFormatter.formatEosBalance(netAmount, ignoreLocale = true),
        )
    }

    suspend fun pushUnDelegateBandwidthNet(
        blockchainType: BlockchainType,
        accountName: String,
        stakeNetQuantity: Double,
        symbol: String,
        precision: Int
    ): Result<String> {
        val netAmount = Balance(stakeNetQuantity, symbol, precision)
        val cpuAmount = Balance(0.0, symbol, precision)

        return pushUnDelegateBandwidth(
            blockchainType,
            accountName,
            BalanceFormatter.formatEosBalance(cpuAmount, ignoreLocale = true),
            BalanceFormatter.formatEosBalance(netAmount, ignoreLocale = true),
        )
    }

    suspend fun requestUnDelegateBandwidthCpu(
        blockchainType: BlockchainType,
        accountName: String,
        stakeCpuQuantity: Double,
        symbol: String,
        precision: Int
    ): Result<ResourceProviderResponse> {
        val netAmount = Balance(0.0, symbol, precision)
        val cpuAmount = Balance(stakeCpuQuantity, symbol, precision)

        return requestUnDelegateBandwidth(
            blockchainType,
            accountName,
            BalanceFormatter.formatEosBalance(cpuAmount, ignoreLocale = true),
            BalanceFormatter.formatEosBalance(netAmount, ignoreLocale = true),
        )
    }

    suspend fun pushUnDelegateBandwidthCpu(
        blockchainType: BlockchainType,
        accountName: String,
        stakeCpuQuantity: Double,
        symbol: String,
        precision: Int
    ): Result<String> {

        val netAmount = Balance(0.0, symbol, precision)
        val cpuAmount = Balance(stakeCpuQuantity, symbol, precision)

        return pushUnDelegateBandwidth(
            blockchainType,
            accountName,
            BalanceFormatter.formatEosBalance(cpuAmount, ignoreLocale = true),
            BalanceFormatter.formatEosBalance(netAmount, ignoreLocale = true),
        )
    }

    private suspend fun requestDelegateBandwidth(
        blockchainType: BlockchainType,
        accountName: String,
        stakeCpuQuantity: String,
        stakeNetQuantity: String,
        transfer: Int
    ): Result<ResourceProviderResponse> {
        return constructAndRequestTransaction(
            blockchainType,
            accountName
        ) { permission ->
            generateDelegateBandwidthUseCase(
                blockchainType = blockchainType,
                senderAccountName = accountName,
                signingPermissionName = permission.permissionType.permissionName,
                receiverAccountName = accountName,
                stakeCpuQuantity = stakeCpuQuantity,
                stakeNetQuantity = stakeNetQuantity,
                transfer = transfer
            )
        }
    }

    private suspend fun pushDelegateBandwidth(
        blockchainType: BlockchainType,
        accountName: String,
        stakeCpuQuantity: String,
        stakeNetQuantity: String,
        transfer: Int
    ): Result<String> {
        return constructAndPushTransaction(
            blockchainType,
            accountName
        ) { permission ->
            generateDelegateBandwidthUseCase(
                blockchainType = blockchainType,
                senderAccountName = accountName,
                signingPermissionName = permission.permissionType.permissionName,
                receiverAccountName = accountName,
                stakeCpuQuantity = stakeCpuQuantity,
                stakeNetQuantity = stakeNetQuantity,
                transfer = transfer
            )
        }
    }

    private suspend fun requestUnDelegateBandwidth(
        blockchainType: BlockchainType,
        accountName: String,
        stakeCpuQuantity: String,
        stakeNetQuantity: String,
    ): Result<ResourceProviderResponse> {
        return constructAndRequestTransaction(
            blockchainType,
            accountName
        ) { permission ->
            generateUnDelegateBandwidthUseCase(
                blockchainType = blockchainType,
                senderAccountName = accountName,
                signingPermissionName = permission.permissionType.permissionName,
                receiverAccountName = accountName,
                stakeCpuQuantity = stakeCpuQuantity,
                stakeNetQuantity = stakeNetQuantity,
            )
        }
    }

    private suspend fun pushUnDelegateBandwidth(
        blockchainType: BlockchainType,
        accountName: String,
        stakeCpuQuantity: String,
        stakeNetQuantity: String,
    ): Result<String> {
        return constructAndPushTransaction(
            blockchainType,
            accountName
        ) { permission ->
            generateUnDelegateBandwidthUseCase(
                blockchainType = blockchainType,
                senderAccountName = accountName,
                signingPermissionName = permission.permissionType.permissionName,
                receiverAccountName = accountName,
                stakeCpuQuantity = stakeCpuQuantity,
                stakeNetQuantity = stakeNetQuantity,
            )
        }
    }
}