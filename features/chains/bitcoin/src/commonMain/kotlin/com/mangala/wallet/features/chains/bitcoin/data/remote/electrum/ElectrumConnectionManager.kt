package com.mangala.wallet.features.chains.bitcoin.data.remote.electrum

import com.mangala.wallet.model.blockchain.BlockchainType
import fr.acinq.lightning.utils.Connection
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class ElectrumConnectionManager(
    private val electrumManager: ElectrumManager,
    private val scope: CoroutineScope = MainScope()
) {
    private var connectionJob: Job? = null
    private var connectionAttempts = 0

    val connectionStatus = electrumManager.connectionStatus

    fun startConnectionLoop(blockchainType: BlockchainType) {
        println("ElectrumConnectionManager starting connection loop")
        if (connectionJob != null) return

        connectionJob = scope.launch {
            connectionStatus.collect { status ->
                when (status) {
                    is Connection.ESTABLISHED -> {
                        println("ElectrumConnectionManager connected")
                        connectionAttempts = 0
                    }
                    is Connection.CLOSED -> {
                        println("ElectrumConnectionManager connection closed ${status.reason}")
                        delay(getReconnectionDelay(connectionAttempts))
                        connectionAttempts++
                        tryConnect(blockchainType)
                    }

                    Connection.ESTABLISHING -> {
                        println("ElectrumConnectionManager establishing connection")
                    }
                }
            }
        }

        // Initial connection attempt
        scope.launch { tryConnect(blockchainType) }
    }

    fun stopConnectionLoop() {
        connectionJob?.cancel()
        connectionJob = null
        scope.launch {
            electrumManager.disconnect()
        }
    }

    private suspend fun tryConnect(blockchainType: BlockchainType) {
        electrumManager.connectToRandomServer(blockchainType)
    }

    private fun getReconnectionDelay(attempts: Int): Duration {
        return when {
            attempts <= 1 -> 1.seconds
            attempts <= 3 -> 5.seconds
            attempts <= 6 -> 15.seconds
            attempts <= 10 -> 30.seconds
            else -> 60.seconds
        }
    }
}