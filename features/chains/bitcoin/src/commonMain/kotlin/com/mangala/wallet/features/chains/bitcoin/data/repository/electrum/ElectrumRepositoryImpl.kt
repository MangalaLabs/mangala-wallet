package com.mangala.wallet.features.chains.bitcoin.data.repository.electrum

import com.mangala.wallet.features.chains.bitcoin.data.local.balance.BitcoinBalanceLocalDataSource
import com.mangala.wallet.features.chains.bitcoin.data.remote.electrum.ElectrumConnectionManager
import com.mangala.wallet.features.chains.bitcoin.data.remote.electrum.ElectrumManager
import com.mangala.wallet.features.chains.bitcoin.data.repository.electrum.mapper.toBitcoinBalance
import com.mangala.wallet.features.chains.bitcoin.domain.model.balance.BitcoinBalance
import com.mangala.wallet.features.chains.bitcoin.domain.model.utxo.BitcoinUtxo
import com.mangala.wallet.features.chains.bitcoin.domain.repository.electrum.ElectrumRepository
import com.mangala.wallet.model.blockchain.BlockchainType
import com.mangala.wallet.model.util.Resource
import com.mangala.wallet.remote.di.ApiResponse
import com.mangala.wallet.remote.utils.networkBoundResource
import fr.acinq.lightning.utils.Connection
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.datetime.Clock

class ElectrumRepositoryImpl(
    private val electrumManager: ElectrumManager,
    private val electrumConnectionManager: ElectrumConnectionManager,
    private val electrumBalanceManager: ElectrumBalanceManager,
    private val bitcoinBalanceLocalDataSource: BitcoinBalanceLocalDataSource
) : ElectrumRepository {

    override val connectionStatus: Flow<Connection> = electrumConnectionManager.connectionStatus

    override val walletBalance: StateFlow<ElectrumManager.WalletBalance>
        get() = _walletBalance.asStateFlow()

    private val _walletBalance = MutableStateFlow(
        ElectrumManager.WalletBalance(
            confirmedBalance = 0,
            unconfirmedBalance = 0,
            lastUpdated = Clock.System.now().toEpochMilliseconds()
        )
    )

    override fun startConnection(blockchainType: BlockchainType) {
        electrumConnectionManager.startConnectionLoop(blockchainType)
    }

    override fun stopConnection() {
        electrumConnectionManager.stopConnectionLoop()
    }

    override suspend fun addAddress(blockchainType: BlockchainType, address: String) {
        electrumBalanceManager.addAddress(blockchainType, address)

        val balance = electrumBalanceManager.walletBalance.value

        _walletBalance.value = ElectrumManager.WalletBalance(
            confirmedBalance = balance.confirmedBalance,
            unconfirmedBalance = balance.unconfirmedBalance,
            addresses = balance.addresses,
            lastUpdated = balance.lastUpdated
        )
    }

    override suspend fun addAddresses(blockchainType: BlockchainType, addresses: List<String>) {
        electrumBalanceManager.addAddresses(blockchainType, addresses)

        // Update wallet balance after adding addresses
        val balance = electrumBalanceManager.walletBalance.value

        _walletBalance.value = ElectrumManager.WalletBalance(
            confirmedBalance = balance.confirmedBalance,
            unconfirmedBalance = balance.unconfirmedBalance,
            addresses = balance.addresses,
            lastUpdated = balance.lastUpdated
        )
    }

    override suspend fun getAddressBalance(
        blockchainType: BlockchainType,
        address: String
    ): ElectrumManager.AddressBalance {
        return electrumManager.getAddressBalance(blockchainType, address)
    }

    override suspend fun getAddressUtxos(
        blockchainType: BlockchainType,
        address: String
    ): List<BitcoinUtxo> {
        return electrumManager.getAddressUnspentOutputs(blockchainType, address)
    }

    override suspend fun updateWalletBalance(blockchainType: BlockchainType) {
        electrumBalanceManager.updateWalletBalance(blockchainType)

        // Update our state flow with the latest balance
        val balance = electrumBalanceManager.walletBalance.value

        _walletBalance.value = ElectrumManager.WalletBalance(
            confirmedBalance = balance.confirmedBalance,
            unconfirmedBalance = balance.unconfirmedBalance,
            addresses = balance.addresses,
            lastUpdated = balance.lastUpdated
        )
    }

    override fun getBalance(
        forceRefresh: Boolean,
        accountId: String,
        bip84Address: String,
        blockchainType: BlockchainType
    ): Flow<Resource<BitcoinBalance?>> {
        return networkBoundResource(
            query = {
                bitcoinBalanceLocalDataSource.getBalance(accountId, blockchainType)
            },
            fetch = {
                startConnection(blockchainType)
                addAddress(blockchainType, bip84Address)
                updateWalletBalance(blockchainType)
                ApiResponse.Success(walletBalance.value)
            },
            saveFetchResult = {
                val balance = it

                bitcoinBalanceLocalDataSource.insertOrUpdateBalance(
                    accountId = accountId,
                    blockchainType = blockchainType,
                    confirmedBalance = balance.addresses[bip84Address]?.confirmed ?: 0,
                    unconfirmedBalance = balance.addresses[bip84Address]?.unconfirmed ?: 0,
                    lastUpdated = balance.lastUpdated
                )
            },
            shouldFetch = {
                if (it == null || forceRefresh) return@networkBoundResource true

                val currentTime = Clock.System.now().toEpochMilliseconds()

                currentTime - it.last_updated > BALANCE_CACHE_EXPIRATION_TIME_MILLIS
            },
            entityToDomain = {
                it?.toBitcoinBalance()
            }
        )
    }

    companion object {
        private const val BALANCE_CACHE_EXPIRATION_TIME_MILLIS = 5 * 60 * 1000 // 5 minutes
    }
}