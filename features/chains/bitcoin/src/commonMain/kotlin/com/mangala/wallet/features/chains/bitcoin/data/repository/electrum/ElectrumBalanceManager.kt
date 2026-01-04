package com.mangala.wallet.features.chains.bitcoin.data.repository.electrum

import com.mangala.wallet.features.chains.bitcoin.data.remote.electrum.ElectrumManager
import com.mangala.wallet.model.blockchain.BlockchainType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock

class ElectrumBalanceManager(
    private val electrumManager: ElectrumManager,
    private val scope: CoroutineScope = MainScope()
) {
    data class WalletBalance(
        val confirmedBalance: Long = 0,
        val unconfirmedBalance: Long = 0,
        val totalBalance: Long = confirmedBalance + unconfirmedBalance,
        val addresses: Map<String, ElectrumManager.AddressBalance> = emptyMap(),
        val lastUpdated: Long = Clock.System.now().toEpochMilliseconds()
    )

    private val _walletAddresses = MutableStateFlow<Set<String>>(emptySet())
    private val _walletBalance = MutableStateFlow(WalletBalance())
    val walletBalance: StateFlow<WalletBalance> = _walletBalance

    // Subscribe to Electrum notifications to update wallet balance
    init {
        scope.launch {
            electrumManager.electrumHeaders.collect {
                // When a new block is found, refresh the wallet balance
                updateWalletBalance(BlockchainType.BitcoinTestnet4) // TODO: Need to pass the correct blockchain type
            }
        }
    }

    // Add a new address to track
    suspend fun addAddress(blockchainType: BlockchainType, address: String) {
        if (_walletAddresses.value.contains(address)) return

        _walletAddresses.update { it + address }

        // Subscribe to changes for this address
        try {
            electrumManager.subscribeToAddress(blockchainType, address)
            updateAddressBalance(blockchainType, address)
        } catch (e: Exception) {
            println("ElectrumBalanceManager $e")
            // Handle connection errors
        }
    }

    // Add multiple addresses at once
    suspend fun addAddresses(blockchainType: BlockchainType, addresses: List<String>) {
        val newAddresses = addresses.filter { !_walletAddresses.value.contains(it) }
        if (newAddresses.isEmpty()) return

        _walletAddresses.update { it + newAddresses }

        // Subscribe to each address
        newAddresses.forEach { address ->
            try {
                electrumManager.subscribeToAddress(blockchainType, address)
            } catch (e: Exception) {
                // Handle errors
            }
        }

        // Update overall wallet balance
        updateWalletBalance(blockchainType)
    }

    // Update balance for a specific address
    private suspend fun updateAddressBalance(blockchainType: BlockchainType, address: String) {
        try {
            val balance = electrumManager.getAddressBalance(blockchainType, address)

            // Update the wallet balance map
            _walletBalance.update { currentBalance ->
                val updatedAddresses = currentBalance.addresses.toMutableMap()
                updatedAddresses[address] = balance

                // Recalculate total balance
                val confirmedSum = updatedAddresses.values.sumOf { it.confirmed }
                val unconfirmedSum = updatedAddresses.values.sumOf { it.unconfirmed }

                currentBalance.copy(
                    confirmedBalance = confirmedSum,
                    unconfirmedBalance = unconfirmedSum,
                    addresses = updatedAddresses,
                    lastUpdated = Clock.System.now().toEpochMilliseconds()
                )
            }
        } catch (e: Exception) {
            // Handle errors
        }
    }

    // Update all addresses and recalculate wallet balance
    suspend fun updateWalletBalance(blockchainType: BlockchainType) {
        val addresses = _walletAddresses.value
        val addressBalances = mutableMapOf<String, ElectrumManager.AddressBalance>()

        for (address in addresses) {
            try {
                val balance = electrumManager.getAddressBalance(blockchainType, address)
                addressBalances[address] = balance
            } catch (e: Exception) {
                // Continue with other addresses if one fails
                println("ElectrumBalanceManager $e")
            }
        }

        val confirmedSum = addressBalances.values.sumOf { it.confirmed }
        val unconfirmedSum = addressBalances.values.sumOf { it.unconfirmed }

        _walletBalance.value = WalletBalance(
            confirmedBalance = confirmedSum,
            unconfirmedBalance = unconfirmedSum,
            addresses = addressBalances,
            lastUpdated = Clock.System.now().toEpochMilliseconds()
        )
    }
}