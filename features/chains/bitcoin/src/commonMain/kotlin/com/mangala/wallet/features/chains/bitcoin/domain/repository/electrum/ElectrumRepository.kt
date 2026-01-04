package com.mangala.wallet.features.chains.bitcoin.domain.repository.electrum

import com.mangala.wallet.features.chains.bitcoin.data.remote.electrum.ElectrumManager
import com.mangala.wallet.features.chains.bitcoin.domain.model.balance.BitcoinBalance
import com.mangala.wallet.features.chains.bitcoin.domain.model.utxo.BitcoinUtxo
import com.mangala.wallet.model.blockchain.BlockchainType
import com.mangala.wallet.model.util.Resource
import fr.acinq.lightning.utils.Connection
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface ElectrumRepository {
    val connectionStatus: Flow<Connection>
    
    val walletBalance: StateFlow<ElectrumManager.WalletBalance>
    
    fun startConnection(blockchainType: BlockchainType)

    fun stopConnection()
    
    suspend fun addAddress(blockchainType: BlockchainType, address: String)
    
    suspend fun addAddresses(blockchainType: BlockchainType, addresses: List<String>)
    
    suspend fun getAddressBalance(blockchainType: BlockchainType, address: String): ElectrumManager.AddressBalance
    
    suspend fun getAddressUtxos(blockchainType: BlockchainType, address: String): List<BitcoinUtxo>
    
    suspend fun updateWalletBalance(blockchainType: BlockchainType)

    fun getBalance(
        forceRefresh: Boolean,
        accountId: String,
        bip84Address: String,
        blockchainType: BlockchainType
    ): Flow<Resource<BitcoinBalance?>>
}