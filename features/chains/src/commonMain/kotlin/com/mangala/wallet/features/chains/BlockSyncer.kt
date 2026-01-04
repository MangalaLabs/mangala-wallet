package com.mangala.wallet.features.chains

import com.mangala.wallet.model.blockchain.BlockchainType
import com.mangala.wallet.model.blockchain.Chain

interface BlockSyncer {
    fun startSyncIfNotRunning(url: String, chain: Chain)
    fun stopSync()
    suspend fun waitUntilTransactionConfirmed(
        blockchainType: BlockchainType,
        txHash: String,
    ): String?
}