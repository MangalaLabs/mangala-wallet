package com.mangala.wallet.features.chains

import com.mangala.wallet.domain.transaction.history.Transaction
import com.mangala.wallet.model.blockchain.BlockchainType

interface BlockSyncerPlugin {
    suspend fun onTransactionStatusUpdatedForAccount(blockchainType: BlockchainType, successfulTransactionsByAccount: Map<String, List<Transaction>>, accountId: String)
}