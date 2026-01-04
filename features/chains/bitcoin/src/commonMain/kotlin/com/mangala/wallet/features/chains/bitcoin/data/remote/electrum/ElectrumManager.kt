package com.mangala.wallet.features.chains.bitcoin.data.remote.electrum

import com.mangala.wallet.features.chains.bitcoin.domain.model.utxo.BitcoinUtxo
import com.mangala.wallet.features.chains.bitcoin.domain.utils.getChainHash
import com.mangala.wallet.model.blockchain.BlockchainType
import fr.acinq.bitcoin.Bitcoin
import fr.acinq.bitcoin.Block
import fr.acinq.bitcoin.BlockHash
import fr.acinq.bitcoin.ByteVector32
import fr.acinq.bitcoin.Crypto
import fr.acinq.bitcoin.Script
import fr.acinq.lightning.blockchain.electrum.ElectrumClient
import fr.acinq.lightning.blockchain.electrum.ElectrumConnectionStatus
import fr.acinq.lightning.blockchain.electrum.HeaderSubscriptionResponse
import fr.acinq.lightning.io.TcpSocket
import fr.acinq.lightning.logging.LoggerFactory
import fr.acinq.lightning.utils.Connection
import fr.acinq.lightning.utils.ServerAddress
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class ElectrumManager(
    loggerFactory: LoggerFactory,
    scope: CoroutineScope = MainScope(),
    private val socketBuilder: suspend () -> TcpSocket.Builder,
    pingInterval: Duration = 30.seconds,
    rpcTimeout: Duration = 10.seconds
) {
    private val electrumClient = ElectrumClient(
        scope = scope,
        loggerFactory = loggerFactory,
        pingInterval = pingInterval,
        rpcTimeout = rpcTimeout
    )

    val connectionStatus: Flow<Connection> = electrumClient.connectionStatus.map { it.toConnectionState() }

    private val _lastServer = MutableStateFlow<ServerAddress?>(null)
    val lastServer: StateFlow<ServerAddress?> = _lastServer

    // Track Electrum headers to monitor blockchain sync
    private val _electrumHeaders = MutableStateFlow<HeaderSubscriptionResponse?>(null)
    val electrumHeaders: StateFlow<HeaderSubscriptionResponse?> = _electrumHeaders

    private fun ElectrumConnectionStatus.toConnectionState(): Connection {
        return when (this) {
            is ElectrumConnectionStatus.Connected -> Connection.ESTABLISHED
            is ElectrumConnectionStatus.Closed -> Connection.CLOSED(reason = this.reason)
            ElectrumConnectionStatus.Connecting -> Connection.ESTABLISHING
        }
    }

    init {
        // Listen for Electrum header notifications
        scope.launch {
            electrumClient.notifications.filterIsInstance<HeaderSubscriptionResponse>().collect {
                _electrumHeaders.value = it
            }
        }
    }

    suspend fun connectToServer(server: ServerAddress, timeout: Duration = 10.seconds): Boolean {
        try {
            electrumClient.connect(server, socketBuilder(), timeout = timeout)
            _lastServer.value = server
            return true
        } catch (e: Exception) {
            return false
        }
    }

    suspend fun connectToRandomServer(blockchainType: BlockchainType, timeout: Duration = 10.seconds): Boolean {
        val servers = getElectrumServerConfig(blockchainType)

        if (servers.isEmpty()) return false

        val server = servers.random()
        return connectToServer(server, timeout)
    }

    suspend fun disconnect() {
        electrumClient.disconnect()
    }

    suspend fun getAddressHistory(blockchainType: BlockchainType, address: String): Boolean {
        val scriptHash = addressToElectrumScriptHash(blockchainType, address)
        val history = electrumClient.getScriptHashHistory(scriptHash)
        return history.isNotEmpty()
    }

    suspend fun getAddressBalance(blockchainType: BlockchainType, address: String): AddressBalance {
        TODO()
//        val scriptHash = addressToElectrumScriptHash(blockchainType, address)
//        val balance = electrumClient.getScriptHashBalance(scriptHash)
//        return AddressBalance(
//            confirmed = balance.confirmed,
//            unconfirmed = balance.unconfirmed
//        )
    }

    suspend fun getAddressUnspentOutputs(blockchainType: BlockchainType, address: String): List<BitcoinUtxo> {
        val scriptHash = addressToElectrumScriptHash(blockchainType, address)
        val utxos = electrumClient.getScriptHashUnspents(scriptHash)

        return utxos.map { utxo ->
            BitcoinUtxo(
                txId = utxo.txid.value.toHex(),
                vout = utxo.outputIndex,
                amountInSatoshis = utxo.value
            )
        }
    }

    suspend fun subscribeToAddress(blockchainType: BlockchainType, address: String) {
        val scriptHash = addressToElectrumScriptHash(blockchainType, address)
        electrumClient.startScriptHashSubscription(scriptHash)
    }

    // Helper models
    data class AddressBalance(
        val confirmed: Long,
        val unconfirmed: Long,
        val total: Long = confirmed + unconfirmed
    )
    
    data class WalletBalance(
        val confirmedBalance: Long = 0,
        val unconfirmedBalance: Long = 0,
        val totalBalance: Long = confirmedBalance + unconfirmedBalance,
        val addresses: Map<String, AddressBalance> = emptyMap(),
        val lastUpdated: Long = 0
    )

    data class UnspentOutput(
        val txid: String,
        val vout: Int,
        val value: Long,
        val height: Int
    )

    private fun getElectrumServerConfig(blockchainType: BlockchainType): List<ServerAddress> {
        return when (blockchainType) {
            BlockchainType.Bitcoin -> mainnetElectrumServers
            BlockchainType.BitcoinTestnet4 -> testnetElectrumServers
            else -> throw IllegalArgumentException("Unsupported blockchain type $blockchainType")
        }
    }

    companion object {
        private val mainnetElectrumServers = listOf(
            electrumServer("electrum.blockstream.info"),
            electrumServer("electrum.acinq.co"),
        )

        private val testnetElectrumServers = listOf(
            electrumServer(
                host = "testnet.qtornado.com", port = 51002, publicKey =
                    "MIICIjANBgkqhkiG9w0BAQEFAAOCAg8AMIICCgKCAgEAwkLgqNkkTbwpV3gMdgDA" +
                            "+jJFdzrOp8vIDT/qxVIox8NZ53pxPc2N44aeY1NJx4TyfpHUGcI7l+gxZfLr8a13" +
                            "o3CIQSotDbZZdJhS6Ir5tT4iRqwZJch+HayTQf9rztv8OQWgrflWDzCiYtBA5PGx" +
                            "6LEQWyah/xPPUbeANe/ndEzlfAhXjNcynSfrkikzTgFNBqnc5CcTkHjYgzCXqMwy" +
                            "ZCD6kQTQG+eqIHSHul21dwUougfCWCR+P0zFA7LeUfPz2mLZktmGXjqTyYZ+0ZTU" +
                            "gJz/MMZt9PDWGJZsHQzoFSCMicukKtnvZ4Q0gbPOoYp8+WjD4SH+WmC3MZdLagsi" +
                            "05hUDdm7PHIM1VHQTALLGRnW3yTOaqhsvYGAM5UOkDcmgUqIr6IztHGWCKldfbhS" +
                            "c4l7BIgvwW2M6FxYlSAcavIodNfvEC1ythdMzl8bZsBjGIOZ39WtiM0grgcg7bb8" +
                            "W5ovZpLOXpzZBjS0zB0sZJnumjS+3jCSjy9rZXGUn3JmMdqtTV8RQxkB8OBJhFf5" +
                            "qtMSZXiJIr9RH71VoJKjnds/hoILHuCKU3HOJeo0+4KSD8+q4g3tZLr/haIrsHg5" +
                            "uifT9db6tDML1PTKpbHkW+f3w9PdhSmsNUUXrgNmQ0MoBhxV7U2Qcug3jX3xaf1P" +
                            "gwWDg3nZZizhuvBceY0IYLECAwEAAQ=="
            )
        )

        private fun electrumServer(host: String, port: Int = 50002): ServerAddress =
            ServerAddress(host = host, port = port, tls = TcpSocket.TLS.TRUSTED_CERTIFICATES())

        private fun electrumServer(host: String, port: Int = 50002, publicKey: String): ServerAddress =
            ServerAddress(host = host, port = port, tls = TcpSocket.TLS.PINNED_PUBLIC_KEY(publicKey))

        private fun electrumServerOnion(host: String, port: Int = 50002): ServerAddress =
            ServerAddress(host = host, port = port, tls = TcpSocket.TLS.DISABLED)
    }
}

private fun addressToElectrumScriptHash(blockchainType: BlockchainType, address: String): ByteVector32 {
    val scriptElements = Bitcoin.addressToPublicKeyScript(getChainHash(blockchainType), address).right.orEmpty()
    val scriptBytes = Script.write(scriptElements)
    
    val digest = Crypto.sha256(scriptBytes)
    
    return ByteVector32(digest.reversedArray())
}